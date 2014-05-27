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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.microsoft.live.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;
import org.lunders.client.android.bmk.services.AktivitetService;
import org.lunders.client.android.bmk.services.BackendFileService;
import org.lunders.client.android.bmk.services.impl.AbstractServiceImpl;
import org.lunders.client.android.bmk.util.StringUtil;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LiveServiceImpl extends AbstractServiceImpl implements LiveAuthListener, BackendFileService {

	private LiveAuthClient authClient;
	private LiveConnectClient liveClient;
	private String aktiviteterFileId;
	private String twitterFileId;
	private Set<BackendFileServiceListener> backendListeners;

	private static final String LIVE_CLIENT_ID = "000000004811DAAB";
	private static final String LIVE_CLIENT_SECRET = "oKYURXYnltPwdb3DMgguXUZSY75ooqaO";
	private static final String FOLDER_BMK_APP = "bmk-app";
	public static final String FOLDER_ROOT = "Application Data";
	public static final String FOLDER_TWITTER = "twitter-cache";
	public static final String FILE_TWITTER = "twitter-cache.json";
	public static final String FOLDER_AKTIVITETER = "aktiviteter";
	public static final String FILE_AKTIVITETER = "aktiviteter.yaml";
	public static final String FOLDER_SKYDRIVE = "me/skydrive/";

	private static final Iterable<String> SCOPES = Arrays.asList("wl.signin", "wl.offline_access", "wl.skydrive", "wl.skydrive_update");

	private static final String TAG = LiveServiceImpl.class.getSimpleName();

	private static LiveServiceImpl INSTANCE;
	private Context activity;

	public static LiveServiceImpl getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new LiveServiceImpl(context);
		}
		return INSTANCE;
	}

	LiveServiceImpl(Context activity) {
		this.activity = activity;
		authClient = new LiveAuthClient(activity, LIVE_CLIENT_ID);
		authClient.initialize(SCOPES, this);
		//authClient.login(activity, SCOPES, this);

		backendListeners = new HashSet<>();
	}

	@Override
	public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
		if (status == LiveStatus.CONNECTED) {
			Log.i(TAG, "Signed in.");
			liveClient = new LiveConnectClient(session);

			aktiviteterFileId = readFileIdFromStorage("aktiviteter");
			twitterFileId = readFileIdFromStorage("twitter");

			if (aktiviteterFileId == null || twitterFileId == null) {
				new VerifyFolderStructureTask().execute();
			}
			else {
				notifyBackendListeners();
			}
		}
		else {
			Log.i(TAG, "Not signed in.");
			liveClient = null;
		}
	}

	@Override
	public void onAuthError(LiveAuthException exception, Object userState) {
		Log.i(TAG, "Error signing in: " + exception.getMessage());
		exception.printStackTrace();
		liveClient = null;
	}

	@Override
	public void addBackendListener(BackendFileServiceListener listener) {
		this.backendListeners.add(listener);
	}

	@Override
	public void removeBackendListener(BackendFileServiceListener listener) {
		this.backendListeners.remove(listener);
	}

	@Override
	public void hentAktiviteter(AktivitetService.AktivitetListener listener) {
		List<AbstractAktivitet> result = loadAktiviteterFromStorage();

		new HentAktiviteterThread(activity, liveClient, listener, aktiviteterFileId).start();

		if ( result != null && !result.isEmpty()) {
			listener.onAktiviteterHentet(result);
		}
	}

	private List<AbstractAktivitet> loadAktiviteterFromStorage() {
		try {
			final FileInputStream fis = activity.openFileInput("aktivitetCache");
			final ObjectInputStream ois = new ObjectInputStream(fis);
			List<AbstractAktivitet> aktiviteter = (List<AbstractAktivitet>) ois.readObject();
			return aktiviteter;
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private class VerifyFolderStructureTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void aVoid) {
			notifyBackendListeners();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				long t0 = System.currentTimeMillis();
				String rootFolderId = getFileId(FOLDER_SKYDRIVE, FOLDER_ROOT);
				if (StringUtil.isBlank(rootFolderId)) {
					rootFolderId = createFolder(FOLDER_SKYDRIVE, FOLDER_ROOT);
				}

				String bmkAppFolderId = getFileId(rootFolderId, FOLDER_BMK_APP);
				if (StringUtil.isBlank(bmkAppFolderId)) {
					bmkAppFolderId = createFolder(rootFolderId, FOLDER_BMK_APP);
				}

				String twitterCacheFolderId = getFileId(bmkAppFolderId, FOLDER_TWITTER);
				if (StringUtil.isBlank(twitterCacheFolderId)) {
					twitterCacheFolderId = createFolder(bmkAppFolderId, FOLDER_TWITTER);
				}

				String aktiviteterFolderId = getFileId(bmkAppFolderId, FOLDER_AKTIVITETER);
				if (StringUtil.isBlank(aktiviteterFolderId)) {
					aktiviteterFolderId = createFolder(bmkAppFolderId, FOLDER_AKTIVITETER);
				}

				aktiviteterFileId = getFileId(aktiviteterFolderId, FILE_AKTIVITETER);
				if (StringUtil.isBlank(aktiviteterFileId)) {
					aktiviteterFileId = createFile(aktiviteterFolderId, FILE_AKTIVITETER);
				}
				writeFileIdToStorage("aktiviteter", aktiviteterFileId);

				twitterFileId = getFileId(twitterCacheFolderId, FILE_TWITTER);
				if (StringUtil.isBlank(twitterFileId)) {
					twitterFileId = createFile(twitterCacheFolderId, FILE_TWITTER);
				}
				writeFileIdToStorage("twitter", twitterFileId);

				long t1 = System.currentTimeMillis();
				Log.i(TAG, "Confirmed Live.com folder structure in " + (t1 - t0) + " ms");
			}
			catch (JSONException ex) {
				Log.w(TAG, "Failed to verify folder structure");
				ex.printStackTrace();
			}
			catch (LiveOperationException ex) {
				Log.w(TAG, "Failed to verify folder structure");
				ex.printStackTrace();
			}
			catch (FileNotFoundException e) {
				Log.w(TAG, "Failed to write to local storage");
				e.printStackTrace();
			}
			return null;
		}
	}

	private void notifyBackendListeners() {
		for (BackendFileServiceListener listener : backendListeners) {
			listener.onBackendReady(this);
		}
	}

	private void writeFileIdToStorage(String fileName, String fileId) throws FileNotFoundException {
		final FileOutputStream fos = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
		final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(fos));
		printWriter.println(fileId);
		printWriter.flush();
		printWriter.close();
	}

	private String readFileIdFromStorage(String fileName) {
		BufferedReader reader = null;
		try {
			final FileInputStream fis = activity.openFileInput(fileName);
			reader = new BufferedReader(new InputStreamReader(fis));
			return reader.readLine();
		}
		catch (IOException e) {
			Log.w(TAG, "Failed to read fileID from local storage");
			return null;
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException e) {
					//Ignore
				}
			}
		}
	}

	private String getFileId(String parentFolderId, String folderName) throws LiveOperationException, JSONException {
		final LiveOperation op = liveClient.get(parentFolderId + "/files/");
		final JSONObject result = op.getResult();
		JSONArray data = result.getJSONArray("data");
		if (data == null) {
			return null;
			//TODO throw something?
		}
		for (int i = 0; i < data.length(); i++) {
			JSONObject jsonObject = data.getJSONObject(i);
			String name = jsonObject.getString("name");
			if (folderName.equals(name)) {
				return jsonObject.getString("id");
			}
		}
		return null;
	}

	private String createFolder(String parentFolderId, String folderName) throws LiveOperationException, JSONException {
		JSONObject body = new JSONObject();
		body.put("name", folderName);
		final LiveOperation op = liveClient.post(parentFolderId, body);
		return op.getResult().getString("id");
	}

	private String createFile(String parentFolderId, String fileName) throws LiveOperationException, JSONException {
		JSONObject body = new JSONObject();
		body.put("name", fileName);
		final LiveOperation op = liveClient.put(parentFolderId + "/files/" + fileName, body);
		return op.getResult().getString("id");
	}


}
