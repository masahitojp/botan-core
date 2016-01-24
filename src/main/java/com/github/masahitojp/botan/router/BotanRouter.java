package com.github.masahitojp.botan.router;

import com.github.masahitojp.botan.utils.BotanUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.router.Router;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class BotanRouter {
    protected final Map<String, Route> httpget = new HashMap<>();
    protected final Map<String, Route> httppost = new HashMap<>();
    final ExecutorService executor = Executors.newSingleThreadExecutor();

    public final void GET(String path, Route route) {
        httpget.put(path, route);
    }
    public final void POST(String path, Route route) {
        httppost.put(path, route);
    }

    public final void startWeb() {
        if (httpget.size() + httppost.size() >0) {
            executor.submit(() -> {
                final String addr = BotanUtils.envToOpt("HTTP_IP_ADDR").orElse("0.0.0.0");
                final int port = Integer.valueOf(BotanUtils.envToOpt("HTTP_PORT").orElse("8080"));
                final Router<Route> router = new Router<>();

                httpget.forEach(router::GET);
                httppost.forEach(router::POST);
                final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
                final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

                try {
                    final ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                            .childOption(ChannelOption.TCP_NODELAY, java.lang.Boolean.TRUE)
                            .childOption(ChannelOption.SO_KEEPALIVE, java.lang.Boolean.TRUE)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new HttpRouterServerInitializer(router));
                    log.info("RESTful API: {}:{}", addr, port);
                    final Channel ch = b.bind(addr, port).sync().channel();
                    ch.closeFuture().sync();
                } catch (final Exception ignore) {
                    //
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            });
        }
    }

    public final void beforeShutdown() {
        executor.shutdownNow();
    }
}
