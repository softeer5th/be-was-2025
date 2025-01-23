package dto;

import java.util.Optional;

/**
 * The type Cursor.
 *
 * @param <T>  the type parameter
 */
public class Cursor<T> {
	private final Optional<T> content;
	private final Integer prevPageNumber;
	private final Integer nextPageNumber;

	private final boolean hasPrevPage;
	private final boolean hasNextPage;

	/**
	 * Instantiates a new Cursor.
	 *
	 * @param content the content
	 * @param prevPageNumber the prev page number
	 * @param nextPageNumber the next page number
	 * @param hasPrevPage the has prev page
	 * @param hasNextPage the has next page
	 */
	public Cursor(Optional<T> content, Integer prevPageNumber, Integer nextPageNumber, boolean hasPrevPage,
		boolean hasNextPage) {
		this.content = content;
		this.prevPageNumber = prevPageNumber;
		this.nextPageNumber = nextPageNumber;
		this.hasPrevPage = hasPrevPage;
		this.hasNextPage = hasNextPage;
	}

	/**
	 * Gets content.
	 *
	 * @return the content
	 */
	public Optional<T> getContent() {
		return content;
	}

	/**
	 * Has prev page boolean.
	 *
	 * @return the boolean
	 */
	public boolean hasPrevPage() {
		return hasPrevPage;
	}

	/**
	 * Has next page boolean.
	 *
	 * @return the boolean
	 */
	public boolean hasNextPage() {
		return hasNextPage;
	}

	/**
	 * Gets prev page number.
	 *
	 * @return the prev page number
	 */
	public Integer getPrevPageNumber() {
		return prevPageNumber;
	}

	/**
	 * Gets next page number.
	 *
	 * @return the next page number
	 */
	public Integer getNextPageNumber() {
		return nextPageNumber;
	}
}
