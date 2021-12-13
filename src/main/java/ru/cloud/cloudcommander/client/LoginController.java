package ru.cloud.cloudcommander.client;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    private Controller controller;

    public TextField login;
    public TextField password;
    public Button connectButton;

    public void tryToAuth(ActionEvent actionEvent) {
        String nick = login.getText().trim();
        String pass = password.getText().trim();

        controller.authentication(nick,pass);

        Stage stage = (Stage) connectButton.getScene().getWindow();
        stage.close();

    }

    public void setController(Controller controller) {
        this.controller = controller;
    }
}
