package org.peg4d.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.peg4d.*;

public class SchemaMatcher {
	private Map<String, Set<String>> schema = null;
	private Map<String, ArrayList<ArrayList<String>>> table = null;
	public SchemaMatcher(Map<String, Set<String>> schema) {
		this.schema = new HashMap<String, Set<String>>();
		this.schema = schema;
		this.initTable();
	}
	
	private void initTable() {
		this.table = new HashMap<String, ArrayList<ArrayList<String>>>();
		for(String column : this.schema.keySet()) {
			this.table.put(column, new ArrayList<ArrayList<String>>());
		}
	}
	
	private String getColumnData(ParsingObject subnode, String column) {
		if(subnode == null) return null;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(subnode);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if(node.getText().toString().equals(column)) {
				System.out.println("column: " + column);
				System.out.println("data:   " + node.getParent().get(1).getText().toString());
				return node.getParent().get(1).getText().toString();
			}
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
		System.out.println("column: " + column);
		System.out.println("data:   " + null);
		return null;
	}
	
	private void getTupleData(ParsingObject node, String tablename, Set<String> columns) {
		ArrayList<ArrayList<String>> tabledata = this.table.get(tablename);
		ArrayList<String> columndata = new ArrayList<String>();
		for(String column : columns) {
			String data = this.getColumnData(node, column);
			columndata.add(data);
			System.out.println("---------------------------------------");
		}
		tabledata.add(columndata);
	}
	
	private boolean isTableName(String value) {
		return this.schema.containsKey(value) ? true : false;
	}
	
	private void matching(ParsingObject node) {
		if(node == null) return;
		if(node.size() == 0 && this.isTableName(node.getText().toString())) {
			String tablename = node.getText().toString();
			this.getTupleData(node.getParent().get(1), tablename, this.schema.get(tablename));
		}
		for(int i = 0; i < node.size(); i++) {
			this.matching(node.get(i));
		}
	}
	
	private void showTable() {
		for(String tablename : this.schema.keySet()) {
			StringBuilder buf = new StringBuilder();
			buf.append("tablename: " + tablename + "\n");
			buf.append("-------------------------------------------\n");
			Set<String> set = this.schema.get(tablename);
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
	
	public void matcher(ParsingObject root) {
		this.matching(root);
		this.showTable();
	}
}
