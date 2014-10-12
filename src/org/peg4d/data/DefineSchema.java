package org.peg4d.data;

import org.peg4d.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class DefineSchema {
	private NominateSchema nominatedschema      = null;
	private SchemaTypeChecker schematypechecker = null;
	private ParsingObject root                  = null;
	
	public DefineSchema(NominateSchema nominatedschema, ParsingObject root) {
		this.nominatedschema   = nominatedschema;
		this.schematypechecker = new SchemaTypeChecker();
		this.root              = root;
	}

	private boolean isSubTree(SubNodeDataSet subnodedatasetX, SubNodeDataSet subnodedatasetY) {
 		return this.schematypechecker.check(root, subnodedatasetX, subnodedatasetY);
	}
	
	private ArrayList<SubNodeDataSet> sortNominatedSchemaTable() {
		Map<String, SubNodeDataSet> schema = this.nominatedschema.getSchema();
		ArrayList<SubNodeDataSet> list = new ArrayList<SubNodeDataSet>();
		for(String tablename : schema.keySet()) {
			SubNodeDataSet subnodedataset = schema.get(tablename);
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
			System.out.println("tablename: " + key);
			System.out.println("--------------------------------------------------");
			SubNodeDataSet subnodeset = map.get(key);
			Set<String> preset = subnodeset.getAssumedColumnSet();
			subnodeset.setFinalColumnSet("OBJECTID");
			subnodeset.setFinalColumnSet(preset);
			Set<String> postset = subnodeset.getFinalColumnSet();
			System.out.println("columns Set: " + postset);
			System.out.println();
			System.out.println();
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
	
	public Map<String, SubNodeDataSet> define() {
		return this.defineSchema();
	}
}
