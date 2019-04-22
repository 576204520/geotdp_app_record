package com.cj.record.baen;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2019/3/18.
 */

public class PageBean<T> implements Serializable {
    private int totleSize;
    private int page;
    private int size;
    private List<T> list;

    public int getTotleSize() {
        return totleSize;
    }

    public void setTotleSize(int totleSize) {
        this.totleSize = totleSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
