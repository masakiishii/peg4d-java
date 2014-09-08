package org.peg4d;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;

import java.sql.*;

public class DBBuilder {

	class TableData implements Comparator<TableData> {
		private String tag   = null;
		private String value = null;
		private int ltpos    = -1;
		private int rtpos    = -1;
		private int depth    = -1;
		public TableData() {
		}
		public TableData(ParsingObject node, int depth, int currentpos) {
			this.tag   = node.getTag().toString();
			this.value = (node.size() == 0 && node.getText().length() < 64) ? node.getText() : null;
			this.ltpos = currentpos;
			this.depth = depth;
		}
		public void setRightPostion(int rtpos) {
			this.rtpos = rtpos;
		}
		@Override
		public int compare(TableData data1, TableData data2) {
			int ltpos1 = data1.ltpos;
			int ltpos2 = data2.ltpos;
			if(ltpos1 > ltpos2) {
				return 1;
			}
			else if(ltpos1 == ltpos2) {
				return 0;
			}
			else {
				return -1;
			}
		}
	}
	
	private int currentpos = -1;
	private LinkedList<TableData> datalist = null;
	public DBBuilder() {
		this.currentpos = 1;
		this.datalist   = new LinkedList<TableData>();
	}
	
	private void parseAST(ParsingObject node, int depth) {
		if(node == null) return;
		TableData tabledata = new TableData(node, depth, this.currentpos++);
		for(int index = 0; index < node.size(); index++) {
			this.parseAST(node.get(index), depth + 1);
		}
		tabledata.setRightPostion(this.currentpos++);
		this.datalist.add(tabledata);
	}
	private void sortList() {
		Collections.sort(this.datalist, new TableData());
	}
	private void showDataList() {
		System.out.println(" Tag  |  Value |  lt |  rt  |  depth");
		System.out.println("=====================================");
		for(int i = 0; i < this.datalist.size(); i++) {
			System.out.print(this.datalist.get(i).tag   + " | ");
			System.out.print(this.datalist.get(i).value + " | ");
			System.out.print(this.datalist.get(i).ltpos + " | ");
			System.out.print(this.datalist.get(i).rtpos + " | ");
			System.out.println(this.datalist.get(i).depth);
		}
	}
	
	private void executeInsertSQL(String table, Statement stmt) {
		try {
			String pre = "INSERT INTO " + table + " VALUES ";
			String sql = "";
			TableData td;
			for(int index = 0; index < this.datalist.size(); index++) {
				td = this.datalist.get(index);
				sql = pre + "( '" + td.tag + "', '" + td.value + "', " + td.ltpos + ", " + td.rtpos + ", " + td.depth + ");";
				stmt.execute(sql);
			}
		} catch (Exception e){
			System.out.println("Exception：" + e);
		}
	}

	private void buildDataBase() {
		String msg = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			msg = "Success!!";
			Connection con   = DriverManager.getConnection( "jdbc:mysql://localhost:3306/peg4dDB", "masaki","masaki");
			Statement  stmt  = con.createStatement();
			String     table = "parsingObjectDB";
			String     sql   = "CREATE TABLE " + table + "( "
					+ "tag VARCHAR(16),"
					+ "value VARCHAR(256),"
					+ "lt INT PRIMARY KEY,"
					+ "rt INT,"
					+ "depth INT );";
			stmt.execute(sql);

			executeInsertSQL(table, stmt);
			stmt.close();
		} catch (ClassNotFoundException e){
			msg = "Fail!!";
		} catch (Exception e){
			System.out.println("Exception：" + e);
		}
		System.out.println(msg);
	}

	public void build(ParsingObject root) {
		this.parseAST(root, 0);
		this.sortList();
		//this.showDataList();
		this.buildDataBase();
		System.out.println("----------------------------------");
	}
}
