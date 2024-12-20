package com.enthusiasm.plurelogger.storage.database.maria.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.enthusiasm.plurelogger.storage.database.maria.entity.SourceEntity;

public class SourceRepository extends AbstractRepository<SourceEntity> {
    public SourceRepository(SessionFactory sessionFactory) {
        super(sessionFactory, SourceEntity.class);
    }

    public boolean exists(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(*) from SourceEntity where name = :name", Long.class);
            query.setParameter("name", name);
            return query.uniqueResult() > 0;
        }
    }
}
