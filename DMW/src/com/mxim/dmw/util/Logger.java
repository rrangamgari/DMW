package com.mxim.dmw.util;

import org.apache.log4j.Level;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Ravinder.Rangamgari
 *
 */
public final class Logger {

	/**
	 * Logger names. These names are assigned to the Loggers during
	 * initialization.
	 */
	private static final String LOGGER_DEBUG = "DebugLogger";

	private static final String LOGGER_ERROR = "ErrorLogger";

	private static final String LOGGER_FATAL = "FatalLogger";
	private static final String LOGGER_INFO = "InfoLogger";

	private static final String LOGGER_WARN = "WarnLogger";

	private static org.apache.log4j.Logger DebugLogger;

	private static org.apache.log4j.Logger InfoLogger;

	private static org.apache.log4j.Logger WarnLogger;

	private static org.apache.log4j.Logger ErrorLogger;

	private static org.apache.log4j.Logger FatalLogger;
	// log4j.xml path
	private static String msPropPathName;

	// method start and end on flag,
	// method start and end will be printed if its true
	private static boolean printStartAndEnd = true;

	// Initialize loggers
	static {
		if (System.getProperty("os.name").indexOf("Windows") > -1) {
			msPropPathName = "E:\\Workspace2016\\DMW\\WebContent\\DMW\\resources\\log4j.xml";
			msPropPathName = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
					.getSession().getServletContext().getRealPath("/") + "resources\\log4j.xml";
			System.out.println(msPropPathName);
		} else {
			msPropPathName = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
					.getSession().getServletContext().getRealPath("/");
			msPropPathName = msPropPathName.substring(0, msPropPathName.lastIndexOf("/"));
			msPropPathName = msPropPathName.substring(0, msPropPathName.lastIndexOf("/"));
			msPropPathName = msPropPathName.substring(0, msPropPathName.lastIndexOf("/"));
			System.out.println(msPropPathName);
			msPropPathName += "/webapps/DMW/resources/log4j.xml";
			System.out.println(msPropPathName);
		}
		DOMConfigurator.configure(msPropPathName);

		DebugLogger = org.apache.log4j.Logger.getLogger(LOGGER_DEBUG);
		InfoLogger = org.apache.log4j.Logger.getLogger(LOGGER_INFO);
		WarnLogger = org.apache.log4j.Logger.getLogger(LOGGER_WARN);
		ErrorLogger = org.apache.log4j.Logger.getLogger(LOGGER_ERROR);
		FatalLogger = org.apache.log4j.Logger.getLogger(LOGGER_FATAL);

		try {
		} catch (Exception e) {
			// do nothing if an exception is thrown,
			// assuming default value of printStartAndEnd = true
		}
	}

	/**
	 * Composes a log message from the parameters passed by the client.
	 * 
	 * @param cname
	 *            the class name where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 * @return the log message.
	 */
	protected static String composeMessage(String cname, String method, String message, String trackingId,
			Throwable ex) {
		// Constants used to compose the log message
		final String SEPARATOR = " ";
		final String INDENT = "\n\t";
		final String CRLF = "\n";
		final String AT = "at ";

		StringBuffer result = new StringBuffer();

		result.append(" ");
		if (cname != null /* && cname.length() > 0 */) {
			result.append(cname).append(":");
		}
		if (method != null /* && method.length() > 0 */) {
			result.append(method).append(SEPARATOR);
		}
		if (message != null) {
			result.append(message);
		}
		if (ex != null) {
			String stdMessage = ex.getMessage();
			String errorMessage = null;

			if (errorMessage != null && !errorMessage.equals(stdMessage)) {
				result.append(INDENT).append(errorMessage);
			}
			result.append(CRLF).append(ex.toString());
			StackTraceElement[] stack = ex.getStackTrace();
			for (int i = 0; i < stack.length; i++) {
				result.append(INDENT).append(AT);
				result.append(stack[i].toString());
			}

			Throwable parent = ex.getCause();
			if (parent != null) {
				result.append(CRLF).append("Caused by: ").append(parent.toString());
				stack = parent.getStackTrace();
				for (int i = 0; i < stack.length; i++) {
					result.append(INDENT).append(AT);
					result.append(stack[i].toString());
				}
			}
		}
		return result.toString();
	}

