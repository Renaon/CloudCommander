package ru.cloud.cloudcommander.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.server.communicate.Request;
import ru.cloud.cloudcommander.server.communicate.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;

public class ActionHandler extends SimpleChannelInboundHandler<Response> {
    private Logger LOG = LogManager.getLogger("log4j2.xml");
    private String rootPath = "D://Projects//Cloud Commander//src//main//java//ru//cloud//cloudcommander//client//clientroot//";
    private Request request = new Request();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        LOG.log(Level.INFO, "We are ready to catch server response");
        if (response.getCommand().equals("get")){
            saveFile(ctx, response);
        }else if (response.getCommand().equals("ls")) {
            List<String> list = response.getFiles();
            for (String file : list) {
                System.out.print(file + "; ");
            }
        }
        LOG.log(Level.INFO, response.getCommand()+ ": " + response.getAnswer());
    }

    private void saveFile(ChannelHandlerContext ctx, Response msg) throws FileNotFoundException {
        String filename = rootPath + msg.getFilename();
        File file = new File(filename);
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")){
            raf.write(msg.getFile());

        } catch (IOException e) {
            LOG.error("Не удалось получить файл");
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.log(Level.INFO, "Right this sec send test message");
        request = new Request();
        request.setCommand("ping");
        request.setMessage("It`s a test message");
        ctx.writeAndFlush(request);
        LOG.log(Level.INFO, "Message was sent");
        workLoop(ctx);
    }

    private void workLoop(ChannelHandlerContext chf){
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
                    File file = new File(rootPath + "/" + filename);
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
                    return;
                default:
                    request.setCommand(command);
                    chf.channel().writeAndFlush(request);
                    break;
            }
        }
    }

}
