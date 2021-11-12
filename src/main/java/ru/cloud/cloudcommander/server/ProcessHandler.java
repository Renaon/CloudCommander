package ru.cloud.cloudcommander.server;

import io.netty.buffer.ByteBuf;
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

public class ProcessHandler extends SimpleChannelInboundHandler<Request> {
    private String rootPath = "ru/cloud/cloudcommander/server/root";
    private ByteBuf buf;
    private Logger LOG = LogManager.getLogger("log4j2.xml");
    private Response response;


    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.log(Level.INFO, "Someone connected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        response = new Response();
        LOG.info("Someone connected");
        System.out.println(msg);
        if (msg.getCommand().equals("send")) saveFile(ctx, msg);
        else if (msg.getCommand().equals("ping")) {
            LOG.log(Level.INFO, msg.getMessage());
            System.out.println(msg.getCommand() + msg.getMessage());
        }
    }

    private void saveFile(ChannelHandlerContext ctx, Request msg) throws FileNotFoundException {
        String filename = rootPath + msg.getFilename();
        File file = new File(filename);
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")){
            raf.write(msg.getFile());
        } catch (IOException e) {
            LOG.error("Не удалось получить файл");
        }
    }

    public String getRootPath() {
        return rootPath;
    }
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
