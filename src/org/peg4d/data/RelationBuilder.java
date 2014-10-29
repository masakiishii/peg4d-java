package org.peg4d.data;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.peg4d.Main;
import org.peg4d.ParsingObject;

public class RelationBuilder {
	private ParsingObject root   = null;
	private int segmentidpos = 0;
	private ArrayList<SubNodeDataSet> allsubnodesetlist = null;
	public RelationBuilder(ParsingObject root) {
		this.root   = root;
		this.segmentidpos++;
		this.allsubnodesetlist = new ArrayList<SubNodeDataSet>();
	}

	public ArrayList<SubNodeDataSet> getSubNodeDataSetList() {
		return this.allsubnodesetlist;
	}

	static public boolean isNumber(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// private void recollectAllSubNode(ParsingObject root) {
	// if (root == null) {
	// return;
	// }
	// Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
	// queue.offer(root);
	// while (!queue.isEmpty()) {
	// ParsingObject node = queue.poll();
	// if (node.getTag().toString().equals("List")) {
	// ParsingObject assumedtablenode = node.getParent().get(0);
	// String tablename = assumedtablenode.getText();
	// for (int i = 0; i < node.size(); i++) {
	// SubNodeDataSet subnodeset = new SubNodeDataSet(this,
	// node.get(i), tablename,
	// this.getObjectId(assumedtablenode));
	// subnodeset.buildAssumedColumnSet();
	// if (subnodeset.getAssumedColumnSet().size() > 1) {
	// // if (subnodeset.getAssumedColumnSet().size() > 0) {
	// this.allsubnodesetlist.add(subnodeset);
	// }
	// }
	// continue;
	// } else if (node.size() != 0 && node.get(0).size() == 0) {
	// ParsingObject assumedtablenode = node.get(0);
	// String value = assumedtablenode.getText();
	// if (!this.isNumber(value)) {
	// // Main.DebugPrint("id: " + this.getObjectId(node.get(0)) +
	// // ", " + value);
	// SubNodeDataSet subnodeset = new SubNodeDataSet(this, node,
	// value, this.getObjectId(assumedtablenode));
	// subnodeset.buildAssumedColumnSet();
	// if (subnodeset.getAssumedColumnSet().size() > 1) {
	// // if (subnodeset.getAssumedColumnSet().size() > 0) {
	// this.allsubnodesetlist.add(subnodeset);
	// }
	// }
	// }
	// for (int index = 0; index < node.size(); index++) {
	// queue.offer(node.get(index));
	// }
	// }
	// }

	private void collectAllSubNode(LappingObject node) {
		if (node == null) {
			return;
		}
		if (node.getTag().toString().equals("List")) {
			LappingObject assumedtablenode = node.getParent().get(0);
			String tablename = assumedtablenode.getText();
			for (int i = 0; i < node.size(); i++) {
				SubNodeDataSet subnodeset = new SubNodeDataSet(this,
						node.get(i), tablename, assumedtablenode.getObjectId());
				subnodeset.buildAssumedColumnSet();
				if (subnodeset.getAssumedColumnSet().size() > 1) {
					// if (subnodeset.getAssumedColumnSet().size() > 0) {
					this.allsubnodesetlist.add(subnodeset);
				}
			}
		} else if (node.size() != 0 && node.get(0).size() == 0) {
			LappingObject assumedtablenode = node.get(0);
			String value = assumedtablenode.getText();
			if (!this.isNumber(value)) {
				SubNodeDataSet subnodeset = new SubNodeDataSet(this, node,
						value, assumedtablenode.getObjectId());
				subnodeset.buildAssumedColumnSet();
				if (subnodeset.getAssumedColumnSet().size() > 1) {
					// if (subnodeset.getAssumedColumnSet().size() > 0) {
					this.allsubnodesetlist.add(subnodeset);
				}
			}
		}
		for (int i = 0; i < node.size(); i++) {
			this.collectAllSubNode(node.get(i));
		}
	}

	private void buildLappingTree(ParsingObject node, LappingObject lappingnode) {
		if (node == null) {
			return;
		}
		lappingnode.getCoord().setLpos(this.segmentidpos++);
		int size = node.size();
		if (size > 0) {
			LappingObject[] AST = new LappingObject[size];
			for (int i = 0; i < node.size(); i++) {
				AST[i] = new LappingObject(node.get(i));
				AST[i].setParent(lappingnode);
				this.buildLappingTree(node.get(i), AST[i]);
			}
			lappingnode.setAST(AST);
		}
		lappingnode.getCoord().setRpos(this.segmentidpos++);
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
		LappingObject lappingrootnode = new LappingObject(this.root);
		this.buildLappingTree(this.root, lappingrootnode);
		this.collectAllSubNode(lappingrootnode);
		// DomainBuilder domainbuilder = new DomainBuilder();
		// domainbuilder.build(this.root);
		// this.showSubNodeSet();
		NominateSchema preschema = new NominateSchema(this);
		preschema.nominating();
		DefineSchema defineschema = new DefineSchema(preschema, lappingrootnode);
		Map<String, SubNodeDataSet> definedschema = defineschema.define();
		SchemaMatcher schemamatcher = new SchemaMatcher(this, definedschema);
		schemamatcher.match(lappingrootnode);
		Main.DebugPrint("--------------------------------------------------");
	}
}
