package org.peg4d.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.peg4d.*;

public class SchemaMatcher {
	private RelationBuilder                           rbuilder  = null;
	private Map<String, SubNodeDataSet>               schema    = null;
	private Map<String, ArrayList<ArrayList<String>>> table     = null;
	private GenerateCSV                               generator = null;
	private RootTableBuilder                          builder   = null;
	public SchemaMatcher(RelationBuilder rbuilder, Map<String, SubNodeDataSet> schema) {
		this.rbuilder = rbuilder;
		this.schema = new HashMap<String, SubNodeDataSet>();
		this.schema = schema;
		this.initTable();
		this.generator = new GenerateCSV();
		this.builder   = new RootTableBuilder(rbuilder);
	}
	
	private void initTable() {
		this.table = new HashMap<String, ArrayList<ArrayList<String>>>();
		for(String column : this.schema.keySet()) {
			this.table.put(column, new ArrayList<ArrayList<String>>());
		}
	}
	
	public Map<String, ArrayList<ArrayList<String>>> getTable() {
		return this.table;
	}
	public Map<String, SubNodeDataSet> getSchema() {
		return this.schema;
	}
	
	private String getColumnData(ParsingObject subnode, ParsingObject tablenode, String column) {
		if(subnode == null) return null;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(subnode);
		StringBuffer sbuf = new StringBuffer();
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if(node.getText().toString().equals(column)) {
				node.visited();
				ParsingObject parent = node.getParent();
				for(int i = 1; i < parent.size(); i++) {
					ParsingObject sibling = parent.get(i);
					sibling.visited();
					String linefeed = System.getProperty("line.separator");
					if(sibling.size() == 0) {
						String data = sibling.getText().toString();
						if(data.length() > 128) {
							sbuf.append("{too long text}");
						}
						else {
							sbuf.append(sibling.getText().toString().replaceAll(linefeed, "").replaceAll("  ", ""));
						}
					}
					else {
						if(sibling.getTag().toString().equals("List")) { // FIXME
							for(int j = 0; j < sibling.size(); j++) {
								sibling.get(j).visited();
								sbuf.append(sibling.get(j).getText().toString());
								if(j != sibling.size() - 1) sbuf.append(",");
							}
						}
						else {
							sibling.get(0).visited();
							sbuf.append(sibling.get(0).getText().toString().replaceAll(linefeed, "").replaceAll("  ", ""));
							sbuf.append(":");
							sbuf.append(this.rbuilder.getObjectId(sibling));
						}
					}
					sbuf.append(",");
				}				
			}
			for(int index = 0; index < node.size(); index++) {
				if(!node.equals(tablenode)) queue.offer(node.get(index));
			}
		}
		if(sbuf.length() > 0) {
			System.out.println("column: " + column);
			System.out.println("data:   " + sbuf.toString());
			return "[" + sbuf.toString() + "]";
		}
		else {
			System.out.println("column: " + column);
			System.out.println("data:   " + null);
			return null;
		}
	}
	
	private void getTupleData(ParsingObject subnode, ParsingObject tablenode, String tablename, SubNodeDataSet columns) {
		ArrayList<ArrayList<String>> tabledata = this.table.get(tablename);
		ArrayList<String> columndata = new ArrayList<String>();
		for(String column : columns.getFinalColumnSet()) {
			if(column.equals("OBJECTID")) {
				columndata.add(String.valueOf(this.rbuilder.getObjectId(subnode)));
				continue;
			}
			else {
				System.out.println("start [" + column + "] matching・・・・・");
				System.out.println("=======================================");
				String data = this.getColumnData(subnode, tablenode, column);
				columndata.add(data);
				System.out.println("---------------------------------------");
				System.out.println();
				System.out.println();
			}
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
			ParsingObject parent = queue.poll();
			if(parent.size() == 0) continue;
			ParsingObject child  = parent.get(0);
			if(child.size() == 0 && this.isTableName(child.getText().toString())) {
				child.visited();
				String tablename = child.getText().toString();
				this.getTupleData(parent, child, tablename, this.schema.get(tablename));
				parent.visited();
				continue;
			}
			for(int index = 0; index < parent.size(); index++) {
				queue.offer(parent.get(index));
			}
		}
	}
	
	public void match(ParsingObject root) {
		this.matching(root);
		this.builder.build(root);
		this.generator.generateCSV(this);
	}
}
