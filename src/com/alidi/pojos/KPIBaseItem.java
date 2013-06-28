package com.alidi.pojos;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: anisimov.da
 * Date: 17.05.13
 * Time: 17:50
 * To change this template use File | Settings | File Templates.
 */
public class KPIBaseItem {

    public KPIBaseItem(KPIBaseItem additionalParam, ArrayList<KPIBaseItem> children, Integer id, String name, String value) {
        this.additionalParam = additionalParam;
        this.children = children;
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public KPIBaseItem(ArrayList<KPIBaseItem> children, Integer id, String name, String value) {
        this.children = children;
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public KPIBaseItem(ArrayList<KPIBaseItem> children, String name, String value) {
        this.children = children;
        this.name = name;
        this.value = value;
    }

    String name;
    Integer id;
    String value;
    KPIBaseItem additionalParam;
    public ArrayList<KPIBaseItem> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public KPIBaseItem getAdditionalParam() {
        return additionalParam;
    }

    public void setAdditionalParam(KPIBaseItem additionalParam) {
        this.additionalParam = additionalParam;
    }
}
