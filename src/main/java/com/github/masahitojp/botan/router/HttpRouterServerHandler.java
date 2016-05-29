package com.github.masahitojp.botan.router;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.router.RouteResult;
import io.netty.handler.codec.http.router.Router;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable
class HttpRouterServerHandler extends SimpleChannelInboundHandler<HttpObject> {
	private final Router<Route> router;
	private HttpRequest request;
	private final StringBuilder buf = new StringBuilder();
	HttpRouterServerHandler(Router<Route> router) {
		this.router = router;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
	@Override
	public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
		if (msg instanceof HttpRequest) {
			final HttpRequest request = this.request = (HttpRequest) msg;
			if (HttpUtil.is100ContinueExpected(request)) {
				ctx.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
				return;
			}
		}
		if (msg instanceof HttpContent) {
			final HttpContent httpContent = (HttpContent) msg;

			ByteBuf content = httpContent.content();
			if (content.isReadable()) {
				// http body
				buf.append(content.toString(CharsetUtil.UTF_8));
			}

			if (msg instanceof LastHttpContent) {
				HttpResponse res = createResponse(request, router,buf.toString());
				flushResponse(ctx, res);
			}
		}
	}

	private static HttpResponse createResponse(HttpRequest req, Router<Route> router, String body) {
		final RouteResult<Route> routeResult = router.route(req.method(), req.uri());
		if (routeResult != null) {
			final BotanHttpResponse res = new BotanHttpResponse();
			final Object obj = routeResult.target().handle(new BotanHttpRequest(routeResult, body), res);
			final String content;
			final String type;
			final HttpResponseStatus responseStatus;
			if (obj instanceof BotanHttpResponse) {
				content = ((BotanHttpResponse) obj).content();
				type = ((BotanHttpResponse) obj).type();
				responseStatus = HttpResponseStatus.OK;
			} else if (obj instanceof Integer) {
				responseStatus = HttpResponseStatus.valueOf((int) obj);
				return new DefaultFullHttpResponse(
						HttpVersion.HTTP_1_1, responseStatus);
			} else {
				content = obj.toString();
				type = res.type();
				responseStatus = HttpResponseStatus.OK;
			}
			final FullHttpResponse response = new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, responseStatus,
					Unpooled.copiedBuffer(content, CharsetUtil.UTF_8));

			response.headers().set(HttpHeaderNames.CONTENT_TYPE, type);
			response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
			return response;
		} else {
			return new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
		}
	}

	private static void flushResponse(ChannelHandlerContext ctx, HttpResponse res) {
		ctx.write(res);
		ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
	}
}
