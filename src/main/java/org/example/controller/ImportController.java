package org.example.controller;

import org.example.model.ImportHistory;
import org.example.repository.ImportHistoryRepository;
import org.example.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;
    private final ImportHistoryRepository importHistoryRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            String username = authentication.getName();
            ImportHistory history = importService.importFromFile(file, username);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<Page<ImportHistory>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "importDate")
        );

        // Проверяем, является ли пользователь администратором
        boolean isAdmin = authentication.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        Page<ImportHistory> history;
        if (isAdmin) {
            // Администратор видит всю историю
            history = importHistoryRepository.findAll(pageRequest);
        } else {
            // Обычный пользователь видит только свою историю
            history = importHistoryRepository.findByUsername(
                    authentication.getName(),
                    pageRequest
            );
        }

        return ResponseEntity.ok(history);
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class ErrorResponse {
        private String message;
    }
}
