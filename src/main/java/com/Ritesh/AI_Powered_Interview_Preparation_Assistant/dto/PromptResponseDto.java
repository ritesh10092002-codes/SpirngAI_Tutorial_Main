package com.Ritesh.AI_Powered_Interview_Preparation_Assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromptResponseDto {
    public List<String> response;
}
