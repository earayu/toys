package cn.eovie.socks5.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socks.SocksRequest;
import io.netty.handler.codec.socksx.v5.*;

/**
 * Created by earayu on 2017/8/30.
 */
public class BrowserHandler extends SimpleChannelInboundHandler<Socks5Message> {


    protected void channelRead0(ChannelHandlerContext context, Socks5Message socks5Message) throws Exception {
        if(socks5Message instanceof Socks5InitialRequest)
        {
            context.channel().writeAndFlush(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH));
            context.pipeline().addFirst(new Socks5CommandRequestDecoder());
        }else if(socks5Message instanceof Socks5CommandRequest){
            Socks5CommandRequest socks5CommandRequest = (Socks5CommandRequest) socks5Message;
            if(socks5CommandRequest.type()==Socks5CommandType.CONNECT) {
                context.pipeline().addLast(new BrowserConnectionHandler());
                context.pipeline().remove(this);
                context.fireChannelRead(socks5Message);
            }else {
                context.close();
            }
        }else {
            context.close();
        }
    }
}
