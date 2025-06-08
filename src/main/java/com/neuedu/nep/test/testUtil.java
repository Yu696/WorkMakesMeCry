package com.neuedu.nep.test;

import com.neuedu.nep.util.AlertUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.neuedu.nep.util.AlertUtils.showAlert;

public class testUtil extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/neuedu/nep/hello-fxml.fxml"));
        try {
            Parent pane=fxmlLoader.load();
            Scene scene=new Scene(pane);
            primaryStage.setScene(scene);
            primaryStage.show();
            showAlert("错误","错误信息测试", AlertUtils.AlertType.ERROR,primaryStage);
            showAlert("警告","警告信息测试", AlertUtils.AlertType.WARNING,primaryStage);
            showAlert("成功","成功信息测试", AlertUtils.AlertType.SUCCESS,primaryStage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
