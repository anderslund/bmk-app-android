package org.lunders.client.android.bmk.util;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class StringUtil {

	public static String truncate(String s, int maxSize) {
		if (isBlank(s)) {
			return null;
		}

		if ( s.length() >= maxSize - 3) {
			return s.substring(0, maxSize-3) + "...";
		}
		return s;
	}

	public static boolean isBlank(String s) {
		return s == null || s.trim().length() == 0;
	}
}
