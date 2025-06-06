package com.neuedu.nep.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class HelloCotroller {
    @FXML
    private Button registerButton;

    @FXML
    private Button loginButton;

    @FXML
    public void registerButtonClicked(){
        registerButton.setText("已点击");
    }

    @FXML
    public void loginButtonClicked(){
        loginButton.setText("已点击");
    }

}
