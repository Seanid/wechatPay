package com.sean.controler;

import org.springframework.web.bind.annotation.ModelAttribute;

public class BaseControler {

	@ModelAttribute
	public void init(){
		System.out.println("");
	}
	
	
}
