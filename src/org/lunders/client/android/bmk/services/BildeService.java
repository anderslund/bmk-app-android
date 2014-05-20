package org.lunders.client.android.bmk.services;

import org.lunders.client.android.bmk.model.bilde.Bilde;

import java.io.IOException;
import java.util.Collection;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public interface BildeService {
	Collection<Bilde> hentBilder() throws IOException;

	byte[] hentRaadata(String url) throws IOException;
}
