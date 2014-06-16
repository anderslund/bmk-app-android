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

package org.lunders.client.android.bmk.fragments.felles;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;
import org.lunders.client.android.bmk.R;
import org.lunders.client.android.bmk.services.SessionService;
import org.lunders.client.android.bmk.services.impl.session.SessionServiceImpl;
import org.lunders.client.android.bmk.util.DisplayUtil;


public class LogoutFragment extends DialogFragment implements SessionService.LogoutListener {

	private SessionServiceImpl mSessionService;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		mSessionService = SessionServiceImpl.getInstance(getActivity());

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
			.setMessage(R.string.logout_message)
			.setTitle(R.string.logout_title)
			.setPositiveButton(
				R.string.logout_title, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int id) {
						mSessionService.logout(LogoutFragment.this);
						dismiss();
					}
				})

			.setNegativeButton(
				R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						LogoutFragment.this.getDialog().cancel();
					}
				});
		return builder.create();
	}

	@Override
	public void logoutAttempted(SessionService.LoginStatus status) {
		getActivity().recreate();
		DisplayUtil.showToast(getActivity(), R.string.logout_ok, Toast.LENGTH_LONG);
	}

}
