package com.sean.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "demo")
public class Demo extends BaseEntity{


	private String name;
	private String password;

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
