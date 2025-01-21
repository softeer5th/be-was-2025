package model;

public class Article {
	private int id;
	private String title;
	private String content;
	private String userId;

	public Article(String content, String userId) {
		this.content = content;
		this.userId = userId;
	}

	public Article(int id, String title, String content, String userId) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.userId = userId;
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
}
