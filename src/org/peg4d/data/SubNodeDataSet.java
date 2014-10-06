package org.peg4d.data;

import org.peg4d.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SubNodeDataSet {
	private RelationBuilder relationbuilder   = null;
	private ParsingObject   subNode           = null;
	private String          assumedTableName  = null;
	private Set<String>     assumedColumnSet  = null;
	public SubNodeDataSet(RelationBuilder relationbuilder, 
				ParsingObject subNode, String assumedTableName) {
		this.relationbuilder  = relationbuilder;
		this.subNode          = subNode;
		this.assumedTableName = assumedTableName;
		this.assumedColumnSet = new HashSet<String>();
	}
	
	public void buildAssumedColumnSet() {
		if(this.subNode == null) return;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(this.subNode);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if(node.size() != 0 && node.get(0).size() == 0) {
				String value = node.get(0).getText();
				if(!this.relationbuilder.isNumber(value)) {
					this.assumedColumnSet.add(value);
				}
			}
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
		return;
	}
	
	public String getAssumedTableName() {
		return this.assumedTableName;
	}
	public Set<String> getAssumedColumnSet() {
		return this.assumedColumnSet;
	}
}
