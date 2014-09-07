package org.peg4d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DBBuilder {
	class ColumnData {
		
		private String value         = null;
		private String tag           = null;
		private ParsingObject parent = null;
		private int parentorder      = -1;
		private int depthlevel       = -1;
		private ArrayList<String> fullpath = null;

		
		public ColumnData(ParsingObject node) {
			this.value       = node.getText();
			this.tag         = node.getTag().toString();
			this.parent      = node.getParent();
			this.parentorder = -1;  // TODO
			this.depthlevel  = -1;  // TODO
			this.fullpath = new ArrayList<String>(); // TODO using stack
		}
	}
	private Map<String, ArrayList<ColumnData>> mapdata;

	public DBBuilder() {
		this.mapdata = new HashMap<String, ArrayList<ColumnData>>();
	}
	
	private void buildMap(ParsingObject node) {
		ColumnData columndata = new ColumnData(node);
		String nodetag = node.getTag().toString();
		if(this.mapdata.containsKey(nodetag)) {
			this.mapdata.get(nodetag).add(columndata);
		}
		else {
			ArrayList<ColumnData> columndatalist = new ArrayList<ColumnData>();
			columndatalist.add(columndata);
			this.mapdata.put(nodetag, columndatalist);
		}
	}
	
	private void parseAST(ParsingObject node) {
		if(node == null) return;

		if(node.size() == 0) this.buildMap(node);
		
		for(int index = 0; index < node.size(); index++) {
			this.parseAST(node.get(index));
		}
	}
	
	private void showMap() {
		for(String key : this.mapdata.keySet()) {
			ArrayList<ColumnData> d = this.mapdata.get(key);
			System.out.println("key symbol: " + key + ", list size : " + d.size());
			System.out.println("=============================================");
			for(int i = 0; i < d.size(); i++) {
				System.out.println(d.get(i).value);
			}
		}
	}
	
	public void build(ParsingObject root) {
		this.parseAST(root);
		showMap();
		System.out.println("----------------------------------");
	}
}