	/**
	 * Logs a message with the DEBUG level.
	 * 
	 * @param cls
	 *            the class where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	// @SuppressWarnings( "unchecked" )
	public static void debug(@SuppressWarnings("rawtypes") Class cls, String method, String message, String trackingId,
			Throwable ex) {
		String name = "";
		if (cls != null) {
			name = cls.getName();
		}
		debug(name, method, message, trackingId, ex);
	}

	/**
	 * Logs a message with the DEBUG level.
	 * 
	 * @param obj
	 *            the object where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	public static void debug(Object obj, String method, String message, String trackingId, Throwable ex) {
		String name = "";
		if (obj != null) {
			name = obj.getClass().getName();
		}
		debug(name, method, message, trackingId, ex);
	}

	/**
	 * Logs a message with the DEBUG level.
	 * 
	 * @param cname
	 *            the class name where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	private static void debug(String cname, String method, String message, String trackingId, Throwable ex) {
		if (message == null) {
			message = "";
		}

		if (printStartAndEnd) {
			if (DebugLogger.isDebugEnabled()) {
				DebugLogger.debug(composeMessage(cname, method, message, trackingId, ex));
			}
		} else {
			if (!message.equalsIgnoreCase("Method Start") && !message.equalsIgnoreCase("Method End")) {
				if (DebugLogger.isDebugEnabled()) {
					DebugLogger.debug(composeMessage(cname, method, message, trackingId, ex));
				}
			}
		}
	}

	// @SuppressWarnings( "unchecked" )
	public static void end(@SuppressWarnings("rawtypes") Class c, String method) {
		if (printStartAndEnd) {
			info(c, method, "END", null, null);
		}
	}

	public static void end(Object o, String method) {
		if (printStartAndEnd) {
			info(o, method, "END", null, null);
		}
	}

	/**
	 * Logs a message with the ERROR level.
	 * 
	 * @param cls
	 *            the class where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	// @SuppressWarnings( "unchecked" )
	public static void error(@SuppressWarnings("rawtypes") Class cls, String method, String message, String trackingId,
			Throwable ex) {
		String name = "";
		if (cls != null) {
			name = cls.getName();
		}
		error(name, method, message, trackingId, ex);
	}

	/**
	 * Logs a message with the ERROR level.
	 * 
	 * @param obj
	 *            the object where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	public static void error(Object obj, String method, String message, String trackingId, Throwable ex) {
		String name = "";
		if (obj != null) {
			name = obj.getClass().getName();
		}
		error(name, method, message, trackingId, ex);
	}

	/**
	 * Logs a message with the ERROR level.
	 * 
	 * @param cname
	 *            the class name where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	private static void error(String cname, String method, String message, String trackingId, Throwable ex) {
		if (ErrorLogger.isEnabledFor(Level.ERROR)) {
			ErrorLogger.error(composeMessage(cname, method, message, trackingId, ex));
		}
	}

	/**
	 * Logs a message with the FATAL level.
	 * 
	 * @param cls
	 *            the class where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	// @SuppressWarnings( "unchecked" )
	public static void fatal(@SuppressWarnings("rawtypes") Class cls, String method, String message, String trackingId,
			Throwable ex) {
		String name = "";
		if (cls != null) {
			name = cls.getName();
		}
		fatal(name, method, message, trackingId, ex);
	}

	/**
	 * Logs a message with the FATAL level.
	 * 
	 * @param obj
	 *            the object where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	public static void fatal(Object obj, String method, String message, String trackingId, Throwable ex) {
		String name = "";
		if (obj != null) {
			name = obj.getClass().getName();
		}
		fatal(name, method, message, trackingId, ex);
	}

	/**
	 * Logs a message with the FATAL level.
	 * 
	 * @param cname
	 *            the class name where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	private static void fatal(String cname, String method, String message, String trackingId, Throwable ex) {
		FatalLogger.fatal(composeMessage(cname, method, message, trackingId, ex));
	}

	/**
	 * Logs a message with the INFO level.
	 * 
	 * @param cls
	 *            the class where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	// @SuppressWarnings( "unchecked" )
	public static void info(@SuppressWarnings("rawtypes") Class cls, String method, String message, String trackingId,
			Throwable ex) {
		String name = "";
		if (cls != null) {
			name = cls.getName();
		}
		info(name, method, message, trackingId, ex);
	}

	/**
	 * Logs a message with the INFO level.
	 * 
	 * @param obj
	 *            the object where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	public static void info(Object obj, String method, String message, String trackingId, Throwable ex) {
		String name = "";
		if (obj != null) {
			name = obj.getClass().getName();
		}
		info(name, method, message, trackingId, ex);
	}

	/**
	 * Logs a message with the INFO level.
	 * 
	 * @param cname
	 *            the class name where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	private static void info(String cname, String method, String message, String trackingId, Throwable ex) {
		if (message == null) {
			message = "";
		}

		if (printStartAndEnd) {
			if (InfoLogger.isInfoEnabled()) {
				InfoLogger.info(composeMessage(cname, method, message, trackingId, ex));
			}
		} else {
			if (!message.equalsIgnoreCase("Method Start") && !message.equalsIgnoreCase("Method End")) {
				if (InfoLogger.isInfoEnabled()) {
					InfoLogger.info(composeMessage(cname, method, message, trackingId, ex));
				}
			}
		}
	}

	/**
	 * Checks if DEBUG level is enable.
	 * 
	 * @return boolean . true if Debug is enabled.
	 * 
	 */
	public static boolean isDebugEnabled() {
		return DebugLogger.isDebugEnabled();
	}

