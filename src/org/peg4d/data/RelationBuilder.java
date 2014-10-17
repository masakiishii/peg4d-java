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
	private static int rootid = -1;
	private ArrayList<SubNodeDataSet> allsubnodesetlist = null;
	public RelationBuilder(ParsingObject root) {
		this.root   = root;
		RelationBuilder.rootid = root.getObjectId();
		this.allsubnodesetlist = new ArrayList<SubNodeDataSet>();
	}

	public ArrayList<SubNodeDataSet> getSubNodeDataSetList() {
		return this.allsubnodesetlist;
	}

	public static int getObjectId(ParsingObject node) {
		return node.getObjectId() - RelationBuilder.rootid;
	}

	public static boolean isNumber(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	class Worklist<T1, T2> {
		T2 argumen;

		Worklist(T2 argument) {
			this.argument = this.argument;
		}
		void apply(T1 object) {
		}
	}

	private void collectAllSubNode(ParsingObject root) {
		if(root == null) {
			return;
		}
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if(node.size() != 0 && node.get(0).size() == 0) {
				ParsingObject assumedtablenode = node.get(0);
				String value = assumedtablenode.getText();
				if (!isNumber(value)) {
					Main.DebugPrint("id: "
							+ RelationBuilder.getObjectId(node.get(0)) + ", "
							+ value);
					SubNodeDataSet subnodeset = new SubNodeDataSet(node, value,
							RelationBuilder.getObjectId(assumedtablenode));
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
			Main.DebugPrint("tableName: " + subnodedata.getAssumedTableName());
			Main.DebugPrint("-----------------------------------------------");
			for(String element : subnodeset) {
				Main.DebugPrint(element);
			}
			Main.DebugPrint("\n");
		}
	}

	public void build() {
		SegmentTreeBuilder stbuilder = new SegmentTreeBuilder();
		stbuilder.build(this.root);
		this.collectAllSubNode(this.root);
		this.showSubNodeSet();
		NominatedSchemaSelector nselector = new NominatedSchemaSelector(this);
		Map<String, SubNodeDataSet> nominated = nselector.getSchema();
		ConnotedSchemaFilter csfilter = new ConnotedSchemaFilter(nominated, this.root);
		Map<String, SubNodeDataSet> definedschema = csfilter.getSchema();
		SchemaMatcher schemamatcher = new SchemaMatcher(this, definedschema);
		schemamatcher.match(this.root);
		Main.DebugPrint("--------------------------------------------------");
	}
}
