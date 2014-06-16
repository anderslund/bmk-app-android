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

package org.lunders.client.android.bmk.services.impl.session;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.lunders.client.android.bmk.services.SessionService;
import org.lunders.client.android.bmk.util.StringUtil;

import java.security.MessageDigest;

public class SessionServiceImpl implements SessionService {

	private Context mContext;
	private String  mLoggedInUserId;
	private String  mLoggedInUserDisplayName;
	private boolean mLocalSessionRead;
	private Handler mResponseHandler;

	private static final MessageDigest DIGEST;

	private static final String TAG = SessionServiceImpl.class.getSimpleName();

	private static SessionServiceImpl INSTANCE;

	static {
		try {
			DIGEST = MessageDigest.getInstance("SHA");
		}
		catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}


	public static synchronized SessionServiceImpl getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new SessionServiceImpl(c);
		}
		return INSTANCE;
	}


	private SessionServiceImpl(Context context) {
		mContext = context;
		mResponseHandler = new Handler(Looper.getMainLooper());
	}


	public void logout(LogoutListener listener) {
		mLoggedInUserDisplayName = null;
		mLoggedInUserId = null;
		sendValue(LoginStatus.OK, listener);
	}


	public void login(CharSequence username, CharSequence password, final LoginListener listener) {
		if (StringUtil.isBlank(username)) {
			sendValue(LoginStatus.MISSING_USERNAME, listener);
			return;
		}

		if (StringUtil.isBlank(password)) {
			sendValue(LoginStatus.MISSING_PASSWORD, listener);
			return;
		}

		String hashBase = username.toString() + password.toString();
		String hashed = hashEncode(hashBase);

		Log.i(TAG, "Logging in " + username + " with " + hashed);

		mLoggedInUserId = username.toString();
		mLoggedInUserDisplayName = "Anders Lund";

		//TODO: Skriv token til lokalt om at brukeren er logget på
		sendValue(LoginStatus.OK, listener);
	}


	public void setPassword(CharSequence username, CharSequence existingPassword,
	                        CharSequence newPassword, LoginListener listener) {
		login(username, existingPassword, listener);

		//TODO: Send et kall til GitHub med nytt passord for brukeren og commit.
	}


	public boolean isLoggedIn() {
		if (mLoggedInUserId != null) {
			return true;
		}

		if (mLocalSessionRead) {
			return false;
		}

		mLoggedInUserId = getLoggedInUserIdFromLocalStorage();
		return mLoggedInUserId != null;
	}


	private String getLoggedInUserIdFromLocalStorage() {
		mLocalSessionRead = true;
		return null;
	}

	public String getCurrentUserID() {
		return mLoggedInUserId;
	}


	public String getCurrentUserDisplayName() {
		return mLoggedInUserDisplayName;
	}



	private void sendValue(final LoginStatus loginStatus, final LoginListener listener) {
		mResponseHandler.post(
			new Runnable() {
				@Override
				public void run() {
					listener.loginAttempted(loginStatus);
				}
			});
	}

	private void sendValue(final LoginStatus loginStatus, final LogoutListener listener) {
		mResponseHandler.post(
			new Runnable() {
				@Override
				public void run() {
					listener.logoutAttempted(loginStatus);
				}
			});
	}


	private static String hashEncode(String stringToEncode) {

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
