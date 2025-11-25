package org.example.service;

import org.example.dto.HumanBeingDto;
import org.example.model.*;
import org.example.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final HumanBeingRepository humanBeingRepository;
    private final ImportHistoryRepository importHistoryRepository;
    private final Validator validator;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    /**
     * Импортирует объекты из файла с полной транзакционностью.
     * При любой ошибке все изменения откатываются.
     */
    @Transactional(rollbackFor = Exception.class)
    public ImportHistory importFromFile(MultipartFile file, String username) {
        ImportHistory history = new ImportHistory();
        history.setUsername(username);
        history.setFileName(file.getOriginalFilename());

        try {
            // 1. Парсинг файла
            List<HumanBeingDto> dtos = parseFile(file);
            
            if (dtos == null || dtos.isEmpty()) {
                throw new IllegalArgumentException("Файл не содержит объектов для импорта");
            }

            // 2. Валидация всех объектов перед сохранением
            List<String> validationErrors = validateAll(dtos);
            if (!validationErrors.isEmpty()) {
                String errorMessage = "Обнаружены ошибки валидации:\n" + 
                    String.join("\n", validationErrors);
                throw new IllegalArgumentException(errorMessage);
            }

            // 3. Сохранение всех объектов в одной транзакции
            int count = 0;
            for (HumanBeingDto dto : dtos) {
                createHumanBeing(dto);
                count++;
            }

            // 4. Успешное завершение
            history.setStatus(ImportStatus.SUCCESS);
            history.setObjectsAdded(count);
            
            // Сохраняем историю успешного импорта
            return saveHistory(history);
            
        } catch (IllegalArgumentException e) {
            // Ошибки валидации - транзакция откатится автоматически
            history.setStatus(ImportStatus.FAILURE);
            history.setErrorMessage(e.getMessage());
            history.setObjectsAdded(0);
            // Сохраняем историю ошибки в отдельной транзакции
            saveHistoryInNewTransaction(history);
            // Прокидываем исключение дальше для отката транзакции
            throw e;
        } catch (Exception e) {
            // Любые другие ошибки - транзакция откатится автоматически
            history.setStatus(ImportStatus.FAILURE);
            history.setErrorMessage("Ошибка при импорте: " + e.getMessage());
            history.setObjectsAdded(0);
            // Сохраняем историю ошибки в отдельной транзакции
            saveHistoryInNewTransaction(history);
            // Прокидываем исключение дальше для отката транзакции
            throw new RuntimeException("Ошибка импорта: " + e.getMessage(), e);
        }
    }

    /**
     * Сохраняет историю в текущей транзакции (для успешного импорта).
     */
    private ImportHistory saveHistory(ImportHistory history) {
        return importHistoryRepository.save(history);
    }

    /**
     * Сохраняет историю в новой транзакции (для ошибок, чтобы не откатилась).
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void saveHistoryInNewTransaction(ImportHistory history) {
        importHistoryRepository.save(history);
    }

    private List<HumanBeingDto> parseFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        ObjectMapper mapper;

        if (fileName.endsWith(".json")) {
            mapper = jsonMapper;
        } else if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            mapper = yamlMapper;
        } else {
            throw new IllegalArgumentException("Неподдерживаемый формат файла. Используйте JSON или YAML");
        }

        HumanBeingListWrapper wrapper = mapper.readValue(
                file.getInputStream(),
                HumanBeingListWrapper.class
        );
        return wrapper.getHumans();
    }

    /**
     * Валидация всех объектов с использованием Bean Validation и кастомных правил.
     * Возвращает список ошибок вместо выбрасывания исключения.
     */
    private List<String> validateAll(List<HumanBeingDto> dtos) {
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < dtos.size(); i++) {
            HumanBeingDto dto = dtos.get(i);
            int objectNumber = i + 1;
            
            // 1. Bean Validation (аннотации из DTO) - включая вложенные объекты
            Set<ConstraintViolation<HumanBeingDto>> violations = validator.validate(dto);
            for (ConstraintViolation<HumanBeingDto> violation : violations) {
                String fieldPath = violation.getPropertyPath().toString();
                errors.add(String.format("Объект %d, поле '%s': %s", 
                    objectNumber, 
                    fieldPath.isEmpty() ? violation.getRootBeanClass().getSimpleName() : fieldPath,
                    violation.getMessage()));
            }
            
            // Валидация вложенных объектов
            if (dto.getCoordinates() != null) {
                Set<ConstraintViolation<org.example.model.Coordinates>> coordViolations = 
                    validator.validate(dto.getCoordinates());
                for (ConstraintViolation<org.example.model.Coordinates> violation : coordViolations) {
                    errors.add(String.format("Объект %d, координаты.%s: %s", 
                        objectNumber, 
                        violation.getPropertyPath(), 
                        violation.getMessage()));
                }
            }
            
            if (dto.getCar() != null) {
                Set<ConstraintViolation<org.example.model.Car>> carViolations = 
                    validator.validate(dto.getCar());
                for (ConstraintViolation<org.example.model.Car> violation : carViolations) {
                    errors.add(String.format("Объект %d, машина.%s: %s", 
                        objectNumber, 
                        violation.getPropertyPath(), 
                        violation.getMessage()));
                }
            }
            
            // 2. Дополнительная бизнес-логика валидации
            List<String> customErrors = validateBusinessRules(dto, objectNumber);
            errors.addAll(customErrors);
        }

        return errors;
    }

    /**
     * Дополнительная валидация бизнес-правил, не покрытых Bean Validation.
     */
    private List<String> validateBusinessRules(HumanBeingDto dto, int objectNumber) {
        List<String> errors = new ArrayList<>();

        // Проверка координат
        if (dto.getCoordinates() != null) {
            if (dto.getCoordinates().getY() != null && dto.getCoordinates().getY() > 277) {
                errors.add(String.format("Объект %d: Координата Y не может быть больше 277 (получено: %s)", 
                    objectNumber, dto.getCoordinates().getY()));
            }
        }

        // Проверка ImpactSpeed
        if (dto.getImpactSpeed() <= 0) {
            errors.add(String.format("Объект %d: ImpactSpeed должен быть больше 0 (получено: %s)", 
                objectNumber, dto.getImpactSpeed()));
        }
        if (dto.getImpactSpeed() > 664) {
            errors.add(String.format("Объект %d: ImpactSpeed не может быть больше 664 (получено: %s)", 
                objectNumber, dto.getImpactSpeed()));
        }

        // Проверка Car (если указан)
        if (dto.getCar() != null) {
            if (dto.getCar().getName() == null || dto.getCar().getName().trim().isEmpty()) {
                errors.add(String.format("Объект %d: Если указана машина, её имя не может быть пустым", 
                    objectNumber));
            }
            if (dto.getCar().getCool() == null) {
                errors.add(String.format("Объект %d: Поле 'cool' машины не может быть null", 
                    objectNumber));
            }
        }

        return errors;
    }

    private void createHumanBeing(HumanBeingDto humanBeingDto) {
        HumanBeing humanBeing = HumanBeingDto.convertFromDto(humanBeingDto);
        humanBeingRepository.save(humanBeing);
    }
    // DTO классы
    @lombok.Data
    public static class HumanBeingListWrapper {
        private List<HumanBeingDto> humans;
    }
}
