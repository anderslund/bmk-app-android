package org.lunders.client.android.bmk.services;

import org.lunders.client.android.bmk.model.nyheter.Nyhet;

import java.util.List;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public interface NyhetService {
    List<Nyhet> hentNyheter();
}