	/**
	 * Checks if INFO level is enable.
	 * 
	 * @return boolean . true if Info is enabled.
	 * 
	 */
	public static boolean isInfoEnabled() {
		return InfoLogger.isInfoEnabled();
	}

	// @SuppressWarnings( "unchecked" )
	public static void start(@SuppressWarnings("rawtypes") Class c, String method) {
		if (printStartAndEnd) {
			info(c, method, "START", null, null);
		}
	}

	public static void start(Object o, String method) {
		if (printStartAndEnd) {
			info(o, method, "START", null, null);
		}
	}

	/**
	 * Logs a message with the WARN level.
	 * 
	 * @param cls
	 *            the class where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	// @SuppressWarnings( "unchecked" )
	public static void warn(@SuppressWarnings("rawtypes") Class cls, String method, String message, String trackingId,
			Throwable ex) {
		String name = "";
		if (cls != null) {
			name = cls.getName();
		}
		warn(name, method, message, trackingId, ex);
	}

	/**
	 * Logs a message with the WARN level.
	 * 
	 * @param obj
	 *            the object where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	public static void warn(Object obj, String method, String message, String trackingId, Throwable ex) {
		String name = "";
		if (obj != null) {
			name = obj.getClass().getName();
		}
		warn(name, method, message, trackingId, ex);
	}

	/**
	 * Logs a message with the WARN level.
	 * 
	 * @param cname
	 *            the class name where logging was called from.
	 * @param method
	 *            the method name where logging was called from.
	 * @param message
	 *            the unique text for the log message.
	 * @param trackingId
	 *            the unique request tracking id.
	 * @param ex
	 *            the error object; pass null if no error.
	 */
	private static void warn(String cname, String method, String message, String trackingId, Throwable ex) {
		if (WarnLogger.isEnabledFor(Level.WARN)) {
			WarnLogger.warn(composeMessage(cname, method, message, trackingId, ex));
		}
	}

}
