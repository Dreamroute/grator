package com.github.dreamroute.grator.wrapper;

/**
 * 
 * @author 342252328@qq.com
 * @version 1.0
 * @date 2018-05-01
 *
 */
public class Association {
    private String associationProp;
    private Object association;
    private String primaryKey;

    public String getAssociationProp() {
        return associationProp;
    }

    public void setAssociationProp(String associationProp) {
        this.associationProp = associationProp;
    }

    public Object getAssociation() {
        return association;
    }

    public void setAssociation(Object association) {
        this.association = association;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

}
