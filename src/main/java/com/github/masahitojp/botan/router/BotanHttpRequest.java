package com.github.masahitojp.botan.router;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.router.RouteResult;

import java.util.Optional;

public class BotanHttpRequest {
	private final RouteResult result;
	public BotanHttpRequest(final RouteResult result) {
		this.result = result;
	}

	public Optional<String> params(String key) {
		return Optional.ofNullable(result.param(key));
	}

}
