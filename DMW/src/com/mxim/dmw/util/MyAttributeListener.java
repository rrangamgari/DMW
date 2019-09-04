/**
 * 
 */
package com.mxim.dmw.util;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import com.mxim.dmw.domain.TableInfo;

/**
 * @author Ravinder.Rangamgari
 *
 */
public class MyAttributeListener implements HttpSessionAttributeListener {

	/**
	 * 
	 */
	public MyAttributeListener() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attributeAdded(HttpSessionBindingEvent event) {
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		if (attributeName.equalsIgnoreCase("TableInfo")) {
			TableInfo info = (TableInfo) attributeValue;
			info.getTableAlias();
			System.out.println("Attribute added : " + info.getTableAlias());
		}
		//System.out.println("Attribute added : " + attributeName + " : " + attributeValue);
	}

	@Override
	public void attributeRemoved(HttpSessionBindingEvent event) {
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		//System.out.println("Attribute removed : " + attributeName + " : " + attributeValue);
		if (attributeName.equalsIgnoreCase("TableInfo")) {
			TableInfo info = (TableInfo) attributeValue;
			info.getTableAlias();
			System.out.println("Attribute removed : " + info.getTableAlias());
		}
	}

	@Override
	public void attributeReplaced(HttpSessionBindingEvent event) {
		String attributeName = event.getName();
		Object attributeValue = event.getValue();
		//System.out.println("Attribute replaced : " + attributeName + " : " + attributeValue);
		if (attributeName.equalsIgnoreCase("TableInfo")) {
			TableInfo info = (TableInfo) attributeValue;
			info.getTableAlias();
			System.out.println("Attribute replaced : " + info.getTableAlias());
		}
	}

}
