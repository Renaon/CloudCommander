package ru.cloud.cloudcommander.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.client.coders.ObjectDecoder;
import ru.cloud.cloudcommander.client.coders.ObjectEncoder;


public class Client {
    private Logger LOG = LogManager.getLogger("log4j2.xml");

    private static  int PORT = 71;
    private static  String ADDRESS = "localhost";

    public Client(int port, String address) {
        PORT = port;
        ADDRESS = address;
    }

    public void start() {
        NioEventLoopGroup con = new NioEventLoopGroup();

        try {
            ObjectMapper hui = new ObjectMapper();
            Bootstrap client = new Bootstrap();
            client.group(con)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(degree(1024, 3),
                                    0,
                                    8,
                                    0,
                                    8));
                            nioSocketChannel.pipeline().addLast(
                                    new LengthFieldPrepender(8),
                                    new ByteArrayDecoder(),
                                    new ByteArrayEncoder(),
                                    new ObjectDecoder(),
                                    new ObjectEncoder(),
                                    new ActionHandler()
                            );
                        }
                    });
            ChannelFuture chf = client.connect(ADDRESS, PORT).sync();
            LOG.log(Level.INFO, "Connection success. Ready to work");
            chf.channel().closeFuture().sync();
        }catch (Exception e) {
            LOG.error("Can not connect");
        } finally {
            LOG.log(Level.ERROR, "Клиент пидр и решил отключиться самостоятельно");
            con.shutdownGracefully();
        }
    }

    private int degree(int a, int b){
        if ( b ==1 ) return a;
        else return (a * degree(a, b -1));
    }

    public static void main(String[] args) {
        new Client(PORT, ADDRESS).start();
    }
}
