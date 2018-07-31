package com.leco.rpc.service.util;


import com.leco.rpc.core.entity.RequestSerialize;
import com.leco.rpc.core.entity.ResponseSerialize;
import com.leco.rpc.core.serialization.ISerialization;
import com.leco.rpc.core.serialization.ProtostuffSerialization;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @创建人 leco
 * @创建时间 2018/7/30
 */
public class ConfigProvider {

    public static Class<?> requestEneity = RequestSerialize.class; //解码实体 默认 ： RequestSerialize.class

    public static Class<?> responseEneity = ResponseSerialize.class; //编码实体 默认 ： ResponseSerialize.class

    public static String host = "127.0.0.1";//绑定的ip 默认：127.0.0.1

    public static int port = 9920;//绑定的端口 默认：9920

    public static int ChannelOption_SO_BACKLOG = 128;

    public static boolean ChannelOption_SO_KEEPALIVE = true;

    public static ISerialization serialization = new ProtostuffSerialization(); //选择的序列化实现类 默认为Protostuff序列化

    public static String baseServiceDir = "";

    public static Properties properties = new Properties(); //配置文件在程序内的载体

    //根据指定配置文件初始化配置
    //配置文件内没有的配置则使用默认配置
    public static void init(String configPath) {
        //加载配置文件
        File file = new File(configPath);
        if (!file.exists()) {
            System.out.println("not find config file : " + configPath);
        } else if (file.isDirectory()) {
            System.out.println("configPath : " + configPath + "is directory");
        } else if (file.getName().endsWith(".properties")) {
            try {
                InputStream in = new FileInputStream(file);
                properties.load(in);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //通过反射设置ConfigProvider的字段值
        Class<ConfigProvider> provider = ConfigProvider.class;
        Field[] declaredFields = provider.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);//设置为字段可被修改
            String name = declaredField.getName();
            if (properties.containsKey(name)) {//通过字段名检查properties内是否存在所对应的配置
                String value = properties.getProperty(name);
                Class<?> type = declaredField.getType();//获取字段的类型
                try {
                    //通过不同类型进行set值
                    if (type == String.class) {
                        declaredField.set(ConfigProvider.class, value);
                    } else if (type == Integer.class) {
                        //检查值是否为整数数字
                        Pattern pattern = Pattern.compile("^[\\d]*$");
                        boolean matches = pattern.matcher(value).matches();
                        if (matches){
                            declaredField.setInt(ConfigProvider.class, Integer.parseInt(value));
                        }
                    } else if (type == Boolean.class) {
                        boolean bValue ;
                        //检查布尔值是否合法
                        if ("true".equals(value)) {
                            bValue = true;
                        } else if ("false".equals(value)) {
                            bValue = false;
                        } else {
                            throw new IllegalArgumentException(declaredField.getName() + " field set value error");
                        }
                        declaredField.setBoolean(ConfigProvider.class, bValue);
                    } else if (type == ISerialization.class) {
                        Class<?> aClass = Class.forName(value);
                        Object o = aClass.newInstance();
                        if (o instanceof ISerialization) {
                            declaredField.set(ConfigProvider.class, o);
                        }
                    } else if (type == Class.class) {
                        Class<?> aClass = Class.forName(value);
                        declaredField.set(ConfigProvider.class, aClass);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
            declaredField.setAccessible(false);//设置为字段不可被修改
        }

    }


}
