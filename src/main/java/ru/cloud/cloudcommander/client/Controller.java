package ru.cloud.cloudcommander.client;

import io.netty.channel.socket.SocketChannel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.communicate.Request;
import ru.cloud.cloudcommander.client.handlers.CloudController;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Controller implements CloudController {
    public TextField userdata;
    public TableView<String> filestable;
    private SocketChannel channel;
    private Logger LOG = LogManager.getLogger("log4j2.xml");

    private static  int PORT = 71;
    private static  String ADDRESS = "localhost";

    @Override
    public void download(ActionEvent actionEvent) {
        String filename = userdata.getText();
        Request request = new Request();
        request.setCommand("get");
        request.setFilename(filename);
        channel.writeAndFlush(request);

    }

    @Override
    public void upload(ActionEvent actionEvent) {
        Request request = new Request();
        String filename = userdata.getText();
        request.setCommand("send");
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
            channel.writeAndFlush(request);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @Override
    public void refresh(ActionEvent actionEvent) {
        Request request = new Request();
        request.setCommand("ls");
        channel.writeAndFlush(request);
        try{
            while (Client.getFilesList()==null) Thread.sleep(500);
            ObservableList<String> filesList = FXCollections.observableArrayList(Client.getFilesList());
            filestable.setItems(filesList);
        }catch (InterruptedException e){
            LOG.error(e);
        }


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
        Request request = new Request();
        request.setCommand("ping");
        request.setMessage("It`s a test message");
        channel.writeAndFlush(request);
        LOG.log(Level.INFO, "Message was sent");
    }

}
