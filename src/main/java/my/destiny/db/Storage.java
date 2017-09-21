package my.destiny.db;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class Storage {

    private ThreadLocal<EntityManager> localEntityManager;
    private EntityManagerFactory factory;
    private List<EntityManager> entityManagers;
    boolean stopCalled;

    public Storage() {
        stopCalled = false;
        entityManagers = new ArrayList<>();
        localEntityManager = new ThreadLocal<>();
        factory = Persistence.createEntityManagerFactory("destinyreport");
    }

    private EntityManager getManager() {
        synchronized (localEntityManager) {
            if (stopCalled) {
                throw new IllegalStateException("This Storage class has been stopped and can't be reused!");
            }
            EntityManager manager = localEntityManager.get();
            if (manager == null) {
                manager = factory.createEntityManager();
                entityManagers.add(manager);
                localEntityManager.set(manager);
            }
            return manager;
        }
    }

    public void stop() {
        synchronized (localEntityManager) {
            if (stopCalled) {
                throw new IllegalStateException("This Storage class has already been stopped!");
            }
            stopCalled = true;
            for (EntityManager manager : entityManagers) {
                try {
                    manager.close();
                } catch (RuntimeException e) {
                    e.printStackTrace(); // TODO(paulrene): For debugging
                    // ignored
                }
            }
        }
    }

    public void removeManager() {
        synchronized (localEntityManager) {
            EntityManager manager = localEntityManager.get();
            entityManagers.remove(manager);
            localEntityManager.remove();
            manager.close();
        }
    }

    public void begin() {
        getManager().getTransaction().begin();
    }

    public void rollback() {
        getManager().getTransaction().rollback();
    }

    public void commit() {
        getManager().getTransaction().commit();
    }

    public void persist(Object obj) {
        getManager().persist(obj);
    }

    public void delete(Object obj) {
        getManager().remove(obj);
    }

    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        TypedQuery<T> query = getManager().createQuery(qlString, resultClass);
        return query;
    }

    public <T> T createSingleQuery(String qlString, Class<T> resultClass) {
        TypedQuery<T> query = getManager().createQuery(qlString, resultClass);
        return query.getSingleResult();
    }

    public Query createQuery(String qlString) {
        return getManager().createQuery(qlString);
    }

    public void refresh(Object obj) {
        getManager().refresh(obj);
    }

    public Object merge(Object obj) {
        return getManager().merge(obj);
    }

    public void clear() {
        getManager().clear();
    }

}
