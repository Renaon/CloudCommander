package ru.cloud.cloudcommander.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.server.communicate.Request;

public class ProcessHandler extends SimpleChannelInboundHandler<Request> {
    private ByteBuf tmp;
    private Logger LOG = LogManager.getLogger(ProcessHandler.class);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        LOG.log(Level.INFO, "Handler added");
        tmp = ctx.alloc().buffer(4);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request msg) throws Exception {

    }
}
