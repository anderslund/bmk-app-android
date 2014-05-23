package org.lunders.client.android.bmk.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public abstract class AbstractServiceImpl {

	public byte[] hentRaadata(String urlSpec) throws IOException {
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
