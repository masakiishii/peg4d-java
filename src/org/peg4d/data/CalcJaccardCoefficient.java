package org.peg4d.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CalcJaccardCoefficient {
	private RelationBuilder relationbuilder = null;
	private Map<String, Set<String>> schema = null;
	public CalcJaccardCoefficient(RelationBuilder relationbuilder) {
		this.relationbuilder = relationbuilder;
		this.schema = new HashMap<String, Set<String>>();
	}
	private Set<String> calcIntersection(Set<String> setX, Set<String> setY) {
		Set<String> intersection = new HashSet<String>(setX);
		intersection.retainAll(setY);
		return intersection;
	}
	
	private Set<String> calcUnion(Set<String> setX, Set<String> setY) {
		Set<String> union = new HashSet<String>(setX);
		union.addAll(setY);
		return union;
	}
	
	private double calculatiingJaccard(Set<String> setX, Set<String> setY) {
		Set<String> intersection  = this.calcIntersection(setX, setY);
		Set<String> union         = this.calcUnion(setX, setY);
		return (double)intersection.size() / union.size(); // jaccard coefficient
	}
	
	private void buildSchema(String tablename, Set<String> setX, Set<String> setY, double jaccardcoefficient) {
		if(this.schema.containsKey(tablename)) return;
		if(setX.size() > setY.size()) {
			this.schema.put(tablename, setX);
		}
		else {
			this.schema.put(tablename, setY);
		}
	}
	
	public Map<String, Set<String>> getSchema() {
		return this.schema;
	}
	
	public void calculating() {
		ArrayList<SubNodeDataSet> list = this.relationbuilder.getSubNodeDataSetList();
		for(int i = 0; i < list.size(); i++) {
			for(int j = i + 1; j < list.size(); j++) {
				Set<String> setX = list.get(i).getAssumedColumnSet();
				Set<String> setY = list.get(j).getAssumedColumnSet();
				String setXname  = list.get(i).getAssumedTableName();
				String setYname  = list.get(j).getAssumedTableName();
				if(setX.size() > 0 && setY.size() > 0 && setXname.equals(setYname)) {
					double jaccardcoefficient = this.calculatiingJaccard(setX, setY);
					if(jaccardcoefficient > 0.5) {
						this.buildSchema(setXname, setX, setY, jaccardcoefficient);
					}
				}
			}
		}
	}
}
