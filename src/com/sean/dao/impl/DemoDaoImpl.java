package com.sean.dao.impl;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.sean.dao.DemoDao;
import com.sean.entity.Demo;

@Repository("demoDaoImpl")
public class DemoDaoImpl implements DemoDao{

	@Resource(name="sessionFactory")
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public boolean save(Demo demo) {
		// TODO Auto-generated method stub
		Session session =sessionFactory.openSession();
		session.save(demo);
		return false;
	}

}
