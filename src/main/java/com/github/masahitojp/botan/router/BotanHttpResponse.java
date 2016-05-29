package com.github.masahitojp.botan.router;

public class BotanHttpResponse {
	private int _status;
	private String _content = "";
	private String _contentType = "text/plain";

	@SuppressWarnings("unused")
	public int status() {
		return _status;
	}

	@SuppressWarnings("unused")
	public BotanHttpResponse status(final int status) {
		this._status = status;
		return this;
	}

	public String content() {
		return _content;
	}

	@SuppressWarnings("unused")
	public BotanHttpResponse content(final String content) {
		this._content = content;
		return this;
	}

	public String type() {
		return _contentType;
	}

	@SuppressWarnings("unused")
	public BotanHttpResponse type(final String contentType) {
		this._contentType = contentType;
		return this;
	}
}
