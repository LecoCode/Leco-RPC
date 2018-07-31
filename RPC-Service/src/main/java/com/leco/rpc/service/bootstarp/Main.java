package com.leco.rpc.service.bootstarp;

import com.leco.rpc.core.util.AnnotationUtil;
import com.leco.rpc.core.annotations.Filtr;
import com.leco.rpc.core.annotations.RpcService;
import com.leco.rpc.core.filter.IFilter;
import com.leco.rpc.service.service.IServer;
import com.leco.rpc.service.service.RpcNettyServer;
import com.leco.rpc.core.util.ClassUtil;
import com.leco.rpc.service.util.ConfigProvider;
import com.leco.rpc.service.util.God;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("input param <host> <port> <lib dir>");
            System.exit(-1);
        }
        String host = args[0];
        String port = args[1];
        String baseServiceDir = args[2];
        if (!"".equals(port)) {
            ConfigProvider.port = Integer.parseInt(port);
        }
        Pattern hostPattern = Pattern.compile("^\\d+.\\d+.\\d+.\\d+$");
        if (!"".equals(host)) {
            if (!hostPattern.matcher(host).matches()) {
                System.out.println("host format error : " + host);
                System.exit(0);
            }
            ConfigProvider.host = host;
        }
        if (!"".equals(baseServiceDir)) {
            File baseServiceDirFile = new File(baseServiceDir);
            if (baseServiceDirFile.isDirectory()) {
                ConfigProvider.baseServiceDir = baseServiceDir;
            } else {
                System.out.println("null base service dir : " + baseServiceDir);
                System.exit(0);
            }
        } else {
            System.out.println("null base service dir : " + baseServiceDir);
            System.exit(0);
        }
        HashMap<String, JarFile> jarFiles = new HashMap<>();
        System.out.println("----------- start find jar file -------------");
        ClassUtil.findJarFiles(ConfigProvider.baseServiceDir, jarFiles);
        System.out.println("----------- start load class file -------------");
        Set<Class<?>> classes = ClassUtil.loadClass(jarFiles);
        Iterator<Class<?>> iterator = classes.iterator();
        God god = God.getInstance();
        while (iterator.hasNext()) {
            Class<?> aClass = iterator.next();
            RpcService rpcService = AnnotationUtil.findAnnotation(aClass, RpcService.class);
            Filtr filtr = AnnotationUtil.findAnnotation(aClass, Filtr.class);
            try {
                if (null != rpcService) {
                    String implName = rpcService.implName();
                    if ("".equals(implName)) {
                        implName = aClass.getName();
                    }
                    god.addService(implName, aClass.newInstance());
                }
                if (null != filtr) {
                    god.addFilter((IFilter) aClass.newInstance());
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        System.out.println("----------- create rpcServer -------------");
        IServer rpcServer = new RpcNettyServer();
        rpcServer.start();


    }


}
