package org.lunders.client.android.bmk.services.impl.bilde;

import android.graphics.Bitmap;
import org.lunders.client.android.bmk.model.bilde.Bilde;

/**
* Copyright (c) 2014 - Gjensidige Forsikring ASA
* All rights reserved
* <p/>
* www.gjensidige.com
*
* @author G009430
*/
public interface DownloadListener<Token> {
	void onImageDownloaded(Token token, Bilde image);
}
