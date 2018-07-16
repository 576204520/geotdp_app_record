package com.cj.record.baen;

import com.cj.record.db.TemplateDao;
import com.cj.record.utils.Common;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/9.
 */
@DatabaseTable(tableName = "compile_template_detail")
public class TemplateDetail implements Serializable {
    private static final long serialVersionUID = 1L;
    @DatabaseField(columnName = "ids", id = true)
    String ids;//主键
    @DatabaseField
    String templateId;//模板id
    @DatabaseField
    String fieldKey;//字段key
    @DatabaseField
    String fieldValue;//字段value
    @DatabaseField
    String sort;//排序

    public TemplateDetail() {
    }

    public TemplateDetail(String templateId, String fieldKey, String fieldValue) {
        this.ids = Common.getUUID();
        this.templateId = templateId;
        this.fieldKey = fieldKey;
        this.fieldValue = fieldValue;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
