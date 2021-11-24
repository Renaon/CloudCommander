package ru.cloud.cloudcommander.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.client.communicate.Request;
import ru.cloud.cloudcommander.client.communicate.Response;
import ru.cloud.cloudcommander.client.handlers.ActionHandler;
import java.io.IOException;

public class Client  implements Runnable{
    private static Logger LOG = LogManager.getLogger("log4j2.xml");
    static Request request = new Request();
    private static SocketChannel channel;

    private static  int PORT = 71;
    private static  String ADDRESS = "localhost";

    public Client(int port, String address) {
        PORT = port;
        ADDRESS = address;
    }

    public static synchronized SocketChannel getChannel() {
        return Client.channel;
    }


    public static void start() {
        NioEventLoopGroup con = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap();
            client.group(con)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel nioSocketChannel) throws Exception {
                            Client.channel = nioSocketChannel;
                            nioSocketChannel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.weakCachingResolver(Response.class.getClassLoader())),
                                    new ObjectEncoder(),
                                    new ActionHandler()
                            );
                        }
                    });
            ChannelFuture chf = client.connect(ADDRESS, PORT).sync();
            test(channel);
            LOG.log(Level.INFO, "Connection success. Ready to work");
            chf.channel().closeFuture().sync();
        }catch (Exception e) {
            LOG.error("Can not connect");
        } finally {
            LOG.log(Level.ERROR, "Клиент пидр и решил отключиться самостоятельно");
            con.shutdownGracefully();
        }
    }

    private static void test(SocketChannel ctx) throws IOException {
        LOG.log(Level.INFO, "Right this sec send test message");
        request = new Request();
        request.setCommand("ping");
        request.setMessage("It`s a test message");
        ctx.writeAndFlush(request);
        ctx.flush();
        LOG.log(Level.INFO, "Message was sent");
    }



    public static void main(String[] args) {
        new Client(PORT, ADDRESS).start();
    }

    @Override
    public void run() {
        Client.start();

    }
}
