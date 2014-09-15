package org.peg4d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Queue;
import java.sql.*;

public class DBBuilder {
	
	private HashMap<String, HashMap<String, NodeData>> datamap = null;
	private ArrayList<ParsingObject> targetlist  = null;
	private ArrayList<String>        columnfield = null;
	
	public DBBuilder() {
		datamap     = new HashMap<String, HashMap<String, NodeData>>();
		targetlist  = new ArrayList<ParsingObject>();
		columnfield = new ArrayList<String>();
	}
	
	private void buildDataMap(ParsingObject node) {
		String tag  = node.getTag().toString();
		String key  = node.getText();
		if(!this.datamap.containsKey(tag)) {
			HashMap<String, NodeData> ndm = new HashMap<String, NodeData>();
			NodeData nodedata = new NodeData(tag, key, node);
			ndm.put(key, nodedata);
			this.datamap.put(tag, ndm);
		}
		else {
			HashMap<String, NodeData> ndm = this.datamap.get(tag);
			if(!ndm.containsKey(key)) {
				NodeData nodedata = new NodeData(tag, key, node);
				ndm.put(key, nodedata);
			}
			else {
				NodeData nodedata = ndm.get(key);
				nodedata.increment();
				nodedata.addnode(node);
			}
		}
	}
	
	private void analyzeFrequency(ParsingObject root) {
		if(root == null) return;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			if(node.size() == 0) this.buildDataMap(node);
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
	}
	
	private String showNodeIDList(ArrayList<ParsingObject> list) {
		ArrayList<Integer> idlist = new ArrayList<Integer>();
		for(int i = 0; i < list.size(); i++) {
			int id = list.get(i).getId();
			idlist.add(id);
		}
		return idlist.toString();
	}
	
	private int culcFrequencyAverage() {
		int counter = 0, sum = 0;
		int a[] = new int[100000000];
		for(String tag : this.datamap.keySet()) {
			HashMap<String, NodeData> nodedatamap = this.datamap.get(tag);
			for(String value : nodedatamap.keySet()) {
				NodeData nodedata = nodedatamap.get(value);
				a[counter] = nodedata.getFrequency();
				counter++;
				sum += nodedata.getFrequency();
			}
		}
//		for(int i = 0; i < a.length; i++) {
//			if(a[i] != 0) {
//				System.out.println("a[" + "]: " + a[i]);
//			}
//		}
		return sum / counter;
//		return 100;
	}
	private boolean isNumber(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	private boolean isKeyword(String str) {
		switch(str) {
		case "null": case "from": case "to":
			return true;
		}
		return false;
	}
	
	private void nominateColumnField() {
		int average = this.culcFrequencyAverage();
		for(String tag : this.datamap.keySet()) {
			HashMap<String, NodeData> nodedatamap = this.datamap.get(tag);
			for(String value : nodedatamap.keySet()) {
				NodeData nodedata = nodedatamap.get(value);
				if(nodedata.getFrequency() > average) {
					String nodevalue = nodedata.getValue();
					if(!this.isNumber(nodevalue) && !this.isKeyword(nodevalue)) {
						ArrayList<String> data = new ArrayList<String>();
						this.columnfield.add(nodevalue);
					}
				}
			}
		}
	}
	
	private void analyzeAverageDepthLevel(ParsingObject node, int depthlevel, int a[]) {
		if(node == null) return;
		if(node.size() == 0) a[depthlevel]++;
		for(int i = 0; i < node.size(); i++) {
			this.analyzeAverageDepthLevel(node.get(i), depthlevel+1, a);
		}
	}
	
	private int getTargetDepth(ParsingObject root) {
		int a[] = new int[16];
		this.analyzeAverageDepthLevel(root, 1, a);
		int max      = 0;
		int maxindex = 0;
		int sum      = 0;
		for(int i = 0; i < a.length; i++) {
			sum += a[i];
			if(max < a[i]) {
				max = a[i];
				maxindex = i;
			}
		}
		return maxindex;
	}
	
	private void getTargetDepthNode(ParsingObject node, int depth, int target) {
		if(node == null) return;
		if(depth == target) this.targetlist.add(node);
		for(int i = 0; i < node.size(); i++) {
			this.getTargetDepthNode(node.get(i), depth + 1, target);
		}
	}
	private void numberingNodeID(ParsingObject root, int id) {
		if(root == null) return;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			node.setId(id++);
			for(int index = 0; index < node.size(); index++) {
				queue.offer(node.get(index));
			}
		}
	}

