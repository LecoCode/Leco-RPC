package com.leco.rpc.service.util;

import com.leco.rpc.core.entity.RequestSerialize;
import com.leco.rpc.core.filter.IFilter;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.util.*;

/**
 * @创建人 leco
 * @创建时间 2018/7/31
 */
public final class God {
    private static final God god = new God();
    private Map<String, Object> serviceMap = new HashMap<String, Object>(); //rpc服务的集合
    private Set<IFilter> filterSet = new HashSet<IFilter>();
    private Object serviceLock = new Object();
    private Object filterLock = new Object();
    private Object contextLock = new Object();
    private God() {
    }

    public static God getInstance() {
        return god;
    }



    public <T> Object handle(T serialize) throws Exception {
        Class<?> requClass = serialize.getClass(); // 获取需要处理的对象所属类
        Object result = null;
        if (requClass == RequestSerialize.class) {//如果处理对象是RequestSerialize
            RequestSerialize requestSerialize = (RequestSerialize) serialize;//类型转换
            //get参数
            String className = requestSerialize.getClassName();
            String methodName = requestSerialize.getMethodName();
            Class<?>[] paramerType = requestSerialize.getParamerType();
            Object[] paramerValues = requestSerialize.getParamerValues();
            //校验
            if ("".equals(className)) {
                throw new NullPointerException("className null pointer");
            }
            if ("".equals(methodName)) {
                throw new NullPointerException("methodName null pointer");
            }
            //根据className获取对应的service的实例
            Object service = serviceMap.get(className);
            //得到service的Class对象
            Class<?> serviceClass = service.getClass();
//            Method declaredMethod = serviceClass.getDeclaredMethod(methodName, paramerType);
            //通过cglib得到rpc调用的方法
            FastClass serviceFastClass = FastClass.create(serviceClass);
            FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, paramerType);

            //执行前置过滤器
            Iterator<IFilter> filterIterator = filterSet.iterator();
            while (filterIterator.hasNext()) {
                IFilter filter = filterIterator.next();
                filter.before(requestSerialize);
            }

            result = serviceFastMethod.invoke(service, paramerValues);

            //执行后置过滤器
            while (filterIterator.hasNext()) {
                IFilter filter = filterIterator.next();
                filter.After(requestSerialize, result);
            }

        } else {

        }
        return result;
    }

    public void addService(String key, Object value) {
        synchronized (serviceLock) {
            if (!serviceMap.containsKey(key)) {
                serviceMap.put(key, value);
                System.out.println("put service name : " + key );
            }
        }
    }

    public void addFilter(IFilter filter) {
        synchronized (filterLock) {
            if (!filterSet.contains(filter)) {
                filterSet.add(filter);
                System.out.println("put service name : " + filter.getClass().getName() );
            }
        }
    }


}
