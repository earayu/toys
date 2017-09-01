package cn.eovie.time;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Created by earayu on 2017/9/1.
 */
public class TimeServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast("timeServerHandler", new TimeServerHandler());
    }

}
