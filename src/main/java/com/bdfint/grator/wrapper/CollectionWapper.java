package com.bdfint.grator.wrapper;

import java.util.Collection;

/**
 * 
 * @author wangdehai@bdfint.com
 * @version 1.0
 * @date 2018-05-01
 *
 */
public class CollectionWapper {
    Collection<? extends Object> collection;
    String foreignKey;
    String collectionProp;

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
