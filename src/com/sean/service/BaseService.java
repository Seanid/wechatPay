package com.sean.service;

import java.io.Serializable;
import java.util.List;

import com.sean.beans.Page;

public interface BaseService<T, PK extends Serializable> {


	public T get(PK id);


	public List<T> getAllList();


	public Long getTotalCount();


	public PK save(T entity);


	public boolean update(T entity);


	public boolean delete(T entity);


	public boolean delete(PK id);



	public void flush();


	public Page findPager(Page page);


	public Page findPageBySql(Page page, String sql);
}
