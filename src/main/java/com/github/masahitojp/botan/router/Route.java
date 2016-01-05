package com.github.masahitojp.botan.router;

@FunctionalInterface
public interface Route {
	BotanHttpResponse handle(BotanHttpRequest request, BotanHttpResponse response);
}
