package ru.cloud.cloudcommander.client.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.client.Client;
import ru.cloud.cloudcommander.communicate.Request;
import ru.cloud.cloudcommander.communicate.Response;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class ActionHandler extends SimpleChannelInboundHandler<Response> {

    private Logger LOG = LogManager.getLogger("log4j2.xml");
    private Request request = new Request();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response response) throws Exception {
        LOG.log(Level.INFO, "We are ready to catch server response");
        switch (response.getCommand()) {
            case "get":
                saveFile(response);
                break;
            case "ls":
                Client.setFilesList(response.getFiles());
                break;
            case "send":
                sendFile(ctx, response.getCommand(), response.getFilename());
                break;
            case "auth":
                if (response.isAutorisate()) {
                    LOG.log(Level.INFO, "Мы смогли залогиниться");
                    Client.setAuthenticated(response.isAutorisate());
                }else {
                    LOG.log(Level.INFO, "Авторизация неуспешна");
                }

        }
        LOG.log(Level.INFO, response.getCommand()+ ": " + response.getMessage());
    }

    private void saveFile(Response msg) throws IOException {
        File file = new File(Client.getRootPath(), msg.getFilename());
       try(RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
           raf.seek(msg.getPosition());
           raf.write(msg.getFile());
       }
    }


    private void sendFile(ChannelHandlerContext ctx, String command, String filename){
        request.setCommand(command);
        request.setFilename(filename);
        byte[] buffer = new byte[1024*512];
        File file = new File(Client.getRootPath(), filename);
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
