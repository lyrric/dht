# java DHT磁力种子爬虫

## DHT协议
DHT协议作为BT协议的一个辅助，是非常好玩的。它主要是为了在BT正式下载时得到种子或者BT资源。传统的网络，需要一台中央服务器存放种子或者BT资源，不仅浪费服务器资源，还容易出现单点的各种问题，而DHT网络则是为了去中心化，也就是说任意时刻，这个网络总有节点是亮的，你可以去询问问这些亮的节点，从而将自己加入DHT网络。
  
重要概念:  
1.node:负责P2P路由信息，P2P网络的组网就是它来负责  
2.peer:负责管理资源，生成种子文件，发布资源信息  
3.infohash:资源的唯一标识，也是一个160bit的hash值，其和nodeid使用同一个算法   

DHT的重要四个请求：
ping
是用检查Node状态，用以更新Routing table

find_node
通常是用来初始化Routing table，因为一开始，你在Routing table是空的，需要通过向公共节点发送find_node来填充之。

get_peers
是当用户要下载种子资源时向其它Node发起的。如果Node有该资源，则返回资源的下载端口以供对方下载，如果没有，则根据异或算法在自己的Routing table中寻找离资源最近的Node返回给对方，对方如此递归发送get_peers，直到找到资源为止。

announce_peer
当用用户下载完种子资源，通过种子开始下载时（这里下载行为通常会回倒为tracker式下载，但也有有种子文件是有Nodes字段的，可以通过纯p2p下载）通知所有曾经get_peers咨询过的node。announce_peer是爬虫的关键，当下载开始，用户就会通知，于是就得到了一个有效的InfoHash。

要实现DHT协议的网络爬虫，主要分3步：
 > 1.固定的节点发送find_node请求，然后根据向其回复内容中node节点发送find_node，从而认识更多的node节点  
 > 2.回复其他节点发送的ping，find_node，get_peers请求  
 > 3.接收到announce_peer请求时，从peer中获取种子信息

## 用到的框架，工具
1. Srping Boot 2.3.0.BUILD-SNAPSHOT（只有这个版本才支持elastic search 7.5）
2. Mybatis
3. Mysql 8.0
4. redis
5. Elastic Search 7.5
6. netty
  
## 模块功能介绍
**dht-server**：该模块是dht的核心，负责启动netty，加入dht，发送相关的请求，以及回复其他节点的请求。当收到announce_peer请求时，将种子hash_info和peer信息，保存到redis队列。  
**dht-download**: 该模块主要是从redis队列中获取hash_info和peer信息，然后向peer发送请求，下载hash_info种子信息，最后保存到mysql数据库中。  
**dht-web**: 该模块主要是将mysql中的种子信息通过定时任务同步到elastic-search中，并提供搜索接口。  
**dht-common**: 该模块是一些公共实体类，和工具类。


### 引用、参考链接
>DHT协议：<http://www.bittorrent.org/beps/bep_0005.html>  
>DHT扩展协议：<http://www.bittorrent.org/beps/bep_0009.html>  


### 已知问题\注意事项
> - 需要公网IP才能正常运行本程序
> - 在阿里云和腾讯云上测试时，发现在腾讯云上如果每秒发送的数据过多，会导致ssh卡顿，甚至连接不上。在阿里云上未发现该问题  
> - 在家里宽带测试时，即使有公网IP，程序依然不一定正常运行，接收不到announce_peer请求，初步判定可能是运行商限制  
> - 单个Ip获取到的种子信息是有限的，我十天在腾讯云上跑了三百多万的种子信息后，速度明显下降。  
