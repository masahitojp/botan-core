package com.github.masahitojp.botan.router;

import io.netty.handler.codec.http.router.RouteResult;
import java.util.Optional;

public class BotanHttpRequest {
	private final RouteResult result;
	private final String body;
	public BotanHttpRequest(final RouteResult result, String body) {
		this.result = result;
		this.body = body;
	}

	@SuppressWarnings("unused")
	public Optional<String> params(String key) {
		return Optional.ofNullable(result.param(key));
	}

	@SuppressWarnings("unused")
	public String body() {
		return body;
	}

}
