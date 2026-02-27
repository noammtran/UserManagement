package com.user_servce.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_servce.back_end.entity.CompanyProfile;

public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
}
