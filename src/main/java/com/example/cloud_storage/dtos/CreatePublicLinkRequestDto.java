package com.example.cloud_storage.dtos;

import lombok.Data;

@Data
public class CreatePublicLinkRequestDto {
    private int expirationHours;
}