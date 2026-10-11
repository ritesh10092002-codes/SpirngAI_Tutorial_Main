package com.Ritesh.AI_Powered_Interview_Preparation_Assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMemoryRequestDto {

    private String conversationId;
    private String message;
}
