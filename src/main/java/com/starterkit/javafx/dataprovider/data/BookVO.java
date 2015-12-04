package com.starterkit.javafx.dataprovider.data;

import java.util.List;

public class BookVO {

	private String title;
	private List<AuthorVO> authors;

	public BookVO(String title, List<AuthorVO> authors) {
		this.title = title;
		this.authors = authors;

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<AuthorVO> getAuthors() {
		return authors;
	}

	public void setAuthors(List<AuthorVO> authors) {
		this.authors = authors;
	}

}
