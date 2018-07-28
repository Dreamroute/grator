package com.github.dreamroute.grator.wrapper;

import java.util.Collection;

/**
 * 
 * @author 342252328@qq.com
 * @version 1.0
 * @date 2018-05-01
 *
 */
public class CollectionWapper {
    private Collection<? extends Object> collection;
    private String foreignKey;
    private String collectionProp;

    public Collection<? extends Object> getCollection() {
        return collection;
    }

    public void setCollection(Collection<? extends Object> collection) {
        this.collection = collection;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getCollectionProp() {
        return collectionProp;
    }

    public void setCollectionProp(String collectionProp) {
        this.collectionProp = collectionProp;
    }
}
