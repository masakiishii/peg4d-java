package org.peg4d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;


public class DBBuilder {

	class TableData {
		private String tag   = null;
		private String value = null;
		private int ltpos    = -1;
		private int rtpos    = -1;
		private int depth    = -1;
		public TableData(ParsingObject node, int depth, int currentpos) {
			this.tag   = node.getTag().toString();
			this.value = node.size() == 0 ? node.getText() : null;
			this.ltpos = currentpos;
			this.depth = depth;
		}
		public void setRightPostion(int rtpos) {
			this.rtpos = rtpos;
		}
	}
	
	private int currentpos = -1;
	private LinkedList<TableData> datalist = null;
	public DBBuilder() {
		this.currentpos = 1;
		this.datalist   = new LinkedList<TableData>();
	}
	
	private void parseAST(ParsingObject node, int depth) {
		if(node == null) return;
		TableData tabledata = new TableData(node, depth, this.currentpos++);
		for(int index = 0; index < node.size(); index++) {
			this.parseAST(node.get(index), depth + 1);
		}
		tabledata.setRightPostion(this.currentpos++);
		this.datalist.add(tabledata);
	}
	private void showDataList() {
		System.out.println(" Tag  |  Value |  lt |  rt  |  depth");
		System.out.println("=====================================");
		for(int i = 0; i < this.datalist.size(); i++) {
			System.out.print(this.datalist.get(i).tag   + " | ");
			System.out.print(this.datalist.get(i).value + " | ");
			System.out.print(this.datalist.get(i).ltpos + " | ");
			System.out.print(this.datalist.get(i).rtpos + " | ");
			System.out.println(this.datalist.get(i).depth);
		}
	}
	
	public void build(ParsingObject root) {
		this.parseAST(root, 0);
		this.showDataList();
		System.out.println("----------------------------------");
	}
}
