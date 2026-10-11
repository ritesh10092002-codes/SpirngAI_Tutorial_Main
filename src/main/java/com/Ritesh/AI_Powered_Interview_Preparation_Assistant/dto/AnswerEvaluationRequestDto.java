package com.Ritesh.AI_Powered_Interview_Preparation_Assistant.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerEvaluationRequestDto {

    private String question;
    private String answer;


}
