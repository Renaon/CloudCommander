package ru.cloud.cloudcommander.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.client.communicate.Request;
import ru.cloud.cloudcommander.client.handlers.CloudController;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Controller implements CloudController {
    private SocketChannel channel;
    private Logger LOG = LogManager.getLogger("log4j2.xml");
    Request request = new Request();

    private static  int PORT = 71;
    private static  String ADDRESS = "localhost";

    @Override
    public void download(ActionEvent actionEvent) {

    }

    @Override
    public void upload(ActionEvent actionEvent) {

    }

    @Override
    public void refresh(ActionEvent actionEvent) {

    }

    @Override
    public void ping(ActionEvent actionEvent) {

    }

    public void connect(ActionEvent actionEvent) throws IOException, InterruptedException {
        Thread starter = new Thread(new Client(PORT, ADDRESS));
        starter.start();
        while (Client.getChannel() == null) Thread.sleep(500);
        channel = Client.getChannel();
//        test(channel);
    }

    private void test(SocketChannel ctx) throws IOException {
        LOG.log(Level.INFO, "Right this sec send test message");
        request = new Request();
        request.setCommand("ping");
        request.setMessage("It`s a test message");

        LOG.log(Level.INFO, "Message was sent");
    }

}
