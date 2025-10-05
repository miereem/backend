package org.example.repository;

import jakarta.transaction.Transactional;
import org.example.model.HumanBeing;
import org.example.model.WeaponType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;

import java.util.List;

@Repository
public interface HumanBeingRepository extends JpaRepository<HumanBeing, Long> {

    List<HumanBeing> findByNameContainingIgnoreCase(String name);
    Page<HumanBeing> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<HumanBeing> findByWeaponType(WeaponType weaponType);

    @Modifying
    @Query("DELETE FROM HumanBeing h WHERE h.weaponType = :weaponType")
    void deleteAllByWeaponType(WeaponType weaponType);

    @Query("SELECT h.soundtrackName, COUNT(h) FROM HumanBeing h GROUP BY h.soundtrackName")
    List<Object[]> countBySoundtrackName();
}
