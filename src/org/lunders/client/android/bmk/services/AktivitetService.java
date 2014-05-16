package org.lunders.client.android.bmk.services;

import org.lunders.client.android.bmk.model.aktivitet.AbstractAktivitet;

import java.util.Date;
import java.util.List;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public interface AktivitetService {
    List<AbstractAktivitet> hentAktiviteter(Date tilDato);
}
