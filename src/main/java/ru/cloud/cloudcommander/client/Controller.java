package ru.cloud.cloudcommander.client;

import io.netty.channel.socket.SocketChannel;
import javafx.event.ActionEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.communicate.Request;
import ru.cloud.cloudcommander.communicate.Response;
import ru.cloud.cloudcommander.client.handlers.CloudController;

import java.io.IOException;

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
    public void ping(ActionEvent actionEvent){
        try {
            test();
        } catch (IOException e){
            LOG.log(Level.ERROR, "Проблема при проверке связи");
        }
    }

    public void connect(ActionEvent actionEvent)  {
        Thread starter = new Thread(new Client(PORT, ADDRESS));
        starter.start();
        try {
            while (Client.getChannel() == null) Thread.sleep(500);
            channel = Client.getChannel();
            test();
        } catch (IOException | InterruptedException e) {
            LOG.error(e);
        }

    }

    private void test() throws IOException {
        LOG.log(Level.INFO, "Right this sec send test message");
        request = new Request();
        request.setCommand("ping");
        request.setMessage("It`s a test message");
        channel.writeAndFlush(request);
        LOG.log(Level.INFO, "Message was sent");
    }

}
