package com.github.masahitojp.botan.router;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.BadClientSilencer;
import io.netty.handler.codec.http.router.Router;

/**
 *
 */
public class HttpRouterServerInitializer extends ChannelInitializer<SocketChannel> {
	private final HttpRouterServerHandler handler;
	private final BadClientSilencer badClientSilencer = new BadClientSilencer();

	public HttpRouterServerInitializer(final Router<Route> router) {
		handler = new HttpRouterServerHandler(router);
	}

	@Override
	public void initChannel(final SocketChannel ch) {
		ch.pipeline()
				.addLast(new HttpServerCodec())
				.addLast(handler)
				.addLast(badClientSilencer);
	}
}
