package ru.cloud.cloudcommander.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.client.Client;
import ru.cloud.cloudcommander.client.communicate.Request;
import ru.cloud.cloudcommander.client.communicate.Response;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
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
            saveFile(response);
        }else if (response.getCommand().equals("ls")) {
            List<String> list = response.getFiles();
            for (String file : list) {
                System.out.print(file + "; ");
            }
        }
        LOG.log(Level.INFO, response.getCommand()+ ": " + response.getMessage());
//        workLoop(ctx);
    }

    private void saveFile(Response msg) throws IOException {
        File file = new File(rootPath, msg.getFilename());
       try(RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
           raf.seek(msg.getPosition());
           raf.write(msg.getFile());
       }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
//        Client.setChannel(ctx.channel());
//        LOG.log(Level.INFO, "Right this sec send test message");
//        request = new Request();
//        request.setCommand("ping");
//        request.setMessage("It`s a test message");
//        ctx.writeAndFlush(request);
//        LOG.log(Level.INFO, "Message was sent");
    }

    private void workLoop(ChannelHandlerContext chf){
        Scanner scanner = new Scanner(System.in);
        String command;
            System.out.println("Enter your actions \n");
            command = scanner.nextLine();
            switch (command) {
                case "send":
                    System.out.println("Введите имя загружаемого файла");
                    String filename = new Scanner(System.in).nextLine();
                    sendFile(chf, command, filename);
                    break;
                case "get":
                    request.setCommand(command);
                    System.out.println("Введите имя файла, который хотите получить");
                    request.setFilename(scanner.nextLine());
                    chf.writeAndFlush(request);
                    break;
                case "break":
                    LOG.log(Level.INFO, "You want to disconnect? Okay, let`s try!");
                    chf.close();
                default:
                    request.setCommand(command);
                    chf.writeAndFlush(request);
                    break;

        }
    }

    private void sendFile(ChannelHandlerContext ctx, String command, String filename){
        request.setCommand(command);
        request.setFilename(filename);
        byte[] buffer = new byte[1024*512];
        File file = new File(rootPath, filename);
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            request.setPosition(raf.getFilePointer());
            int read = raf.read(buffer);
            if(read< buffer.length && read != -1){
                byte[] tmp = new byte[read];
                System.arraycopy(buffer, 0, tmp, 0, read);
                request.setFile(tmp);
            }else {
                request.setFile(buffer);
            }
            ctx.writeAndFlush(request);
        } catch (IOException e) {
            LOG.error(e);
        }
    }


    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
