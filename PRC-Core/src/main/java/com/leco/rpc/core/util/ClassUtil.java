package com.leco.rpc.core.util;

import com.leco.rpc.core.util.AnnotationUtil;
import com.leco.rpc.core.annotations.Filtr;
import com.leco.rpc.core.annotations.RpcService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @创建人 leco
 * @创建时间 2018/7/31
 */
public class ClassUtil {

    /**
     * 找查 basePath 目录下所有的jar包
     * @param basePath 根目录
     * @param jarFiles jarFile集合
     */
    public static void findJarFiles(String basePath, HashMap<String, JarFile> jarFiles) {
        File dirfile = new File(basePath);

        //检查该路径是否存在文件或文件夹
        if (!dirfile.exists()) return;

        //只读取文件夹以及后缀为.jar的文件 到files
        File[] files = dirfile.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith(".jar"));

        if (files.length==0){
            return;
        }

        for (File file : files) {
            //如果这个file是文件夹则进行递归找查
            if (file.isDirectory()) {
                findJarFiles(basePath + File.separator + file.getName(), jarFiles);
            }
            //如果是文件
                System.out.println("jar file load path : " + file.getPath());
                JarFile jarFile = null;
                try {
                    jarFile = new JarFile(file);
                    jarFiles.put(file.getPath(), jarFile);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    continue;
                }
        }


    }

    private static boolean isFileExist(File file) {
        if (file.exists() || file.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 加载jarFile中的class文件
     * @param JarFileMap //jiaFile集合  Key：jar包路径  value：jarFile
     * @return  包含RpcService与Filtr注解的class
     */
    public static Set<Class<?>> loadClass(HashMap<String, JarFile> JarFileMap) {
        Set<Class<?>> classSet = new HashSet<>();
        for (String jarPath : JarFileMap.keySet()) {
            try {
                URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{new File(jarPath).toURI().toURL()}); //根据jar包的路径创建一个类加载器
                JarFile jarFile = JarFileMap.get(jarPath);//获取这个jarfile
                Enumeration<JarEntry> entries = jarFile.entries();//获取这个jarfile的节点树
                while (entries.hasMoreElements()) {//遍历这个节点树  （每个文件都代表着一个节点）
                    JarEntry jarEntry = entries.nextElement();//得到这个节点
                    String jarEntryName = jarEntry.getName();//拿到这个节点的名称 如：com.sun.source.tree.AnnotationTree.class
                    if (jarEntryName.endsWith(".class")) {//如果这个节点名称的后缀为.class
                        jarEntryName = jarEntryName.substring(0, jarEntryName.length() -
                                (".class".length())).replaceAll("/", ".").trim();//去掉后缀  替换所有“/”为“.”为后面load做准备
                        Class<?> aClass = urlClassLoader.loadClass(jarEntryName);//通过前面创建的类加载器加载这个class文件
                        RpcService service = AnnotationUtil.findAnnotation(aClass, RpcService.class);//判断是否包含RpcService这个注解
                        Filtr filtr = AnnotationUtil.findAnnotation(aClass, Filtr.class);//判断是否包含Filter这个注解
                        if (null != service) {
                            System.out.println("load service class : " + aClass.getName());
                            classSet.add(aClass);//添加到集合中
                        } else if (null != filtr) {
                            System.out.println("load filtr class : " + aClass.getName());
                            classSet.add(aClass);//添加到集合中
                        }
                    }
                }
                urlClassLoader.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                continue;
            }
        }
        return classSet;
    }

}

