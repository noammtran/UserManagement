package com.user_servce.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_servce.back_end.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
