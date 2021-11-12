package ru.cloud.cloudcommander.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.server.coders.ObjectDecoder;
import ru.cloud.cloudcommander.server.coders.ObjectEncoder;


public class Server {
    private int PORT;
    EventLoopGroup boss; //входящий пул потоков
    EventLoopGroup workerGroup; // обработка потков данных
    private Logger LOG = LogManager.getLogger("log4j2.xml");

    public Server(int port) {
        this.PORT = port;
    }

    public void run() throws Exception {
        boss = new NioEventLoopGroup(); //см выше
        workerGroup = new NioEventLoopGroup(); //тож самое
        try{
            LOG.info("Trying to start server");
            ServerBootstrap bootstrap = new ServerBootstrap();

            //вот туть перехватываются сообщения от клиента
            bootstrap.group(boss, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(cube(1024, 3),
                                    0,
                                    8,
                                    0,
                                    8));
                            socketChannel.pipeline().addLast(
                                    new LengthFieldPrepender(8),
                                    new ByteArrayDecoder(),
                                    new ByteArrayEncoder(),
                                    new ObjectDecoder(),
                                    new ObjectEncoder(),
                                    new ProcessHandler()
                            );
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = bootstrap.bind(PORT).sync();
            LOG.log(Level.INFO, "Server started on " + PORT + " port");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            boss.shutdownGracefully();
            workerGroup.shutdownGracefully();
            LOG.log(Level.ERROR, "Какая-то хуйня");
        }


    }

    public static void main(String[] args) throws Exception {
        try {
            new Server(71).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int cube(int a, int b){
        if ( b ==1 ) return a;
        else return (a * cube(a, b -1));
    }
}
