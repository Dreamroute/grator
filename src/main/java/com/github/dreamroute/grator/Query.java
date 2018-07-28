package com.github.dreamroute.grator;

import java.io.Serializable;
import java.util.List;

import com.github.dreamroute.grator.wrapper.Association;
import com.github.dreamroute.grator.wrapper.CollectionWapper;

/**
 * 
 * @author 342252328@qq.com
 * @version 1.0
 * @date 2018-05-01
 *
 */
public class Query implements Serializable {

    private static final long serialVersionUID = 7488151930267886073L;

    private Object master;
    private String pk;
    private List<String> foreignKeys;
    private List<Association> associations;
    private List<CollectionWapper> collections;
    private Class<?> resultCls;

    public Object getMaster() {
        return master;
    }

    public void setMaster(Object master) {
        this.master = master;
    }

    public List<String> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<String> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public List<Association> getAssociations() {
        return associations;
    }

    public void setAssociations(List<Association> associations) {
        this.associations = associations;
    }

    public Class<?> getResultCls() {
        return resultCls;
    }

    public void setResultCls(Class<?> resultCls) {
        this.resultCls = resultCls;
    }

    public List<CollectionWapper> getCollections() {
        return collections;
    }

    public void setCollections(List<CollectionWapper> collections) {
        this.collections = collections;
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

}
