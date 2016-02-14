package com.mermix.model.common;

import java.io.Serializable;

/**
 * Created on 06/08/2015
 * Description:
 * field image of drupal entity 'node'
 */
public class Image implements Serializable{
	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public class File implements Serializable {
		private String uri;
		private String id;

		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
}
