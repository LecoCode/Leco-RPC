package com.leco.rpc.core.entity;

/**
 * 请求序列化对象
 * @创建人 leco
 * @创建时间 2018/7/30
 */
public class RequestSerialize {
    private String requestId;
    private String ClassName;
    private String methodName;
    private Class<?> [] paramerType;
    private Object [] paramerValues;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamerType() {
        return paramerType;
    }

    public void setParamerType(Class<?>[] paramerType) {
        this.paramerType = paramerType;
    }

    public Object[] getParamerValues() {
        return paramerValues;
    }

    public void setParamerValues(Object[] paramerValues) {
        this.paramerValues = paramerValues;
    }
}
