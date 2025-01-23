package dto;

import java.util.Optional;

public class Cursor<T> {
	private final Optional<T> content;
	private final Integer prevPageNumber;
	private final Integer nextPageNumber;

	private final boolean hasPrevPage;
	private final boolean hasNextPage;

	public Cursor(Optional<T> content, Integer prevPageNumber, Integer nextPageNumber, boolean hasPrevPage,
		boolean hasNextPage) {
		this.content = content;
		this.prevPageNumber = prevPageNumber;
		this.nextPageNumber = nextPageNumber;
		this.hasPrevPage = hasPrevPage;
		this.hasNextPage = hasNextPage;
	}

	public Optional<T> getContent() {
		return content;
	}

	public boolean hasPrevPage() {
		return hasPrevPage;
	}

	public boolean hasNextPage() {
		return hasNextPage;
	}

	public Integer getPrevPageNumber() {
		return prevPageNumber;
	}

	public Integer getNextPageNumber() {
		return nextPageNumber;
	}
}
