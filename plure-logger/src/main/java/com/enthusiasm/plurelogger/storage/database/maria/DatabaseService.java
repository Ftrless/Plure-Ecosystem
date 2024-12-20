package com.enthusiasm.plurelogger.storage.database.maria;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import com.enthusiasm.plurelogger.storage.IDatabaseService;
import com.mojang.authlib.GameProfile;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.*;
import lombok.Getter;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import com.enthusiasm.plurelogger.actions.IActionType;
import com.enthusiasm.plurelogger.actionutils.ActionSearchParams;
import com.enthusiasm.plurelogger.actionutils.Preview;
import com.enthusiasm.plurelogger.actionutils.SearchResults;
import com.enthusiasm.plurelogger.config.ConfigWrapper;
import com.enthusiasm.plurelogger.config.PLConfig;
import com.enthusiasm.plurelogger.registry.ActionRegistry;
import com.enthusiasm.plurelogger.storage.database.maria.entity.*;
import com.enthusiasm.plurelogger.storage.database.maria.repository.*;
import com.enthusiasm.plurelogger.utils.Logger;
import com.enthusiasm.plurelogger.utils.Negatable;
import com.enthusiasm.plurelogger.utils.PlayerResult;

public class DatabaseService {
    @Getter
    private static final DatabaseCacheService cache = DatabaseCacheService.getInstance();
    private static SessionFactory sessionFactory;

    @Getter
    private static ActionRepository actionRepository;
    @Getter
    private static ActionIdentifierRepository actionIdentifierRepository;
    @Getter
    private static ObjectIdentifierRepository objectIdentifierRepository;
    @Getter
    private static PlayerRepository playerRepository;
    @Getter
    private static SourceRepository sourceRepository;
    @Getter
    private static WorldRepository worldRepository;

