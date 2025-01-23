package enums;

public enum ContentType {
	TEXT_PLAIN("text/plain", "txt"),
	TEXT_HTML("text/html", "html"),
	TEXT_CSS("text/css", "css"),
	IMAGE_JPEG("image/jpeg", "jpeg"),
	IMAGE_JPG("image/jpeg", "jpg"),
	IMAGE_PNG("image/png", "png"),
	IMAGE_GIF("image/gif", "gif"),
	IMAGE_SVG("image/svg+xml", "svg"),
	IMAGE_ICO("image/x-icon", "ico"),
	APPLICATION_JAVASCRIPT("application/javascript", "js"),
	APPLICATION_JSON("application/json", "json"),
	APPLICATION_XML("application/xml", "xml"),
	APPLICATION_OCTET_STREAM("application/octet-stream", ".bin"),
	MULTIPART_FORM_DATA("multipart/form-data", "multipart");

	private final String mimeType;

	private final String extention;

	ContentType(String mimeType, String extention) {
		this.mimeType = mimeType;
		this.extention = extention;
	}

	/**
	 * From content type.
	 *
	 * @param extention the extention
	 * @return the content type
	 */
	public static ContentType from(String extention){

		for(ContentType contentType : ContentType.values()){
			if(contentType.extention.equals(extention)){
				return contentType;
			}
		}

		return APPLICATION_OCTET_STREAM;
	}

	/**
	 * Gets mime type.
	 *
	 * @return the mime type
	 */
	public String getMimeType() {
		return mimeType;
	}
}
