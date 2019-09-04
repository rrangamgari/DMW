package com.mxim.dmw.dao;

import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mxim.dmw.util.AuditMessage;
import com.mxim.dmw.util.DateDeserializer;
import com.mxim.dmw.util.DateSerializer;
//import com.mxim.dmw.util.Employee;
import com.mxim.dmw.util.GenericMessage;
import com.mxim.dmw.util.MessageSender;

public class DMWMessageQueue {
	ApplicationContext context;
	MessageSender messageSender, genMessageSender;

	public DMWMessageQueue() {
		context = WebApplicationContextUtils
				.getWebApplicationContext(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
						.getRequest().getSession().getServletContext());
		// context = new
		// FileSystemXmlApplicationContext("./conf/spring-app.xml");
		messageSender = (MessageSender) context.getBean("messageSender");
		genMessageSender = (MessageSender) context.getBean("genMessageSender");
	}

	public void insertMessageLog(String eventName, String eventDesc, String eventQuery, String userId, String oldValue,
			String newValue) {

		// init spring context

		// ApplicationContext context = new
		// ClassPathXmlApplicationContext("./conf/spring-beans.xml");

		AuditMessage auditMessage = new AuditMessage();
		auditMessage.setEventName(eventName);
		auditMessage.setEventDesc(eventDesc);
		auditMessage.setEventQuery(eventQuery);
		auditMessage.setUserId(userId);
		auditMessage.setOldValue(oldValue);
		auditMessage.setNewValue(newValue);
		auditMessage.setUpdateDate(new Date());

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
		gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
		Gson gson = gsonBuilder.create();

		//System.out.println(gson.toJson(auditMessage));
		GenericMessage message = new GenericMessage();
		message.setDataClass("com.mxim.dmw.util.AuditMessage");
		message.setData(auditMessage);
		messageSender.send(gson.toJson(message, GenericMessage.class));
		//System.out.println("Message Sent");

	}

	public void insertAuditLog(String eventName, String eventDesc, String eventQuery, String userId) {

		// init spring context

		// ApplicationContext context = new
		// ClassPathXmlApplicationContext("./conf/spring-beans.xml");

		AuditMessage auditMessage = new AuditMessage();
		auditMessage.setEventName(eventName);
		auditMessage.setEventDesc(eventDesc);
		auditMessage.setEventQuery(eventQuery);
		auditMessage.setUserId(userId);
		auditMessage.setUpdateDate(new Date());

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new DateSerializer());
		gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
		Gson gson = gsonBuilder.create();

		//System.out.println(gson.toJson(auditMessage));
		GenericMessage message = new GenericMessage();
		message.setDataClass("com.mxim.dmw.util.AuditMessage");
		message.setData(auditMessage);
		genMessageSender.send(gson.toJson(message, GenericMessage.class));
		//System.out.println("Message Sent");

	}
}
