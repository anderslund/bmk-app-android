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

package org.lunders.client.android.bmk.services.impl.file;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import com.microsoft.live.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.lunders.client.android.bmk.services.BackendFileService;
import org.lunders.client.android.bmk.services.impl.AbstractServiceImpl;

import java.util.Arrays;

public class LiveServiceImpl extends AbstractServiceImpl implements LiveAuthListener, BackendFileService {

	private LiveAuthClient authClient;
	private LiveConnectClient liveClient;

	private static final String LIVE_CLIENT_ID = "000000004811DAAB";
	private static final String LIVE_CLIENT_SECRET = "oKYURXYnltPwdb3DMgguXUZSY75ooqaO";

	public static final Iterable<String> SCOPES =
		Arrays.asList("wl.signin", "wl.offline_access", "wl.skydrive", "wl.skydrive_update");

	private static final String TAG = LiveServiceImpl.class.getSimpleName();
	private String aktiviteterFolderId;
	private String twitterCacheFolderId;

	public LiveServiceImpl(Activity activity) {
		authClient = new LiveAuthClient(activity, LIVE_CLIENT_ID);
		authClient.initialize(SCOPES, this);
		//authClient.login(activity, SCOPES, this);
	}

	public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
		if (status == LiveStatus.CONNECTED) {
			Log.i(TAG, "Signed in.");
			liveClient = new LiveConnectClient(session);
			new VerifyFolderStructureTask().execute();
		}
		else {
			Log.i(TAG, "Not signed in.");
			liveClient = null;
		}
	}

	public void onAuthError(LiveAuthException exception, Object userState) {
		Log.i(TAG, "Error signing in: " + exception.getMessage());
		exception.printStackTrace();
		liveClient = null;
	}

	private class VerifyFolderStructureTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				JSONObject body = new JSONObject();

				body.put("name", "Application Data");
				LiveOperation op = liveClient.post("me/skydrive/files/", body);
				String folderId = getFolderId(op, "Application Data");

				body.put("name", "bmk-app");
				op = liveClient.post(folderId + "/files/", body);
				String bmkAppFolderId = getFolderId(op, "bmk-app");

				body.put("name", "twitter-cache");
				op = liveClient.post(bmkAppFolderId + "/files/", body);
				twitterCacheFolderId = getFolderId(op, "twitter-cache");

				body.put("name", "aktiviteter");
				op = liveClient.post(bmkAppFolderId + "/files/", body);
				aktiviteterFolderId = getFolderId(op, "aktiviteter");
			}
			catch (JSONException ex) {
				Log.w(TAG, "Error signing in: " + ex.getMessage());
			}
			catch (LiveOperationException ex) {
				Log.w(TAG, "Error signing in: " + ex.getMessage());
			}
			return null;
		}

		private String getFolderId(LiveOperation op, String s) {
			return "";
		}
	}
}
