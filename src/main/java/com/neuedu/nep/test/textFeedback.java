package com.neuedu.nep.test;

import com.neuedu.nep.controller.SupervisorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class textFeedback extends Application {
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/neuedu/nep/supervisorView.fxml"));
        Parent pane = fxmlLoader.load();
        Scene scene = new Scene(pane);

        // 获取 FeedbackController 实例
        SupervisorController supervisorController = fxmlLoader.getController();
        // 调用 setDialogStage 方法设置 FeedbackStage
        supervisorController.setDialogStage(stage);

        stage.setTitle("NEUEDU 测试版");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}