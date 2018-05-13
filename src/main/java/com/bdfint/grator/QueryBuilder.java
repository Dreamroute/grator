package com.bdfint.grator;

import java.lang.reflect.Field;
import java.lang.reflect.ReflectPermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.bdfint.grator.exception.GratorException;
import com.bdfint.grator.wrapper.Association;
import com.bdfint.grator.wrapper.CollectionWapper;

/**
 * 
 * @author wangdehai@bdfint.com
 * @version 1.0
 * @date 2018-05-01
 *
 */
public final class QueryBuilder {

    private QueryBuilder() {}

    private Query query = new Query();
    private Type type = null;

    // 1.many2one; 2.one2many
    private enum Type {
        MANY2ONE, ONE2MANY;
    }

    public static QueryBuilder newInstance() {
        return new QueryBuilder();
    }

    public QueryBuilder many2one(Object master, String... foreignKeys) {
        this.type = Type.MANY2ONE;
        query.setMaster(master);
        List<String> fks = Arrays.asList(foreignKeys);
        query.setForeignKeys(fks);
        return this;
    }

    public QueryBuilder one2many(Object master, String pk) {
        this.type = Type.ONE2MANY;
        query.setMaster(master);
        query.setPk(pk);
        return this;
    }

    public QueryBuilder association(Object domain, String primaryKey, String associationProp) {
        List<Association> associationList = query.getAssociations();
        if (associationList == null) {
            associationList = new ArrayList<>();
            query.setAssociations(associationList);
        }
        Association association = new Association();
        association.setAssociation(domain);
        association.setPrimaryKey(primaryKey);
        association.setAssociationProp(associationProp);
        associationList.add(association);
        return this;
    }

    public QueryBuilder collection(Collection<? extends Object> collection, String foreignKey, String collectionProp) {
        List<CollectionWapper> cs = query.getCollections();
        if (cs == null) {
            cs = new ArrayList<>();
        }
        CollectionWapper cw = new CollectionWapper();
        cw.setCollection(collection);
        cw.setForeignKey(foreignKey);
        cw.setCollectionProp(collectionProp);
        cs.add(cw);
        query.setCollections(cs);
        return this;
    }

