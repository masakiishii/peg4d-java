package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.peg4d.Main;
import org.peg4d.ParsingObject;

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

	private void recollectAllSubNode(ParsingObject root) {
		if(root == null) {
			return;
		}
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if (node.getTag().toString().equals("List")) {
				ParsingObject assumedtablenode = node.getParent().get(0);
				String tablename = assumedtablenode.getText();
				for (int i = 0; i < node.size(); i++) {
					SubNodeDataSet subnodeset = new SubNodeDataSet(this, node.get(i), tablename, this.getObjectId(assumedtablenode));
					subnodeset.buildAssumedColumnSet();
					if (subnodeset.getAssumedColumnSet().size() > 1) {
						this.allsubnodesetlist.add(subnodeset);
					}
				}
				continue;
			} else if (node.size() != 0 && node.get(0).size() == 0) {
				ParsingObject assumedtablenode = node.get(0);
				String value = assumedtablenode.getText();
				if(!this.isNumber(value)) {
					// Main.DebugPrint("id: " + this.getObjectId(node.get(0)) +
					// ", " + value);
					SubNodeDataSet subnodeset = new SubNodeDataSet(this, node, value, this.getObjectId(assumedtablenode));
					subnodeset.buildAssumedColumnSet();
					if (subnodeset.getAssumedColumnSet().size() > 1) {
						this.allsubnodesetlist.add(subnodeset);
					}
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
			Main.DebugPrint("tableName: " + subnodedata.getAssumedTableName());
			Main.DebugPrint("-----------------------------------------------");
			for(String element : subnodeset) {
				Main.DebugPrint(element);
			}
			Main.DebugPrint("\n");
		}
	}

	public void build() {
		DomainBuilder domainbuilder = new DomainBuilder();
		domainbuilder.build(this.root);
		this.recollectAllSubNode(this.root);
		// this.showSubNodeSet();
		NominateSchema preschema = new NominateSchema(this);
		preschema.nominating();
		DefineSchema defineschema = new DefineSchema(preschema, this.root);
		Map<String, SubNodeDataSet> definedschema = defineschema.define();
		SchemaMatcher schemamatcher = new SchemaMatcher(this, definedschema);
		schemamatcher.match(this.root);
		Main.DebugPrint("--------------------------------------------------");
	}
}
