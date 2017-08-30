package cn.eovie.socks5.boot;

import cn.eovie.socks5.handler.BrowserHandler;
import cn.eovie.socks5.handler.Show;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socks.*;
import io.netty.handler.codec.socksx.v5.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by earayu on 2017/8/29.
 */
public class Client {

    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        protected void initChannel(NioSocketChannel sch) throws Exception {
                            sch.pipeline()
//                                    .addFirst(new Show())
                                    .addLast(new Socks5InitialRequestDecoder())
                                    .addLast(new BrowserHandler())
                                    .addLast(Socks5ServerEncoder.DEFAULT);
                        }
                    });
        ChannelFuture future = b.bind("127.0.0.1", 1082);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
//
//            future.addListener(new ChannelFutureListener() {
//        public void operationComplete(ChannelFuture channelFuture) throws Exception {
//            Channel channel = channelFuture.channel();
//            channel.writeAndFlush(init());
//            Thread.currentThread().sleep(1000);
//            channel.writeAndFlush(cmd());
//            Thread.currentThread().sleep(1000);
//            channel.writeAndFlush(msg());
//
//        }
//    });

    public static Socks5InitialRequest init()
    {
        List<Socks5AuthMethod> methods = new ArrayList<Socks5AuthMethod>();
        methods.add(Socks5AuthMethod.NO_AUTH);
        Socks5InitialRequest result = new DefaultSocks5InitialRequest(methods);
        return result;
    }

    public static Socks5CommandRequest cmd()
    {
        Socks5CommandRequest result = new DefaultSocks5CommandRequest(Socks5CommandType.CONNECT,
                Socks5AddressType.IPv4,
                "127.0.0.1",
                1444);
        return result;
    }

    public static ByteBuf msg()
    {
        ByteBuf byteBuf = Unpooled.buffer();
        byte[] bytes = hexStringToBytes(req);
        try {
            System.out.println(new String(bytes, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }

    public static String req = "474554202f68656c6c6f20485454502f312e310d0a486f73743a203132372e302e302e313a313434340d0a557365722d4167656e743a206375726c2f372e35342e300d0a4163636570743a202a2f2a0d0a0d0a";

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

}
