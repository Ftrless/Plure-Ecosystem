package com.enthusiasm.plurelogger.storage.database.maria.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.enthusiasm.plurelogger.storage.database.maria.entity.WorldEntity;

public class WorldRepository extends AbstractRepository<WorldEntity> {
    public WorldRepository(SessionFactory sessionFactory) {
        super(sessionFactory, WorldEntity.class);
    }

    public boolean exists(String id) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(*) from WorldEntity where identifier = :id", Long.class);
            query.setParameter("id", id);
            return query.uniqueResult() > 0;
        }
    }
}
