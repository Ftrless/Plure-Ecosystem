package com.enthusiasm.plurelogger.storage.database.maria.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.enthusiasm.plurelogger.storage.database.maria.entity.ActionIdentifierEntity;

public class ActionIdentifierRepository extends AbstractRepository<ActionIdentifierEntity> {
    public ActionIdentifierRepository(SessionFactory sessionFactory) {
        super(sessionFactory, ActionIdentifierEntity.class);
    }

    public boolean exists(String identifier) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("select count(*) from ActionIdentifierEntity where actionIdentifier = :identifier", Long.class);
            query.setParameter("identifier", identifier);
            return query.uniqueResult() > 0;
        }
    }
}
