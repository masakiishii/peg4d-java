package org.peg4d.data;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.peg4d.ParsingObject;

public class SubNodeDataSet implements Comparator<SubNodeDataSet> {
	private RelationBuilder relationbuilder    = null;
	private ParsingObject   subNode            = null;
	private Point           subNodePoint       = null;
	private String          assumedTableName   = null;
	private Set<String>     assumedColumnSet   = null;
	private Set<String>     finalColumnSet     = null;
	private int             assumedTableNodeId = -1;
	private double Coefficient = -1;

	public SubNodeDataSet(RelationBuilder relationbuilder,
			ParsingObject subNode, String assumedTableName, int assumedTableId) {
		this.relationbuilder  = relationbuilder;
		this.subNode          = subNode;
		this.subNodePoint     = new Point(subNode.getLpos(), subNode.getRpos());
		this.assumedTableName = assumedTableName;
		this.assumedColumnSet = new LinkedHashSet<String>();
		this.finalColumnSet   = new LinkedHashSet<String>();
		this.assumedTableNodeId   = assumedTableId;
	}
	public SubNodeDataSet() {

	}

	@Override
	public int compare(SubNodeDataSet o1, SubNodeDataSet o2) {
		Point p1 = o1.getPoint();
		Point p2 = o2.getPoint();
		return p2.getRange() - p1.getRange();
	}

	public void buildAssumedColumnSet() {
		if(this.subNode == null) {
			return;
		}
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(this.subNode);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if(node.size() != 0 && node.get(0).size() == 0
					&& this.relationbuilder.getObjectId(node.get(0)) != this.assumedTableNodeId) {
				String value = node.get(0).getText();
				if(!this.relationbuilder.isNumber(value)) {
					this.assumedColumnSet.add(value);
				}
			}
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
	}

	public Point getPoint() {
		return this.subNodePoint;
	}
	public ParsingObject getSubNode() {
		return this.subNode;
	}
	public String getAssumedTableName() {
		return this.assumedTableName;
	}
	public Set<String> getAssumedColumnSet() {
		return this.assumedColumnSet;
	}

	public void setCoefficient(double coefficient) {
		this.Coefficient = coefficient;
	}

	public double getCoefficient() {
		return this.Coefficient;
	}
	public void setFinalColumnSet(String headcolumn) {
		this.finalColumnSet.add(headcolumn);
	}
	public void setFinalColumnSet(Set<String> set) {
		this.finalColumnSet.addAll(set);
	}
	public Set<String> getFinalColumnSet() {
		return this.finalColumnSet;
	}
}
