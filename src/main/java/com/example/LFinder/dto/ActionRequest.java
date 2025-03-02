package com.example.LFinder.dto;

import lombok.Data;

@Data
public class ActionRequest {
    private Integer targetUserId;
    private Boolean isLike;
}
