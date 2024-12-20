package com.enthusiasm.plurelogger.storage.database.maria.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "sources")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(name = "name", length = 30, unique = true, nullable = false)
    private String name;
}
