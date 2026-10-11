package com.Ritesh.AI_Powered_Interview_Preparation_Assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerEvaluationResponseDto {

    private int score;
    private String feedback;
    private List<String> strengths;
    private List<String> improvements;
    private String idealAnswer;
}
