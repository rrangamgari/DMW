package com.mxim.dmw.util;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.mxim.dmw.domain.UserInfo;

public class MySessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		System.out.println("****session Created****" + se);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		// TODO Auto-generated method stub
		ServletContext application = se.getSession().getServletContext();
		UserInfo info = (UserInfo) se.getSession().getAttribute("USER_DETAILS");
		System.out.println("****session Destroyed****" + info.getUserId());
		application.setAttribute(info.getUserId(), -1);
		System.out.println(info.getUserId());
	}

}
