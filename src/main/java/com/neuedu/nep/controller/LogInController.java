package com.neuedu.nep.controller;


import com.neuedu.nep.entity.Member;

import com.neuedu.nep.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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

    //处理登录按钮按下之后的用户界面
    @FXML
    void confirmButtonClicked(MouseEvent event) throws URISyntaxException {
        String stuffType = memberTypeChoser.getValue().getText();
        String passWord = loginPassWordText.getText().trim();
        String account = loginAccountNumberText.getText().trim();
        if(passWord.isEmpty() || account.isEmpty()){
            System.out.println("输入为空");
            showAlert("错误","请输入完整信息", AlertUtils.AlertType.ERROR,logInStage);
        }
        Member member=new Member(account,passWord);
        System.out.println("函数进入成功");
        switch (stuffType){
            case "supervisor":
                if(registeredOrNot("/dataBase/members/supervisor.Json",account,passWord)) {
                    System.out.println("在supervisor中找到了！");
                    Member supervisor = getThisPerson("/dataBase/members/supervisor.Json", account);
                    System.out.println("此人已在我手中");
                    handleUserView(supervisor,"supervisor");
                }
                else{
                    showAlert("错误","登录信息有误，请重试密码或检查是否注册", AlertUtils.AlertType.ERROR, logInStage);
                    closeThisPage();
                }

                break;

            case "administrator":
                if(registeredOrNot("/dataBase/members/administrator.json",account,passWord)) {
                    System.out.println("在administrator中找到了");
                    Member administrator = getThisPerson("/dataBase/members/administrator.Json", account);
                    System.out.println("此人已在我手中");
                    handleUserView(administrator,"administrator");
                }else{
                    showAlert("错误","登录信息有误，请重试密码或检查是否注册", AlertUtils.AlertType.ERROR,
                            logInStage);
                    closeThisPage();
                }


                break;

            case "gridder":
                if(registeredOrNot("/dataBase/members/gridder.json",account,passWord)) {
                    System.out.println("在gridder中找到了");
                    Member gridder= getThisPerson("/dataBase/members/gridder.Json", account);
                    System.out.println("此人已在我手中");
                    handleUserView(gridder,"gridder");
                }else{
                    showAlert("错误","登录信息有误，请重试密码或检查是否注册", AlertUtils.AlertType.ERROR,
                            logInStage);
                    closeThisPage();
                }
                break;
        }

    }

    // 代填入gridder 和 supervisor 的fxml 信息

    public void handleUserView(Member member,String type) {
        String dataBaseFilePath = null;
        String fxmlPath = null;
        if (type.equals("supervisor")) {
            dataBaseFilePath = "/dataBase/members/supervisor.Json";
            fxmlPath = "代填入";
        }
        if (type.equals("gridder")) {
            dataBaseFilePath = "/dataBase/members/gridder.Json";
            fxmlPath = "代填入";
        }
        if (type.equals("administrator")) {
            dataBaseFilePath = "/dataBase/members/administrator.Json";
            fxmlPath = "/com/neuedu/nep/aqiReportAssign.fxml";
        }
        FXMLLoader fxmlLoader = null;
        if (fxmlPath != null) {
            fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
        } else {
            System.out.println("文件地址出错");
        }
        Parent root;
        try {
            root = fxmlLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("用户操作界面");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(logInStage);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }
}
