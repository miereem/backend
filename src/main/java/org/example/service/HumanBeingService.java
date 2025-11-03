package org.example.service;

import org.example.model.*;
import org.example.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HumanBeingService {

    private final HumanBeingRepository humanBeingRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final CarRepository carRepository;
    private final WebSocketNotificationService notificationService;

    public Page<HumanBeing> getAllHumans(Pageable pageable) {
        return humanBeingRepository.findAll(pageable);
    }

    public HumanBeing getHumanById(Integer id) {
        return humanBeingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("HumanBeing not found with id: " + id));
    }

    @Transactional
    public HumanBeing createHuman(HumanBeing humanBeing) {
        if (humanBeing.getCoordinates() != null) {
            Coordinates savedCoords = coordinatesRepository.save(humanBeing.getCoordinates());
            humanBeing.setCoordinates(savedCoords);
        }

        if (humanBeing.getCar() != null) {
            Car savedCar = carRepository.save(humanBeing.getCar());
            humanBeing.setCar(savedCar);
        }

        humanBeing.setCreationDate(LocalDate.now());
        HumanBeing saved = humanBeingRepository.save(humanBeing);

        // Отправляем уведомление через WebSocket
        notificationService.notifyHumanBeingCreated(saved);

        return saved;
    }

    @Transactional
    public HumanBeing updateHuman(Integer id, HumanBeing humanBeing) {
        HumanBeing existing = getHumanById(id);

        existing.setName(humanBeing.getName());
        existing.setRealHero(humanBeing.isRealHero());
        existing.setHasToothpick(humanBeing.getHasToothpick());
        existing.setMood(humanBeing.getMood());
        existing.setImpactSpeed(humanBeing.getImpactSpeed());
        existing.setSoundtrackName(humanBeing.getSoundtrackName());
        existing.setWeaponType(humanBeing.getWeaponType());

        if (humanBeing.getCoordinates() != null) {
            if (existing.getCoordinates() != null) {
                existing.getCoordinates().setX(humanBeing.getCoordinates().getX());
                existing.getCoordinates().setY(humanBeing.getCoordinates().getY());
                coordinatesRepository.save(existing.getCoordinates());
            } else {
                Coordinates savedCoords = coordinatesRepository.save(humanBeing.getCoordinates());
                existing.setCoordinates(savedCoords);
            }
        }

        if (humanBeing.getCar() != null) {
            if (existing.getCar() != null) {
                existing.getCar().setName(humanBeing.getCar().getName());
                existing.getCar().setCool(humanBeing.getCar().getCool());
                carRepository.save(existing.getCar());
            } else {
                Car savedCar = carRepository.save(humanBeing.getCar());
                existing.setCar(savedCar);
            }
        }

        HumanBeing updated = humanBeingRepository.save(existing);

        // Отправляем уведомление через WebSocket
        notificationService.notifyHumanBeingUpdated(updated);

        return updated;
    }

    @Transactional
    public void deleteHuman(Integer id) {
        HumanBeing humanBeing = getHumanById(id);
        humanBeingRepository.delete(humanBeing);

        // Отправляем уведомление через WebSocket
        notificationService.notifyHumanBeingDeleted(id);
    }

    public Page<HumanBeing> searchByName(String name, Pageable pageable) {
        return humanBeingRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<HumanBeing> filterBySoundtrackName(String soundtrackName, Pageable pageable) {
        return humanBeingRepository.findBySoundtrackNameContainingIgnoreCase(soundtrackName, pageable);
    }

    // Специальные операции из ТЗ

    @Transactional
    public int deleteAllByWeaponType(WeaponType weaponType) {
        List<HumanBeing> toDelete = humanBeingRepository.findByWeaponType(weaponType);
        int count = toDelete.size();
        humanBeingRepository.deleteAll(toDelete);

        // Отправляем уведомления об удалении
        toDelete.forEach(hb -> notificationService.notifyHumanBeingDeleted(hb.getId()));

        return count;
    }

    @Transactional
    public boolean deleteOneByWeaponType(WeaponType weaponType) {
        return humanBeingRepository.findFirstByWeaponType(weaponType)
                .map(human -> {
                    humanBeingRepository.delete(human);
                    notificationService.notifyHumanBeingDeleted(human.getId());
                    return true;
                })
                .orElse(false);
    }

    public Map<String, Long> groupBySoundtrackName() {
        List<HumanBeing> allHumans = humanBeingRepository.findAll();
        return allHumans.stream()
                .collect(Collectors.groupingBy(
                        HumanBeing::getSoundtrackName,
                        Collectors.counting()
                ));
    }

    @Transactional
    public int makeAllHeroesSad() {
        List<HumanBeing> allHumans = humanBeingRepository.findAll();
        int count = 0;

        for (HumanBeing human : allHumans) {
            if (human.getMood() != Mood.SADNESS) {
                human.setMood(Mood.SADNESS);
                humanBeingRepository.save(human);
                notificationService.notifyHumanBeingUpdated(human);
                count++;
            }
        }

        return count;
    }

    @Transactional
    public int giveRedLadaKalinaToHeroesWithoutCar() {
        List<HumanBeing> heroesWithoutCar = humanBeingRepository.findByCarIsNull();
        int count = 0;

        for (HumanBeing human : heroesWithoutCar) {
            Car ladaKalina = new Car();
            ladaKalina.setName("Lada Kalina");
            ladaKalina.setCool(false);
            Car savedCar = carRepository.save(ladaKalina);

            human.setCar(savedCar);
            humanBeingRepository.save(human);
            notificationService.notifyHumanBeingUpdated(human);
            count++;
        }

        return count;
    }
}