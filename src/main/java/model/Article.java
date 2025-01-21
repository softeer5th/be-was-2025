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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
