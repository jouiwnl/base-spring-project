package com.joao.sampleproject.core;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class BasicRepository {

    private static final String FK_MESSAGE_DEFAULT = "Não é possível excluir este item, o mesmo está em uso em outro registro";

    private static final String POSTGRES_23503_RESTRICAO_INTEGRIDADE = "23503";
    private static final String ORACLE_23000_RESTRICAO_INTEGRIDADE = "23000";


    private EntityManager em;

    public BasicRepository(EntityManager em) {
        this.em = em;
    }

    public <T> List<T> findAll(final Class<T> entityClass, final Predicate... where) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        return query.selectFrom(new PathBuilderFactory().create(entityClass)).where(where).fetch();
    }

    public <T> T findOne(Class<T> entityClass, Predicate... where) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        return query.selectFrom(new PathBuilderFactory().create(entityClass)).where(where).limit(1).fetchOne();
    }

    public <T extends DatabaseEntity<?>> T save(T entity) {
        Objects.requireNonNull(entity, "entity can't be null");
        boolean isNew = exists(entity.getClass());

        if (!isNew) {
            em.merge(entity);
        } else {
            em.persist(entity);
        }

        em.flush();
        return entity;
    }

    public <T extends DatabaseEntity<?>> void remove(T entity) {
        try {
            em.remove(entity);
            em.flush();
        } catch (PersistenceException e) {
            handlerRemoveException(e);
        }
    }

    public <T> boolean exists(Class<T> entityClass, final Predicate... where) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        return query.selectFrom(new PathBuilderFactory().create(entityClass)).where(where).limit(1L).fetchCount() > 0;
    }

    protected void handlerRemoveException(PersistenceException e) {
        if (!(e.getCause() instanceof ConstraintViolationException)) {
            throw e;
        }

        SQLException violationException = (SQLException) e.getCause().getCause();
        if (POSTGRES_23503_RESTRICAO_INTEGRIDADE.equals(violationException.getSQLState())) {
            throw new ValidationException(FK_MESSAGE_DEFAULT);
        }
        if (ORACLE_23000_RESTRICAO_INTEGRIDADE.equals(violationException.getSQLState())) {
            throw new ValidationException(FK_MESSAGE_DEFAULT);
        }
    }

}

