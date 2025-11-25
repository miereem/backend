package org.example.controller;


import lombok.RequiredArgsConstructor;
import org.example.dto.HumanBeingDto;
import org.example.dto.PageResponse;
import org.example.model.HumanBeing;
import org.example.model.WeaponType;
import org.example.service.HumanBeingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/human-beings")
@CrossOrigin(origins = "*",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
public class HumanBeingController {

    private final HumanBeingService humanBeingService;

    @GetMapping
    public PageResponse<HumanBeing> listHumanBeings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Page<HumanBeing> hbPage = (search != null && !search.trim().isEmpty()) ?
                humanBeingService.searchByName(search, PageRequest.of(page, size)) :
                humanBeingService.findAll(PageRequest.of(page, size));

        return PageResponse.from(hbPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HumanBeing> getHumanBeing(@PathVariable Long id) {
        return humanBeingService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<HumanBeing> createHumanBeing(@RequestBody HumanBeingDto humanBeingDto) {
        HumanBeing created = humanBeingService.create(humanBeingDto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HumanBeing> updateHumanBeing(@PathVariable Long id, @RequestBody HumanBeingDto humanBeingDto) {
        HumanBeing updated = humanBeingService.updateById(id, humanBeingDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @CrossOrigin(origins = "http://localhost:3000")
    public ResponseEntity<Void> deleteHumanBeing(@PathVariable Long id) {
        humanBeingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/weapon-type/{weaponType}/all")
    public ResponseEntity<Void> deleteAllByWeaponType(@PathVariable String weaponType) {
        WeaponType type = WeaponType.fromString(weaponType);
        if (type == null) {
            return ResponseEntity.badRequest().build();
        }
        humanBeingService.deleteAllByWeaponType(type);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/weapon-type/{weaponType}/one")
    public ResponseEntity<Void> deleteOneByWeaponType(@PathVariable String weaponType) {
        WeaponType type = WeaponType.fromString(weaponType);
        if (type == null) {
            return ResponseEntity.badRequest().build();
        }
        humanBeingService.deleteOneByWeaponType(type);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/group/soundtrack")
    public ResponseEntity<Map<String, Long>> groupBySoundtrackName() {
        Map<String, Long> grouped = humanBeingService.groupBySoundtrackName();
        return ResponseEntity.ok(grouped);
    }

    @PatchMapping("/mood/sadness")
    public ResponseEntity<Void> updateAllMoodToSadness() {
        humanBeingService.updateAllMoodToSadness();
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/car/assign")
    public ResponseEntity<Void> assignCarToHeroesWithoutCar() {
        humanBeingService.assignCarToHeroesWithoutCar();
        return ResponseEntity.noContent().build();
    }

}
