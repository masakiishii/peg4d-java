package org.peg4d.data;

import org.peg4d.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class DefineSchema {
	private NominateSchema nominatedschema   = null;
	private ArrayList<SubNodeDataSet> buffer = null;
	
	public DefineSchema(NominateSchema nominatedschema) {
		this.nominatedschema = nominatedschema;
		this.buffer = new ArrayList<SubNodeDataSet>();
	}

	private boolean containsColumn(ParsingObject subnode, String column) {
		if(subnode == null) return false;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(subnode);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if(node.size() == 0 && node.getText().toString().equals(column)) return true;
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
		return false;
	}
	
	private void showNominatedSchemaTable() {
		Map<String, SubNodeDataSet> schema = this.nominatedschema.getSchema();
		for(String tablename : schema.keySet()) {
			SubNodeDataSet subnodedataset = schema.get(tablename);
			this.buffer.add(subnodedataset);
			ParsingObject subnode = subnodedataset.getSubNode();
			System.out.println(tablename + ": " + (subnode.getRpos() - subnode.getLpos()));
			//System.out.println(tablename + ": " + (subnode.getRpos() - subnode.getLpos()));
			//System.out.println("lpos: " + subnode.getLpos() + ", rpos: " + subnode.getRpos());
			System.out.println("---------------------------------------");
			System.out.println();
		}
		
	}
	
	private boolean isSubTree(SubNodeDataSet subnodedatasetX, SubNodeDataSet subnodedatasetY) {
 		Set<String> setX = subnodedatasetX.getAssumedColumnSet();
 		Set<String> setY = subnodedatasetY.getAssumedColumnSet();
 		System.out.println("nodex tablename: " + subnodedatasetX.getAssumedTableName());
 		System.out.println("---------------------------------------------------------");
 		System.out.println("setx: " + setX);
 		System.out.println();
 		System.out.println("nodey tablename: " + subnodedatasetY.getAssumedTableName());
 		System.out.println("---------------------------------------------------------");
 		System.out.println("sety: " + setY);
 		System.out.println();
 		
 		return setX.contains(setY) ? true: false;
	}
	
	private void defineSchema() {
		ArrayList<SubNodeDataSet> removelist = new ArrayList<SubNodeDataSet>();
		for(int i = 0; i < this.buffer.size(); i++) {
			SubNodeDataSet subnodedatasetX = this.buffer.get(i);
			ParsingObject subnodeX = this.buffer.get(i).getSubNode();
			for(int j = 0; j < this.buffer.size(); j++) {
				if(i == j) continue;
				SubNodeDataSet subnodedatasetY = this.buffer.get(j);
				ParsingObject subnodeY = subnodedatasetY.getSubNode();
				String column = subnodedatasetY.getAssumedTableName();
				if(this.containsColumn(subnodeX, column) && this.isSubTree(subnodedatasetX, subnodedatasetY)) {
					removelist.add(subnodedatasetY);
				}
			}
		}
		for(int i = 0; i < removelist.size(); i++) {
			System.out.println(removelist.get(i).getAssumedTableName());
		}
	}
	
	public void define() {
		this.showNominatedSchemaTable();
		this.defineSchema();
	}
}
