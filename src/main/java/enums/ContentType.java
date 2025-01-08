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
	APPLICATION_OCTET_STREAM("application/octet-stream", ".unknown");

	private final String mimeType;

	private final String extention;

	ContentType(String mimeType, String extention) {
		this.mimeType = mimeType;
		this.extention = extention;
	}

	public static ContentType from(String extention){

		for(ContentType contentType : ContentType.values()){
			if(contentType.extention.equals(extention)){
				return contentType;
			}
		}

		return APPLICATION_OCTET_STREAM;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getExtention() {
		return extention;
	}
}
