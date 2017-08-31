package cn.eovie.socks5.handler;

import io.netty.channel.*;
import io.netty.handler.codec.socks.SocksCmdRequest;
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

    public ServerConnectionHandler(Channel outboundChannel,Channel browserChannel, Socks5CommandRequest request)
    {
        this.outboundChannel = outboundChannel;
        this.browserChannel = browserChannel;
        this.request = request;
        sendConnectRemoteMessage0();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Socks5CommandResponse)
        {
            outboundChannel.pipeline().remove(Socks5ClientEncoder.class);
            outboundChannel.pipeline().remove(ServerConnectionHandler.class);
            outboundChannel.pipeline().addLast(new RelayHandler(browserChannel));
            outboundChannel.pipeline().remove(Socks5CommandResponseDecoder.class);
            browserChannel.pipeline().remove(BrowserConnectionHandler.class);
            browserChannel.pipeline().addLast(new RelayHandler(outboundChannel));
            browserChannel.writeAndFlush(new DefaultSocks5CommandResponse(
                    Socks5CommandStatus.SUCCESS,
                    Socks5AddressType.IPv4, "127.0.0.1", 1444));
        }else {
            sendConnectRemoteMessage();
            outboundChannel.pipeline().addFirst(new Socks5CommandResponseDecoder());
        }
    }



    private void sendConnectRemoteMessage() throws InterruptedException {
        DefaultSocks5CommandRequest request1 = new DefaultSocks5CommandRequest(Socks5CommandType.CONNECT, request.dstAddrType(), request.dstAddr(), request.dstPort());
        outboundChannel.writeAndFlush(request1).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {


            }
        });
    }

    private void sendConnectRemoteMessage0()  {
        List<Socks5AuthMethod> authMethodList = new ArrayList<Socks5AuthMethod>();
        authMethodList.add(Socks5AuthMethod.NO_AUTH);
        outboundChannel.pipeline().addLast(Socks5ClientEncoder.DEFAULT);
        try {
            outboundChannel.writeAndFlush(new DefaultSocks5InitialRequest(authMethodList)).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
