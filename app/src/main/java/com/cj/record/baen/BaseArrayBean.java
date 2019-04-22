package com.cj.record.baen;

import java.io.Serializable;
import java.util.List;

public class BaseArrayBean<T> implements Serializable {

    /**
     * status : 1
     * msg : 获取成功
     * result : [] 数组
     */

    private boolean status;
    private String message;
    private List<T> result;

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

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }
}
