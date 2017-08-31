package cn.eovie.socks5.handler;

import cn.eovie.socks5.boot.LocalServerConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;

/**
 * Created by earayu on 2017/8/30.
 */
public class BrowserConnectionHandler extends SimpleChannelInboundHandler<Socks5CommandRequest> {

    private LocalServerConfig config;

    public BrowserConnectionHandler(LocalServerConfig config) {
        this.config = config;
    }

    protected void channelRead0(final ChannelHandlerContext context, final Socks5CommandRequest request) throws Exception {

        Bootstrap b = new Bootstrap();
        b.group(context.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getRemoteServer().getTimeout())
                .option(ChannelOption.SO_KEEPALIVE, config.getRemoteServer().isKeepalive())
                .handler(new ServerConnectionHandler(context.channel(), request));

        b.connect(config.getRemoteServer().getAddr(), config.getRemoteServer().getPort())
                .addListener(future -> {
                    if (future.isSuccess()) {
                    } else {
                        context.channel().writeAndFlush(
                                new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, request.dstAddrType()));
//                    SocksServerUtils.closeOnFlush(context.channel());
                    }
                });
    }
}
