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

import android.util.Log;

import java.security.MessageDigest;

public class SessionUtils {

	private static String loggedInUserId;
	private static String loggedInUserDisplayName = "Anders Lund";
	private static boolean localSessionRead;

	public enum LoginStatus {OK, BAD_CREDENTIALS, MISSING_USERNAME, MISSING_PASSWORD, COMM_FAILURE}

	private static final MessageDigest DIGEST;

	private static final String TAG = SessionUtils.class.getSimpleName();

	static {
		try {
			DIGEST = MessageDigest.getInstance("SHA");
		}
		catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}


	public static LoginStatus doLogin(CharSequence username, CharSequence password) {
		if (StringUtil.isBlank(username)) {
			return LoginStatus.MISSING_USERNAME;
		}

		if (StringUtil.isBlank(password)) {
			return LoginStatus.MISSING_PASSWORD;
		}

		String hashBase = username.toString() + password.toString();
		String hashed = hashEncode(hashBase);

		Log.i(TAG, "Logging in " + username + " with " + hashed);

		loggedInUserId = username.toString();
		return LoginStatus.OK;
	}


	public static LoginStatus doSetPassword(CharSequence username, CharSequence existingPassword, CharSequence newPassword) {
		LoginStatus status = doLogin(username, existingPassword);

		if (status != LoginStatus.OK) {
			return status;
		}

		//TODO: Send et kall til GitHub med nytt passord for brukeren og commit.

		return LoginStatus.OK;

	}


	public static boolean isLoggedIn() {
		if (loggedInUserId != null) {
			return true;
		}

		if (localSessionRead) {
			return false;
		}

		loggedInUserId = getLoggedInUserIdFromLocalStorage();
		return loggedInUserId != null;
	}


	private static String getLoggedInUserIdFromLocalStorage() {
		localSessionRead = true;
		return null;
	}

	public static String getLoggedInUserId() {
		return loggedInUserId;
	}

	public static void setLoggedInUserId(String loggedInUserId) {
		SessionUtils.loggedInUserId = loggedInUserId;
	}

	public static String getLoggedInUserDisplayName() {
		return loggedInUserDisplayName;
	}

	public static void setLoggedInUserDisplayName(String loggedInUserDisplayName) {
		SessionUtils.loggedInUserDisplayName = loggedInUserDisplayName;
	}

	public static String hashEncode(String stringToEncode) {

		byte[] digest;
		synchronized (DIGEST) {
			DIGEST.reset();
			DIGEST.update(stringToEncode.getBytes());
			digest = DIGEST.digest();
		}

		StringBuffer hashedFileName = new StringBuffer(40);
		for (int i = 0; i < digest.length; i++) {
			int num = digest[i] & 0xFF;
			String s = ((num < 0x10 ? "0" : "") + Integer.toString(num, 16));
			hashedFileName.append(s);
		}

		String hash = hashedFileName.toString();
		hash = hash.toUpperCase();
		return hash;
	}
}
