package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.peg4d.Main;

public class NominateSchema {
	private RelationBuilder relationbuilder = null;
	private Map<String, SubNodeDataSet> schema = null;
	public NominateSchema(RelationBuilder relationbuilder) {
		this.relationbuilder = relationbuilder;
		this.schema = new LinkedHashMap<String, SubNodeDataSet>();
	}

	private Set<String> calcIntersection(Set<String> setX, Set<String> setY) {
		Set<String> intersection = new LinkedHashSet<String>(setX);
		intersection.retainAll(setY);
		return intersection;
	}

	private Set<String> calcUnion(Set<String> setX, Set<String> setY) {
		Set<String> union = new LinkedHashSet<String>(setX);
		union.addAll(setY);
		return union;
	}

	private double calculatiingJaccard(Set<String> setX, Set<String> setY) {
		Set<String> intersection  = this.calcIntersection(setX, setY);
		Set<String> union         = this.calcUnion(setX, setY);
		return (double)intersection.size() / union.size(); // jaccard coefficient
	}

	private void nominateSchema(String tablename, SubNodeDataSet nodeX, SubNodeDataSet nodeY, double jaccardcoefficient) {
		Set<String> setX = nodeX.getAssumedColumnSet();
		Set<String> setY = nodeY.getAssumedColumnSet();
		nodeX.setJaccardCoefficient(jaccardcoefficient);
		nodeY.setJaccardCoefficient(jaccardcoefficient);
		if(this.schema.containsKey(tablename)) {
			this.schema.get(tablename).getAssumedColumnSet().addAll(setX);
			this.schema.get(tablename).getAssumedColumnSet().addAll(setY);
			return;
		}
		if(setX.size() > setY.size()) {
			this.schema.put(tablename, nodeX);
		}
		else {
			this.schema.put(tablename, nodeY);
		}
	}

	public Map<String, SubNodeDataSet> getSchema() {
		return this.schema;
	}

	public void nominating() {
		ArrayList<SubNodeDataSet> list = this.relationbuilder.getSubNodeDataSetList();
		list.sort(new SubNodeDataSet());
		for(int i = 0; i < list.size(); i++) {
			for(int j = i + 1; j < list.size(); j++) {
				Set<String> setX = list.get(i).getAssumedColumnSet();
				Set<String> setY = list.get(j).getAssumedColumnSet();
				String setXname  = list.get(i).getAssumedTableName();
				String setYname  = list.get(j).getAssumedTableName();
				if(setX.size() > 0 && setY.size() > 0 && setXname.equals(setYname)) {
					Main.DebugPrint(setX);
					Main.DebugPrint(setY);
					double jaccardcoefficient = this.calculatiingJaccard(setX, setY);
					if(jaccardcoefficient > 0.5 && jaccardcoefficient <= 1.0) {
						this.nominateSchema(setXname, list.get(i), list.get(j), jaccardcoefficient);
					}
				}
			}
		}
	}
}
