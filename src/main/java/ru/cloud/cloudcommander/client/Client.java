package ru.cloud.cloudcommander.client;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;


public class Client {
    private Logger LOG = LogManager.getLogger("log4j2.xml");
    Request request = new Request();

    private static  int PORT = 71;
    private static  String ADDRESS = "localhost";
    private NioEventLoopGroup con = new NioEventLoopGroup();

    private String rootpath = "ru/cloud/cloudcommander/server/root";

    public Client(int port, String address) {
        PORT = port;
        ADDRESS = address;
    }

    public void start() {
        con = new NioEventLoopGroup();

        try {
            ObjectMapper hui = new ObjectMapper();
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

    private void workLoop(ChannelFuture chf){
        Scanner scanner = new Scanner(System.in);
        String command;
        label:
        while (true){
            System.out.println("Enter your actions \n");
            command = scanner.nextLine();
            switch (command) {
                case "send":
                    System.out.println("Введите имя загружаемого файла");
                    request.setCommand(command);
                    String filename = scanner.nextLine();
                    request.setFilename(filename);
                    File file = new File(rootpath + "/" + filename);
                    try {
                        byte[] content = Files.readAllBytes(file.toPath());
                        request.setFile(content);
                        chf.channel().writeAndFlush(request);
                    } catch (IOException e) {
                        LOG.log(Level.ERROR, "Cannot send a file");
                    }
                    break;
                case "get":
                    request.setCommand(command);
                    System.out.println("Введите имя файла, который хотите получить");
                    request.setFilename(scanner.nextLine());
                    chf.channel().writeAndFlush(request);
                    break;
                case "break":
                    try {
                        LOG.log(Level.INFO, "You want to disconnect? Okay, let`s try!");
                        chf.channel().closeFuture().sync();
                        con.shutdownGracefully();
                    } catch (InterruptedException e) {
                        LOG.log(Level.ERROR, "Error when disconnect");
                    }
                    break label;
                default:
                    request.setCommand(command);
                    chf.channel().writeAndFlush(request);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        new Client(PORT, ADDRESS).start();
    }
}
