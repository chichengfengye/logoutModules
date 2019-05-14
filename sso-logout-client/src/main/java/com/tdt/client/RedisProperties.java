package com.tdt.client;

import redis.clients.jedis.HostAndPort;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class RedisProperties {
    private Properties properties;
    //#redis地址
    private HostAndPort hostAndPort;//=192.168.171.3:6379
    //            #最小空闲数
    private int minIdle = 3;
    //            #最大空闲数
    private int maxIdle = 10;
    //            #最大连接数
    private int maxTotal = 15;
    //            #客户端超时时间单位是毫秒
    private int timeout = 5000;
    //            #最大建立连接等待时间
    private int maxWait = 1000;
    //            #是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
    private boolean testOnBorrow = true;

    private String channelName;

    public RedisProperties() {
        properties = new Properties();
    }
    public RedisProperties(String filePath) {
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
            URL url = this.getClass().getClassLoader().getResource("/" + filePath);
            String path = url.getPath();
            if (path != null) {
                path = path.replace("%20", " ");
                System.out.println("find redis properties in path : [ "+  url.getPath() +" ]");
            } else {
                System.out.println("redis properties not find in path : [ "+  filePath +" ]");
                return;
            }
            InputStream inputStream = new BufferedInputStream(new FileInputStream(path));
            this.properties.load(inputStream);
            System.out.println("load properties[" + filePath + "] success!");


            String[] arr = properties.getProperty("host.port").split(":");
            this.hostAndPort = new HostAndPort(arr[0], Integer.parseInt(arr[1]));

            this.minIdle = properties.get("minIdle") == null ? 3 : Integer.parseInt(properties.getProperty("minIdle"));
            this.maxIdle = properties.get("maxIdle") == null ? 10 : Integer.parseInt(properties.getProperty("maxIdle"));
            this.maxTotal = properties.get("maxTotal") == null ? 15 : Integer.parseInt(properties.getProperty("maxTotal"));
            this.timeout = properties.get("timeout") == null ? 5000 : Integer.parseInt(properties.getProperty("timeout"));
            this.maxWait = properties.get("maxWait") == null ? 1000 : Integer.parseInt(properties.getProperty("maxWait"));
            if (properties.get("channelName") == null) {
                System.out.println("channelName is not provided! you mast provide a channel name for our cas pub/sub!");
                throw new Exception("channelName is not provided!");
            } else {
                this.channelName = properties.getProperty("channelName");
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

    public int getTimeout() {
        return timeout;
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
