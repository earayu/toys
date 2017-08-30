package cn.eovie.socks5.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

import java.net.InetSocketAddress;

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
                        ChannelFuture responseFuture = context.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                                Socks5CommandStatus.SUCCESS,
                                request.dstAddrType(),
                                request.dstAddr(),
                                request.dstPort()));
                        responseFuture.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture channelFuture) {
                                context.pipeline().remove(BrowserConnectionHandler.this);
                                outboundChannel.pipeline().addLast(new RelayHandler(context.channel()));
                                context.pipeline().addLast(new RelayHandler(outboundChannel));
                            }
                        });
                    }
                }
        );

        Bootstrap b = new Bootstrap();
        b.group(context.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new DirectClientHandler(promise));

        b.connect(request.dstAddr(), request.dstPort()).addListener(new ChannelFutureListener() {
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
