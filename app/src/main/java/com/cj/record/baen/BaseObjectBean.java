package com.cj.record.baen;


import java.io.Serializable;

public class BaseObjectBean<T> implements Serializable {

    /**
     * status : 1
     * msg : 获取成功
     * result : {} 对象
     */

    private boolean status;
    private String message;
    private T result;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }


}
