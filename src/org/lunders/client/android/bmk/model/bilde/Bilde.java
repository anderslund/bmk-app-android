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

	private String thumbnailUrl, fullSizeUrl;

	private String fotograf;

	private int numLikes;

	private String beskrivelse;

	private byte[] thumbnailBytes;
	private byte[] fullSizeBytes;

	public Bilde() {
	}

	public Bilde(String url) {
		this.thumbnailUrl = url;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public String getFullSizeUrl() {
		return fullSizeUrl;
	}

	public void setFullSizeUrl(String fullSizeUrl) {
		this.fullSizeUrl = fullSizeUrl;
	}

	public String getFotograf() {
		return fotograf;
	}

	public void setFotograf(String fotograf) {
		this.fotograf = fotograf;
	}

	public int getNumLikes() {
		return numLikes;
	}

	public void setNumLikes(int numLikes) {
		this.numLikes = numLikes;
	}

	public String getBeskrivelse() {
		return beskrivelse;
	}

	public void setBeskrivelse(String beskrivelse) {
		this.beskrivelse = beskrivelse;
	}

	public byte[] getThumbnailBytes() {
		return thumbnailBytes;
	}

	public void setThumbnailBytes(byte[] thumbnailBytes) {
		this.thumbnailBytes = thumbnailBytes;
	}

	public byte[] getFullSizeBytes() {
		return fullSizeBytes;
	}

	public void setFullSizeBytes(byte[] fullSizeBytes) {
		this.fullSizeBytes = fullSizeBytes;
	}
}
