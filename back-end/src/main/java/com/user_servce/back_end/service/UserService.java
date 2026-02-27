package com.user_servce.back_end.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.user_servce.back_end.dto.BulkActionResult;
import com.user_servce.back_end.dto.BulkDeleteResponse;
import com.user_servce.back_end.dto.DeleteUsersRequest;
import com.user_servce.back_end.dto.FailedItem;
import com.user_servce.back_end.dto.HistoryData;
import com.user_servce.back_end.dto.PaginationInfo;
import com.user_servce.back_end.dto.StatusChangeRequest;
import com.user_servce.back_end.dto.UserFilterRequest;
import com.user_servce.back_end.dto.UserHistoryResponse;
import com.user_servce.back_end.dto.UserRequest;
import com.user_servce.back_end.dto.UserResponse;
import com.user_servce.back_end.dto.UserUpdateRequest;
import com.user_servce.back_end.dto.UsersListResponse;
import com.user_servce.back_end.entity.CompanyProfile;
import com.user_servce.back_end.entity.Department;
import com.user_servce.back_end.entity.User;
import com.user_servce.back_end.entity.UserHistory;
import com.user_servce.back_end.exception.FieldConflictException;
import com.user_servce.back_end.repository.CompanyProfileRepository;
import com.user_servce.back_end.repository.DepartmentRepository;
import com.user_servce.back_end.repository.LookupValueRepository;
import com.user_servce.back_end.repository.UserHistoryRepository;
import com.user_servce.back_end.repository.UserRepository;
import com.user_servce.back_end.specification.UserSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final LookupValueRepository lookupValueRepository;

    @Transactional(readOnly = true)
    public UsersListResponse search(UserFilterRequest filter) {
        Pageable pageable = PageRequest.of(
            Math.max(filter.getPage(), 0),
            Math.max(filter.getSize(), 1),
            Sort.by(Sort.Direction.DESC, "createdDate")
        );
        Page<User> page = userRepository.findAll(UserSpecification.filter(filter), pageable);
        List<UserResponse> items = page.map(this::mapToResponse).getContent();
        return UsersListResponse.builder()
            .total(page.getTotalElements())
            .items(items)
            .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getDetail(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        ensureCreateFieldsUnique(request);

        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        CompanyProfile companyProfile = companyProfileRepository.findById(request.getCompanyProfileId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company profile not found"));

        User user = new User();
        user.setUserName(request.getUserName());
        user.setPassword(request.getPassword());
        user.setFullName(request.getFullName());
        user.setBirthDate(request.getBirthDate());
        user.setGenderCode(request.getGenderCode());
        user.setAddress(request.getAddress());
        user.setPhoneNumber(normalizeNullable(request.getPhoneNumber()));
        user.setFaxNumber(request.getFaxNumber());
        user.setEmail(normalizeNullable(request.getEmail()));
        user.setIdentityNumber(request.getIdentityNumber());
        user.setIdentityTypeCode(request.getIdentityTypeCode());
        user.setIdentityIssuedDate(request.getIdentityIssuedDate());
        user.setIdentityIssuedPlace(request.getIdentityIssuedPlace());
        user.setDepartment(department);
        user.setPositionCode(request.getPositionCode());
        user.setStatusFlag(request.getStatusFlag() != null ? request.getStatusFlag() : 1);
        user.setCompanyProfile(companyProfile);
        user.setDescription(request.getDescription());
        user.setCreatedBy("system");
        user.setLastUpdatedBy("system");

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    @Transactional
    public UserResponse update(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ensureUpdateFieldsUnique(user, request);

        Department department = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found"));
        CompanyProfile companyProfile = companyProfileRepository.findById(request.getCompanyProfileId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company profile not found"));

        List<UserHistory> histories = new ArrayList<>();
        String actor = "system";

        if (!same(user.getFullName(), request.getFullName())) {
            histories.add(buildHistory(user, "FULL_NAME", user.getFullName(), request.getFullName(), actor));
            user.setFullName(request.getFullName());
        }
        if (!same(user.getBirthDate(), request.getBirthDate())) {
            histories.add(buildHistory(user, "BIRTH_DATE", user.getBirthDate(), request.getBirthDate(), actor));
            user.setBirthDate(request.getBirthDate());
        }
        if (!same(user.getGenderCode(), request.getGenderCode())) {
            histories.add(buildHistory(user, "GENDER_CD", user.getGenderCode(), request.getGenderCode(), actor));
            user.setGenderCode(request.getGenderCode());
        }
        if (!same(user.getAddress(), request.getAddress())) {
            histories.add(buildHistory(user, "ADDRESS", user.getAddress(), request.getAddress(), actor));
            user.setAddress(request.getAddress());
        }
        String normalizedPhoneNumber = normalizeNullable(request.getPhoneNumber());
        if (!same(normalizeNullable(user.getPhoneNumber()), normalizedPhoneNumber)) {
            histories.add(buildHistory(user, "PHONE_NO", user.getPhoneNumber(), normalizedPhoneNumber, actor));
            user.setPhoneNumber(normalizedPhoneNumber);
        }
        if (!same(user.getFaxNumber(), request.getFaxNumber())) {
            histories.add(buildHistory(user, "FAX_NO", user.getFaxNumber(), request.getFaxNumber(), actor));
            user.setFaxNumber(request.getFaxNumber());
        }
        String normalizedEmail = normalizeNullable(request.getEmail());
        if (!same(user.getEmail(), normalizedEmail)) {
            histories.add(buildHistory(user, "EMAIL", user.getEmail(), normalizedEmail, actor));
            user.setEmail(normalizedEmail);
        }
        if (!same(user.getIdentityNumber(), request.getIdentityNumber())) {
            histories.add(buildHistory(user, "IDENTITY", user.getIdentityNumber(), request.getIdentityNumber(), actor));
            user.setIdentityNumber(request.getIdentityNumber());
        }
        if (!same(user.getIdentityTypeCode(), request.getIdentityTypeCode())) {
            histories.add(buildHistory(user, "IDENTITY_TYPE_CD", user.getIdentityTypeCode(), request.getIdentityTypeCode(), actor));
            user.setIdentityTypeCode(request.getIdentityTypeCode());
        }
        if (!same(user.getIdentityIssuedDate(), request.getIdentityIssuedDate())) {
            histories.add(buildHistory(user, "IDENTITY_ISSUED_DATE", user.getIdentityIssuedDate(), request.getIdentityIssuedDate(), actor));
            user.setIdentityIssuedDate(request.getIdentityIssuedDate());
        }
        if (!same(user.getIdentityIssuedPlace(), request.getIdentityIssuedPlace())) {
            histories.add(buildHistory(user, "IDENTITY_ISSUED_PLACE", user.getIdentityIssuedPlace(), request.getIdentityIssuedPlace(), actor));
            user.setIdentityIssuedPlace(request.getIdentityIssuedPlace());
        }
        if (!same(user.getDepartment().getId(), request.getDepartmentId())) {
            histories.add(buildHistory(user, "DEPARTMENT_ID", user.getDepartment().getId(), request.getDepartmentId(), actor));
            user.setDepartment(department);
        }
        if (!same(user.getPositionCode(), request.getPositionCode())) {
            histories.add(buildHistory(user, "POSITION_CD", user.getPositionCode(), request.getPositionCode(), actor));
            user.setPositionCode(request.getPositionCode());
        }
        if (!same(user.getCompanyProfile().getId(), request.getCompanyProfileId())) {
            histories.add(buildHistory(user, "COMPANY_PROFILE_ID", user.getCompanyProfile().getId(), request.getCompanyProfileId(), actor));
            user.setCompanyProfile(companyProfile);
        }
        if (!same(user.getDescription(), request.getDescription())) {
            histories.add(buildHistory(user, "DESCRIPTION", user.getDescription(), request.getDescription(), actor));
            user.setDescription(request.getDescription());
        }
        if (request.getStatusFlag() != null && !same(user.getStatusFlag(), request.getStatusFlag())) {
            histories.add(buildHistory(user, "STATUS_FLG", user.getStatusFlag(), request.getStatusFlag(), actor));
            user.setStatusFlag(request.getStatusFlag());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank() && !same(user.getPassword(), request.getPassword())) {
            histories.add(buildHistory(user, "PASSWORD", "******", "******", actor));
            user.setPassword(request.getPassword());
        }

        user.setLastUpdatedBy(actor);

        User saved = userRepository.save(user);
        if (!histories.isEmpty()) {
            userHistoryRepository.saveAll(histories);
        }
        return mapToResponse(saved);
    }

    @Transactional
    public BulkActionResult changeStatus(StatusChangeRequest request) {
        List<User> users = userRepository.findAllById(request.getIds());
        Set<String> foundIds = users.stream().map(User::getId).collect(Collectors.toSet());
        List<FailedItem> failed = new ArrayList<>();
        String actor = "system";

        for (String requestedId : request.getIds()) {
            if (!foundIds.contains(requestedId)) {
                failed.add(new FailedItem(requestedId, "User not found"));
            }
        }

        List<UserHistory> histories = new ArrayList<>();
        int affectedCount = 0;
        for (User user : users) {
            if (!same(user.getStatusFlag(), request.getStatusFlag())) {
                histories.add(buildHistory(user, "STATUS_FLG", user.getStatusFlag(), request.getStatusFlag(), actor));
                user.setStatusFlag(request.getStatusFlag());
                user.setLastUpdatedBy(actor);
                affectedCount++;
            }
        }
        if (!users.isEmpty()) {
            userRepository.saveAll(users);
        }
        if (!histories.isEmpty()) {
            userHistoryRepository.saveAll(histories);
        }
        return BulkActionResult.builder()
            .affectedCount(affectedCount)
            .failed(failed)
            .build();
    }

    @Transactional
    public void changeStatusSingle(String userId, Integer statusFlag, String actor) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        String actorName = "system";
        if (!same(user.getStatusFlag(), statusFlag)) {
            userHistoryRepository.save(buildHistory(user, "STATUS_FLG", user.getStatusFlag(), statusFlag, actorName));
            user.setStatusFlag(statusFlag);
            user.setLastUpdatedBy(actorName);
            userRepository.save(user);
        }
    }

    @Transactional
    public BulkDeleteResponse deleteUsers(DeleteUsersRequest request) {
        List<User> users = userRepository.findAllById(request.getIds());
        Set<String> foundIds = users.stream().map(User::getId).collect(Collectors.toCollection(HashSet::new));
        List<FailedItem> failed = new ArrayList<>();
        for (String requestedId : request.getIds()) {
            if (!foundIds.contains(requestedId)) {
                failed.add(new FailedItem(requestedId, "User not found"));
            }
        }
        int deletedCount = users.size();
        if (!users.isEmpty()) {
            userRepository.deleteAll(users);
        }
        return BulkDeleteResponse.builder()
            .deletedCount(deletedCount)
            .failed(failed)
            .build();
    }

    @Transactional
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public HistoryData getHistory(String userId, int page, int size) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "changedDate"));
        Page<UserHistory> historyPage = userHistoryRepository.findByUser_IdOrderByChangedDateDesc(userId, pageable);
        List<UserHistoryResponse> items = historyPage.stream()
            .map(h -> UserHistoryResponse.builder()
                .id(h.getId())
                .fieldName(h.getFieldName())
                .oldValue(h.getOldValue())
                .newValue(h.getNewValue())
                .changedBy(h.getChangedBy())
                .changedDate(h.getChangedDate())
                .build())
            .collect(Collectors.toList());
        PaginationInfo pagination = PaginationInfo.builder()
            .page(historyPage.getNumber() + 1)
            .pageSize(historyPage.getSize())
            .totalItems((int) historyPage.getTotalElements())
            .totalPages(historyPage.getTotalPages())
            .hasNext(historyPage.hasNext())
            .hasPrev(historyPage.hasPrevious())
            .build();
        return HistoryData.builder()
            .items(items)
            .pagination(pagination)
            .build();
    }

    private void ensureCreateFieldsUnique(UserRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            errors.put("userName", "Username already exists");
        }

        String email = normalizeNullable(request.getEmail());
        if (email != null && userRepository.findByEmail(email).isPresent()) {
            errors.put("email", "Email already exists");
        }

        String phoneNumber = normalizeNullable(request.getPhoneNumber());
        if (phoneNumber != null && userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            errors.put("phoneNumber", "Phone number already exists");
        }

        if (!errors.isEmpty()) {
            throw new FieldConflictException(errors);
        }
    }

    private void ensureUpdateFieldsUnique(User currentUser, UserUpdateRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();

        String email = normalizeNullable(request.getEmail());
        if (email != null && !currentUser.getEmail().equalsIgnoreCase(email)) {
            Optional<User> other = userRepository.findByEmail(email);
            if (other.isPresent() && !other.get().getId().equals(currentUser.getId())) {
                errors.put("email", "Email already exists");
            }
        }

        String phoneNumber = normalizeNullable(request.getPhoneNumber());
        String currentPhoneNumber = normalizeNullable(currentUser.getPhoneNumber());
        if (phoneNumber != null && !same(currentPhoneNumber, phoneNumber)) {
            Optional<User> other = userRepository.findByPhoneNumber(phoneNumber);
            if (other.isPresent() && !other.get().getId().equals(currentUser.getId())) {
                errors.put("phoneNumber", "Phone number already exists");
            }
        }

        if (!errors.isEmpty()) {
            throw new FieldConflictException(errors);
        }
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private UserHistory buildHistory(User user, String fieldName, Object oldValue, Object newValue, String actor) {
        UserHistory history = new UserHistory();
        history.setUser(user);
        history.setFieldName(fieldName);
        history.setOldValue(valueToString(oldValue));
        history.setNewValue(valueToString(newValue));
        history.setChangedBy(actor);
        return history;
    }

    private boolean same(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    private String valueToString(Object value) {
        return value == null ? "" : value.toString();
    }

    private UserResponse mapToResponse(User user) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String positionTitle = null;
        if (user.getPositionCode() != null) {
            positionTitle = lookupValueRepository.findByLookupGroupAndLookupCode("POSITION", user.getPositionCode())
                .map(lv -> lv.getLookupValue())
                .orElse(null);
        }

        String birthDate = formatLocalDate(user.getBirthDate(), dateFormatter);
        String identityIssuedDate = formatLocalDate(user.getIdentityIssuedDate(), dateFormatter);
        String createdDate = formatLocalDateTime(user.getCreatedDate(), dateFormatter);
        String lastUpdatedDate = formatLocalDateTime(user.getLastUpdatedDate(), dateFormatter);
        String statusFlag = null;
        if (user.getStatusFlag() != null) {
            statusFlag = user.getStatusFlag() == 1 ? "Active" : "Inactive";
        }

        return UserResponse.builder()
            .id(user.getId())
            .userName(user.getUserName())
            .fullName(user.getFullName())
            .birthDate(birthDate)
            .genderCode(user.getGenderCode())
            .address(user.getAddress())
            .phoneNumber(user.getPhoneNumber())
            .faxNumber(user.getFaxNumber())
            .email(user.getEmail())
            .identityNumber(user.getIdentityNumber())
            .identityTypeCode(user.getIdentityTypeCode())
            .identityIssuedDate(identityIssuedDate)
            .identityIssuedPlace(user.getIdentityIssuedPlace())
            .departmentId(user.getDepartment() != null ? user.getDepartment().getId() : null)
            .departmentName(user.getDepartment() != null ? user.getDepartment().getDepartmentName() : null)
            .positionCode(user.getPositionCode())
            .statusFlag(statusFlag)
            .companyProfileId(user.getCompanyProfile() != null ? user.getCompanyProfile().getId() : null)
            .companyName(user.getCompanyProfile() != null ? user.getCompanyProfile().getCompanyName() : null)
            .stockCode(user.getCompanyProfile() != null ? user.getCompanyProfile().getStockCode() : null)
            .stockExchange(user.getCompanyProfile() != null ? user.getCompanyProfile().getStockExchange() : null)
            .positionTitle(positionTitle)
            .description(user.getDescription())
            .createdBy(user.getCreatedBy())
            .createdDate(createdDate)
            .lastUpdatedBy(user.getLastUpdatedBy())
            .lastUpdatedDate(lastUpdatedDate)
            .build();
    }

    private String formatLocalDate(LocalDate date, DateTimeFormatter formatter) {
        if (date == null) {
            return null;
        }
        return date.format(formatter);
    }

    private String formatLocalDateTime(LocalDateTime dateTime, DateTimeFormatter formatter) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(formatter);
    }
}
