package com.user_servce.back_end.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user_servce.back_end.dto.ApiResponse;
import com.user_servce.back_end.dto.BulkActionResult;
import com.user_servce.back_end.dto.BulkDeleteResponse;
import com.user_servce.back_end.dto.DeleteUsersRequest;
import com.user_servce.back_end.dto.HistoryData;
import com.user_servce.back_end.dto.StatusChangeRequest;
import com.user_servce.back_end.dto.UserFilterRequest;
import com.user_servce.back_end.dto.UserRequest;
import com.user_servce.back_end.dto.UserResponse;
import com.user_servce.back_end.dto.UserUpdateRequest;
import com.user_servce.back_end.dto.UsersListResponse;
import com.user_servce.back_end.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user-service/api/v1/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    
    @GetMapping
    public ResponseEntity<UsersListResponse> search(
        @RequestParam(required = false) String fullName,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Integer statusFlg,
        @RequestParam(required = false) Long departmentId,
        @RequestParam(required = false) Integer positionCd,
        @RequestParam(required = false) Long companyProfileId,
        @RequestParam(required = false) Integer genderCd,
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "10") @Min(1) int limit
    ) {
        UserFilterRequest filter = new UserFilterRequest();
        filter.setKeyword(fullName != null ? fullName : keyword);
        filter.setStatusFlag(statusFlg);
        filter.setDepartmentId(departmentId);
        filter.setPositionCode(positionCd);
        filter.setCompanyProfileId(companyProfileId);
        filter.setGenderCode(genderCd);
        filter.setPage(page - 1); // zero-based for service
        filter.setSize(limit);
        UsersListResponse result = userService.search(filter);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, UserResponse>> getDetail(@PathVariable String id) {
        UserResponse response = userService.getDetail(id);
        Map<String, UserResponse> body = new HashMap<>();
        body.put("items", response);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.create(request);
        ApiResponse<UserResponse> body = ApiResponse.<UserResponse>builder()
            .success(true)
            .statusCode(HttpStatus.CREATED.value())
            .message("Tạo người dùng thành công")
            .data(response)
            .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable String id, @Valid @RequestBody UserUpdateRequest request) {
        userService.update(id, request);
        return ApiResponse.<Void>builder()
            .success(true)
            .statusCode(HttpStatus.NO_CONTENT.value())
            .message("Cập nhật người dùng thành công")
            .data(null)
            .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOne(@PathVariable String id) {
        userService.deleteUser(id);
        return ApiResponse.<Void>builder()
            .success(true)
            .statusCode(HttpStatus.NO_CONTENT.value())
            .message("Xóa người dùng thành công")
            .data(null)
            .build();
    }

    @DeleteMapping
    public ApiResponse<BulkDeleteResponse> deleteMany(@Valid @RequestBody DeleteUsersRequest request) {
        BulkDeleteResponse result = userService.deleteUsers(request);
        return ApiResponse.<BulkDeleteResponse>builder()
            .success(true)
            .statusCode(HttpStatus.NO_CONTENT.value())
            .message("Xóa người dùng thành công")
            .data(result)
            .build();
    }

    @PatchMapping("/{id}/activate")
    public ApiResponse<Void> activateOne(@PathVariable String id) {
        userService.changeStatusSingle(id, 1, null);
        return ApiResponse.<Void>builder()
            .success(true)
            .statusCode(HttpStatus.NO_CONTENT.value())
            .message("Kích hoạt người dùng thành công")
            .data(null)
            .build();
    }

    @PatchMapping("/activate")
    public ApiResponse<BulkActionResult> activateMany(@Valid @RequestBody StatusChangeRequest request) {
        request.setStatusFlag(1);
        BulkActionResult result = userService.changeStatus(request);
        return ApiResponse.<BulkActionResult>builder()
            .success(true)
            .statusCode(HttpStatus.NO_CONTENT.value())
            .message("Kích hoạt người dùng thành công")
            .data(result)
            .build();
    }

    @PatchMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivateOne(@PathVariable String id) {
        userService.changeStatusSingle(id, 0, null);
        return ApiResponse.<Void>builder()
            .success(true)
            .statusCode(HttpStatus.NO_CONTENT.value())
            .message("Hủy kích hoạt người dùng thành công")
            .data(null)
            .build();
    }

    @PatchMapping("/deactivate")
    public ApiResponse<BulkActionResult> deactivateMany(@Valid @RequestBody StatusChangeRequest request) {
        request.setStatusFlag(0);
        BulkActionResult result = userService.changeStatus(request);
        return ApiResponse.<BulkActionResult>builder()
            .success(true)
            .statusCode(HttpStatus.NO_CONTENT.value())
            .message("Hủy kích hoạt người dùng thành công")
            .data(result)
            .build();
    }

    @GetMapping("/{id}/history")
    public ApiResponse<HistoryData> history(
        @PathVariable String id,
        @RequestParam(defaultValue = "1") @Min(1) int page,
        @RequestParam(defaultValue = "20") @Min(1) int limit
    ) {
        HistoryData result = userService.getHistory(id, page - 1, limit);
        return ApiResponse.<HistoryData>builder()
            .success(true)
            .statusCode(HttpStatus.OK.value())
            .message("Lấy lịch sử thay đổi thành công")
            .data(result)
            .build();
    }
}
