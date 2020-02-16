package com.github.lyrric.server.model;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/***
 * 去重的节点阻塞队列
 *
 * @author Mr.Xu
 * @date 2019-02-17 18:44
 **/
@Slf4j
public class UniqueBlockingQueue {

	private Set<String> ips = new HashSet<>();
	private BlockingQueue<Node> nodes = new LinkedBlockingQueue<>();

	public int size() {
		return ips.size();
	}

	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	public boolean offer(Node node) {
		if (ips.add(node.getAddr().getHostString()))
			return nodes.offer(node);
		return false;
	}

	public Node take() throws InterruptedException {
		Node node = nodes.take();
		ips.remove(node.getAddr().getHostString());
		return node;
	}
}
