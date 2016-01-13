package com.sean.dao;

import java.io.Serializable;
import java.util.List;
import org.hibernate.Session;
import com.sean.beans.Page;

public interface BaseDao<T, PK extends Serializable> {


	public Session getSession();


	public T get(PK id);


	public List<T> getAllList();


	public Long getTotalCount();


	public PK save(T entity);


	public boolean update(T entity);


	public boolean delete(T entity);


	public boolean delete(PK id);


	public void flush();


	public Page findPage(Page page);


	public Page findPageBySql(Page page, String sql);

}