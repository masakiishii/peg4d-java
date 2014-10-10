package org.peg4d.data;

import org.peg4d.*;

import java.util.ArrayList;
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
	
	private void defineSchema() {
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
	}
	
	public void define() {
		this.defineSchema();
	}
}
