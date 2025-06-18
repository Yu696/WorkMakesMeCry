package com.neuedu.nep.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.text.html.ImageView;
import java.io.IOException;


//主控制器
public class HelloCotroller {
    @FXML
    private AnchorPane paneRoot;
    @FXML
    private Button registerButton;

    @FXML
    private ImageView sd;

    @FXML
    private Button loginButton;

    @FXML
    private Stage mainStage;

    @FXML
    public void registerButtonClicked(){
        handleRegister();
    }

    @FXML
    public void loginButtonClicked(){
        handleLogIn();
    }
    //加载背景图片，并随容器的大小变化而改变
    @FXML
    private void initialize(){
        Image backgroundImage=new Image(getClass().getResourceAsStream("/image/back3.jpg"));
        //初始化
        updateBackGroundImage(backgroundImage);

        //随容器大小改变
        paneRoot.widthProperty().addListener((v,oldVal,newVal)->{
            updateBackGroundImage(backgroundImage);
        });
        paneRoot.heightProperty().addListener((n,oldval,newval)->{
            updateBackGroundImage(backgroundImage);
        });
    }

    private void updateBackGroundImage(Image image){
        BackgroundSize backgroundSize=new BackgroundSize(paneRoot.getWidth(),paneRoot.getHeight(),false,false,false,false);
        BackgroundImage image1=new BackgroundImage(image,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER,backgroundSize);
        paneRoot.setBackground(new Background(image1));
    }
    //注册按钮点击后跳转注册页面，主页面不消失
    public void handleRegister(){
        try {
            //加载注册界面的FXML文件
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/com/neuedu/nep/registerWindow.fxml"));
            Parent root=fxmlLoader.load();

            Stage registerStage = new Stage();
            registerStage.setTitle("用户注册");
            registerStage.initModality(Modality.APPLICATION_MODAL);
            registerStage.initOwner(mainStage);

            //调用controller
            RegisterController registerController= fxmlLoader.getController();
            registerController.setDialogStage(registerStage);

            //显示注册界面
            registerStage.setScene(new Scene(root));
            registerStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void handleLogIn(){
        try {
            //加载注册界面的FXML文件
            FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/com/neuedu/nep/logInWindow.fxml"));
            Parent root=fxmlLoader.load();

            Stage logInStage = new Stage();
            logInStage.setTitle("用户登录");
            logInStage.initModality(Modality.APPLICATION_MODAL);
            logInStage.initOwner(mainStage);

            //调用controller
            LogInController logInController= fxmlLoader.getController();
            logInController.setDialogStage(logInStage);

            //显示注册界面
            logInStage.setScene(new Scene(root));
            logInStage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
