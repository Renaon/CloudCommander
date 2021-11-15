package ru.cloud.cloudcommander.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.server.communicate.Request;
import ru.cloud.cloudcommander.server.communicate.Response;

public class ActionHandler extends SimpleChannelInboundHandler<Response> {
    private Logger LOG = LogManager.getLogger("log4j2.xml");

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
        LOG.log(Level.INFO, "We are ready to catch server response");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.log(Level.INFO, "Right this sec send test message");
        Request request = new Request();
        request.setCommand("ping");
        request.setMessage("It`s a test message");
        ctx.writeAndFlush(request);
        LOG.log(Level.INFO, "Message was sended");
    }
}
