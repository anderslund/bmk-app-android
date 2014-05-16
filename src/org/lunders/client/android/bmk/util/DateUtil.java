package org.lunders.client.android.bmk.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class DateUtil {

	private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static final SimpleDateFormat formattedDateFormat = new SimpleDateFormat("dd. MMM yyyy");

	private static final SimpleDateFormat formattedDateTime = new SimpleDateFormat("EEEE d. MMM yyyy 'kl' HH':'mm");

	private static final SimpleDateFormat formattedTime = new SimpleDateFormat("HH':'mm");

	public static synchronized String getFormattedCurrentDate() {
		return formattedDateFormat.format(new Date());
	}

	public static synchronized String getFormattedDateTime(Date d) {
		return d == null ? "Ukjent" : formattedDateTime.format(d);
	}

	public static synchronized String getFormattedEndTime(Date d) {
		return d == null ? "" : " - " + formattedTime.format(d);
	}

	public static synchronized Date getDate(String s) {
		if (s == null || s.trim().length() == 0) {
			return null;
		}

		if (s.indexOf(':') > 0) {
			return dateTimeFormat.parse(s, new ParsePosition(0));
		}
		return dateFormat.parse(s, new ParsePosition(0));
	}
}
