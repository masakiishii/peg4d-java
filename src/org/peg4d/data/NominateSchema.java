package org.peg4d.data;

import org.peg4d.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NominateSchema {
	private RelationBuilder relationbuilder = null;
	private Map<String, SubNodeDataSet> schema = null;
	public NominateSchema(RelationBuilder relationbuilder) {
		this.relationbuilder = relationbuilder;
		this.schema = new HashMap<String, SubNodeDataSet>();
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
	
	private void nominateSchema(String tablename, SubNodeDataSet nodeX, SubNodeDataSet nodeY, double jaccardcoefficient) {
		if(this.schema.containsKey(tablename)) return;
		Set<String> setX = nodeX.getAssumedColumnSet();
		Set<String> setY = nodeY.getAssumedColumnSet();
		nodeX.setJaccardCoefficient(jaccardcoefficient);
		nodeY.setJaccardCoefficient(jaccardcoefficient);
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
		for(int i = 0; i < list.size(); i++) {
			for(int j = i + 1; j < list.size(); j++) {
				Set<String> setX = list.get(i).getAssumedColumnSet();
				Set<String> setY = list.get(j).getAssumedColumnSet();
				String setXname  = list.get(i).getAssumedTableName();
				String setYname  = list.get(j).getAssumedTableName();
				if(setX.size() > 0 && setY.size() > 0 && setXname.equals(setYname)) {
					double jaccardcoefficient = this.calculatiingJaccard(setX, setY);
					//if(jaccardcoefficient > 0.5) {
					//System.out.println(jaccardcoefficient);
					if(jaccardcoefficient > 0.5 && jaccardcoefficient <= 1.0) {
						this.nominateSchema(setXname, list.get(i), list.get(j), jaccardcoefficient);
					}
				}
			}
		}
	}
}
