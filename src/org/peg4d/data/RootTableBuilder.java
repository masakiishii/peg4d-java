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
		this.schema.add("KEY");
		this.schema.add("VALUE");
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
		ParsingObject parent = node.getParent();
		String key = node.getText().toString();
		StringBuffer sbuf = new StringBuffer();
		sbuf.append("[");
		for(int i = 1; i < parent.size(); i++) {
			ParsingObject sibling = parent.get(i);
			if(sibling.size() == 0) {
				sbuf.append(sibling.getText().toString());
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
			if(i != parent.size() - 1) sbuf.append(",");
		}
		sbuf.append("]");
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
	}
}
