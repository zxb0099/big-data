package com.xb.zhang.utils.spring;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.Resource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

import com.xb.zhang.system.ConfigPropertieUtils;

/**
 * 加载资源文件
 * @Author：wangzhiwei       
 * @Create Date：2014-11-04
 * @Modified By： 
 * @Modified Date:
 */
public class DecryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	
	private Resource[] locations;
	private String fileEncoding;
	
    public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}
	public void setLocations(Resource[] locations) {
        this.locations = locations;
    }
    public void loadProperties(Properties props) throws IOException {
    	String systemBasePath = "/etc/hnaconf/consumer";
        if (this.locations != null) {
            PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();
            for (int i = 0; i < this.locations.length; i++) {
            	InputStream is = null;
                Resource location = this.locations[i];
                File configDir = new File(systemBasePath);
            	if(!configDir.exists()) {
            		is = this.getClass().getResourceAsStream("/config/"+location.getFilename());
            	}else {
            		String  filePath = systemBasePath + File.separator + location.getFilename();
                    is = new FileInputStream(filePath);
            	}
                try {
                    if (fileEncoding != null) {
                        propertiesPersister.load(props, new InputStreamReader(is, fileEncoding));
                    } else {
                        propertiesPersister.load(props, is);
                    }
                    ConfigPropertieUtils.prop = props;
                } catch (Exception e){
                    logger.error("加载配置文件出错",e);
                } finally{
                    if (is != null) is.close();
                }
            }
        }
    }
}
