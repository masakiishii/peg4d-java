package org.peg4d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;


public class DBBuilder {
	class ColumnData {
		
		private String value         = null;
		private String tag           = null;
		private String fullpath      = null;
		private ParsingObject parent = null;
		private int parentorder      = -1;
		private int depthlevel       = -1;
		
		public ColumnData(ParsingObject node, int parentorder, int depthlevel, String fullpath) {
			this.value       = node.getText();
			this.tag         = node.getTag().toString();
			this.parent      = node.getParent();
			this.parentorder = parentorder;
			this.depthlevel  = depthlevel;
			this.fullpath    = fullpath;
		}
	}
	
	private Map<String, ArrayList<ColumnData>> mapdata = null;
	private LinkedList<String> pathstack               = null;

	public DBBuilder() {
		this.mapdata   = new HashMap<String, ArrayList<ColumnData>>();
		this.pathstack = new LinkedList<String>();
	}
	
	private String buildFullPath(LinkedList<String> stack) {
		StringBuilder fullpath = new StringBuilder();
		for(int i = stack.size() - 1; i >= 0; i--) {
			fullpath.append(stack.get(i) + "/");
		}
		return fullpath.toString();
	}
	
	private void buildMap(ParsingObject node, int parentorder, int depthlevel) {
		String nodetag  = node.getTag().toString();
		String fullpath = this.buildFullPath(this.pathstack);
		ColumnData columndata = new ColumnData(node, parentorder, depthlevel, fullpath);
		if(this.mapdata.containsKey(nodetag)) {
			this.mapdata.get(nodetag).add(columndata);
		}
		else {
			ArrayList<ColumnData> columndatalist = new ArrayList<ColumnData>();
			columndatalist.add(columndata);
			this.mapdata.put(nodetag, columndatalist);
		}	
	}
	
	private void parseAST(ParsingObject node, int parentorder, int depthlevel) {
		if(node == null) return;
		this.pathstack.push(node.getTag().toString());
		//this.showPathStack(this.pathstack, parentorder);
		for(int index = 0; index < node.size(); index++) {
			if(node.get(index).size() == 0) { 
				this.buildMap(node.get(index), parentorder, depthlevel);
			}
			else {
				this.parseAST(node.get(index), index, depthlevel + 1);
			}
		}
		this.pathstack.pop();
	}
	
	private void showPathStack(LinkedList<String> stack, int parentorder) {
		System.out.print("path: ");
		for(int i = stack.size() - 1; i >= 0; i--) {
			System.out.print(stack.get(i) + "/");
		}
		System.out.print(", parentorder: " + parentorder);
		System.out.println("");
	}

	private void showMap() {
		for(String key : this.mapdata.keySet()) {
			ArrayList<ColumnData> d = this.mapdata.get(key);
			int id = 0;
			System.out.println(" ID | #" + key + " |      fullpath      | parentorder");
			System.out.println("================================================");
			for(int i = 0; i < d.size(); i++) {
				id = i + 1;
				System.out.print(" " + id + "  | ");
				System.out.print(" " + d.get(i).value + " | ");
				System.out.print(" " + d.get(i).fullpath + " | ");
				System.out.println(" " + d.get(i).parentorder + " | ");
			}
		}
	}
	
	public void build(ParsingObject root) {
		this.parseAST(root, 0, 0);
		showMap();
		System.out.println("----------------------------------");
	}
}
