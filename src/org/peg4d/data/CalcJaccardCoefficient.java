package org.peg4d.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CalcJaccardCoefficient {
	private RelationBuilder relationbuilder = null;
	public CalcJaccardCoefficient(RelationBuilder relationbuilder) {
		this.relationbuilder = relationbuilder;
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
	
	public void calculating() {
		ArrayList<SubNodeDataSet> list = this.relationbuilder.getSubNodeDataSetList();
		for(int i = 0; i < list.size(); i++) {
			for(int j = i + 1; j < list.size(); j++) {
				Set<String> setX = list.get(i).getAssumedColumnSet();
				Set<String> setY = list.get(j).getAssumedColumnSet();
				String setXname  = list.get(i).getAssumedTableName();
				String setYname  = list.get(j).getAssumedTableName();
				if(setX.size() > 0 && setY.size() > 0) {
					double jaccardcoefficient = this.calculatiingJaccard(setX, setY);
					if(jaccardcoefficient > 0.0) {
						System.out.println("SetX: " + setXname + ", SetY: " + setYname);
						System.out.println("jaccard Coefficient: " + jaccardcoefficient);
						System.out.println("------------------------------------------");
					}
				}
			}
		}
	}
}
