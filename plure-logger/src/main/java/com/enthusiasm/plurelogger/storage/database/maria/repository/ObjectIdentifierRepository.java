package com.enthusiasm.plurelogger.storage.database.maria.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import net.minecraft.util.Identifier;

import com.enthusiasm.plurelogger.storage.database.maria.entity.ObjectIdentifierEntity;

public class ObjectIdentifierRepository extends AbstractRepository<ObjectIdentifierEntity> {
    public ObjectIdentifierRepository(SessionFactory sessionFactory) {
        super(sessionFactory, ObjectIdentifierEntity.class);
    }

    public Set<String> getExistingIdentifiers(Set<Identifier> identifiers) {
        final int BATCH_SIZE = 1000;
        Set<String> existingIdentifiers = new HashSet<>();

        try (Session session = this.sessionFactory.openSession()) {
            List<Identifier> identifierList = new ArrayList<>(identifiers);
            for (int i = 0; i < identifierList.size(); i += BATCH_SIZE) {
                List<Identifier> batch = identifierList.subList(i, Math.min(i + BATCH_SIZE, identifierList.size()));

                Query<String> query = session.createQuery(
                        "select identifier from ObjectIdentifierEntity where identifier in (:identifiers)", String.class);
                query.setParameterList("identifiers", batch.stream().map(Identifier::toString).collect(Collectors.toSet()));
                existingIdentifiers.addAll(query.getResultList());
            }
        }
        return existingIdentifiers;
    }
}
