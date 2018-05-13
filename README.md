# grator
微服务返回数据集成&lt;br>integration the multi result of micro-service

## 为什么要使用微服务数据集？
* 由于微服务拆分比较细，往往在一个服务里面无法进行数据库的inner join或者left/right join操作，而需要查询多个服务，那么查询多个服务的返回结果如组合成为
一个结果（对象或者json），数据集就是提供组合多个微服务查询结果的工具，类似于单库的join操作。
