package com.mxim.dmw.util;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

/**
 * @author Ravinder.Rangamgari
 *
 */
public class FormatUtil {
	public String formatDate(Date date) {
		String str = "";
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy");
			str = sdf.format(date);
		}
		return str;
	}

	public String formatNumber(double dd) {
		// System.out.println(dd);
		DecimalFormat df2 = new DecimalFormat("#,###,###,###.00");
		// dd = new Double(df2.format(dd)).doubleValue();
		// System.out.println("after :" + dd);
		return df2.format(dd) + "";
	}

	public String removeNulls(String data) {
		String str = "";
		if (data != null) {
			// SimpleDateFormat sdf = new SimpleDateFormat( "MM/dd/yyy" );
			str = data;
		}
		return str;
	}

	public String escapeSplCharacters(String data) {
		String str = "";
		if (data != null) {
			data = data.replaceAll("\"", "&quot;");
			data = data.replaceAll("'", "&apos;");
			data = data.replaceAll("<", "&lt;");
			data = data.replaceAll(">", "&gt;");
			data = data.replaceAll("&", "&amp;");
			str = data;
		}
		return str;
	}

	public static String replace(String s, char from, char to) {
		if (s.length() < 1) {
			return s;
		} else {
			char first = from == s.charAt(0) ? to : s.charAt(0);
			return first + replace(s.substring(1), from, to);
		}
	}

	public static String formatDate(String date) {
		// return date;
		String formatedDate = new String();

		String yr = null;
		String mo = null;
		String dy = null;
		if (date != null) {
			if (!date.trim().equals("")) {
				if (date.indexOf(" ") > 0) {
					date = date.substring(0, date.indexOf(" "));
					date = date.trim();
				} else if (date.indexOf("T") > 0) {
					date = date.substring(0, date.indexOf("T"));
					date = date.trim();
				}
			}

			if (date.length() == 10 && date.contains("-")) {
				StringTokenizer st = new StringTokenizer(date, "-");
				if (st.hasMoreTokens()) {
					yr = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					mo = st.nextToken();
				}
				if (st.hasMoreTokens()) {
					dy = st.nextToken();
				}

				formatedDate = mo + "/" + dy + "/" + yr;
			} else {
				formatedDate = date;
			}
		} else {
			formatedDate = date;
		}
		return formatedDate;
	}
}
