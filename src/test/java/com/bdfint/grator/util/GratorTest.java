package com.bdfint.grator.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.bdfint.grator.QueryBuilder;
import com.bdfint.grator.util.domain.Addr;
import com.bdfint.grator.util.domain.Order;
import com.bdfint.grator.util.domain.User;

public class GratorTest {

    /**
     * 多对一
     */
    @Test
    public void many2oneTest() {

        Order order = new Order();
        order.setId(1L);
        order.setPrice(new BigDecimal("1.2"));
        order.setNum(3);
        order.setUserId(100L);
        order.setAddrId(10L);

        User user = new User();
        user.setId(100L);
        user.setName("w.dehai");
        user.setEmail("wangdehai@bdfint.com");

        Addr addr = new Addr();
        addr.setId(10L);
        addr.setName("四川成都");

        Order odr = QueryBuilder.newInstance()
                .many2one(order, "userId", "addrId")
                .association(user, "id", "user")
                .association(addr, "id", "addr")
                .result(Order.class);
        System.err.println(odr);

    }
    
    @Test
    public void many2oneCollectionTest() {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setId(Long.valueOf(i));
            order.setPrice(new BigDecimal(String.valueOf(i + 1.2)));
            order.setUserId(Long.valueOf(i));
            orders.add(order);
        }
        
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setId(Long.valueOf(i));
            user.setName("w.dehai" + i);
            users.add(user);
        }
        
        List<?> orderList = QueryBuilder.newInstance().many2one(orders, "userId").association(users, "id", "user").result(List.class);
        System.err.println(orderList);
    }

    /**
     * 一对多
     */
    @Test
    public void one2manyTest() {

        User user = new User();
        user.setId(100L);
        user.setName("w.dehai");
        user.setEmail("wangdehai@bdfint.com");

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setId(Long.valueOf(i));
            order.setNum(3);
            order.setPrice(new BigDecimal("1.2"));
            order.setUserId(100L);
            orders.add(order);
        }

        List<Addr> addrs = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Addr addr = new Addr();
            addr.setId(Long.valueOf(i));
            addr.setName("四川成都" + i);
            addr.setUserId(100L);
            addrs.add(addr);
        }

        User result = QueryBuilder.newInstance().one2many(user, "id").collection(orders, "userId", "orders").collection(addrs, "userId", "addrs").result(User.class);
        String resultStr = QueryBuilder.newInstance().one2many(user, "id").collection(orders, "userId", "orders").collection(addrs, "userId", "addrs").result(String.class);
        System.err.println(result);
        System.err.println(resultStr);

    }
    
    /**
     * 一对多
     */
    @Test
    public void one2manyCollectionTest() {

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            User user = new User();
            user.setId(100L + i);
            user.setName("w.dehai");
            user.setEmail("wangdehai@bdfint.com");
            users.add(user);
        }

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setId(Long.valueOf(i));
            order.setNum(3);
            order.setPrice(new BigDecimal("1.2"));
            order.setUserId(100L + i);
            orders.add(order);
        }

        List<Addr> addrs = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Addr addr = new Addr();
            addr.setId(Long.valueOf(i));
            addr.setName("四川成都" + i);
            addr.setUserId(100L);
            addrs.add(addr);
        }

        List<?> result = QueryBuilder.newInstance().one2many(users, "id").collection(orders, "userId", "orders").collection(addrs, "userId", "addrs").result(List.class);
        String resultStr = QueryBuilder.newInstance().one2many(users, "id").collection(orders, "userId", "orders").collection(addrs, "userId", "addrs").result(String.class);
        System.err.println(result);
        System.err.println(resultStr);

    }

}
