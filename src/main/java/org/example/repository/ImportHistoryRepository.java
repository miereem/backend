package org.example.repository;

import org.example.model.ImportHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportHistoryRepository extends JpaRepository<ImportHistory, Integer> {

    Page<ImportHistory> findByUsername(String username, Pageable pageable);

    Page<ImportHistory> findAll(Pageable pageable);
}