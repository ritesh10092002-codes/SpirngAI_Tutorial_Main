package com.Ritesh.AI_Powered_Interview_Preparation_Assistant.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class DocumentUploadService {
    private final VectorStore vectorStore;

    public DocumentUploadService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public Map<String, Object> uploadFile(MultipartFile file)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please upload a non-empty file."
            );
        }

        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException(
                    "Filename is required."
            );
        }

        String lowerName = filename.toLowerCase(Locale.ROOT);
        String text;

        if (lowerName.endsWith(".pdf")) {
            try (PDDocument pdf = Loader.loadPDF(file.getBytes())) {
                text = new PDFTextStripper().getText(pdf);
            }
        } else if (lowerName.endsWith(".txt")) {
            text = new String(
                    file.getBytes(),
                    java.nio.charset.StandardCharsets.UTF_8
            );
        } else {
            throw new IllegalArgumentException(
                    "Only PDF and TXT files are supported."
            );
        }

        if (text.isBlank()) {
            throw new IllegalArgumentException(
                    "No readable text found in the uploaded file."
            );
        }

        // Store the original filename as source metadata.
        Document document = new Document(
                text,
                Map.of("source", filename)
        );

        // Split the document into smaller chunks for retrieval.
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .build();

        List<Document> chunks = splitter.split(List.of(document));
        // Generate embeddings and save the chunks to PGVector.
        vectorStore.add(chunks);

        return Map.of(
                "message", "File uploaded and indexed successfully.",
                "filename", filename,
                "chunksStored", chunks.size()
        );
    }
}
