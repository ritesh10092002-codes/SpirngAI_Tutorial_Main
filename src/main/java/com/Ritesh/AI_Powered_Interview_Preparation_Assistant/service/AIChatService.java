package com.Ritesh.AI_Powered_Interview_Preparation_Assistant.service;

import com.Ritesh.AI_Powered_Interview_Preparation_Assistant.dto.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.springframework.web.servlet.function.RequestPredicates.param;

@Service
@RequiredArgsConstructor
public class AIChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private  final VectorStore vectorStore;

    public @Nullable MessageResponseDto chatApi(MessageRequestDto requestDto) {

        String message= chatClient.
                prompt()
                .user(requestDto.getMessage())
                .call()
                .content();

        MessageResponseDto messageResponseDto=new MessageResponseDto();
        messageResponseDto.setResponse(message);
        return messageResponseDto;
    }
/*
Use entity property to convert the prompt result to the java object as
needed response

Here param are dynamic variable set from prompt response dto
 */
    public PromptResponseDto interviewQuestions(PromptRequestDto requestDto) {
    return chatClient
                .prompt()
                .user(user -> user
                        .text("""
                        Generate {count} {difficulty}-level interview questions
                        for a developer with {experience} years of experience
                        on {technology}.
                        """)
                        .param("technology", requestDto.getTechnology())
                        .param("difficulty", requestDto.getDifficulty())
                        .param("experience", requestDto.getExperience())
                        .param("count", requestDto.getCount())
                )
                .call()
             .entity(PromptResponseDto.class);

    }

    public @Nullable AnswerEvaluationResponseDto answerEvaluation(AnswerEvaluationRequestDto requestDto) {
        return chatClient
                .prompt()
                .system("""
                    You are an expert Java technical interviewer.

                    Evaluate the candidate's answer objectively.

                    Give a score from 0 to 10.

                    Consider:
                    - Technical correctness
                    - Understanding of the concept
                    - Completeness
                    - Clarity

                    Return the evaluation in the requested structured format.
                    """)
                .user(user -> user
                        .text("""
                            Evaluate the following interview answer.

                            Question:
                            {question}

                            Candidate Answer:
                            {answer}
                            """)
                        .param("question", requestDto.getQuestion())
                        .param("answer", requestDto.getAnswer())
                )
                .call()
                .entity(AnswerEvaluationResponseDto.class);
    }

    /*
        Here chatmemory and advisory is used to store the mesaage in memory
     */
    public @Nullable ChatMemoryResponseDto memory(ChatMemoryRequestDto requestDto) {
        String response = chatClient
                .prompt()
                .system("""
                    You are a helpful Java interview assistant.
                    Remember relevant details from the current conversation.
                    Answer questions using the available conversation history.
                    """)
                .user(requestDto.getMessage())
                .advisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .advisors(advisorSpec ->
                        advisorSpec.param(
                                ChatMemory.CONVERSATION_ID,
                                requestDto.getConversationId()
                        )
                )
                .call()
                .content();

        return new ChatMemoryResponseDto(
                requestDto.getConversationId(),
                response
        );
    }
    public Map<String, Object> askQuestion(String question) {

        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(5)
                        .build()
        );

        if (documents == null || documents.isEmpty()) {
            return Map.of(
                    "question", question,
                    "answer", "No relevant information found in uploaded documents.",
                    "sources", List.of()
            );
        }

        String context = documents.stream()
                .map(Document::getText)
                .reduce((a, b) -> a + "\n\n" + b)
                .orElse("");

        String promptText = """
                Answer the question using only the context below.
                If the answer is not available in the context,
                say that the uploaded documents do not contain
                sufficient information.

                Context:
                {context}

                Question:
                {question}

                Answer:
                """;

        PromptTemplate template = new PromptTemplate(promptText);

        String prompt = template.create(Map.of(
                "context", context,
                "question", question
        )).getContents();

        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return Map.of(
                "question", question,
                "answer", answer == null ? "" : answer,
                "sources", documents.stream()
                        .map(doc -> String.valueOf(
                                doc.getMetadata().getOrDefault(
                                        "source", "Unknown"
                                )
                        ))
                        .distinct()
                        .toList()
        );
    }
}
