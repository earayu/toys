package cn.eovie.socks5.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by earayu on 2017/8/30.
 */
public class BrowserConnectionHandler extends SimpleChannelInboundHandler<Socks5CommandRequest> {
    protected void channelRead0(final ChannelHandlerContext context, final Socks5CommandRequest request) throws Exception {
        Promise<Channel> promise = context.executor().newPromise();
        promise.addListener(
                new FutureListener<Channel>() {
                    @Override
                    public void operationComplete(final Future<Channel> future) throws Exception {
                        final Channel outboundChannel = future.getNow();
                        outboundChannel.pipeline().addLast(new ServerConnectionHandler(outboundChannel, context.channel(), request));
                    }
                }
        );

        Bootstrap b = new Bootstrap();
        b.group(context.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new DirectClientHandler(promise));

        b.connect("127.0.0.1", 1083).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                } else {
                    context.channel().writeAndFlush(
                            new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, request.dstAddrType()));
//                    SocksServerUtils.closeOnFlush(context.channel());
                }
            }
        });

    }
}
