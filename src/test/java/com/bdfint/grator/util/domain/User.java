package com.bdfint.grator.util.domain;

import java.util.List;
import java.util.Set;

public class User {

    private Long id;

    // 姓名
    private String name;

    // 邮箱
    private String email;

    private List<Order> orders;

    private Set<Addr> addrs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Set<Addr> getAddrs() {
        return addrs;
    }

    public void setAddrs(Set<Addr> addrs) {
        this.addrs = addrs;
    }

}