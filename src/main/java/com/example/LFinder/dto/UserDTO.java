package com.example.LFinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String username;
    private String profilePicture;
}
