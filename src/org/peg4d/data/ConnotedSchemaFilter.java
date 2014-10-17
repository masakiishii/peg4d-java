package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.peg4d.Main;
import org.peg4d.ParsingObject;

public class ConnotedSchemaFilter implements SchemaFilter {
	private final Map<String, SubNodeDataSet> nominatedSchema;
	private SchemaTypeChecker schematypechecker = null;
	private ParsingObject root                  = null;

	public ConnotedSchemaFilter(Map<String, SubNodeDataSet> schema, ParsingObject root) {
		this.nominatedSchema = schema;
		this.schematypechecker = new SchemaTypeChecker();
		this.root              = root;
	}

	private boolean isSubTree(SubNodeDataSet subnodedatasetX, SubNodeDataSet subnodedatasetY) {
		return this.schematypechecker.check(this.root, subnodedatasetX, subnodedatasetY);
	}

	private ArrayList<SubNodeDataSet> sortNominatedSchemaTable() {
		ArrayList<SubNodeDataSet> list = new ArrayList<SubNodeDataSet>();
		for (String tablename : this.nominatedSchema.keySet()) {
			SubNodeDataSet subnodedataset = this.nominatedSchema.get(tablename);
			list.add(subnodedataset);
		}
		list.sort(new SubNodeDataSet());
		return list;
	}

	private Map<String, SubNodeDataSet> buildMap(ArrayList<SubNodeDataSet> list) {
		Map<String, SubNodeDataSet> map = new LinkedHashMap<String, SubNodeDataSet>();
		for(int i = 0; i < list.size(); i++) {
			SubNodeDataSet set = list.get(i);
			String tablename   = set.getAssumedTableName();
			map.put(tablename, set);
		}
		for(String key : map.keySet()) {
			Main.DebugPrint("tablename: " + key);
			Main.DebugPrint("--------------------------------------------------");
			SubNodeDataSet subnodeset = map.get(key);
			Set<String> preset = subnodeset.getAssumedColumnSet();
			subnodeset.setFinalColumnSet("OBJECTID");
			subnodeset.setFinalColumnSet(preset);
			Set<String> postset = subnodeset.getFinalColumnSet();
			Main.DebugPrint("columns Set: " + postset);
			Main.DebugPrint("");
			Main.DebugPrint("");
		}
		return map;
	}

	private Map<String, SubNodeDataSet> defineSchema() {
		ArrayList<SubNodeDataSet> sortedschemalist = this.sortNominatedSchemaTable();
		for(int i = 0; i < sortedschemalist.size(); i++) {
			SubNodeDataSet subnodedatasetX = sortedschemalist.get(i);
			ParsingObject  subnodeX = sortedschemalist.get(i).getSubNode();
			for(int j = 0; j < sortedschemalist.size(); j++) {
				SubNodeDataSet subnodedatasetY = sortedschemalist.get(j);
				ParsingObject subnodeY = subnodedatasetY.getSubNode();
				String column = subnodedatasetY.getAssumedTableName();
				if(this.isSubTree(subnodedatasetX, subnodedatasetY)) {
					sortedschemalist.remove(subnodedatasetY);
					i = 0;
					j = 0;
				}
			}
		}
		return this.buildMap(sortedschemalist);
	}

	public Map<String, SubNodeDataSet> getSchema() {
		return this.defineSchema();
	}
}
