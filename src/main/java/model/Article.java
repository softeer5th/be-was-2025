package model;

public class Article {
	private int id;
	private String title;
	private String content;
	private String userId;
	private byte[] image;

	public Article(String content, String userId, byte[] image) {
		this.content = content;
		this.userId = userId;
		this.image = image;
	}

	public Article(int id, String title, String content, String userId, byte[] image) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.userId = userId;
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public String getUserId() {
		return userId;
	}

	public String getContent() {
		return content;
	}

	public byte[] getImage() {
		return image;
	}
}
