package com.enthusiasm.plurelogger.storage.database.maria.repository;

import org.hibernate.SessionFactory;

import com.enthusiasm.plurelogger.storage.database.maria.entity.PlayerEntity;

public class PlayerRepository extends AbstractRepository<PlayerEntity> {
    public PlayerRepository(SessionFactory sessionFactory) {
        super(sessionFactory, PlayerEntity.class);
    }
}
