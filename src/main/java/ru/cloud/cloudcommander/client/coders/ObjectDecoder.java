package ru.cloud.cloudcommander.client.coders;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import ru.cloud.cloudcommander.server.communicate.Request;

import java.util.List;

public class ObjectDecoder extends MessageToMessageDecoder<byte[]> {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, byte[] msg, List<Object> list) throws Exception {
        Request request = mapper.readValue(msg, Request.class);
        list.add(request);
    }
}
