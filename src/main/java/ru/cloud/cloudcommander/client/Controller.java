package ru.cloud.cloudcommander.client;

import io.netty.channel.socket.SocketChannel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.cloud.cloudcommander.communicate.Request;
import ru.cloud.cloudcommander.client.handlers.CloudController;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

public class Controller implements CloudController {
    public TextField userdata;
    public TextArea serverFiles;
    public TextArea clientFiles;
    private Stage loginStage;
    private SocketChannel channel;
    private Logger LOG = LogManager.getLogger("log4j2.xml");

    private static  int PORT = 71;
    private static  String ADDRESS = "localhost";

    private boolean authenticated = false;

    private LoginController loginController;

    @Override
    public void download(ActionEvent actionEvent) {
        if (authenticated) {
            String filename = userdata.getText();
            Request request = new Request();
            request.setCommand("get");
            request.setFilename(filename);
            channel.writeAndFlush(request);

        }else {
            LOG.log(Level.WARN,"You are not log in");
        }

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
        serverFiles.clear();
        Request request = new Request();
        request.setCommand("ls");
        channel.writeAndFlush(request);
        try{
            while (Client.getFilesList()==null) Thread.sleep(500);
            for (String i: Client.getFilesList()) serverFiles.appendText(i + "\n");
        }catch (InterruptedException e){
            LOG.error(e);
        }

        initClientRoot();
    }

    private void initClientRoot(){
        clientFiles.clear();
        File dir = new File(Client.getRootPath());
        try {
            for(File i: Objects.requireNonNull(dir.listFiles())){
                clientFiles.appendText(i.getName() + "\n");
            }
        }catch (NullPointerException e){
            LOG.log(Level.WARN, "Папки не существует. Создаем.");
            if(dir.mkdir()) initClientRoot();
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

    public void connect()  {
        Thread starter = new Thread(new Client(PORT, ADDRESS));
        starter.start();
        try {
            while (Client.getChannel() == null) Thread.sleep(500);
            this.channel = Client.getChannel();
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

    public void authentication(String login, String password){
        if (Client.getChannel() == null) connect();
        Request request = new Request();
        request.setCommand("auth");
        request.setPassword(password);
        request.setMessage(login);
        channel.writeAndFlush(request);
    }

    public void LogIN(ActionEvent actionEvent) {
        if(loginStage == null) createLoginWindow();
        Platform.runLater( () -> loginStage.show());
    }

    private void createLoginWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = fxmlLoader.load();
            loginStage = new Stage();
            loginStage.setTitle("LOG IN");
            loginStage.setScene(new Scene(root, 225, 250));

            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.initStyle(StageStyle.UTILITY);

            loginController = fxmlLoader.getController();
            loginController.setController(this);
        } catch (IOException e) {
            LOG.error(e);
        }

    }
}
