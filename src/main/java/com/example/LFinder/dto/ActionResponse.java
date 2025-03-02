package com.example.LFinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActionResponse {
    private String message;
    private boolean match;          // Indica si se creó un match
    private String matchedUsername; // Nombre del usuario con el que se hizo match (null si no hay match)
}
