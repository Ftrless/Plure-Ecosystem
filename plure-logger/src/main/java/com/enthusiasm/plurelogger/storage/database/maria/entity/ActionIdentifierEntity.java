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
@Table(name = "action_identifiers")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ActionIdentifierEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(length = 16, unique = true, nullable = false)
    private String actionIdentifier;
}