	private void generateSchemaSQL() {
		String msg = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			msg = "Success!!";
			Connection con  = DriverManager.getConnection( "jdbc:mysql://localhost:3306/peg4dDB", "masaki","masaki");
			Statement  stmt = con.createStatement();
			String tablename = "Json_Table";
//			String tablename = "XML_Table";
			String sql   = "CREATE TABLE " + tablename + "(";
			String field = "";
			for(int i = 0; i < this.columnfield.size(); i++) {
				field += this.columnfield.get(i) + " VARCHAR(128)";
				if(i != this.columnfield.size() - 1) {
					field += ", ";
				}
			}
			sql += field + ");";
			System.out.println(sql);
			sql.replace("$", "\\$");
			stmt.execute(sql);
			stmt.close();
		} catch (ClassNotFoundException e){
			msg = "Fail!!";
		} catch (Exception e){
			System.out.println("Exception：" + e);
		}
		System.out.println(msg);
	}
	private void generateInsertSQL(ParsingObject node, HashMap<String, ArrayList<String>> map) {
		if(node == null) return;
		if(node.size() == 0 && this.columnfield.contains(node.getText())) {
			ParsingObject parent   = node.getParent();
			ArrayList<String> data = map.get(node.getText());
			for(int i = 0; i < parent.size(); i++) {
				ParsingObject cur = parent.get(i);
				if(cur.equals(node)) continue;
				if(cur.size() == 0) data.add(cur.getText());
			}
		}
		for(int i = 0; i < node.size(); i++) {
			generateInsertSQL(node.get(i), map);
		}
	}

	private LinkedHashMap<String, ArrayList<String>> initFieldData() {
		LinkedHashMap<String, ArrayList<String>> map = new LinkedHashMap<String, ArrayList<String>>();
		for(int i = 0; i < this.columnfield.size(); i++) {
			String field = this.columnfield.get(i);
			ArrayList<String> data = new ArrayList<String>();
			map.put(field, data);
		}
		return map;
	}
	
	private String concatList(ArrayList<String> value) {
		String ret = "";
		for(int i = 0; i < value.size(); i++) {
			ret += value.get(i);
			if(i != value.size() - 1) ret += ",";
		}
		return ret;
	}
	
	private void executeInsertSQLStmt(String sql) {
		String msg = "";
		try {
			msg = "Success!!";
			Class.forName("com.mysql.jdbc.Driver");
			Connection con  = DriverManager.getConnection( "jdbc:mysql://localhost:3306/peg4dDB", "masaki","masaki");
			Statement  stmt = con.createStatement();
			stmt.execute(sql);
			stmt.close();
		} catch (ClassNotFoundException e){
			msg = "Fail!!";
		} catch (Exception e){
			System.out.println("Exception：" + e);
		}
		System.out.println(msg);
	}
	
	private void createSQLStmt(LinkedHashMap<String, ArrayList<String>> fielddatamap) {
		String tablename = "Json_Table";
//		String tablename = "XML_Table";
		String pre = "INSERT INTO " + tablename + " VALUES (";
		String sql = pre;
		int i = 0;
		for(String key : fielddatamap.keySet()) {
			ArrayList<String> valuelist = fielddatamap.get(key);
			if(valuelist.size() == 1) {
				sql += "'" + valuelist.get(0) + "'";
			}
			else {
				String value = (valuelist.toString().length() > 128) ? "too long" : this.concatList(valuelist);
				sql += "'" + value + "'";
			}
			sql += (i != fielddatamap.size() - 1) ? ", " : ");";
			i++;
		}
		this.executeInsertSQLStmt(sql);
	}

	public void build(ParsingObject root) {
		this.numberingNodeID(root, 1);
		int targetdepth = this.getTargetDepth(root);
		this.getTargetDepthNode(root, 1, targetdepth - 3);
		for(int i = 0; i < this.targetlist.size(); i++) {
			this.analyzeFrequency(this.targetlist.get(i));
		}
		this.nominateColumnField();
		this.generateSchemaSQL();
		for(int i = 0; i < this.targetlist.size(); i++) {
			LinkedHashMap<String, ArrayList<String>> fielddatamap = this.initFieldData();;
			this.generateInsertSQL(this.targetlist.get(i), fielddatamap);
			this.createSQLStmt(fielddatamap);
		}
		System.out.println("-----------------------------------------");
	}
}

class NodeData {
	private String tag                = null;
	private String value              = null;
	private int freq                  = -1;
	ArrayList<ParsingObject> nodelist = null;
	
	public NodeData(String tag, String value, ParsingObject node) {
		this.tag   = tag;
		this.value = value;
		this.freq  = 1;
		this.nodelist = new ArrayList<ParsingObject>();
		this.nodelist.add(node);
	}
	public void increment() {
		this.freq++;
	}
	public void addnode(ParsingObject node) {
		this.nodelist.add(node);
	}
	public String getTag() {
		return this.tag;
	}
	public String getValue() {
		return this.value;
	}

	public int getFrequency() {
		return this.freq;
	}
	public ArrayList<ParsingObject> getNodeList() {
		return this.nodelist;
	}
}