package com.sean.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.sean.beans.Page;
import com.sean.dao.BaseDao;
import com.sean.service.BaseService;

@Transactional
public abstract class BaseServiceImpl<T, PK extends Serializable> implements
		BaseService<T, PK> {

	private BaseDao<T, PK> baseDao;

	protected abstract void setEntityDao(BaseDao<T, PK> baseDaoImpl);

	@Transactional(readOnly = true)
	public T get(PK id) {
		return baseDao.get(id);
	}

	@Transactional(readOnly = true)
	public List<T> getAllList() {
		// TODO Auto-generated method stub
		return baseDao.getAllList();
	}

	@Transactional(readOnly = true)
	public Long getTotalCount() {
		// TODO Auto-generated method stub
		return baseDao.getTotalCount();
	}

	@Transactional
	public PK save(T entity) {
		// TODO Auto-generated method stub
		return baseDao.save(entity);
	}

	@Transactional
	public boolean update(T entity) {
		return baseDao.update(entity);

	}

	@Transactional
	public boolean delete(T entity) {
		return baseDao.delete(entity);

	}

	@Transactional
	public boolean delete(PK id) {
		return baseDao.delete(id);

	}

	@Override
	public void flush() {
		baseDao.flush();

	}

	@Override
	public Page findPager(Page page) {
		return baseDao.findPage(page);
	}

	@Override
	public Page findPageBySql(Page page, String sql) {
		return baseDao.findPageBySql(page, sql);
	}

}
