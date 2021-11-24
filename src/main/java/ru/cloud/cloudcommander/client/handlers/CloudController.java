package ru.cloud.cloudcommander.client.handlers;

import javafx.event.ActionEvent;

import java.io.IOException;

public interface CloudController {

    public void download(ActionEvent actionEvent);
    public  void upload(ActionEvent actionEvent) ;
    public  void refresh(ActionEvent actionEvent) ;
    public  void ping(ActionEvent actionEvent) ;
    public void connect(ActionEvent actionEvent) throws IOException, InterruptedException;
}
