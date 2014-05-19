package org.lunders.client.android.bmk.model.bilde;

import java.io.Serializable;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class Bilde implements Serializable {

	private String url;

	public Bilde() {
	}

	public Bilde(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