    public <T> T result(Class<T> resultType) {
        query.setResultCls(resultType);

        // validata params
        validateParams();

        T result = null;
        if (this.type == Type.MANY2ONE) {
            result = many2one();
        } else if (this.type == Type.ONE2MANY) {
            result = one2many();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> T many2one() {
        Class<?> masterCls = query.getMaster().getClass();
        List<String> fks = query.getForeignKeys();

        List<Association> associations = query.getAssociations();
        if (associations != null && !associations.isEmpty()) {
            
            Object result = null;
            
            if (isCollection(masterCls)) {
                result = new ArrayList<Map<String,Object>>();
                Collection<?> master = (Collection<?>) query.getMaster();
                for (Object mst : master) {
                    Map<String, Object> masterMap = (Map<String, Object>) JSON.toJSON(mst);
                    processResult(mst.getClass(), fks, associations, masterMap, mst);
                    ((List<Map<String,Object>>) result).add(masterMap);
                }
            } else {
                result = (Map<String, Object>) JSON.toJSON(query.getMaster());
                processResult(masterCls, fks, associations, (Map<String, Object>) result, query.getMaster());
            }
            
            
            Class<?> resultType = query.getResultCls();
            String resultStr = JSON.toJSONString(result);
            return (T) (Objects.equals(resultType, String.class) ? resultStr : JSON.parseObject(resultStr, resultType));
        }

        throw new GratorException();
    }

    private void processResult(Class<?> masterCls, List<String> fks, List<Association> associations, Map<String, Object> masterMap, Object master) {
        for (int i = 0; i < associations.size(); i++) {
            Association association = associations.get(i);
            Class<?> associationCls = association.getAssociation().getClass();
            if (Collection.class.isAssignableFrom(association.getAssociation().getClass())) {
                Collection<?> o = ((Collection<?>) association.getAssociation());
                associationCls = o.iterator().next().getClass();
            }
            String pk = association.getPrimaryKey();
            try {
                Field fkField = getFieldByName(masterCls, fks.get(i));
                Object fkValue = getFieldValue(fkField, master);
                Field associationKeyField = getFieldByName(associationCls, pk);
                
                Object asstion = association.getAssociation();
                if (Collection.class.isAssignableFrom(asstion.getClass())) {
                    Collection<?> ass = (Collection<?>) asstion;
                    for (Object as : ass) {
                        Object associationKeyValue = getFieldValue(associationKeyField, as);
                        if (Objects.equals(fkValue, associationKeyValue)) {
                            masterMap.put(association.getAssociationProp(), JSON.toJSON(as));
                            break;
                        }
                    }
                } else {
                    Object associationKeyValue = getFieldValue(associationKeyField, asstion);
                    if (Objects.equals(fkValue, associationKeyValue)) {
                        masterMap.put(association.getAssociationProp(), JSON.toJSON(association.getAssociation()));
                        break;
                    }
                }
                
            } catch (SecurityException | IllegalArgumentException e) {
                throw new GratorException(e);
            }
        }
    }

    private boolean isCollection(Class<?> masterCls) {
        return Collection.class.isAssignableFrom(masterCls) ? true : false;
    }

    @SuppressWarnings("unchecked")
    private <T> T one2many() {
        Object master = query.getMaster();
        String pk = query.getPk();
        Object result = null;
        if (Collection.class.isAssignableFrom(master.getClass())) {
            result = new ArrayList<Map<String, Object>>();
            for (Object mst : (Collection<?>) master) {
                Field pkField = getFieldByName(mst.getClass(), pk);
                Object pkValue = getFieldValue(pkField, mst);
                Map<String, Object> res = processResult(mst, pkValue);
                ((ArrayList<Map<String, Object>>) result).add(res);
            }
        } else {
            Field pkField = getFieldByName(master.getClass(), pk);
            Object pkValue = getFieldValue(pkField, master);
            result = processResult(master, pkValue);
        }
        Class<?> resultType = query.getResultCls();
        String resultStr = JSON.toJSONString(result);
        return (T) (Objects.equals(resultType, String.class) ? resultStr : JSON.parseObject(resultStr, resultType));
    }

    private Map<String, Object> processResult(Object master, Object pkValue) {
        @SuppressWarnings("unchecked") Map<String, Object> masterMap = (Map<String, Object>) JSON.toJSON(master);
        List<CollectionWapper> cwList = query.getCollections();
        if (cwList != null && !cwList.isEmpty()) {
            for (int i = 0; i < cwList.size(); i++) {
                List<Object> collectionValue = new ArrayList<>();
                CollectionWapper cw = cwList.get(i);
                Collection<?> cs = cw.getCollection();
                if (cs != null && !cs.isEmpty()) {
                    Iterator<?> it = cs.iterator();
                    while (it.hasNext()) {
                        Object c = it.next();
                        Field fkField = getFieldByName(c.getClass(), cw.getForeignKey());
                        Object fkValue = getFieldValue(fkField, c);
                        if (Objects.equals(pkValue, fkValue)) {
                            collectionValue.add(c);
                        }
                    }
                }
                masterMap.put(cw.getCollectionProp(), JSON.toJSON(collectionValue));
            }
        }
        return masterMap;
    }

    private Object getFieldValue(Field field, Object domain) {
        if (canAccessPrivateMethodsAndFields()) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(domain);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new GratorException(e);
            }
            field.setAccessible(accessible);
            return value;
        }
        throw new GratorException("can not setAccessible(true), please check your platform access privilege.");
    }

    private Field getFieldByName(Class<?> cls, String fieldName) {
        try {
            Field field = cls.getDeclaredField(fieldName);
            if (field == null) {
                cls = cls.getSuperclass();
                field = getFieldByName(cls, fieldName);
            }
            return field;
        } catch (NoSuchFieldException | SecurityException e) {
            throw new GratorException(e);
        }
    }

    public void validateParams() {
        // the type of master must as same as return type/String.
        Class<?> masterType = query.getMaster().getClass();
        Class<?> returnType = query.getResultCls();
        if (!(returnType.equals(masterType) || returnType.equals(String.class) || returnType.isAssignableFrom(masterType))) {
            throw new GratorException("the type of master must as same as return type[resultCls] or String.class.");
        }
    }

    private static boolean canAccessPrivateMethodsAndFields() {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (null != securityManager) {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }

}
