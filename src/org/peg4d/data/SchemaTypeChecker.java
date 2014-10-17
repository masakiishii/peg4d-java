package org.peg4d.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.peg4d.ParsingObject;

public class SchemaTypeChecker {
	public SchemaTypeChecker() {
	}

	private ArrayList<Point> getParsingObjectDomainList(ParsingObject root, SubNodeDataSet dataSet) {
		if(root == null) {
			return null;
		}
		ArrayList<Point> domainlist = new ArrayList<Point>();
		String tablename = dataSet.getAssumedTableName();
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if(node.size() == 0 && node.getText().toString().equals(tablename)) {
				ParsingObject target = node.getParent();
				Point domain = new Point(target.getLpos(), target.getRpos());
				domainlist.add(domain);
			}
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
		return domainlist;
	}

	public boolean checkDataSetContainsDomain(SubNodeDataSet dataSet, ArrayList<Point> list) {
		for(int i = 0; i < list.size(); i++) {
			Point p = list.get(i);
			if (dataSet.contains(p)) {
				return true;
			}
		}
		return false;
	}

	public boolean check(ParsingObject root, SubNodeDataSet subnodedatasetX, SubNodeDataSet subnodedatasetY) {
		ArrayList<Point> list = this.getParsingObjectDomainList(root, subnodedatasetX);
		return this.checkDataSetContainsDomain(subnodedatasetY, list);
	}
}
