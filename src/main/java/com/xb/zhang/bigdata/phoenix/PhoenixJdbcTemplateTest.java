package com.xb.zhang.bigdata.phoenix;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

public class PhoenixJdbcTemplateTest {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception{
		
		ApplicationContext cxt = new ClassPathXmlApplicationContext(new String[]{"config/spring_common.xml"});
		JdbcTemplate phoenixJdbcTemplate = (JdbcTemplate)cxt.getBean("phoenixJdbcTemplate");
		// create table
		phoenixJdbcTemplate.update("create table student_1 (pk varchar not null primary key, info.age varchar, info.sex varchar)");
		// add column
		phoenixJdbcTemplate.update("alter table student_1 add course.math varchar(255)");
		phoenixJdbcTemplate.update("alter table student_1 add course.china varchar(255)");
		phoenixJdbcTemplate.update("alter table student_1 add course.english varchar(255)");
		// query all data
		List<Map<String, Object>> result = phoenixJdbcTemplate.queryForList("select * from student_1");
		if(StringUtils.isEmpty(result)){
			System.out.println("result is null !!!");
		}else{
			for(Map<String, Object> line : result){
				System.out.println(JSON.toJSONString(line));
			}
		}
	}

}
