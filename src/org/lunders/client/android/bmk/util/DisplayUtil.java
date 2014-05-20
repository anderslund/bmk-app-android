package org.lunders.client.android.bmk.util;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Copyright (c) 2014 - Gjensidige Forsikring ASA
 * All rights reserved
 * <p/>
 * www.gjensidige.com
 *
 * @author G009430
 */
public class DisplayUtil {

	public static int spToPixels(Resources r, int spSize) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spSize, r.getDisplayMetrics());
	}
}
