package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "operation_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ImportStatus status;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "import_date", nullable = false)
    private LocalDateTime importDate;

    @Column(name = "objects_added")
    private Integer objectsAdded;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "file_name")
    private String fileName;

    @PrePersist
    protected void onCreate() {
        importDate = LocalDateTime.now();
    }
}