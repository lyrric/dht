package com.github.lyrric.server.util;

import com.github.lyrric.server.model.Node;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created on 2020-02-27.
 * 假的
 * @author wangxiaodong
 */
@Slf4j
@Component
public class RouteTable {

    private final int MAX_SIZE = 10;
    /**
     * 存十个
     */
    private List<Node> nodes = new LinkedList<>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private long lastTime = System.currentTimeMillis()/1000;

    /**
     * 每秒新增一个
     * @param node
     */
    public void add(Node node){
        if(lock.writeLock().tryLock()){
            if(exist(node)){
                return ;
            }
            try {
                if(nodes.size() <= MAX_SIZE){
                    nodes.add(node);
                    log.info("add node {}", node.getAddr());
                    return;
                }
                long time = System.currentTimeMillis()/1000;
                if(time == lastTime){
                    return;
                }
                lastTime = time;
                nodes.add(node);
                nodes.remove(0);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.writeLock().unlock();
            }
        }
    }

    /**
     * 获取所有节点
     * @return
     */
    public List<Node> getAll(){
        return new ArrayList<>(nodes);
    }

    private boolean exist(Node node){
        return nodes.stream().anyMatch(t->t.getAddr().getHostString().equals(node.getAddr().getHostString()));
    }

}
