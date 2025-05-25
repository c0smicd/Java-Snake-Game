package com.cosmic.snakegamecraft.controller;

import com.cosmic.snakegamecraft.AppContext;
import com.cosmic.snakegamecraft.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    private TextField nameField;

    @FXML
    private void handleContinue(){
        String username = nameField.getText().trim();

        if(!username.isEmpty()){
            SceneManager sceneManger = new SceneManager(AppContext.getStage());
            AppContext.setUsername(username);
            sceneManger.showMenu(username);
        }
    }

}
