package cn.eovie.socks5.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.StringUtil;

/**
 * Created by earayu on 2017/8/29.
 */
public class Show extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        int len = byteBuf.readableBytes();
        byte[] arr = new byte[len];
        byteBuf.getBytes(0,arr);
        System.out.println(StringUtil.toHexString(arr));
        System.out.println(new String(arr, "UTF-8"));
        super.channelRead(ctx, msg);
    }
}
