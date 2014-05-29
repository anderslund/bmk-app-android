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

import android.text.Html;

public class StringUtil {

	public static CharSequence truncate(CharSequence s, int maxSize) {
		if (isBlank(s)) {
			return null;
		}

		if ( s.length() >= maxSize - 3) {
			return s.toString().substring(0, maxSize-3) + "...";
		}
		return s;
	}

	public static boolean isBlank(CharSequence s) {
		return s == null || s.length() == 0;
	}

	/**
	 * Fjerner HTML-tagger fra s og trimmer den.
	 *
	 * @param s
	 * @return
	 */
	public static String cleanHtml(String s) {
		if (StringUtil.isBlank(s)) {
			return "";
		}

		return Html.fromHtml(s).toString().trim();
	}
}
