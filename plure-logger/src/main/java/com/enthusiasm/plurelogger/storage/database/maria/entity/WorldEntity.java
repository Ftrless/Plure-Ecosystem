package com.enthusiasm.plurelogger.storage.database.maria.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import net.minecraft.util.Identifier;

@NoArgsConstructor
@Entity
@Table(name = "worlds")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class WorldEntity {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(length = 191, unique = true, nullable = false)
    private String identifier;

    public Identifier getIdentifier() {
        return Identifier.tryParse(identifier);
    }
}
