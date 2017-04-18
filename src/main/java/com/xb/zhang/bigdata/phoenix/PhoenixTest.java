package com.xb.zhang.bigdata.phoenix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

public class PhoenixTest {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception{
		
		ApplicationContext cxt = new ClassPathXmlApplicationContext(new String[]{"config/spring_common.xml"});
		JdbcTemplate phoenixJdbcTemplate = (JdbcTemplate)cxt.getBean("phoenixJdbcTemplate");
		List<Map<String, Object>> result = phoenixJdbcTemplate.queryForList("select * from student");
		if(StringUtils.isEmpty(result)){
			System.out.println("result is null !!!");
		}else{
			for(Map<String, Object> line : result){
				System.out.println(JSON.toJSONString(line));
			}
		}
	}

}
