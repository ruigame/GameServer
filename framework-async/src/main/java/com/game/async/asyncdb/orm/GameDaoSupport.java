package com.game.async.asyncdb.orm;//package com.game.framework.asyncdb.orm;

import com.game.util.ExceptionUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author liguorui
 * @date 2018/1/15 00:15
 */
@Transactional
public abstract class GameDaoSupport<E> extends HibernateDaoSupport implements BaseDao<E> {

    private Class<E> entityClazz;

    private boolean deserialize;

    public GameDaoSupport() {
        super();
        Type type = getClass().getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(type.getClass())) {
            throw new RuntimeException("类" + getClass() + "继承必须使用泛型");
        }
        this.entityClazz = (Class)((ParameterizedType)type).getActualTypeArguments()[0];
        this.deserialize = BaseDBEntity.class.isAssignableFrom(entityClazz);
    }

    @Autowired
    public void setSuperSessionFactory(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);
    }

    public Session getSession() {
        return getSessionFactory().getCurrentSession();
    }

    public E get(Serializable id) {
        E e = getHibernateTemplate().get(entityClazz, id);
        if (e != null && deserialize) {
            BaseDBEntity entity = (BaseDBEntity)e;
            entity.deserialize();
        }
        return e;
    }

    @Override
    public List<E> getAll() {
        List<E> eList = getHibernateTemplate().loadAll(entityClazz);
        if (deserialize) {
            for (E e : eList) {
                BaseDBEntity entity = (BaseDBEntity)e;
                entity.deserialize();
            }
        }
        return eList;
    }

    @Override
    public boolean insert(E object) {
        try {
            getHibernateTemplate().save(object);
        } catch (Exception e) {
            ExceptionUtils.log("{}", object, e);
            return false;
        }
        return true;
    }

    @Override
    public boolean update(E object) {
        try {
            getHibernateTemplate().update(object);
        } catch (Exception e) {
            ExceptionUtils.log("{}", object, e);
            return false;
        }
        return true;
    }

    public boolean delete(E object) {
        try {
            getHibernateTemplate().delete(object);
        } catch (Exception e) {
            ExceptionUtils.log("{}", object, e);
            return false;
        }
        return true;
    }
}
