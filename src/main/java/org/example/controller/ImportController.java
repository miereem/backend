package org.example.controller;

import org.example.model.ImportHistory;
import org.example.model.ImportStatus;
import org.example.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.example.repository.ImportHistoryRepository;
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
@CrossOrigin(origins = "*",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
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

            if (history.getStatus() == ImportStatus.FAILURE) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(history);
            }

            return ResponseEntity.ok(history);
        } catch (IllegalArgumentException e) {
            // Ошибки валидации
            ImportHistory errorHistory = new ImportHistory();
            errorHistory.setStatus(ImportStatus.FAILURE);
            errorHistory.setErrorMessage(e.getMessage());
            errorHistory.setObjectsAdded(0);
            errorHistory.setUsername(authentication.getName());
            errorHistory.setFileName(file.getOriginalFilename());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorHistory);
        } catch (Exception e) {
            // Другие ошибки
            ImportHistory errorHistory = new ImportHistory();
            errorHistory.setStatus(ImportStatus.FAILURE);
            errorHistory.setErrorMessage("Ошибка при обработке файла: " + e.getMessage());
            errorHistory.setObjectsAdded(0);
            errorHistory.setUsername(authentication.getName());
            errorHistory.setFileName(file.getOriginalFilename());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorHistory);
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
}
