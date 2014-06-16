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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.lunders.client.android.bmk.R;
import org.lunders.client.android.bmk.services.SessionService;
import org.lunders.client.android.bmk.services.impl.session.SessionServiceImpl;
import org.lunders.client.android.bmk.util.DisplayUtil;


public class LoginFragment extends DialogFragment implements SessionService.LoginListener {

	private SessionService mSessionService;

	public static LoginFragment newInstance() {
		LoginFragment fragment = new LoginFragment();
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mSessionService = SessionServiceImpl.getInstance(getActivity());
		View theView = getActivity().getLayoutInflater().inflate(R.layout.dialog_login, null);

		final TextView tvUsername = (TextView) theView.findViewById(R.id.login_username);
		final TextView tvPassword = (TextView) theView.findViewById(R.id.login_password);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(theView)

			.setPositiveButton(
				R.string.signin, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						CharSequence username = tvUsername.getText();
						CharSequence password = tvPassword.getText();
						mSessionService.login(username, password, LoginFragment.this);
						dismiss();
					}
				})

			.setNegativeButton(
				R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						LoginFragment.this.getDialog().cancel();
					}
				});
		return builder.create();
	}

	@Override
	public void loginAttempted(SessionService.LoginStatus loginStatus) {
		if (loginStatus == SessionServiceImpl.LoginStatus.OK) {
			getActivity().recreate();
			DisplayUtil.showToast(getActivity(), R.string.login_ok, Toast.LENGTH_LONG);
		}
		else if (loginStatus == SessionServiceImpl.LoginStatus.MISSING_USERNAME) {
			DisplayUtil.showToast(getActivity(), R.string.login_brukernavn_mangler, Toast.LENGTH_LONG);
		}
		else if (loginStatus == SessionServiceImpl.LoginStatus.MISSING_PASSWORD) {
			DisplayUtil.showToast(getActivity(), R.string.login_passord_mangler, Toast.LENGTH_LONG);
		}
		else if (loginStatus == SessionServiceImpl.LoginStatus.BAD_CREDENTIALS) {
			DisplayUtil.showToast(getActivity(), R.string.login_bad_creds, Toast.LENGTH_LONG);
		}
		else if (loginStatus == SessionServiceImpl.LoginStatus.COMM_FAILURE) {
			DisplayUtil.showToast(getActivity(), R.string.login_comm_failure, Toast.LENGTH_LONG);
		}
	}

}
