package org.peg4d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Queue;
import java.sql.*;

public class DBBuilder {
	
	private HashMap<String, HashMap<String, NodeData>> datamap = null;
	
	public DBBuilder() {
		datamap = new HashMap<String, HashMap<String, NodeData>>();
	}
	
	private void buildDataMap(ParsingObject node) {
		String tag  = node.getTag().toString();
		String key  = node.getText();
		if(!this.datamap.containsKey(tag)) {
			HashMap<String, NodeData> ndm = new HashMap<String, NodeData>();
			NodeData nodedata = new NodeData(tag, key, node);
			ndm.put(key, nodedata);
			this.datamap.put(tag, ndm);
		}
		else {
			HashMap<String, NodeData> ndm = this.datamap.get(tag);
			if(!ndm.containsKey(key)) {
				NodeData nodedata = new NodeData(tag, key, node);
				ndm.put(key, nodedata);
			}
			else {
				NodeData nodedata = ndm.get(key);
				nodedata.increment();
				nodedata.addnode(node);
			}
		}
	}
	
	private void analyzeFrequency(ParsingObject root, int id) {
		if(root == null) return;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			node.setId(id++);
			if(node.size() == 0) this.buildDataMap(node);
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
	}
	
	private String showNodeIDList(ArrayList<ParsingObject> list) {
		ArrayList<Integer> idlist = new ArrayList<Integer>();
		for(int i = 0; i < list.size(); i++) {
			int id = list.get(i).getId();
			idlist.add(id);
		}
		return idlist.toString();
	}
	
	private void showDataMap() {
		int counter = 0;
		for(String tag : this.datamap.keySet()) {
			HashMap<String, NodeData> nodedatamap = this.datamap.get(tag);
			for(String value : nodedatamap.keySet()) {
				NodeData nodedata = nodedatamap.get(value);
				if(nodedata.getFrequency() > 2) {
					counter++;
					System.out.println("tag: " + nodedata.getTag());
					System.out.println("value: " + nodedata.getValue());
					System.out.println("freq: " + nodedata.getFrequency());
					System.out.println("nodelist: " + this.showNodeIDList(nodedata.getNodeList()));
				}
			}
		}
		System.out.println("================");
		System.out.println("column size: " + counter);
		System.out.println("================");
	}
	
	private void analyzeAverageDepthLevel(ParsingObject node, int depthlevel, int a[]) {
		if(node == null) return;
		if(node.size() == 0) a[depthlevel]++;
		for(int i = 0; i < node.size(); i++) {
			this.analyzeAverageDepthLevel(node.get(i), depthlevel+1, a);
		}
	}
	
	public int getTargetDepth(ParsingObject root) {
		int a[] = new int[16];
		this.analyzeAverageDepthLevel(root, 1, a);
		int max = 0;
		int maxindex = 0;
		for(int i = 0; i < a.length; i++) {
			System.out.println("a[" + i + "]: " + a[i]);
			if(max < a[i]) {
				max = a[i];
				maxindex = i;
			}
		}
		System.out.println("MaxIndex: " + maxindex + ", Max: " + a[maxindex]);
		return maxindex;
	}
	
	public void build(ParsingObject root) {
		int targetdepth = this.getTargetDepth(root);
		this.analyzeFrequency(root, 1);
		//this.showDataMap();
		System.out.println("-----------------------------------------");
	}
}

class NodeData {
	private String tag                = null;
	private String value              = null;
	private int freq                  = -1;
	ArrayList<ParsingObject> nodelist = null;
	
	public NodeData(String tag, String value, ParsingObject node) {
		this.tag   = tag;
		this.value = value;
		this.freq  = 1;
		this.nodelist = new ArrayList<ParsingObject>();
		this.nodelist.add(node);
	}
	public void increment() {
		this.freq++;
	}
	public void addnode(ParsingObject node) {
		this.nodelist.add(node);
	}
	public String getTag() {
		return this.tag;
	}
	public String getValue() {
		return this.value;
	}

	public int getFrequency() {
		return this.freq;
	}
	public ArrayList<ParsingObject> getNodeList() {
		return this.nodelist;
	}
}