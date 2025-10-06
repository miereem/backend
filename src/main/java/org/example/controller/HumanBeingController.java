package org.example.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.example.model.WeaponType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.example.dto.HumanBeingDto;
import org.example.model.HumanBeing;
import org.example.service.HumanBeingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/human-beings")
@CrossOrigin(origins = "*",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS, RequestMethod.PATCH})
public class HumanBeingController {

    private static final Log log = LogFactory.getLog(HumanBeingController.class);
    private final HumanBeingService humanBeingService;

@GetMapping
public Map<String, Object> listHumanBeings(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search) {

    Page<HumanBeing> hbPage = (search != null && !search.trim().isEmpty()) ?
            humanBeingService.searchByName(search, PageRequest.of(page, size)) :
            humanBeingService.findAll(PageRequest.of(page, size));

    Map<String, Object> response = new HashMap<>();
    response.put("content", hbPage.getContent());
    response.put("page", hbPage.getNumber());
    response.put("size", hbPage.getSize());
    response.put("total", hbPage.getTotalElements());

    return response;
}

    @GetMapping("/{id}")
    public String getHumanBeing(@PathVariable Long id, Model model) {
        Optional<HumanBeing> humanBeing = humanBeingService.findById(id);
        humanBeing.ifPresent(hb -> model.addAttribute("humanBeing", hb));
        return "human-being-details";
    }

    @GetMapping("/new")
    public String showCreationForm(Model model) {
        model.addAttribute("humanBeing", new HumanBeing());
        log.info("create called");
        return "human-being-form";
    }

    @PostMapping
    public HumanBeing createHumanBeing(@RequestBody HumanBeingDto humanBeingDto) {
        return humanBeingService.create(humanBeingDto);
    }


    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<HumanBeing> humanBeing = humanBeingService.findById(id);
        humanBeing.ifPresent(hb -> model.addAttribute("humanBeing", hb));
        return "human-being-form";
    }

    @PutMapping("/{id}")
    public String updateHumanBeing(@PathVariable Long id, @RequestBody HumanBeingDto humanBeingDto) {
        humanBeingService.updateById(id, humanBeingDto);
        return "redirect:/human-beings";
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
