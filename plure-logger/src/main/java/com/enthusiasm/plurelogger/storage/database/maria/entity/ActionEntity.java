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
@Table(name = "actions", indexes = {
        @Index(columnList = "actionIdentifier_id"),
        @Index(columnList = "objectId_id"),
        @Index(columnList = "oldObjectId_id"),
        @Index(columnList = "sourceName_id"),
        @Index(columnList = "sourcePlayer_id"),
        @Index(columnList = "x, y, z")
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private ActionIdentifierEntity actionIdentifier;

    @Setter
    @Column(nullable = false)
    private Instant timestamp;

    @Setter
    @Column(nullable = false)
    private Integer x;

    @Setter
    @Column(nullable = false)
    private Integer y;

    @Setter
    @Column(nullable = false)
    private Integer z;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private WorldEntity world;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private ObjectIdentifierEntity objectId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private ObjectIdentifierEntity oldObjectId;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String blockState;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String oldBlockState;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private SourceEntity sourceName;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private PlayerEntity sourcePlayer;

    @Setter
    @Column(columnDefinition = "MEDIUMTEXT")
    private String extraData;

    @Setter
    @Column()
    private Boolean rolledBack;
}