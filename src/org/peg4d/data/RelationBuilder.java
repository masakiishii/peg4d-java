package org.peg4d.data;

import org.peg4d.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Set;

public class RelationBuilder {
	private ParsingObject root   = null;
	private int           rootid = -1;
	private ArrayList<SubNodeDataSet> allsubnodesetlist = null;
	public RelationBuilder(ParsingObject root) {
		this.root   = root;
		this.rootid = root.getObjectId();
		this.allsubnodesetlist = new ArrayList<SubNodeDataSet>();
	}
	
	public ArrayList<SubNodeDataSet> getSubNodeDataSetList() {
		return this.allsubnodesetlist;
	}
	public int getObjectId(ParsingObject node) {
		return node.getObjectId() - this.rootid;
	}
	
	public boolean isNumber(String value) {
		try {
		 Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	private void breadthFirstSearch(ParsingObject root) {
		if(root == null) return;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if(node.size() != 0 && node.get(0).size() == 0) {
				ParsingObject assumedtablenode = node.get(0);
				String value = assumedtablenode.getText();
				if(!this.isNumber(value)) {
					//System.out.println("id: " + this.getObjectId(node.get(0)) + ", " + value);
					SubNodeDataSet subnodeset
						= new SubNodeDataSet(this, node, value, this.getObjectId(assumedtablenode));
					subnodeset.buildAssumedColumnSet();
					this.allsubnodesetlist.add(subnodeset);
				}
			}
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
	}
	
	private void showSubNodeSet() {
		for(int i = 0; i < this.allsubnodesetlist.size(); i++) {
			SubNodeDataSet subnodedata = this.allsubnodesetlist.get(i);
			Set<String> subnodeset     = subnodedata.getAssumedColumnSet();
			System.out.println("tableName: " + subnodedata.getAssumedTableName());
			System.out.println("-----------------------------------------------");
			for(String element : subnodeset) {
				System.out.println(element);
			}
			System.out.println("\n");
		}
	}

	public void build() {
		this.breadthFirstSearch(root);
		//this.showSubNodeSet();
		CalcJaccardCoefficient jaccard = new CalcJaccardCoefficient(this);
		jaccard.calculating();
		SchemaMatcher schemamatcher = new SchemaMatcher(jaccard.getSchema());
		schemamatcher.matcher(root);
		
		System.out.println("----------------------------------------");
	}
}
