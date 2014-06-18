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
import org.lunders.client.android.bmk.services.impl.ServiceHelper;
import org.lunders.client.android.bmk.util.StringUtil;
import org.lunders.client.android.bmk.util.ThreadPool;

import java.io.*;
import java.security.MessageDigest;

public class SessionServiceImpl implements SessionService {

	private Context mContext;
	private String  mLoggedInUserId;
	private String  mLoggedInUserDisplayName;
	private boolean mLocalSessionRead;
	private Handler mResponseHandler;

	private static final MessageDigest DIGEST;

	private static SessionServiceImpl INSTANCE;

	public static final String SESSION_CACHE = "sessionCache";

	private static final String GITHUB_SESSION_PREFIX
		= "https://raw.githubusercontent.com/anderslund/bmk/master/users/";

	private static final String TAG = SessionServiceImpl.class.getSimpleName();

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
		removeCredsFromLocalStorage();
		sendValue(LoginStatus.OK, listener);
	}


	public void login(final CharSequence username, final CharSequence password, final LoginListener listener) {
		if (StringUtil.isBlank(username)) {
			sendValue(LoginStatus.MISSING_USERNAME, listener);
			return;
		}

		if (StringUtil.isBlank(password)) {
			sendValue(LoginStatus.MISSING_PASSWORD, listener);
			return;
		}

		ThreadPool.getInstance().execute(
			new Runnable() {
				@Override
				public void run() {
					try {
						byte[] bytes = ServiceHelper.hentRaadata(GITHUB_SESSION_PREFIX + username);

						if (bytes == null) {
							sendValue(LoginStatus.BAD_CREDENTIALS, listener);
							return;
						}

						String userData = new String(bytes, "UTF-8");

						String[] tokens = userData.split(":");
						String displayName = tokens[0];
						String storedHash = tokens[1];

						String hashBase = username.toString() + password.toString();
						String hashed = hashEncode(hashBase);
						if (!hashed.equals(storedHash)) {
							sendValue(LoginStatus.BAD_CREDENTIALS, listener);
							return;
						}

						mLoggedInUserId = username.toString();
						mLoggedInUserDisplayName = displayName;

						sendValue(LoginStatus.OK, listener);

						final FileOutputStream fileOutputStream = mContext.openFileOutput(SESSION_CACHE, Context.MODE_PRIVATE);
						PrintWriter writer = new PrintWriter(new OutputStreamWriter(fileOutputStream));
						writer.println(mLoggedInUserId + ":" + mLoggedInUserDisplayName);
						writer.flush();
						writer.close();
					}
					catch (FileNotFoundException e) {
						sendValue(LoginStatus.BAD_CREDENTIALS, listener);
						return;
					}
					catch (Exception e) {
						sendValue(LoginStatus.COMM_FAILURE, listener);
						return;
					}
				}
			});
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

		establishCredentialsFromLocalStorage();
		return mLoggedInUserId != null;
	}


	private void establishCredentialsFromLocalStorage() {
		try {
			final FileInputStream fileInputStream = mContext.openFileInput(SESSION_CACHE);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
			String creds = reader.readLine();
			String[] splitCreds = creds.split(":");
			mLoggedInUserId = splitCreds[0];
			mLoggedInUserDisplayName = splitCreds[1];
			Log.i(TAG, "Creds hentet fra lokalt lager");
		}
		catch (IOException e) {
			Log.i(TAG, "Fant ikke creds i lokalt lager");
		}
		finally {
			mLocalSessionRead = true;
		}
	}

	private void removeCredsFromLocalStorage() {
		mContext.deleteFile(SESSION_CACHE);
		Log.i(TAG, "Creds fjernet fra lokalt lager");
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
