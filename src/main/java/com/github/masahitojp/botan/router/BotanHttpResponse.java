package com.github.masahitojp.botan.router;

public class BotanHttpResponse {
	private int _status;
	private String _content = "";
	private String _contentType = "text/plain";

	public int status() {
		return _status;
	}

	public BotanHttpResponse status(final int status) {
		this._status = status;
		return this;
	}

	public String content() {
		return _content;
	}

	public BotanHttpResponse content(final String content) {
		this._content = content;
		return this;
	}

	public String type() {
		return _contentType;
	}

	public BotanHttpResponse type(final String contentType) {
		this._contentType = contentType;
		return this;
	}
}
