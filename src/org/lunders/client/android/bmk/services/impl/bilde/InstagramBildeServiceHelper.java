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

package org.lunders.client.android.bmk.services.impl.bilde;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.lunders.client.android.bmk.model.bilde.Bilde;
import org.lunders.client.android.bmk.services.BildeService;
import org.lunders.client.android.bmk.services.impl.ServiceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstagramBildeServiceHelper implements BildeService {

	@Override
	public List<Bilde> hentBilder() throws IOException {
		List<Bilde> bilder = new ArrayList<>();

		byte[] jsonResponse = ServiceHelper.hentRaadata("https://api.instagram.com/v1/tags/borgemusikken/media/recent?client_id=49ba2748920b40abbc1102daf446f2a0");
		JSONTokener tokener = new JSONTokener(new String(jsonResponse));
		try {
			final JSONObject jo = new JSONObject(tokener);
			final JSONArray imageData = jo.getJSONArray("data");
			//For hver "data":
			for (int i = 0; i < imageData.length(); i++) {
				JSONObject data = (JSONObject) imageData.get(i);

				//Hent ut objektet som heter "thumbnail" under "images"
				JSONObject thumbnail = data.getJSONObject("images").getJSONObject("thumbnail");
				JSONObject fullSize = data.getJSONObject("images").getJSONObject("standard_resolution");
				int numLikes = data.getJSONObject("likes").getInt("count");
				final JSONObject caption = data.getJSONObject("caption");
				String text = caption.getString("text");
				String from = caption.getJSONObject("from").getString("full_name");


				//Legg URLen til bildet til i listen
				final Bilde bilde = new Bilde(thumbnail.getString("url"));
				bilde.setFullSizeUrl(fullSize.getString("url"));
				bilde.setBeskrivelse(text);
				bilde.setFotograf(from);
				bilde.setNumLikes(numLikes);
				bilder.add(bilde);
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return bilder;
	}
}