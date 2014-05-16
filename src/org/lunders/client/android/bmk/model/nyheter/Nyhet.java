package org.lunders.client.android.bmk.model.nyheter;

import java.io.Serializable;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class Nyhet implements Serializable, Comparable<Nyhet> {

    private String header;

    private String content;

    private Nyhetskilde kilde;

    public Nyhet(String header, String content, Nyhetskilde kilde) {
        this.header = header;
        this.content = content;
        this.kilde = kilde;
    }

    public String getHeader() {
        return header;
    }

    public String getContent() {
        return content;
    }

    public Nyhetskilde getKilde() {
        return kilde;
    }

	@Override
	public String toString() {
		return String.format("%s: %s\n%s", kilde, header, content);
	}

	@Override
	public int compareTo(Nyhet another) {
		return 0;
	}
}
