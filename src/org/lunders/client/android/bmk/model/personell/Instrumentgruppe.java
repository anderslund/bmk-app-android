package org.lunders.client.android.bmk.model.personell;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class Instrumentgruppe implements Serializable {

    private String navn;

    private List<Medlem> medlemmer;

    private Medlem gruppeleder;
}
