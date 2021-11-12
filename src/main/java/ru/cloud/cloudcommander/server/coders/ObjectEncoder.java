package ru.cloud.cloudcommander.server.coders;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import ru.cloud.cloudcommander.server.communicate.Response;

import java.util.List;

public class ObjectEncoder extends MessageToMessageEncoder<Response> {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Response msg, List<Object> list) throws Exception {
        byte[] bytes = mapper.writeValueAsBytes(msg);
        list.add(bytes);
    }
}
