package com.user_servce.back_end.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user_servce.back_end.dto.ApiResponse;
import com.user_servce.back_end.dto.MasterDataResponse;
import com.user_servce.back_end.service.MasterDataService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/masterdata_service/api/v1")
@RequiredArgsConstructor
public class MasterDataController {

    private final MasterDataService masterDataService;

    @GetMapping("/masterdata")
    public ApiResponse<MasterDataResponse> getMasterData() {
        MasterDataResponse data = masterDataService.getMasterData();
        return ApiResponse.<MasterDataResponse>builder()
            .success(true)
            .statusCode(HttpStatus.OK.value())
            .message("Lấy master data thành công")
            .data(data)
            .build();
    }
}
