package ru.cloud.cloudcommander.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.client.communicate.Request;
import ru.cloud.cloudcommander.client.handlers.ActionHandler;


public class Client {
    private Logger LOG = LogManager.getLogger("log4j2.xml");
    Request request = new Request();

    private static  int PORT = 71;
    private static  String ADDRESS = "localhost";
    private NioEventLoopGroup con = new NioEventLoopGroup();

    public Client(int port, String address) {
        PORT = port;
        ADDRESS = address;
    }

    public void start() {
        con = new NioEventLoopGroup();

        try {
            Bootstrap client = new Bootstrap();
            client.group(con)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
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

    public static void main(String[] args) {
        new Client(PORT, ADDRESS).start();
    }
}
