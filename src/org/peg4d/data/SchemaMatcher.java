package org.peg4d.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.peg4d.*;

public class SchemaMatcher {
	private Map<String, SubNodeDataSet> schema = null;
	private Map<String, ArrayList<ArrayList<String>>> table = null;
	public SchemaMatcher(Map<String, SubNodeDataSet> schema) {
		this.schema = new HashMap<String, SubNodeDataSet>();
		this.schema = schema;
		this.initTable();
	}
	
	private void initTable() {
		this.table = new HashMap<String, ArrayList<ArrayList<String>>>();
		for(String column : this.schema.keySet()) {
			this.table.put(column, new ArrayList<ArrayList<String>>());
		}
	}
	
	private String getColumnData(ParsingObject subnode, ParsingObject tablenode, String column) {
		if(subnode == null) return null;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(subnode);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if(node.getText().toString().equals(column)) {
				ParsingObject parent = node.getParent();
				if(parent.size() == 2) {
					if(parent.get(1).size() == 0) {
						System.out.println("column: " + column);
						System.out.println("data:   " + node.getParent().get(1).getText().toString());
						return node.getParent().get(1).getText().toString();
					}
					else {
						StringBuffer sbuf = new StringBuffer();
						for(int i = 1; i < parent.size(); i++) {
							ParsingObject sibling = parent.get(i);
							sbuf.append(sibling.get(0).getText().toString());
							sbuf.append(":");
							sbuf.append(parent.getObjectId());
							if(i != parent.size() - 1) sbuf.append(",");
						}
						System.out.println("column: " + column);
						System.out.println("data:   " + sbuf.toString());
						return sbuf.toString();
					}
				}
				else {
					return null;
				}
			}
			for(int index = 0; index < node.size(); index++) {
				if(!node.equals(tablenode)) queue.offer(node.get(index));
			}
		}
		System.out.println("column: " + column);
		System.out.println("data:   " + null);
		return null;
	}
	
	private void getTupleData(ParsingObject subnode, ParsingObject tablenode, String tablename, SubNodeDataSet columns) {
		ArrayList<ArrayList<String>> tabledata = this.table.get(tablename);
		ArrayList<String> columndata = new ArrayList<String>();
		for(String column : columns.getAssumedColumnSet()) {
			String data = this.getColumnData(subnode, tablenode, column);
			columndata.add(data);
			System.out.println("---------------------------------------");
		}
		tabledata.add(columndata);
	}
	
	private boolean isTableName(String value) {
		return this.schema.containsKey(value) ? true : false;
	}
	
	private void matching(ParsingObject root) {
		if(root == null) return;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if(node.size() == 0 && this.isTableName(node.getText().toString())) {
				String tablename = node.getText().toString();
				this.getTupleData(node.getParent(), node, tablename, this.schema.get(tablename));
				return;
			}
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
	}
	
	private void showTable() {
		for(String tablename : this.schema.keySet()) {
			StringBuilder buf = new StringBuilder();
			buf.append("tablename: " + tablename + "\n");
			if(tablename.equals("item")) {
				System.out.println("break");
			}
			buf.append("-------------------------------------------\n");
			Set<String> set = this.schema.get(tablename).getAssumedColumnSet();
			for(String column : set) {
				buf.append(column + ",");
			}
			buf.append("\n");
			ArrayList<ArrayList<String>> data = this.table.get(tablename);
			for(int index = 0; index < data.size(); index++) {
				ArrayList<String> line = data.get(index);
				for(int j = 0; j < line.size(); j++) {
					buf.append(line.get(j) + ",");
				}
				buf.append("\n");
			}
			System.out.println(buf.toString());
		}
	}
	
	public void match(ParsingObject root) {
		this.matching(root);
		this.showTable();
	}
}
