package com.github.masahitojp.botan.router;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.router.RouteResult;
import io.netty.handler.codec.http.router.Router;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
public class HttpRouterServerHandler extends SimpleChannelInboundHandler<HttpRequest> {
	private final Router<Route> router;

	public HttpRouterServerHandler(Router<Route> router) {
		this.router = router;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpRequest req) {
		if (HttpHeaders.is100ContinueExpected(req)) {
			ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
			return;
		}

		HttpResponse res = createResponse(req, router);
		flushResponse(ctx, req, res);
	}

	private static HttpResponse createResponse(HttpRequest req, Router<Route> router) {
		RouteResult<Route> routeResult = router.route(req.getMethod(), req.getUri());
		final BotanHttpResponse obj = routeResult.target().handle(new BotanHttpRequest(routeResult), new BotanHttpResponse());
		final String content = obj.content();

		FullHttpResponse res = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.copiedBuffer(content, CharsetUtil.UTF_8)
		);
		res.headers().set(HttpHeaders.Names.CONTENT_TYPE, obj.type());
		res.headers().set(HttpHeaders.Names.CONTENT_LENGTH, res.content().readableBytes());
		return res;
	}

	private static ChannelFuture flushResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
		if (!HttpHeaders.isKeepAlive(req)) {
			return ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
		} else {
			res.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
			return ctx.writeAndFlush(res);
		}
	}
}
