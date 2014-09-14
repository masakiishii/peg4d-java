package org.peg4d;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Queue;
import java.sql.*;

public class DBBuilder {
	
	private HashMap<String, Schema>         schemadata = null;
	
	public DBBuilder() {
		schemadata = new HashMap<String, Schema>();
	}
	

	private void buildPrimeSchema(ParsingObject node) {
		String tag = node.getTag().toString();
		if(node.size() > 0 && !schemadata.containsKey(tag)) {
			Schema schema = new Schema(tag);
			this.schemadata.put(tag, schema);
		}
	}
	
	private void buildOtherSchema(ParsingObject node) {
		ParsingObject parent = node.getParent();
		ParsingObject child  = node;
		String parenttag     = parent.getTag().toString();
		String childtag      = child.getTag().toString();
		if(parent == null) return;
		if(this.schemadata.containsKey(parenttag)) {
			Schema data = this.schemadata.get(parenttag);
			ArrayList<String> fieldlist = data.getfieldlist();
			if(!fieldlist.contains(childtag)) {
				fieldlist.add(childtag);
			}
		}
	}
	
	private void buildDataBaseSchema(ParsingObject root, int id) {
		if(root == null) return;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			node.setId(id++);
			this.buildPrimeSchema(node);
			for(int index = 0; index < node.size(); index++) {
				ParsingObject childnode = node.get(index);
				this.buildOtherSchema(childnode);
				queue.offer(childnode);
			}
		}
	}

	
	private void createSQlStmt(ParsingObject node) {
		String tag = node.getTag().toString();
		if(this.schemadata.containsKey(tag)) {
			Schema schema = this.schemadata.get(tag);
		}
	}
	
	private void insertDataBase(ParsingObject root) {
		if(root == null) return;
		Queue<ParsingObject> queue = new LinkedList<ParsingObject>();
		queue.offer(root);
		while(!queue.isEmpty()) {
			ParsingObject node = queue.poll();
			this.createSQlStmt(node);
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
			for(String table : this.schemadata.keySet()) {
				String sql = "CREATE TABLE " + table + "( ";
				ArrayList<String> fieldlist = this.schemadata.get(table).getfieldlist();
				String field = "";
				for(int i = 0; i < fieldlist.size(); i++) {
					field += fieldlist.get(i) + " VARCHAR(256)";
					if(i != fieldlist.size() - 1) {
						field += ", ";
					}
				}
				sql += field + ");";
				System.out.println(sql);
				stmt.execute(sql);
			}
			stmt.close();
		} catch (ClassNotFoundException e){
			msg = "Fail!!";
		} catch (Exception e){
			System.out.println("Exception：" + e);
		}
		System.out.println(msg);
	}
	
	public void build(ParsingObject root) {
		this.buildDataBaseSchema(root, 1);
		this.generateSchemaSQL();
		
		//this.insertDataBase(root);
		ArrayList<String> array = this.schemadata.get("Element").getfieldlist();
		System.out.println("table: " + this.schemadata.get("Element").getPrimaryField());
		for(int i = 0; i < array.size(); i++) {
			System.out.println(array.get(i));
		}
		array = this.schemadata.get("Attr").getfieldlist();
		System.out.println("table: " + this.schemadata.get("Attr").getPrimaryField());
		for(int i = 0; i < array.size(); i++) {
			System.out.println(array.get(i));
		}
		System.out.println("-----------------------------------------");
	}
}

class Schema {
	private String            primaryfield = null;
	private ArrayList<String> fieldlist   = null;
	public Schema(String primaryfield) {
		this.primaryfield = primaryfield;
		this.fieldlist   = new ArrayList<String>();
	}
	public String getPrimaryField() {
		return this.primaryfield;
	}
	
	public ArrayList<String> getfieldlist() {
		return this.fieldlist;
	}
}
