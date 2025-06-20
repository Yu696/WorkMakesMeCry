package com.neuedu.nep.controller;


import com.neuedu.nep.entity.Member;

import com.neuedu.nep.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.*;
import java.net.URISyntaxException;


import static com.neuedu.nep.util.AlertUtils.showAlert;
import static com.neuedu.nep.util.FindUtil.getThisPerson;
import static com.neuedu.nep.util.FindUtil.registeredOrNot;


public class LogInController {
    @FXML
    private AnchorPane paneRoot1;

    @FXML
    private TextField loginAccountNumberText;

    @FXML
    private MenuItem administrator;

    @FXML
    private ChoiceBox<MenuItem> memberTypeChoser;

    @FXML
    private Button traceBackButton;

    @FXML
    private MenuItem gridder;

    @FXML
    private PasswordField loginPassWordText;

    @FXML
    private Button confirmButton;

    @FXML
    private MenuItem supervisor;

    @FXML
    private Stage logInStage;

    @FXML
    void closeThisPage() {
        logInStage.close();
    }

    @FXML
    public void setDialogStage(Stage dialogStage){
        this.logInStage=dialogStage;
    }

    @FXML
    public void initialize(){
        Image backgroundImage=new Image(getClass().getResourceAsStream("/image/back.jpg"));
        //初始化
        updateBackGroundImage(backgroundImage);

        //随容器大小改变
        paneRoot1.widthProperty().addListener((v,oldVal,newVal)->{
            updateBackGroundImage(backgroundImage);
        });
        paneRoot1.heightProperty().addListener((n,oldval,newval)->{
            updateBackGroundImage(backgroundImage);
        });
        memberTypeChoser.getItems().addAll(supervisor,administrator,gridder);
        memberTypeChoser.setConverter(new StringConverter<MenuItem>() {
            @Override
            public String toString(MenuItem menuItem) {
                if (menuItem == null) {
                    return "";
                } else {
                    return menuItem.getText();
                }
            }
            @Override
            public MenuItem fromString(String s) {
                return null;}
        });
        memberTypeChoser.setValue(supervisor);
    }

    private void updateBackGroundImage(Image image){
        BackgroundSize backgroundSize=new BackgroundSize(paneRoot1.getWidth(),paneRoot1.getHeight(),false,false,false,false);
        BackgroundImage image1=new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,backgroundSize);
        paneRoot1.setBackground(new Background(image1));
    }

    //处理登录按钮按下之后的用户界面
    @FXML
    void confirmButtonClicked(MouseEvent event) throws URISyntaxException {
        String stuffType = memberTypeChoser.getValue().getText();
        String passWord = loginPassWordText.getText().trim();
        String account = loginAccountNumberText.getText().trim();
        if(passWord.isEmpty() || account.isEmpty()){
            showAlert("错误","请输入完整信息", AlertUtils.AlertType.ERROR,logInStage);
        }
        Member member=new Member(account,passWord);
        switch (stuffType){
            case "supervisor":
                if(registeredOrNot("/dataBase/members/supervisor.Json",account,passWord)) {
                    Member supervisor = getThisPerson("/dataBase/members/supervisor.Json", account);
                    handleUserView(supervisor,"supervisor");
                }
                else{
                    showAlert("错误","登录信息有误，请重试密码或检查是否注册", AlertUtils.AlertType.ERROR, logInStage);
                    closeThisPage();
                }

                break;

            case "administrator":
                if(registeredOrNot("/dataBase/members/administrator.json",account,passWord)) {
                    Member administrator = getThisPerson("/dataBase/members/administrator.Json", account);
                    handleUserView(administrator,"administrator");
                }else{
                    showAlert("错误","登录信息有误，请重试密码或检查是否注册", AlertUtils.AlertType.ERROR,
                            logInStage);
                    closeThisPage();
                }


                break;

            case "gridder":
                if(registeredOrNot("/dataBase/members/gridder.json",account,passWord)) {
                    Member gridder= getThisPerson("/dataBase/members/gridder.Json", account);
                    handleUserView(gridder,"gridder");
                }else{
                    showAlert("错误","登录信息有误，请重试密码或检查是否注册", AlertUtils.AlertType.ERROR,
                            logInStage);
                    closeThisPage();
                }
                break;
        }

    }

    // 代填入supervisor 的fxml 信息

    public void handleUserView(Member member,String type) {
        String dataBaseFilePath = null;
        String fxmlPath = null;
        if (type.equals("supervisor")) {
            dataBaseFilePath = "/dataBase/members/supervisor.Json";
            fxmlPath = "/com/neuedu/nep/supervisorView.fxml";
        }
        if (type.equals("gridder")) {
            dataBaseFilePath = "/dataBase/members/gridder.Json";
            fxmlPath =  "/com/neuedu/nep/Gridder.fxml";
        }
        if (type.equals("administrator")) {
            dataBaseFilePath = "/dataBase/members/administrator.Json";
            fxmlPath = "/com/neuedu/nep/aqiReportAssign.fxml";
        }
        FXMLLoader fxmlLoader = null;
        if (fxmlPath != null) {
            fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        }
        Parent root;
        try {
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("用户操作界面:"+member.getAccount()+","+member.getName());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(logInStage);
            stage.show();
            setDialogStage(stage);
            showAlert("成功","又是美好的打工日~  欢迎"+member.getName()+"员工为本公司添砖加瓦", AlertUtils.AlertType.SUCCESS,stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}
