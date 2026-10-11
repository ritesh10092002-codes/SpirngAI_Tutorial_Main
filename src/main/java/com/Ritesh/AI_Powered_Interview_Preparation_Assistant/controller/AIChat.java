package com.Ritesh.AI_Powered_Interview_Preparation_Assistant.controller;

import com.Ritesh.AI_Powered_Interview_Preparation_Assistant.dto.*;
import com.Ritesh.AI_Powered_Interview_Preparation_Assistant.service.AIChatService;
import com.Ritesh.AI_Powered_Interview_Preparation_Assistant.service.DocumentUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AIChat {

    private final AIChatService aiChatService;
    private final DocumentUploadService uploadService;



    /*
      Basic Chat API --Takes input from user and through -chatClient connects
      to llm model to generate response
     */
    @GetMapping("/chat")
    public ResponseEntity<MessageResponseDto> chatApi(@RequestBody MessageRequestDto requestDto){
        return ResponseEntity.ok(aiChatService.chatApi(requestDto));
    }

    /*
     Chat Api based on prompt set in service layer :- input taken and fields
     and response will be based on the prompt given by servie layer
     */
    @GetMapping("/interview/questions")
    public ResponseEntity<PromptResponseDto> interviewQuestions(@RequestBody PromptRequestDto requestDto){
        return ResponseEntity.ok(aiChatService.interviewQuestions(requestDto));

    }

    /*
    Api interview answer evaluation
    Post request--here in service we have used both system and user prompt
    System used for how should you behave? and user used what should you do now?
     */
    @PostMapping("/interview_question/evaluation")
    public ResponseEntity<AnswerEvaluationResponseDto> answerEvaluation(
            @RequestBody AnswerEvaluationRequestDto requestDto){

        return ResponseEntity.ok(aiChatService.answerEvaluation(requestDto));

    }

    /*
    Implemented memory api with both in memory and redis memory storage
    their is change in config and pom file regarding this both memory
    Create bean of redis
     */
    @PostMapping("/memory")
    public ResponseEntity<ChatMemoryResponseDto> memory(@RequestBody ChatMemoryRequestDto requestDto){
        return ResponseEntity.ok(aiChatService.memory(requestDto));
    }

    @PostMapping("/ask")
    public Map<String, Object> askQuestion(
            @RequestBody RagRequest request) {

        if (request.question() == null ||
                request.question().isBlank()) {
            throw new IllegalArgumentException(
                    "Question must not be empty"
            );
        }

        return aiChatService.askQuestion(request.question());
    }
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Map<String, Object> uploadFile(
            @RequestParam("file") MultipartFile file)
            throws IOException {

        return uploadService.uploadFile(file);
    }
}
