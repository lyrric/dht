package com.github.lyrric.common.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.lyrric.common.util.FileTypeUtil;
import com.github.lyrric.common.util.StringUtil;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tree {

	private Node root;

	@Transient
	@JsonIgnore
	private List<Node> leaves;

	public Tree() {
		super();
	}

	public Tree(String text) {
		root = new Node(0, -1, text, null, -1);
	}

	public void createTree(List<Node> nodes) {
		for (Node node : nodes) {
			//父亲是根节点，直接添加到根节点下面
			if (node.getPid() == root.getNid()) {
				root.addChild(node);
			} else {	//父亲是其他节点
				Node parent = findParent(root, node.getPid());
				if (parent != null) {
					parent.addChild(node);
				}
			}
		}
	}

	private Node findParent(Node node, int pid) {
		Node result = null;
		for (Node n : node.getChildren()) {
			if (n.getNid() == pid) {
				return n;
			} else {
				//递归搜索
				if (n.getChildren() != null)
					result =  findParent(n, pid);
			}
		}
		return result;
	}

	public void middlePrint(Node tnode) {
		if (tnode.getChildren() == null) {
			return;
		}
		for (Node node : tnode.getChildren()) {
			System.out.println(node.getFileName());
			middlePrint(node);
		}
	}

	/**
	 * 构建叶子节点数组，实际上就是构建子文件列表
	 * @return
	 */
	@Transient
	@JsonIgnore
	public List<Node> leafList() {
		leaves = new ArrayList<>();
		deep(root);
		return leaves;
	}

	private void deep(Node tnode) {
	    if(leaves.size() > 20){
	        //大于20个就不保存了
			//避免数据太大，引起异常
            return ;
        }
		if (tnode.getChildren() == null) {
			leaves.add(tnode);
			return;
		}
		for (Node node : tnode.getChildren()) {
			deep(node);
		}
	}

	public String getHtml(Node tnode) {
		
		if (tnode.getChildren() == null) {	//叶子节点
			return "<li><span class=\"" + FileTypeUtil.getFileType(tnode.getFileName()) + "\">" + tnode.getFileName()
					+ ((tnode.getFileSize() != null) ? "<small>(" + StringUtil.formatSize(tnode.getFileSize()) + ")" + "</small>" : "")
					+ "</span></li>";
		}
		
		String str = "";
		if (tnode.getNid() == root.getNid()) {	//根节点
			str += "<ul class=\"filetree treeview\"><p><span class=\"bticon\">" + root.getFileName() + "</span></p>";
		} else {				//子节点
			str += "<li class=\"closed\"><span class=\"folder\">" + tnode.getFileName() + "</span><ul>";
		}
		
		for (Node node : tnode.getChildren()) {
			str += getHtml(node);
		}
		
		if (tnode == root) {	//根节点
			return str += "</ul>";
		} else {				//子节点
			return str += "</ul></li>";
		}
	}
	
	public boolean checkExist(Node tnode, String text) {
		boolean exist = false;
		if (tnode.getChildren() == null) {
			return false;
		}
		for (Node node : tnode.getChildren()) {
			exist |= checkExist(node, text);
		}
		return exist;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}
	
	
}
