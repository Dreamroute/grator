package com.bdfint.grator;

import java.lang.reflect.Field;
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

    public static QueryBuilder newInstance() {
        return new QueryBuilder();
    }

    public QueryBuilder many2one(Object master, String... foreignKeys) {
        query.setMaster(master);
        List<String> fks = Arrays.asList(foreignKeys);
        query.setForeignKeys(fks);
        return this;
    }

    public QueryBuilder one2many(Object master, String pk) {
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

    public <T> T result(Class<T> result) {
        query.setResultCls(result);
//        return many2one();
         return one2many();
    }

    @SuppressWarnings("unchecked")
    private <T> T many2one() {
        Class<?> masterCls = query.getMaster().getClass();
        List<String> fks = query.getForeignKeys();

        List<Association> associations = query.getAssociations();
        if (associations != null && !associations.isEmpty()) {
            Map<String, Object> masterMap = (Map<String, Object>) JSON.toJSON(query.getMaster());
            for (int i = 0; i < associations.size(); i++) {
                Association association = associations.get(i);
                Class<?> associationCls = association.getAssociation().getClass();
                String pk = association.getPrimaryKey();
                try {
                    Field pkField = getFieldByName(masterCls, fks.get(i));
                    Object pkValue = getFieldValue(pkField, query.getMaster());
                    Field associationKeyField = getFieldByName(associationCls, pk);
                    Object associationKeyValue = getFieldValue(associationKeyField, association.getAssociation());
                    if (Objects.equals(pkValue, associationKeyValue)) {
                        masterMap.put(association.getAssociationProp(), JSON.toJSON(association.getAssociation()));
                    }
                } catch (SecurityException | IllegalArgumentException e) {
                    throw new GratorException(e);
                }

            }
            Class<?> resultType = query.getResultCls();
            String resultStr = masterMap.toString();
            return (T) (Objects.equals(resultType, String.class) ? resultStr : JSON.parseObject(resultStr, resultType));
        }

        throw new GratorException();
    }

    @SuppressWarnings("unchecked")
    private <T> T one2many() {
        Object master = query.getMaster();
        String pk = query.getPk();
        Field pkField = getFieldByName(master.getClass(), pk);
        Object pkValue = getFieldValue(pkField, master);
        List<CollectionWapper> cwList = query.getCollections();
        Map<String, Object> masterMap = (Map<String, Object>) JSON.toJSON(master);
        if (cwList != null && !cwList.isEmpty()) {
            for (int i = 0; i<cwList.size(); i++) {
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
        Class<?> resultType = query.getResultCls();
        String resultStr = masterMap.toString();
        return (T) (Objects.equals(resultType, String.class) ? resultStr : JSON.parseObject(resultStr, resultType));
    }

    private Object getFieldValue(Field field, Object domain) {
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

    private static void validateParams(Object[] params) {
        if (params == null || params.length == 0) {
            throw new GratorException("params must not be null or length of params is 0.");
        }
        if (params.length % 2 == 1) {
            throw new GratorException("length of params must be even.");
        }
        if (paramsExistNull()) {
            throw new GratorException("params item must not be null.");
        }
    }

    public static boolean paramsExistNull(Object... params) {
        boolean existNull = false;
        for (Object param : params) {
            if (param == null) {
                existNull = true;
                break;
            }
        }
        return existNull;
    }

}
