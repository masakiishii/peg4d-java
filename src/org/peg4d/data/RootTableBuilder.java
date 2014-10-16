package org.peg4d.data;

import org.peg4d.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

public class RootTableBuilder {
	private RelationBuilder rbuilder   = null;
	private ArrayList<String> schema   = null;
	private Map<String, String> table = null;
	public RootTableBuilder(RelationBuilder rbuilder) {
		this.rbuilder  = rbuilder;
		this.schema    = new ArrayList<String>();
		this.table     = new LinkedHashMap<String, String>();
		this.initSchema();
	}
	
	private void initSchema() {
		this.schema.add("OBJECTID");
		this.schema.add("COLUMN");
		this.schema.add("VALUE");
	}
	
	private void setTableData(ParsingObject node) {
		ParsingObject parent = node.getParent();
		String key = String.valueOf(this.rbuilder.getObjectId(parent));
		String column = node.getText().toString();
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(column);
		sbuf.append(",");
		sbuf.append("[");
		for(int i = 1; i < parent.size(); i++) {
			ParsingObject sibling = parent.get(i);
			if(sibling.size() == 0) {
				sbuf.append(sibling.getText().toString());
				sibling.visited();
			}
			else if(sibling.getTag().toString().equals("List")) {
				for(int j = 0; j < sibling.size(); j++) {
					if(sibling.get(j).size() == 0) {
						sibling.get(j).visited();
						sbuf.append(sibling.get(j).getText().toString());
					}
					else {
						sbuf.append(sibling.get(j).getTag().toString());
						sbuf.append(":");
						sbuf.append(this.rbuilder.getObjectId(sibling.get(j)));
					}
					if(j != sibling.size() - 1) sbuf.append("|");
				}
			}
			else {
				ParsingObject grandchild = sibling.get(0);
				if(grandchild.size() == 0) {
					sbuf.append(grandchild.getText().toString());
				}
				else {
					sbuf.append(sibling.getTag().toString());
				}
				sbuf.append(":");
				sbuf.append(this.rbuilder.getObjectId(sibling));
			}
			if(i != parent.size() - 1) sbuf.append("|");
		}
		sbuf.append("],");
		this.table.put(key, sbuf.toString());
	}
	
	private void buildRootTable(ParsingObject node) {
		if(node == null) return;
		if(node.visitedNode()) {
			return;
		}
		if(node.size() == 0 && !node.visitedNode()) {
			this.setTableData(node);
		}
		for(int i = 0; i < node.size(); i++) {
			this.buildRootTable(node.get(i));
		}
	}
	
	private void generateRootColumns() {
		for(int i = 0; i < this.schema.size(); i++) {
			System.out.print(this.schema.get(i) + ",");
		}
		System.out.println();
	}
	
	public void build(ParsingObject node) {
		this.generateRootColumns();
		this.buildRootTable(node);
		for(String key : this.table.keySet()) {
			System.out.println(key + "," + this.table.get(key));
		}
		System.out.println("----------------------------------");
		System.out.println();
		System.out.println();
		System.out.println();
	}
}
