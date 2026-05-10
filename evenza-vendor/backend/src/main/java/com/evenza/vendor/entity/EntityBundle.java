package com.evenza.vendor.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// ============================================================
// Category Entity
// ============================================================
@Entity @Table(name = "categories")
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
class CategoryEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 100) private String name;
    @Column(nullable = false, unique = true, length = 100) private String slug;
    @Column(length = 255) private String icon;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "is_active") private Boolean isActive = true;
    @Column(name = "sort_order") private Integer sortOrder = 0;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
}
