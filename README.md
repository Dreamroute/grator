## 1.为什么要使用微服务数据集？
* 由于微服务拆分比较细，往往在一个服务里面无法进行数据库的inner join或者left/right join操作，而需要查询多个服务，那么查询多个服务的返回结果如组合成为一个结果（对象或者json），
数据集就是提供组合多个微服务查询结果的工具，类似于单库的join操作。
* 举例：前端查询订单需要显示订单相关的买家信息
    * 订单服务查询出订单对象 Order[id=1, price=1.2, num=3, userId=10];
    * 用户服务查询出用户对象 User[id=10, name="Jay", email="jay@xxx.com"]
    * 此时需要返回给前端Order，并且Order内部包含了User对象的，如果是单库，那么select * from order o left join user u on o.user_id = u.id，利用mybatis或者Hibernate的ORM能力，可以返回Order对象
    * 然而此刻订单和用户未在同一个库，无法进行跨库的join操作，所以首先需要将Order和User单独查出，再人工进行合并数据
    * 利用Grator这样实现：Order odr = QueryBuilder.newInstance().many2one(order, "userId", "addrId").association(user, "id", "user").association(addr, "id", "addr").result(Order.class);和mybatis很类似
## 2.使用方法