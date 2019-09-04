package com.mxim.dmw.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class Utils {
	public static SimpleEmail getSimpleEmail() throws EmailException {
		SimpleEmail email = new SimpleEmail();
		email.setHostName(getSQLProperty("email.host"));
		email.setFrom(getSQLProperty("default.from.email"), getSQLProperty("default.from.email.name"));
		return email;
	}

	public static void sendEmail(SimpleEmail email, String subject, String body) throws EmailException {
		System.out.println("Utils.sendEmail()");
		if (body != null) {
			email.setSubject(subject);
			email.setMsg(body);
			email.addBcc("ravinder.rangamgari@maximintegrated.com");
			email.addBcc(getSQLProperty("default.from.email"));

			if (getProductionMode()) {
				//email.send();
			} else {
				System.out.println("Utils.sendEmail() ProductionMode is false. Will not send email to real recepients");
				List<InternetAddress> toAddresses = email.getToAddresses();
				List<InternetAddress> ccAddresses = email.getCcAddresses();

				email.setSubject("Test Mode. " + subject);
				body = "TEST MODE. IGNORE IF FOUND\r\n\r\n" + body
						+ "\r\n\r\nIn production, this email would have been sent TO: " + getAddressesAsStr(toAddresses)
						+ "\r\n CC:" + getAddressesAsStr(ccAddresses);

				email.setMsg(body);
				email.setTo(email.getBccAddresses());
				email.setCc(simpleAddress);
				email.setBcc(simpleAddress);
			}

			// TODO commented out for now!
			email.send();

			// List list = new ArrayList<E>
			// email.setTo(arg0)
			System.out.println("Mail sent successfully. body=" + body);
		} else
			System.out.println("ModifyEMRBAction.directorDisposition(). email body was null!"); // this
																								// should
																								// never
																								// happen
	}

	private static String getAddressesAsStr(List<InternetAddress> addressesList) {
		StringBuilder output = new StringBuilder("");
		if (addressesList != null && addressesList.size() > 0) {
			for (InternetAddress address : addressesList) {
				output.append(address.getPersonal() + "(" + address.getAddress() + "), ");
			}
		}
		return output.toString();
	}

	private static Properties SQLProperties;
	private static boolean productionMode;
	private static List<InternetAddress> simpleAddress = new ArrayList<InternetAddress>();

	static {
		try {
			simpleAddress.add(new InternetAddress("ravinder.rangamgari@maximintegrated.com"));
		} catch (AddressException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SQLProperties = new Properties();
		try {
			SQLProperties.load(Utils.class.getResourceAsStream("/eMRB.properties"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Context initContext;
			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:comp/env");
			productionMode = (Boolean) envContext.lookup("productionMode");
			System.out.println("Utils.enclosing_method() Set productionMode to " + productionMode);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public static String getSQLProperty(String key) {
		return SQLProperties.getProperty(key);
	}

	public static boolean getProductionMode() {
		return productionMode;
	}
}