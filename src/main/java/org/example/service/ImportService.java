package org.example.service;

import org.example.model.*;
import org.example.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final HumanBeingRepository humanBeingRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final CarRepository carRepository;
    private final ImportHistoryRepository importHistoryRepository;
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    @Transactional
    public ImportHistory importFromFile(MultipartFile file, String username) {
        ImportHistory history = new ImportHistory();
        history.setUsername(username);
        history.setFileName(file.getOriginalFilename());

        try {
            List<HumanBeingDTO> dtos = parseFile(file);
            validateAll(dtos);

            int count = 0;
            for (HumanBeingDTO dto : dtos) {
                createHumanBeing(dto);
                count++;
            }

            history.setStatus(ImportStatus.SUCCESS);
            history.setObjectsAdded(count);

        } catch (Exception e) {
            history.setStatus(ImportStatus.FAILURE);
            history.setErrorMessage(e.getMessage());
            history.setObjectsAdded(0);
            throw new RuntimeException("Ошибка импорта: " + e.getMessage(), e);
        } finally {
            importHistoryRepository.save(history);
        }

        return history;
    }

    private List<HumanBeingDTO> parseFile(MultipartFile file) throws Exception {
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

    private void validateAll(List<HumanBeingDTO> dtos) {
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < dtos.size(); i++) {
            HumanBeingDTO dto = dtos.get(i);
            try {
                validateDTO(dto);
            } catch (Exception e) {
                errors.add("Объект " + (i + 1) + ": " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Ошибки валидации:\n" + String.join("\n", errors));
        }
    }

    private void validateDTO(HumanBeingDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }

        if (dto.getCoordinates() == null) {
            throw new IllegalArgumentException("Координаты не могут быть null");
        }

        if (dto.getCoordinates().getY() == null) {
            throw new IllegalArgumentException("Координата Y не может быть null");
        }

        if (dto.getCoordinates().getY() > 277) {
            throw new IllegalArgumentException("Координата Y не может быть больше 277");
        }

        if (dto.getImpactSpeed() == null) {
            throw new IllegalArgumentException("ImpactSpeed не может быть null");
        }

        if (dto.getImpactSpeed() > 664) {
            throw new IllegalArgumentException("ImpactSpeed не может быть больше 664");
        }

        if (dto.getSoundtrackName() == null) {
            throw new IllegalArgumentException("SoundtrackName не может быть null");
        }

        if (dto.getWeaponType() == null) {
            throw new IllegalArgumentException("WeaponType не может быть null");
        }

        if (dto.getCar() != null) {
            if (dto.getCar().getName() == null) {
                throw new IllegalArgumentException("Имя машины не может быть null");
            }
            if (dto.getCar().getCool() == null) {
                throw new IllegalArgumentException("Поле 'cool' машины не может быть null");
            }
        }
    }

    private void createHumanBeing(HumanBeingDTO dto) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(dto.getCoordinates().getX());
        coordinates.setY(dto.getCoordinates().getY());
        coordinatesRepository.save(coordinates);

        Car car = null;
        if (dto.getCar() != null) {
            car = new Car();
            car.setName(dto.getCar().getName());
            car.setCool(dto.getCar().getCool());
            carRepository.save(car);
        }

        HumanBeing humanBeing = new HumanBeing();
        humanBeing.setName(dto.getName());
        humanBeing.setCoordinates(coordinates);
        humanBeing.setCreationDate(LocalDate.now());
        humanBeing.setRealHero(dto.isRealHero());
        humanBeing.setHasToothpick(dto.getHasToothpick());
        humanBeing.setCar(car);
        humanBeing.setMood(dto.getMood());
        humanBeing.setImpactSpeed(dto.getImpactSpeed());
        humanBeing.setSoundtrackName(dto.getSoundtrackName());
        humanBeing.setWeaponType(dto.getWeaponType());

        humanBeingRepository.save(humanBeing);
    }

    // DTO классы
    @lombok.Data
    public static class HumanBeingListWrapper {
        private List<HumanBeingDTO> humans;
    }

    @lombok.Data
    public static class HumanBeingDTO {
        private String name;
        private CoordinatesDTO coordinates;
        private boolean realHero;
        private Boolean hasToothpick;
        private CarDTO car;
        private Mood mood;
        private Integer impactSpeed;
        private String soundtrackName;
        private WeaponType weaponType;
    }

    @lombok.Data
    public static class CoordinatesDTO {
        private float x;
        private Double y;
    }

    @lombok.Data
    public static class CarDTO {
        private String name;
        private Boolean cool;
    }
}
