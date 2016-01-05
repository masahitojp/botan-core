package com.github.masahitojp.botan.router;

@FunctionalInterface
public interface Route {
	Object handle(BotanHttpRequest request, BotanHttpResponse response);
}
