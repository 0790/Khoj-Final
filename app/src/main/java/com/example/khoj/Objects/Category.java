package com.example.khoj.Objects;

public class Category {
	String name, url;

	public Category setName(String name) {
		this.name = name;
		return this;
	}

	public Category setUrl(String url) {
		this.url = url;
		return this;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
