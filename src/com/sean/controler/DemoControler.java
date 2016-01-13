package com.sean.controler;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sean.dao.DemoDao;
import com.sean.entity.Demo;

@Controller
@RequestMapping("/demo")
public class DemoControler extends BaseControler{

	
	@Resource
	private DemoDao demoDaoImpl;
	
	@RequestMapping("/demo.do")
	public String demo(){
		System.out.println("demo");
		Demo demo=new Demo();
		demo.setName("cccc");
		demo.setPassword("123123");
		demoDaoImpl.save(demo);
		return "";
	}
	
}
