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
		final RouteResult<Route> routeResult = router.route(req.getMethod(), req.getUri());

		final BotanHttpResponse res = new BotanHttpResponse();
		final Object obj = routeResult.target().handle(new BotanHttpRequest(routeResult), res);
		final String content;
		final String type;
		final HttpResponseStatus responseStatus;
		if (obj instanceof BotanHttpResponse) {
			content = ((BotanHttpResponse) obj).content();
			type = ((BotanHttpResponse) obj).type();
			responseStatus = HttpResponseStatus.OK;
		} else if (obj instanceof Integer) {
			content= res.content();
			type = res.type();
			responseStatus = HttpResponseStatus.valueOf((int)obj);
			return new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, responseStatus);
		} else {
			content= obj.toString();
			type = res.type();
			responseStatus = HttpResponseStatus.OK;
		}
		final FullHttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1, responseStatus,
				Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));

		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, type);
		response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
		return response;
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
