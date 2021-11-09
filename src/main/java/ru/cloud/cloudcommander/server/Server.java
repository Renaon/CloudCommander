package ru.cloud.cloudcommander.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.server.communicate.Request;

public class Server {
    private int PORT;
    private String rootPath = "ru/cloud/cloudcommander/server/root";
    EventLoopGroup boss; //входящий пул потоков
    EventLoopGroup workerGroup; // обработка потков данных
    private Logger LOG = LogManager.getLogger(ProcessHandler.class);

    public int getPORT() {
        return PORT;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public void run() throws Exception {
        boss = new NioEventLoopGroup(); //см выше
        workerGroup = new NioEventLoopGroup(); //тож самое

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(boss, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(
                                    //если какая-то срань появится, то вписывать сюда
                                    new ProcessHandler()
                            );
                        }
                    });
        } catch (Exception e) {
            LOG.error("Не смогли запуститься");
        }
    }
}
