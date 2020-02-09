package com.github.lyrric.common.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Node {

	@JsonIgnore
	private int nid; // 父亲id
	@JsonIgnore
	private int pid;

	@JsonAlias(value = "filename")
	private String fileName = "";
	@JsonAlias(value = "filesize")
	private Long fileSize;
	@JsonIgnore
	private int index;
	@JsonIgnore
	private List<Node> children;
	
	public void addChild(Node node) {
		if (children == null) {
			children = new ArrayList<>();
		}
		children.add(node);
	}

	public Node() {

	}

	public Node(int nid, int pid) {
		super();
		this.nid = nid;
		this.pid = pid;
	}
	
	public Node(int nid, int pid, String fileName, Long fileSize, int index) {
		super();
		this.nid = nid;
		this.pid = pid;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.index = index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		return true;
	}
}
