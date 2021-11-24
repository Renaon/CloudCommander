package ru.cloud.cloudcommander.client;

import io.netty.bootstrap.Bootstrap;
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
import ru.cloud.cloudcommander.communicate.Request;
import ru.cloud.cloudcommander.communicate.Response;
import ru.cloud.cloudcommander.client.handlers.ActionHandler;
import java.io.IOException;
import java.util.List;

public class Client  implements Runnable{
    private static Logger LOG = LogManager.getLogger("log4j2.xml");
    private static SocketChannel channel;

    private static  int PORT = 71;
    private static  String ADDRESS = "localhost";

    private static String rootPath = "src/main/java/ru/cloud/cloudcommander/client/clientroot";
    private static List<String> filesList;

    public Client(int port, String address) {
        PORT = port;
        ADDRESS = address;
    }

    public synchronized static List<String> getFilesList() {
        return filesList;
    }

    public synchronized static void setFilesList(List<String> filesList) {
        Client.filesList = filesList;
    }

    public static synchronized SocketChannel getChannel() {
        return channel;
    }

    public static String getRootPath() {
        return rootPath;
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
                            channel = nioSocketChannel;
                            nioSocketChannel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.weakCachingResolver(Response.class.getClassLoader())),
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

    @Override
    public void run() {
        Client.start();

    }
}
