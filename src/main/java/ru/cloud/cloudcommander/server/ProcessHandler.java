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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ProcessHandler extends SimpleChannelInboundHandler<Request> {
    private String rootPath = "ru/cloud/cloudcommander/server/root";
    private Logger LOG = LogManager.getLogger("log4j2.xml");
    private Response response;


    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.log(Level.INFO, "Someone connected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        response = new Response();
        switch (msg.getCommand()) {
            case "send":
                saveFile(ctx, msg);
                break;
            case "ping":
                LOG.log(Level.INFO, msg.getMessage());
                break;
            case "get":
                sendFile(ctx, msg);
                break;
            case "ls":
                ls(ctx,msg);
                break;
            case "mkdir":
                mkdir(ctx, msg);
                break;
            case "cd":
                setRootPath(msg.getMessage());
                break;
        }
    }

    private void saveFile(ChannelHandlerContext ctx, Request msg) throws FileNotFoundException {
        String filename = rootPath + msg.getFilename();
        File file = new File(filename);
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")){
            raf.write(msg.getFile());
            response.setCommand(msg.getCommand());
            response.setMessage("File already uploaded");
            ctx.writeAndFlush(response);
        } catch (IOException e) {
            LOG.error("Не удалось получить файл");
        }
    }

    private void sendFile(ChannelHandlerContext ctx, Request msg){
        response.setFilename(msg.getFilename());
        File file = new File(rootPath + msg.getFilename());
        try {
            byte[] content = Files.readAllBytes(file.toPath());
            response.setFile(content);
            response.setCommand("get");
            ctx.writeAndFlush(response);
        } catch (IOException e) {
            LOG.log(Level.ERROR, e);
        }

    }

    private void ls(ChannelHandlerContext ctx, Request msg){
        File dir = new File(rootPath);
        List<File> list = new ArrayList<>(Arrays.asList(Objects.requireNonNull(dir.listFiles())));
        response.setFiles(list);
        response.setCommand(msg.getCommand());
        ctx.writeAndFlush(response);
    }

    private void mkdir(ChannelHandlerContext ctx, Request msg){
        new File(rootPath + msg.getAnswer()).mkdir();
        response.setAnswer("File is created");
        response.setCommand(msg.getCommand());
        ctx.writeAndFlush(response);
    }

    public String getRootPath() {
        return rootPath;
    }
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
