package model;

/**
 * The type Article.
 */
public class Article {
	private int id;
	private String title;
	private String content;
	private String userId;
	private byte[] image;

	/**
	 * Instantiates a new Article.
	 *
	 * @param content the content
	 * @param userId the user id
	 * @param image the image
	 */
	public Article(String content, String userId, byte[] image) {
		this.content = content;
		this.userId = userId;
		this.image = image;
	}

	/**
	 * Instantiates a new Article.
	 *
	 * @param id the id
	 * @param title the title
	 * @param content the content
	 * @param userId the user id
	 * @param image the image
	 */
	public Article(int id, String title, String content, String userId, byte[] image) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.userId = userId;
		this.image = image;
	}

	/**
	 * Gets title.
	 *
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets user id.
	 *
	 * @return the user id
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Gets content.
	 *
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Get image byte [ ].
	 *
	 * @return the byte [ ]
	 */
	public byte[] getImage() {
		return image;
	}
}
