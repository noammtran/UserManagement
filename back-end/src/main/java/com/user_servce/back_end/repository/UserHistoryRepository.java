package com.user_servce.back_end.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.user_servce.back_end.entity.UserHistory;

public interface UserHistoryRepository extends JpaRepository<UserHistory, String> {

    Page<UserHistory> findByUser_IdOrderByChangedDateDesc(String userId, Pageable pageable);
}
