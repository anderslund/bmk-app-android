package org.lunders.client.android.bmk.services.impl.bilde;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.lunders.client.android.bmk.model.bilde.Bilde;
import org.lunders.client.android.bmk.services.BildeService;
import org.lunders.client.android.bmk.services.impl.AbstractServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class InstagramBildeServiceImpl extends AbstractServiceImpl implements BildeService {

	@Override
	public List<Bilde> hentBilder() throws IOException {
		List<Bilde> bilder = new ArrayList<>();

		byte[] jsonResponse = hentRaadata("https://api.instagram.com/v1/tags/borgemusikken/media/recent?client_id=49ba2748920b40abbc1102daf446f2a0");
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