package cn.eovie.socks5.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.socksx.v5.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by earayu on 2017/8/30.
 */
public class ServerConnectionHandler extends ChannelInboundHandlerAdapter {

    private Channel outboundChannel;
    private Channel browserChannel;
    private Socks5CommandRequest request;

    public ServerConnectionHandler(Channel browserChannel, Socks5CommandRequest request) {
        this.browserChannel = browserChannel;
        this.request = request;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.outboundChannel = ctx.channel();
        sendSocks5InitMsg();
        ctx.fireChannelActive();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Socks5InitialResponse) {
            sendSocks5CmdMsg();
        } else if (msg instanceof Socks5CommandResponse) {
            outboundChannel.pipeline().remove(Socks5ClientEncoder.class);
            outboundChannel.pipeline().remove(ServerConnectionHandler.class);
            outboundChannel.pipeline().remove(Socks5CommandResponseDecoder.class);
            outboundChannel.pipeline().addLast(new RelayHandler(browserChannel));
            browserChannel.pipeline().remove(Socks5CommandRequestDecoder.class);
            browserChannel.pipeline().remove(BrowserConnectionHandler.class);
            browserChannel.pipeline().addLast(new RelayHandler(outboundChannel));
            browserChannel.writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, request.dstAddrType(), request.dstAddr(), request.dstPort()))
                    .addListener(channelFuture -> browserChannel.pipeline().remove(Socks5ServerEncoder.class));
        }
    }




    private void sendSocks5CmdMsg() throws InterruptedException {
        DefaultSocks5CommandRequest request1 = new DefaultSocks5CommandRequest(Socks5CommandType.CONNECT, request.dstAddrType(), request.dstAddr(), request.dstPort());
        outboundChannel.writeAndFlush(request1)
                .addListener(channelFuture -> {
                    outboundChannel.pipeline().remove(Socks5InitialResponseDecoder.class);
                    outboundChannel.pipeline().addFirst(new Socks5CommandResponseDecoder());
                });
    }

    private void sendSocks5InitMsg() {
        List<Socks5AuthMethod> authMethodList = new ArrayList<>();
        authMethodList.add(Socks5AuthMethod.NO_AUTH);
        outboundChannel.pipeline().addLast(Socks5ClientEncoder.DEFAULT);
        outboundChannel.writeAndFlush(new DefaultSocks5InitialRequest(authMethodList))
                .addListener(channelFuture -> outboundChannel.pipeline().addFirst(new Socks5InitialResponseDecoder()));
    }

    public void setOutboundChannel(Channel outboundChannel) {
        this.outboundChannel = outboundChannel;
    }
}
