package com.enthusiasm.plurelogger.storage.database.maria.repository;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import com.enthusiasm.plurelogger.utils.Logger;

public abstract class AbstractRepository<T> {
    private final Class<T> entityType;
    protected final SessionFactory sessionFactory;

    protected AbstractRepository(SessionFactory sessionFactory, Class<T> entityType) {
        this.sessionFactory = sessionFactory;
        this.entityType = entityType;
    }

    public void save(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (ConstraintViolationException e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            Logger.logError("Error saving entity: " + entity, e);
        }
    }

    public void save(List<T> entities) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            for (T entity : entities) {
                session.merge(entity);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            Logger.logError("Error saving entities batch", e);
        }
    }

    public List<T> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<T> query = session.createQuery("from " + entityType.getSimpleName(), entityType);
            return query.list();
        }
    }

    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            Logger.logError("Error deleting entity: " + entity, e);
        }
    }
}
