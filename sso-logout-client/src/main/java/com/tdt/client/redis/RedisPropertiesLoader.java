package com.tdt.client.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class RedisPropertiesLoader {
    private Logger logger = LoggerFactory.getLogger(RedisPropertiesLoader.class);
    
    private Properties properties;

    //#redis地址
    private HostAndPort hostAndPort;//=192.168.171.3:6379
    //            #最小空闲数
    private int minIdle = 1;
    //            #最大空闲数
    private int maxIdle = 3;
    //            #最大连接数
    private int maxTotal = 3;
    //            #最大建立连接等待时间
    private int maxWait = 3000;
    //            #是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
    private boolean testOnBorrow = true;

    private String channelName;

    public RedisPropertiesLoader() {
        properties = new Properties();
    }
    public RedisPropertiesLoader(String filePath) {
        properties = new Properties();
        if (filePath != null) {
            try {
                addProperties(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void addProperties(String filePath) throws Exception{
        try {
//            URL url = this.getClass().getClassLoader().getResource("/" + filePath);
//            URL url = this.getClass().getResourceAsStream("/" + filePath);
//            String path = url.getPath();
//            if (path != null) {
//                path = path.replace("%20", " ");
//                logger.info("find redis properties in path : [ {} ]", url.getPath());
//            } else {
//                logger.info("redis properties not find in path : [ {} ]", filePath);
//                return;
//            }
//            InputStream inputStream = new BufferedInputStream(new FileInputStream(path));
            InputStream inputStream = this.getClass().getResourceAsStream("/" + filePath);
            this.properties.load(inputStream);
            logger.info("load properties[{}] success!", filePath);


            String[] arr = properties.getProperty("host.port").split(":");
            this.hostAndPort = new HostAndPort(arr[0], Integer.parseInt(arr[1]));

            this.minIdle = properties.get("minIdle") == null ? 3 : Integer.parseInt(properties.getProperty("minIdle"));
            this.maxIdle = properties.get("maxIdle") == null ? 10 : Integer.parseInt(properties.getProperty("maxIdle"));
            this.maxTotal = properties.get("maxTotal") == null ? 15 : Integer.parseInt(properties.getProperty("maxTotal"));
            this.maxWait = properties.get("maxWait") == null ? 1000 : Integer.parseInt(properties.getProperty("maxWait"));
            Object channelName = properties.get("channelName");
            if (channelName == null) {
                logger.info("==> channelName is not provided! we will user 'session_notification' as default");
            } else {
                this.channelName = (String) channelName;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Properties getProperties() {
        return properties;
    }

    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxWait() {
        return maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public String getChannelName() {
        return channelName;
    }
}
