package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dto.HumanBeingDto;
import org.example.model.Car;
import org.example.model.HumanBeing;
import org.example.model.Mood;
import org.example.model.WeaponType;
import org.example.repository.HumanBeingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HumanBeingService {

    private final HumanBeingRepository humanBeingRepository;
    private static final Logger log = LoggerFactory.getLogger(HumanBeingService.class);


    public HumanBeing create(HumanBeingDto humanBeingDto) {
        // 1) Логируем DTO, пришедший с фронтенда
        log.info("=== Frontend DTO ===");
        log.info("ID: {}", humanBeingDto.getId());
        log.info("Name: {}", humanBeingDto.getName());
        log.info("Coordinates: x={}, y={}",
                humanBeingDto.getCoordinates() != null ? humanBeingDto.getCoordinates().getX() : null,
                humanBeingDto.getCoordinates() != null ? humanBeingDto.getCoordinates().getY() : null);
        log.info("RealHero: {}", humanBeingDto.isRealHero());
        log.info("HasToothpick: {}", humanBeingDto.getHasToothpick());
        log.info("Car: name={}, cool={}",
                humanBeingDto.getCar() != null ? humanBeingDto.getCar().getName() : null,
                humanBeingDto.getCar() != null ? humanBeingDto.getCar().getCool() : null);
        log.info("Mood: {}", humanBeingDto.getMood());
        log.info("ImpactSpeed: {}", humanBeingDto.getImpactSpeed());
        log.info("SoundtrackName: {}", humanBeingDto.getSoundtrackName());
        log.info("WeaponType: {}", humanBeingDto.getWeaponType());

        // 2) Конвертация DTO в сущность
        HumanBeing humanBeing = HumanBeingDto.convertFromDto(humanBeingDto);

        // 3) Логируем сущность перед сохранением
        log.info("=== Converted Entity ===");
        log.info("ID: {}", humanBeing.getId());
        log.info("Name: {}", humanBeing.getName());
        log.info("Coordinates: x={}, y={}",
                humanBeing.getCoordinates() != null ? humanBeing.getCoordinates().getX() : null,
                humanBeing.getCoordinates() != null ? humanBeing.getCoordinates().getY() : null);
        log.info("RealHero: {}", humanBeing.isRealHero());
        log.info("HasToothpick: {}", humanBeing.getHasToothpick());
        log.info("Car: name={}, cool={}",
                humanBeing.getCar() != null ? humanBeing.getCar().getName() : null,
                humanBeing.getCar() != null ? humanBeing.getCar().getCool() : null);
        log.info("Mood: {}", humanBeing.getMood());
        log.info("ImpactSpeed: {}", humanBeing.getImpactSpeed());
        log.info("SoundtrackName: {}", humanBeing.getSoundtrackName());
        log.info("WeaponType: {}", humanBeing.getWeaponType());

        // 4) Сохраняем в базу
        return humanBeingRepository.save(humanBeing);
    }

    public HumanBeing updateById(Long id, HumanBeingDto humanBeingDto) {
        HumanBeing humanBeing = humanBeingRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("HumanBeing with id \" + id + \" not found")
        );

        // 1) Логируем DTO, пришедший с фронтенда
        log.info("=== Frontend DTO ===");
        log.info("ID: {}", humanBeingDto.getId());
        log.info("Name: {}", humanBeingDto.getName());
        log.info("Coordinates: x={}, y={}",
                humanBeingDto.getCoordinates() != null ? humanBeingDto.getCoordinates().getX() : null,
                humanBeingDto.getCoordinates() != null ? humanBeingDto.getCoordinates().getY() : null);
        log.info("RealHero: {}", humanBeingDto.isRealHero());
        log.info("HasToothpick: {}", humanBeingDto.getHasToothpick());
        log.info("Car: name={}, cool={}",
                humanBeingDto.getCar() != null ? humanBeingDto.getCar().getName() : null,
                humanBeingDto.getCar() != null ? humanBeingDto.getCar().getCool() : null);
        log.info("Mood: {}", humanBeingDto.getMood());
        log.info("ImpactSpeed: {}", humanBeingDto.getImpactSpeed());
        log.info("SoundtrackName: {}", humanBeingDto.getSoundtrackName());
        log.info("WeaponType: {}", humanBeingDto.getWeaponType());



        humanBeing.setName(humanBeingDto.getName());
        humanBeing.setCoordinates(humanBeingDto.getCoordinates());
        humanBeing.setRealHero(humanBeingDto.isRealHero());
        humanBeing.setHasToothpick(humanBeingDto.getHasToothpick());
        humanBeing.setCar(humanBeingDto.getCar());
        humanBeing.setMood(humanBeingDto.getMood());
        humanBeing.setImpactSpeed(humanBeingDto.getImpactSpeed());
        humanBeing.setSoundtrackName(humanBeingDto.getSoundtrackName());
        humanBeing.setWeaponType(humanBeingDto.getWeaponType());

        // 3) Логируем сущность перед сохранением
        log.info("=== Converted Entity ===");
        log.info("ID: {}", humanBeing.getId());
        log.info("Name: {}", humanBeing.getName());
        log.info("Coordinates: x={}, y={}",
                humanBeing.getCoordinates() != null ? humanBeing.getCoordinates().getX() : null,
                humanBeing.getCoordinates() != null ? humanBeing.getCoordinates().getY() : null);
        log.info("RealHero: {}", humanBeing.isRealHero());
        log.info("HasToothpick: {}", humanBeing.getHasToothpick());
        log.info("Car: name={}, cool={}",
                humanBeing.getCar() != null ? humanBeing.getCar().getName() : null,
                humanBeing.getCar() != null ? humanBeing.getCar().getCool() : null);
        log.info("Mood: {}", humanBeing.getMood());
        log.info("ImpactSpeed: {}", humanBeing.getImpactSpeed());
        log.info("SoundtrackName: {}", humanBeing.getSoundtrackName());
        log.info("WeaponType: {}", humanBeing.getWeaponType());
        return humanBeingRepository.save(humanBeing);
    }

    public void delete(Long id) {
        if (!humanBeingRepository.existsById(id)) {
            throw new RuntimeException("HumanBeing with id " + id + " not found");
        }
        humanBeingRepository.deleteById(id);
    }

    @Transactional()
    public Optional<HumanBeing> findById(Long id) {
        return humanBeingRepository.findById(id);
    }

    @Transactional()
    public Page<HumanBeing> findAll(Pageable pageable) {
        return humanBeingRepository.findAll(pageable);
    }

    @Transactional()
    public List<HumanBeing> searchByName(String name) {
        return humanBeingRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional()
    public Page<HumanBeing> searchByName(String name, Pageable pageable) {
        return humanBeingRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public void deleteAllByWeaponType(WeaponType weaponType) {
        humanBeingRepository.deleteAllByWeaponType(weaponType);
    }

    public void deleteOneByWeaponType(WeaponType weaponType) {
        List<HumanBeing> humanBeings = humanBeingRepository.findByWeaponType(weaponType);
        if (!humanBeings.isEmpty()) {
            humanBeingRepository.delete(humanBeings.get(0));
        }
    }

    public Map<String, Long> groupBySoundtrackName() {
        List<Object[]> results = humanBeingRepository.countBySoundtrackName();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]
                ));
    }

    public void updateAllMoodToSadness() {
        List<HumanBeing> allHumanBeings = humanBeingRepository.findAll();
        for (HumanBeing humanBeing : allHumanBeings) {
            humanBeing.setMood(Mood.SADNESS);
        }
        humanBeingRepository.saveAll(allHumanBeings);
    }

    public void assignCarToHeroesWithoutCar() {
        List<HumanBeing> heroesWithoutCar = humanBeingRepository.findAll().stream()
                .filter(hb -> hb.getCar().getName() == null)
                .collect(Collectors.toList());

        for (HumanBeing humanBeing : heroesWithoutCar) {
            Car redLadaKalina = new Car();
            redLadaKalina.setName("Lada Kalina");
            redLadaKalina.setCool(true);
            humanBeing.setCar(redLadaKalina);
        }

        humanBeingRepository.saveAll(heroesWithoutCar);
    }

    @Transactional()
    public long count() {
        return humanBeingRepository.count();
    }

    @Transactional()
    public boolean existsById(Long id) {
        return humanBeingRepository.existsById(id);
    }
}