package com.xb.zhang.bigdata.hive;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

public class HiveJdbcTemplateTest {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception{
		
		ApplicationContext cxt = new ClassPathXmlApplicationContext(new String[]{"config/spring_common.xml"});
		JdbcTemplate hiveJdbcTemplate = (JdbcTemplate)cxt.getBean("hiveJdbcTemplate");
		
		// query all data
		List<Map<String, Object>> result = hiveJdbcTemplate.queryForList("select * from lf_test");
		if(StringUtils.isEmpty(result)){
			System.out.println("result is null !!!");
		}else{
			for(Map<String, Object> line : result){
				System.out.println(JSON.toJSONString(line));
			}
		}
	}

}