    public static void setup() {
        PLConfig config = ConfigWrapper.getConfig();

        Configuration configuration = new Configuration()
                .setProperty("hibernate.connection.driver_class", "org.mariadb.jdbc.Driver")
                .setProperty("hibernate.connection.url", "jdbc:mariadb://127.0.0.1:3306/" + config.database + "?useSSL=false")
                .setProperty("hibernate.connection.username", config.username)
                .setProperty("hibernate.connection.password", config.password)
                .setProperty("hibernate.hikari.maximumPoolSize", config.poolSize.toString())
                .setProperty("hibernate.hikari.minimumIdle", String.valueOf(Math.round((double) config.poolSize / 3)))
                .setProperty("hibernate.hikari.idleTimeout", "10000")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.jdbc.batch_size", config.batchSize.toString())
                .setProperty("hibernate.default_batch_fetch_size", config.batchSize.toString())
                .setProperty("hibernate.order_inserts", "true")
                .setProperty("hibernate.order_updates", "true")
                .setProperty("hibernate.cache.use_second_level_cache", "true")
                .setProperty("hibernate.cache.use_query_cache", "true")
                .setProperty("hibernate.javax.cache.missing_cache_strategy", "create")
                .setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.internal.JCacheRegionFactory")
                .setProperty("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider")
                .setProperty("hibernate.show_sql", config.showSql.toString())
                .setProperty("hibernate.format_sql", config.formatSql.toString())
                .setProperty("hibernate.use_sql_comments", config.showSqlValues.toString())
                .setProperty("hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS", "500")
                .setProperty("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        ensureAnnotateTables(metadataSources);

        Metadata metadata = metadataSources.buildMetadata();
        sessionFactory = metadata.buildSessionFactory();

        buildDao();
    }

    public static void setupCache() {
        getActionIdentifierRepository().getAll().forEach(actionEntity ->
                cache.actionIdentifierKeys.put(actionEntity.getActionIdentifier(), actionEntity.getId())
        );
        getObjectIdentifierRepository().getAll().forEach(objectIdentifierEntity ->
                cache.objectIdentifierKeys.put(objectIdentifierEntity.getIdentifier(), objectIdentifierEntity.getId())
        );
        getSourceRepository().getAll().forEach(sourceEntity ->
                cache.sourceKeys.put(sourceEntity.getName(), sourceEntity.getId())
        );
        getWorldRepository().getAll().forEach(worldEntity ->
                cache.worldIdentifierKeys.put(worldEntity.getIdentifier(), worldEntity.getId())
        );
        getPlayerRepository().getAll().forEach(playerEntity ->
                cache.playerKeys.put(UUID.fromString(playerEntity.getPlayerId()), playerEntity.getId())
        );
    }

    public static void autoPurge() {
        Instant thirtyDaysAgo = Instant.now().minus(ConfigWrapper.getConfig().autoPurge, ChronoUnit.DAYS);
        int deletedCount = getActionRepository().deleteOlderThan(thirtyDaysAgo);
        Logger.logInfo("Successfully purged {} actions", deletedCount);
    }

    public static void purgeActions(ActionSearchParams params) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                CriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery<Long> query = cb.createQuery(Long.class);
                Root<ActionEntity> root = query.from(ActionEntity.class);

                query.select(root.get("id"))
                        .where(buildQueryParams(session, root, params));

                List<Long> idsToDelete = session.createQuery(query).getResultList();

                if (!idsToDelete.isEmpty()) {
                    CriteriaDelete<ActionEntity> delete = cb.createCriteriaDelete(ActionEntity.class);
                    Root<ActionEntity> deleteRoot = delete.from(ActionEntity.class);

                    delete.where(deleteRoot.get("id").in(idsToDelete));

                    session.createMutationQuery(delete).executeUpdate();
                }

                transaction.commit();
            } catch (HibernateException e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                Logger.logError("Failed to purge actions: {}, Cause: {}", e.getMessage(), e.getCause());
            }
        }
    }

    private static void ensureAnnotateTables(MetadataSources configuration) {
        configuration.addAnnotatedClass(ActionEntity.class);
        configuration.addAnnotatedClass(ActionIdentifierEntity.class);
        configuration.addAnnotatedClass(ObjectIdentifierEntity.class);
        configuration.addAnnotatedClass(PlayerEntity.class);
        configuration.addAnnotatedClass(SourceEntity.class);
        configuration.addAnnotatedClass(WorldEntity.class);
    }

    private static void buildDao() {
        actionRepository = new ActionRepository(getSessionFactory());
        actionIdentifierRepository = new ActionIdentifierRepository(getSessionFactory());
        objectIdentifierRepository = new ObjectIdentifierRepository(getSessionFactory());
        playerRepository = new PlayerRepository(getSessionFactory());
        sourceRepository = new SourceRepository(getSessionFactory());
        worldRepository = new WorldRepository(getSessionFactory());
    }

    public static void logActionBatch(List<IActionType> batch) {
        batch.forEach(iActionType -> {
            ActionEntity actionEntity = new ActionEntity();

            actionEntity.setActionIdentifier(getActionIdentifier(iActionType.getIdentifier()));
            actionEntity.setTimestamp(iActionType.getTimestamp());
            actionEntity.setX(iActionType.getPos().getX());
            actionEntity.setY(iActionType.getPos().getY());
            actionEntity.setZ(iActionType.getPos().getZ());
            actionEntity.setWorld(getWorld(iActionType.getWorld().toString()));
            actionEntity.setOldObjectId(getObjectIdentifier(iActionType.getOldObjectIdentifier().toString()));
            actionEntity.setObjectId(getObjectIdentifier(iActionType.getObjectIdentifier().toString()));
            actionEntity.setOldBlockState(iActionType.getOldObjectState());
            actionEntity.setBlockState(iActionType.getObjectState());
            actionEntity.setSourceName(getOrCreateSource(iActionType.getSourceName()));
            actionEntity.setSourcePlayer(iActionType.getSourceProfile() != null ? getPlayer(iActionType.getSourceProfile().getId().toString()) : null);
            actionEntity.setExtraData(iActionType.getExtraData());
            actionEntity.setRolledBack(iActionType.isRolledBack());

            actionRepository.save(actionEntity);
        });
    }

    public static ActionIdentifierEntity getActionIdentifier(String actionIdentifier) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from ActionIdentifierEntity where actionIdentifier = :actionIdentifier";
            Query<ActionIdentifierEntity> query = session.createQuery(hql, ActionIdentifierEntity.class).setCacheable(true);
            query.setParameter("actionIdentifier", actionIdentifier);
            return query.getSingleResult();
        }
    }

    public static WorldEntity getWorld(String identifier) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from WorldEntity where identifier = :identifier";
            Query<WorldEntity> query = session.createQuery(hql, WorldEntity.class).setCacheable(true);
            query.setParameter("identifier", identifier);
            return query.getSingleResult();
        }
    }

    public static ObjectIdentifierEntity getObjectIdentifier(String identifier) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from ObjectIdentifierEntity where identifier = :identifier";
            Query<ObjectIdentifierEntity> query = session.createQuery(hql, ObjectIdentifierEntity.class).setCacheable(true);
            query.setParameter("identifier", identifier);
            return query.getSingleResult();
        }
    }

    public static SourceEntity getOrCreateSource(String name) {
        SourceEntity entity;
        try (Session session = sessionFactory.openSession()) {
            String hql = "from SourceEntity where name = :name";
            Query<SourceEntity> query = session.createQuery(hql, SourceEntity.class).setCacheable(true);
            query.setParameter("name", name);

            try {
                entity = query.getSingleResult();
            } catch (NoResultException e) {
                Transaction transaction = session.beginTransaction();

                entity = registerSource(name);

                session.flush();
                session.refresh(entity);

                transaction.commit();
            }

            return entity;
        }
    }

    public static PlayerEntity getPlayer(String playerId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "from PlayerEntity where playerId = :playerId";
            Query<PlayerEntity> query = session.createQuery(hql, PlayerEntity.class).setCacheable(true);
            query.setParameter("playerId", playerId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public static void registerWorld(String identifier) {
        if (worldRepository.exists(identifier)) {
            return;
        }

        WorldEntity world = new WorldEntity();
        world.setIdentifier(identifier);
        worldRepository.save(world);
    }

    public static void registerActionType(String identifier) {
        if (actionIdentifierRepository.exists(identifier)) {
            return;
        }

        ActionIdentifierEntity actionIdentifier = new ActionIdentifierEntity();
        actionIdentifier.setActionIdentifier(identifier);
        actionIdentifierRepository.save(actionIdentifier);
    }

    public static SourceEntity registerSource(String sourceName) {
        if (sourceRepository.exists(sourceName)) {
            return null;
        }

        SourceEntity source = new SourceEntity();
        source.setName(sourceName);
        sourceRepository.save(source);

        return source;
    }

    public static void logPlayer(UUID uuid, String name) {
        PlayerEntity player = getPlayer(uuid.toString());

        if (player != null) {
            player.setLastJoin(Instant.now());
        } else {
            player = new PlayerEntity();
            player.setPlayerId(uuid.toString());
            player.setPlayerName(name);
            player.setFirstJoin(Instant.now());
            player.setLastJoin(Instant.now());
        }

        playerRepository.save(player);
    }

    public static void insertIdentifiers(Set<Identifier> identifiers) {
        Set<String> existingIdentifiers = objectIdentifierRepository.getExistingIdentifiers(identifiers);

        List<ObjectIdentifierEntity> entitiesToSave = new ArrayList<>();
        for (Identifier identifier : identifiers) {
            if (existingIdentifiers.contains(identifier.toString())) {
                continue;
            }

            ObjectIdentifierEntity objectIdentifier = new ObjectIdentifierEntity();
            objectIdentifier.setIdentifier(identifier.toString());
            entitiesToSave.add(objectIdentifier);
        }

        objectIdentifierRepository.save(entitiesToSave);
    }

    public static List<PlayerResult> selectPlayers(Set<GameProfile> players) {
        try (Session session = getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<PlayerEntity> cq = cb.createQuery(PlayerEntity.class);
            Root<PlayerEntity> root = cq.from(PlayerEntity.class);

            Predicate predicate = cb.disjunction();

            cq.select(root);

            for (GameProfile player : players) {
                Predicate playerPredicate = cb.equal(root.get("playerId"), player.getId().toString());
                predicate = cb.or(predicate, playerPredicate);
            }

            cq.where(predicate);

            Query<PlayerEntity> query = session.createQuery(cq);
            List<PlayerEntity> results = query.getResultList();

            return results.stream()
                    .map(PlayerResult::fromEntity)
                    .collect(Collectors.toList());
        } catch (HibernateException e) {
            Logger.logError("Error selecting players: {}, Cause: {}", e.getMessage(), e.getCause());
            return Collections.emptyList();
        }
    }

    public static void rollbackActions(Set<Integer> actionIds) {
        performActionUpdate(actionIds, true);
    }

    public static void restoreActions(Set<Integer> actionIds) {
        performActionUpdate(actionIds, false);
    }

    private static void performActionUpdate(Set<Integer> actionIds, boolean rolledBack) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String hql = "UPDATE ActionEntity SET rolledBack = :rolledBack WHERE id IN :actionIds AND rolledBack <> :rolledBack";
            MutationQuery query = session.createMutationQuery(hql);
            query.setParameter("rolledBack", rolledBack);
            query.setParameterList("actionIds", actionIds);
            query.executeUpdate();

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            Logger.logError("Error updating actions", e);
        }
    }

    public static SearchResults searchActions(ActionSearchParams params, int page) {
        List<IActionType> actions = new ArrayList<>();

        try (Session session = getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<ActionEntity> criteriaQuery = criteriaBuilder.createQuery(ActionEntity.class);
            Root<ActionEntity> root = criteriaQuery.from(ActionEntity.class);

            long totalActions = countActions(params);

            if (totalActions == 0) {
                return new SearchResults(actions, params, page, 0);
            }

            Predicate predicate = buildQueryParams(session, root, params);

            criteriaQuery.select(root)
                    .where(predicate)
                    .orderBy(criteriaBuilder.desc(root.get("id")));

            Query<ActionEntity> query = session.createQuery(criteriaQuery);

            int pageSize = ConfigWrapper.getConfig().pageSize;
            int offset = pageSize * (page - 1);
            query.setFirstResult(offset);
            query.setMaxResults(pageSize);

            List<ActionEntity> resultList = query.getResultList();
            for (ActionEntity actionEntity : resultList) {
                actions.add(getActionFromQuery(actionEntity));
            }

            int totalPages = (int) Math.ceil((double) totalActions / pageSize);

            return new SearchResults(actions, params, page, totalPages);
        } catch (HibernateException e) {
            Logger.logError("Error fetching actions search results: {}, Cause: {}", e.getMessage(), e.getCause());
            return new SearchResults(new ArrayList<>(), params, page, 0);
        }
    }

    public static Long countActions(ActionSearchParams params) {
        try (Session session = getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
            Root<ActionEntity> root = criteriaQuery.from(ActionEntity.class);

            Predicate predicate = buildQueryParams(session, root, params);

            criteriaQuery
                    .select(criteriaBuilder.count(root))
                    .where(predicate);

            Query<Long> query = session.createQuery(criteriaQuery);
            Long count = query.getSingleResult();

            return count != null ? count : 0;
        } catch (HibernateException e) {
            Logger.logError("Error counting actions: {}, Cause: {}", e.getMessage(), e.getCause());
            return 0L;
        }
    }

    private static IActionType getActionFromQuery(ActionEntity actionEntity) {
        IActionType type = ActionRegistry.getType(actionEntity.getActionIdentifier().getActionIdentifier());

        if (type == null) {
            Logger.logWarn("Unknown action type " + actionEntity.getActionIdentifier());
            return null;
        }

        type.setId(actionEntity.getId());
        type.setTimestamp(actionEntity.getTimestamp());
        type.setPos(new BlockPos(actionEntity.getX(), actionEntity.getY(), actionEntity.getZ()));
        type.setWorld(actionEntity.getWorld().getIdentifier());
        type.setObjectIdentifier(actionEntity.getObjectId().getIdentifier());
        type.setOldObjectIdentifier(actionEntity.getOldObjectId().getIdentifier());
        type.setObjectState(actionEntity.getBlockState());
        type.setOldObjectState(actionEntity.getOldBlockState());
        type.setSourceName(actionEntity.getSourceName().getName());

        if (actionEntity.getSourcePlayer() != null) {
            type.setSourceProfile(new GameProfile(UUID.fromString(actionEntity.getSourcePlayer().getPlayerId()), actionEntity.getSourcePlayer().getPlayerName()));
        }

        type.setExtraData(actionEntity.getExtraData());
        type.setRolledBack(actionEntity.getRolledBack());

        return type;
    }

    public static List<IActionType> previewActions(ActionSearchParams params, Preview.Type type) {
        return switch (type) {
            case ROLLBACK -> selectRollback(params);
            case RESTORE -> selectRestore(params);
        };
    }

    public static List<IActionType> selectRollback(ActionSearchParams params) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ActionEntity> query = cb.createQuery(ActionEntity.class);
            Root<ActionEntity> root = query.from(ActionEntity.class);

            Predicate predicate = buildQueryParams(session, root, params);
            Predicate rolledBackPredicate = cb.equal(root.get("rolledBack"), false);

            query.select(root)
                    .distinct(true)
                    .where(cb.and(predicate, rolledBackPredicate))
                    .orderBy(cb.desc(root.get("id")));

            Query<ActionEntity> hQuery = session.createQuery(query);
            return hQuery.getResultList().stream().map(DatabaseService::getActionFromQuery).collect(Collectors.toList());
        }
    }

    public static List<IActionType> selectRestore(ActionSearchParams params) {
        try (Session session = sessionFactory.openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ActionEntity> query = cb.createQuery(ActionEntity.class);
            Root<ActionEntity> root = query.from(ActionEntity.class);

            Predicate predicate = buildQueryParams(session, root, params);
            Predicate rolledBackPredicate = cb.equal(root.get("rolledBack"), true);

            query.select(root)
                    .distinct(true)
                    .where(cb.and(predicate, rolledBackPredicate))
                    .orderBy(cb.desc(root.get("id")));

            Query<ActionEntity> hQuery = session.createQuery(query);
            return hQuery.getResultList().stream().map(DatabaseService::getActionFromQuery).collect(Collectors.toList());
        }
    }

    private static Integer getPlayerId(UUID playerId) {
        Integer cachedId = cache.playerKeys.getIfPresent(playerId);
        if (cachedId != null) {
            return cachedId;
        }

        try (Session session = getSessionFactory().openSession()) {
            String hql = "SELECT p.id FROM PlayerEntity p WHERE p.playerId = :playerId";
            Query<Integer> query = session.createQuery(hql, Integer.class).setCacheable(true);
            query.setParameter("playerId", playerId);
            Integer playerIdResult = query.uniqueResult();

            if (playerIdResult != null) {
                cache.playerKeys.put(playerId, playerIdResult);
            }
            return playerIdResult;
        }
    }

    private static Integer getWorldId(Identifier identifier) {
        Integer cachedId = cache.worldIdentifierKeys.getIfPresent(identifier);
        if (cachedId != null) {
            return cachedId;
        }

        try (Session session = getSessionFactory().openSession()) {
            String hql = "SELECT w.id FROM WorldEntity w WHERE w.identifier = :identifier";
            Query<Integer> query = session.createQuery(hql, Integer.class).setCacheable(true);
            query.setParameter("identifier", identifier.toString());
            Integer worldIdResult = query.uniqueResult();

            if (worldIdResult != null) {
                cache.worldIdentifierKeys.put(identifier, worldIdResult);
            }
            return worldIdResult;
        }
    }

    private static Integer getActionId(String actionTypeId) {
        Integer cachedId = cache.actionIdentifierKeys.getIfPresent(actionTypeId);
        if (cachedId != null) {
            return cachedId;
        }

        try (Session session = getSessionFactory().openSession()) {
            String hql = "SELECT a.id FROM ActionIdentifierEntity a WHERE a.actionIdentifier = :actionTypeId";
            Query<Integer> query = session.createQuery(hql, Integer.class).setCacheable(true);
            query.setParameter("actionTypeId", actionTypeId);
            Integer actionIdResult = query.uniqueResult();

            if (actionIdResult != null) {
                cache.actionIdentifierKeys.put(actionTypeId, actionIdResult);
            }
            return actionIdResult;
        }
    }

    private static Integer getRegistryKeyId(Identifier identifier) {
        Integer cachedId = cache.objectIdentifierKeys.getIfPresent(identifier);
        if (cachedId != null) {
            return cachedId;
        }

        try (Session session = getSessionFactory().openSession()) {
            String hql = "SELECT o.id FROM ObjectIdentifierEntity o WHERE o.identifier = :identifier";
            Query<Integer> query = session.createQuery(hql, Integer.class).setCacheable(true);
            query.setParameter("identifier", identifier.toString());
            Integer objectIdResult = query.uniqueResult();

            if (objectIdResult != null) {
                cache.objectIdentifierKeys.put(identifier, objectIdResult);
            }
            return objectIdResult;
        }
    }

    private static Predicate buildQueryParams(Session session, Root<ActionEntity> root, ActionSearchParams params) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        Predicate predicate = null;

        Join<ActionEntity, ActionIdentifierEntity> actionIdentifierJoin = root.join("actionIdentifier");
        Join<ActionEntity, WorldEntity> worldJoin = root.join("world");
        Join<ActionEntity, ObjectIdentifierEntity> objectIdJoin = root.join("objectId");
        Join<ActionEntity, ObjectIdentifierEntity> oldObjectIdJoin = root.join("oldObjectId");
        Join<ActionEntity, SourceEntity> sourceNameJoin = root.join("sourceName");
        Join<ActionEntity, PlayerEntity> sourcePlayerJoin = root.join("sourcePlayer");

        if (params.getBounds() != null) {
            Predicate boundsPredicate = criteriaBuilder.and(
                    criteriaBuilder.between(root.get("x"), params.getBounds().getMinX(), params.getBounds().getMaxX()),
                    criteriaBuilder.between(root.get("y"), params.getBounds().getMinY(), params.getBounds().getMaxY()),
                    criteriaBuilder.between(root.get("z"), params.getBounds().getMinZ(), params.getBounds().getMaxZ())
            );
            predicate = appendPredicate(criteriaBuilder, null, boundsPredicate);
        }

        if (params.getBefore() != null) {
            Predicate beforePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), params.getBefore());
            predicate = appendPredicate(criteriaBuilder, predicate, beforePredicate);
        }
        if (params.getAfter() != null) {
            Predicate afterPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), params.getAfter());
            predicate = appendPredicate(criteriaBuilder, predicate, afterPredicate);
        }

        if (params.getRolledBack() != null) {
            Predicate rolledBackPredicate = criteriaBuilder.equal(root.get("rolledBack"), params.getRolledBack());
            predicate = appendPredicate(criteriaBuilder, predicate, rolledBackPredicate);
        }

        predicate = addParameters(
                criteriaBuilder,
                predicate,
                params.getSourceNames(),
                sourceNameJoin.get("name")
        );

        predicate = addParameters(
                criteriaBuilder,
                predicate,
                params.getActions().stream()
                        .map(param -> new Negatable<>(getActionId(param.property()), param.allowed()))
                        .collect(Collectors.toSet()),
                actionIdentifierJoin.get("id")
        );

        predicate = addParameters(
                criteriaBuilder,
                predicate,
                params.getWorlds().stream()
                        .map(param -> new Negatable<>(getWorldId(param.property()), param.allowed()))
                        .collect(Collectors.toSet()),
                worldJoin.get("id")
        );

        predicate = addParameters(
                criteriaBuilder,
                predicate,
                params.getObjects().stream()
                        .map(param -> new Negatable<>(getRegistryKeyId(param.property()), param.allowed()))
                        .collect(Collectors.toSet()),
                objectIdJoin.get("id"),
                oldObjectIdJoin.get("id")
        );

        predicate = addParameters(
                criteriaBuilder,
                predicate,
                params.getSourcePlayerIds().stream()
                        .map(param -> new Negatable<>(getPlayerId(param.property()), param.allowed()))
                        .collect(Collectors.toSet()),
                sourcePlayerJoin.get("id")
        );

        return predicate != null ? predicate : criteriaBuilder.conjunction();
    }

    private static <E extends Comparable<E>, C> Predicate addParameters(
            CriteriaBuilder cb,
            Predicate predicate,
            Set<Negatable<E>> paramSet,
            Path<C> column) {
        return addParameters(cb, predicate, paramSet, column, null);
    }

    private static <E extends Comparable<E>, C> Predicate addParameters(
            CriteriaBuilder cb,
            Predicate predicate,
            Set<Negatable<E>> paramSet,
            Path<C> column,
            @Nullable Path<C> orColumn) {

        if (paramSet == null || paramSet.isEmpty()) return predicate;

        Predicate allowedPredicate = null;
        Predicate deniedPredicate = null;

        for (Negatable<E> negatable : paramSet) {
            E property = negatable.property();
            if (property != null) {
                Predicate columnPredicate = cb.equal(column, property);
                Predicate orColumnPredicate = (orColumn != null) ? cb.equal(orColumn, property) : null;

                if (negatable.allowed()) {
                    Predicate currentAllowedPredicate = (orColumnPredicate != null)
                            ? cb.or(columnPredicate, orColumnPredicate)
                            : columnPredicate;

                    allowedPredicate = (allowedPredicate == null)
                            ? currentAllowedPredicate
                            : cb.or(allowedPredicate, currentAllowedPredicate);
                } else {
                    Predicate currentDeniedPredicate = (orColumnPredicate != null)
                            ? cb.and(cb.not(columnPredicate), cb.not(orColumnPredicate))
                            : cb.not(columnPredicate);

                    deniedPredicate = (deniedPredicate == null)
                            ? currentDeniedPredicate
                            : cb.and(deniedPredicate, currentDeniedPredicate);
                }
            }
        }

        if (allowedPredicate != null) {
            predicate = (predicate == null)
                    ? allowedPredicate
                    : cb.and(predicate, allowedPredicate);
        }

        if (deniedPredicate != null) {
            predicate = (predicate == null)
                    ? deniedPredicate
                    : cb.and(predicate, deniedPredicate);
        }

        return predicate;
    }

    private static Predicate appendPredicate(CriteriaBuilder criteriaBuilder, Predicate existingPredicate, Predicate newPredicate) {
        if (existingPredicate == null) {
            return newPredicate;
        } else {
            return criteriaBuilder.and(existingPredicate, newPredicate);
        }
    }

    public static void stop() {
        try {
            Session session = getSessionFactory().getCurrentSession();

            Transaction transaction = session.getTransaction();

            if (transaction.isActive()) {
                transaction.commit();
            }

            if (session.isOpen()) {
                session.close();
            }
        } catch (HibernateException e) {
            getSessionFactory().close();
        }
    }

    private static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
