package com.leco.rpc.core.entity;

/**
 *响应序列化对象
 * @创建人 leco
 * @创建时间 2018/7/30
 */
public class ResponseSerialize {
    private String requestId;
    private Throwable error;
    private Object result;


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }
}
