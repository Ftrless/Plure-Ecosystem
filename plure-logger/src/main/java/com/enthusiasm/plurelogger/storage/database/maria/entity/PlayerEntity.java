package com.enthusiasm.plurelogger.storage.database.maria.entity;

import java.time.Instant;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "players")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @Column(unique = true, nullable = false)
    private String playerId;

    @Setter
    @Column(length = 16, nullable = false)
    private String playerName;

    @Setter
    @Column(nullable = false)
    private Instant firstJoin;

    @Setter
    @Column(nullable = false)
    private Instant lastJoin;
}
