/*
 * Copyright 2014 Anders Lund
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lunders.client.android.bmk.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

	private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static final SimpleDateFormat formattedDateFormat = new SimpleDateFormat("d. MMM yyyy");

	private static final SimpleDateFormat nmfFormattedDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z", Locale.ENGLISH);

	private static final SimpleDateFormat formattedDateTime = new SimpleDateFormat("EEEE d. MMM yyyy 'kl' HH':'mm");

	private static final SimpleDateFormat formattedTime = new SimpleDateFormat("HH':'mm");

	public static synchronized String getFormattedDate(Date d) {
		return formattedDateFormat.format(d);
	}

	public static synchronized String getFormattedCurrentDate() {
		return getFormattedDate(new Date());
	}

	public static synchronized String getFormattedDateTime(Date d) {
		return d == null ? "Tidspunkt ikke avklart" : formattedDateTime.format(d);
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

	public static synchronized Date getNmfDate(String s) {
		if (s == null || s.trim().length() == 0) {
			return null;
		}

		return nmfFormattedDateFormat.parse(s, new ParsePosition(0));
	}
}
