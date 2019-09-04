package com.mxim.dmw.util;

import java.io.File;
import java.util.ArrayList;

import com.mxim.dmw.domain.TableList;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<TableList> parentlist = new ArrayList<>();
		ArrayList<TableList> childlist = new ArrayList<>();
		TableList list = new TableList();
		list.setName("Test");
		list.setParentId(0);
		list.setTableID(1);
		list.setText("Test");
		list.setLeaf(true);
		list.setTableName("parent");
		childlist.add(list);
		list = new TableList();
		list.setName("Test");
		list.setParentId(0);
		list.setTableID(2);
		list.setText("Test");
		list.setLeaf(true);
		list.setTableName("parent");
		childlist.add(list);
		list = new TableList();
		list.setName("Test");
		list.setParentId(1);
		list.setTableID(3);
		list.setText("Test");
		list.setLeaf(true);
		list.setTableName("parent");
		childlist.add(list);
		list = new TableList();
		list.setName("Test");
		list.setParentId(1);
		list.setTableID(4);
		list.setText("Test");
		list.setLeaf(false);
		list.setTableName("TABLE");
		childlist.add(list);
		parentlist = processTreeList(parentlist, childlist, 0);
		for (int i = 0; i < parentlist.size(); i++) {
			System.out.println(parentlist.get(i).getTableID() + "*******************");
			if (parentlist.get(i).getChildren() != null) {
				System.out.println(parentlist.get(i).getChildren().size() + "*******size child************");
			}
		}

	}

	private static int tableSize = 0;

	private static ArrayList<TableList> processTreeList(ArrayList<TableList> parentlist, ArrayList<TableList> tableList,
			int parentId) {
		ArrayList<TableList> childlist = new ArrayList<>();

		if (tableList.size() > 0) {
			for (int i = 0; i < tableList.size(); i++) {
				if (tableList.get(i).getParentId() == 0 ) {
					parentlist.add(tableList.get(i));
					// childlist.add(tl);
					System.out.println(tableList.get(i).getTableID() + "|");
					tableList.remove(i);
				} else {
					boolean bool=true;
					for (TableList pl : parentlist) {
						System.out.println(pl.getTableID()+"==========="+tableList.get(i).getParentId());
						if (pl.getTableID() == tableList.get(i).getParentId()) {
							childlist = new ArrayList<>();
							childlist.add(tableList.get(i));
							pl.setChildren(childlist);
							tableList.remove(i);
							System.out.println(tableList.get(i).getTableID() + "--child--");
							// tableList.removeAll(childlist);
							parentlist = processTreeList(childlist, tableList, 0);
							bool=false;
						}
					}

				}
			}
			/*
			 * System.out.println("childlist :" + childlist.size());
			 * tableList.removeAll(childlist);
			 * System.out.println(tableList.size());
			 */
			// myList = processTreeList(parentlist, tableList, tableSize -
			// tableList.size());
			System.out.println(tableList.size());
			//parentlist = processTreeList(parentlist, tableList, 0);
		}
		return parentlist;

	}
}
