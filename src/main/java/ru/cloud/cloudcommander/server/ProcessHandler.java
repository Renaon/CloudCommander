package ru.cloud.cloudcommander.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.communicate.Request;
import ru.cloud.cloudcommander.communicate.Response;

import java.io.*;
import java.util.*;

public class ProcessHandler extends SimpleChannelInboundHandler<Request> {
    private String rootPath = "src/main/java/ru/cloud/cloudcommander/server/root";
    private Logger LOG = LogManager.getLogger("log4j2.xml");
    private Response response;
    ChannelHandlerContext ctxChannelHandlerContext;


    public void channelActive(ChannelHandlerContext ctx){
        LOG.log(Level.INFO, "Someone connected");
        this.ctxChannelHandlerContext = ctx;
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return super.acceptInboundMessage(msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg){

        LOG.log(Level.INFO, "Trying to accept user data");
        response = new Response();
        switch (msg.getCommand()) {
            case "send":
                saveFile(ctxChannelHandlerContext, msg);
                break;
            case "ping":
                LOG.log(Level.INFO, msg.getMessage());
                response.setCommand(msg.getCommand());
                response.setMessage("You are awesome!");
                ctxChannelHandlerContext.writeAndFlush(response);
                break;
            case "get":
                sendFile(ctxChannelHandlerContext, msg);
                break;
            case "ls":
                ls(ctxChannelHandlerContext, msg);
                break;
            case "mkdir":
                mkdir(ctxChannelHandlerContext, msg);
                break;
            case "cd":
                setRootPath(msg.getMessage());
                break;
            case "report":
                LOG.log(Level.INFO, "Operation " + msg.getCommand() + " " + msg.getMessage());
                break;
            case "exit":
                LOG.log(Level.INFO, "Client want to disconnect");
            default:
                LOG.log(Level.INFO, msg.getCommand());
        }
    }

    private void saveFile(ChannelHandlerContext ctx, Request msg){
        LOG.log(Level.INFO, "Trying to get a file");
        File file = new File(rootPath, msg.getFilename());
        try(RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            raf.seek(msg.getPosition());
            raf.write(msg.getFile());
        } catch (IOException e) {
            LOG.error("Cannot take a file");
        }
        response.setCommand("report");
        response.setMessage("File " + msg.getFilename() + " uploaded");
        LOG.log(Level.INFO, "Done!");
        ctx.writeAndFlush(response);
    }

    private void sendFile(ChannelHandlerContext ctx, Request msg){
        response.setCommand(msg.getCommand());
        LOG.info("Send file to client");
        String fileName = msg.getFilename();
        byte[] buffer = new byte[1024*512];
        try(RandomAccessFile raf = new RandomAccessFile(rootPath + fileName, "r")) {
                response.setFilename(fileName);
                response.setPosition(raf.getFilePointer());
                int read = raf.read(buffer);
                if(read < buffer.length && read != -1){
                    byte[] tmp = new byte[read];
                    System.arraycopy(buffer, 0, tmp, 0, read);
                    response.setFile(tmp);
                }else {
                    response.setFile(buffer);
                }
                response.setMessage("Server send you present");
                ctx.writeAndFlush(response);
        } catch (IOException e) {
            LOG.error(e);
        }
        LOG.info("Done");
    }

    private void ls(ChannelHandlerContext ctx, Request msg){
        File dir = new File(rootPath);
        List<String> list = new ArrayList<>(Collections.singleton(Arrays
                .asList(Objects.requireNonNull(dir.listFiles())).toString()));
        response.setFiles(list);
        response.setCommand(msg.getCommand());
        ctx.writeAndFlush(response);
    }

    private void mkdir(ChannelHandlerContext ctx, Request msg){
        if (new File(rootPath + msg.getMessage()).mkdir()) LOG.info("Dir " + msg.getMessage() + " created.");
        response.setMessage("Dir is created");
        response.setCommand(msg.getCommand());
        ctx.writeAndFlush(response);
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
