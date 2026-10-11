package com.Ritesh.AI_Powered_Interview_Preparation_Assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromptRequestDto {
    public String technology;
    public String difficulty;
    public String experience;
    public String count;
}
