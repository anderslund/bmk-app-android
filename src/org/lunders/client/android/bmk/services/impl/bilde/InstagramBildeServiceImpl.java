package org.lunders.client.android.bmk.services.impl.bilde;

import android.net.Uri;
import org.lunders.client.android.bmk.model.bilde.Bilde;
import org.lunders.client.android.bmk.services.BildeService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
public class InstagramBildeServiceImpl implements BildeService {

	@Override
	public List<Bilde> hentBilder() {
		List<Bilde> bilder = new ArrayList<>();
		bilder.add(new Bilde("http://origincache-ash.fbcdn.net/1169860_710814422284602_1933998227_s.jpg"));
		bilder.add(new Bilde("http://origincache-prn.fbcdn.net/925231_641994542537743_607653590_s.jpg"));
		bilder.add(new Bilde("http://origincache-prn.fbcdn.net/1208434_316013088536440_1698313592_s.jpg"));
		return bilder;
	}

	@Override
	public byte[] hentBilderaadata(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		try {
			InputStream in = connection.getInputStream();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			int read = 0;
			byte[] buffer = new byte[1024];
			while ((read = in.read(buffer)) > 0) {
				baos.write(buffer, 0, read);
			}
			baos.close();
			return baos.toByteArray();

		}
		finally {
			connection.disconnect();
		}
	}
}
