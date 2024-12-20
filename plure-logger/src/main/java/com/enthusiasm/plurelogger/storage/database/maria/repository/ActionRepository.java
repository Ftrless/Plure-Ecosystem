package com.enthusiasm.plurelogger.storage.database.maria.repository;

import java.time.Instant;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.enthusiasm.plurelogger.storage.database.maria.entity.ActionEntity;
import com.enthusiasm.plurelogger.utils.Logger;

public class ActionRepository extends AbstractRepository<ActionEntity> {
    public ActionRepository(SessionFactory sessionFactory) {
        super(sessionFactory, ActionEntity.class);
    }

    public int deleteOlderThan(Instant timestamp) {
        Transaction transaction = null;
        int deletedCount = 0;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query<ActionEntity> query = session.createQuery("DELETE FROM ActionEntity WHERE timestamp < :timestamp", ActionEntity.class);
            query.setParameter("timestamp", timestamp);
            deletedCount = query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            Logger.logError("Error deleting old actions", e);
        }

        return deletedCount;
    }
}
