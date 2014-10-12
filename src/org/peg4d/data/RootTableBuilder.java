package org.peg4d.data;

import org.peg4d.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class RootTableBuilder {
	private ArrayList<String> schema   = null;
	private Map<String, ArrayList<ArrayList<String>>> table = null;
	private Stack<String> pathstack    = null;
	private ArrayList<Integer> posid   = null;
	private Map<String, ArrayList<String>> datalist = null;
	public RootTableBuilder() {
		this.schema    = new ArrayList<String>();
		this.initSchema();
		this.initTable();
		this.pathstack = new Stack<String>();
		this.posid     = new ArrayList<Integer>();
		this.datalist  = new HashMap<String, ArrayList<String>>();
	}
	
	private void initSchema() {
		this.schema.add("PATH");
		this.schema.add("VALUE");
	}
	private void initTable() {
		this.table = new HashMap<String, ArrayList<ArrayList<String>>>();
		for(int i = 0; i < this.schema.size(); i++) {
			this.table.put(this.schema.get(i), new ArrayList<ArrayList<String>>());
		}
	}
	
	private void setNodeData(ParsingObject node) {
		String data = node.get(0).getText().toString();
		int    oid  = node.get(0).getObjectId();
		ParsingObject parent = node.getParent();
		ArrayList<String> datalist = this.datalist.get(parent.get(0).getText().toString());
		datalist.add(data + ":" + oid);
	}
	
	private String interpolateSlash(Stack<String> pathstack) {
		StringBuffer sbuf = new StringBuffer();
		for(int i = 0; i < pathstack.size(); i++) {
			sbuf.append("/");
			sbuf.append(pathstack.elementAt(i));
		}
		return sbuf.toString();
	}
	
	private void setTableData(ParsingObject node) {
		System.out.println(this.interpolateSlash(this.pathstack));
		String top = this.pathstack.pop();
		ArrayList<String> datalist = this.datalist.get(top);
		System.out.println(datalist);
		datalist.clear();
		if(this.pathstack.size() > 0) {
			String parent = this.pathstack.peek();
			ArrayList<String> parentdatalist = this.datalist.get(parent);
			parentdatalist.add(top + ":" + node.getObjectId());
		}
	}
	
	private void buildRootTable(ParsingObject node, int depthlevel) {
		if(node == null) return;
		if(node.visitedNode()) {
			this.setNodeData(node);
			return;
		}
		if(node.size() == 0 && !node.visitedNode()) {
			String pathvalue = node.getText().toString();
			ArrayList<String> list = new ArrayList<String>();
			this.pathstack.push(pathvalue);
			this.posid.add(node.getParent().getObjectId());
			this.datalist.put(pathvalue, list);
		}
		for(int i = 0; i < node.size(); i++) {
			this.buildRootTable(node.get(i), depthlevel + 1);
		}
		if(this.posid.contains(node.getObjectId())) {
			this.setTableData(node);
		}
	}
	
	public void build(ParsingObject node) {
		int depthlevel = 0;
		this.buildRootTable(node, depthlevel);
	}
}
