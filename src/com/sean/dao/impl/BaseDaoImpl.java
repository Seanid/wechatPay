package com.sean.dao.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.sean.beans.Page;
import com.sean.beans.Page.Order;
import com.sean.dao.BaseDao;
import com.sean.entity.BaseEntity;
/**
 * ͨ��dao�ӿ�
 * @author Sean
 *
 * @param <T>
 * @param <PK>
 */
public class BaseDaoImpl<T, PK extends Serializable> implements
		BaseDao<T, PK> {

	private Class<T> entityClass;
	private SessionFactory sessionFactory;
	private final String ORDER_FILED = "";

	@Resource
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public BaseDaoImpl() {
		Class c = getClass();
		Type type = c.getGenericSuperclass();
		if (type instanceof ParameterizedType) {
			Type[] parameterizedType = ((ParameterizedType) type)
					.getActualTypeArguments();
			this.entityClass = (Class<T>) parameterizedType[0];
		}
	}


	public Session getSession() {

		return sessionFactory.getCurrentSession();
	}


	public T get(Serializable id) {
		// TODO Auto-generated method stub
		return (T) getSession().get(entityClass, id);
	}


	public List<T> getAllList() {
		String hql = "from " + entityClass.getName();
		if (StringUtils.isNotBlank(ORDER_FILED)) {
			hql += "order by" + ORDER_FILED;
		}
		return getSession().createQuery(hql).list();
	}


	public Long getTotalCount() {
		String hql = "select count(*) from " + entityClass.getName();
		return (Long) getSession().createQuery(hql).uniqueResult();
	}


	public PK save(T entity) {
		if (entity instanceof BaseEntity) {
			try {
				Method method = entity.getClass().getMethod("onSave");
				method.invoke(entity);
				return (PK) getSession().save(entity);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return (PK) getSession().save(entity);
		}
	}


	public boolean update(T entity) {
		if (entity instanceof BaseEntity) {
			try {
				Method method = entity.getClass().getMethod("onUpdate");
				method.invoke(entity);
				getSession().update(entity);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			try {
				getSession().update(entity);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}


	public boolean delete(T entity) {
		try {
			getSession().delete(entity);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}


	public boolean delete(Serializable id) {
		try {
			T entity = (T) getSession().load(entityClass, id);
			getSession().delete(entity);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	public void flush() {
		getSession().flush();

	}


	public Page findPage(Page page) {
		Integer pageNumber = page.getPageNumber();
		Integer pageSize = page.getPageSize();
		String searchBy = page.getSearchBy();
		String keyword = page.getKeyword();
		String orderBy = page.getOrderBy();
		Order order = page.getOrder();
		String hql = "from " + entityClass.getName() + "as entity ";
		if (StringUtils.isNotEmpty(searchBy) && StringUtils.isNotEmpty(keyword)) {
			hql += "where entity." + searchBy + "like %" + keyword + "% ";
		}

		if (StringUtils.isNotEmpty(orderBy) && order != null) {
			if (order == Order.asc) {
				hql += "order by " + orderBy + " ASC";
			} else {
				hql += "order by " + orderBy + " DESC";
			}
		}
		List list = getSession().createQuery(hql)
				.setFirstResult((pageNumber - 1) * pageSize)
				.setMaxResults(pageSize).list();
		page.setTotalCount( getSession().createQuery(hql).list().size());

		page.setResult(list);
		return page;
	}


	public Page findPageBySql(Page page, String sql) {
		Integer pageNumber = page.getPageNumber();
		Integer pageSize = page.getPageSize();
		String searchBy = page.getSearchBy();
		String keyword = page.getKeyword();
		String orderBy = page.getOrderBy();
		Order order = page.getOrder();
		List list = getSession().createSQLQuery(sql)
				.setFirstResult((pageNumber - 1) * pageSize)
				.setMaxResults(pageSize).list();
		page.setTotalCount(getSession().createQuery(sql).list().size());

		page.setResult(list);
		return page;
	}

}
