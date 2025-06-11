package com.neuedu.nep.controller;

import com.neuedu.nep.entity.Member;
import com.neuedu.nep.entity.Supervisor;
import com.neuedu.nep.io.JsonIO;
import com.neuedu.nep.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.skin.CellSkinBase;
import javafx.stage.Stage;
import javafx.util.StringConverter;




import static com.neuedu.nep.io.JsonIO.read;
import static com.neuedu.nep.io.JsonIO.writer;
import static com.neuedu.nep.util.AlertUtils.registeredOrNot;
import static com.neuedu.nep.util.AlertUtils.showAlert;

public class RegisterController {
    @FXML
    private TextField registerAccountNumberText;

    @FXML
    private MenuItem administrator;

    @FXML
    private ChoiceBox<MenuItem> memberTypeChoser;

    @FXML
    private TextField registerSexText;

    @FXML
    private PasswordField registerAgainText;

    @FXML
    private Button traceBackButton;

    @FXML
    private TextField registerNameText;

    @FXML
    private MenuItem gridder;

    @FXML
    private PasswordField registerPassWordText;

    @FXML
    private Button confirmButton;

    @FXML
    private MenuItem supervisor;

    @FXML
    public void closeRegister(){
        registerStage.close();
    }

    @FXML
    public void confirmButtonClicked(){
       if(registerStage!=null){
        readingRegister();}
       else {
           System.out.println("系统出错，请退出重试！");
       }
    }

    @FXML
    public void initialize() {
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

    @FXML
    private Stage registerStage;

    @FXML
    public void setDialogStage(Stage dialogStage){
        this.registerStage=dialogStage;
    }


    //判断注册是否有效并注册或报错
    @FXML
    public void readingRegister() {
        //将不同的工种存入不同的文件，实现分别管理
        MenuItem menuItem=memberTypeChoser.getValue();
        String choice=menuItem.getText();
        System.out.println(choice);
        String name = registerNameText.getText().trim();
        String sex = registerSexText.getText().trim();
        String account = registerAccountNumberText.getText().trim();
        String passWord = registerPassWordText.getText();
        String again = registerAgainText.getText();
        Member member = new Member(name, sex, account, passWord);
        //简单判定是否合规
        if (name.isEmpty() || sex.isEmpty() || account.isEmpty() || passWord.isEmpty()) {
            showAlert("错误",
                    "注册信息不能为空",
                    AlertUtils.AlertType.ERROR,
                    registerStage);
            return;
        }

        //判定前后输入密码是否一致
        if (!again.equals(passWord)) {
            showAlert("错误",
                    "密码与之前不一致",
                    AlertUtils.AlertType.ERROR,
                    registerStage);
            return;
        }
        boolean registerUser;
        String ownFilePath;
        //检测之前是否有注册过
        switch (choice){
            case "supervisor":
                registerUser = registeredOrNot("/dataBase/members/supervisor.Json", account);
                ownFilePath="/dataBase/members/supervisor.Json";
                member=new Supervisor(name,sex,account,passWord,"free");
                break;
                
            case "administrator":
                registerUser = registeredOrNot("/dataBase/members/administrator.Json", account);
                ownFilePath="/dataBase/members/administrator.Json";
                break;
                
            case "gridder": 
                registerUser = registeredOrNot("/dataBase/members/gridder.Json", account);
                ownFilePath="/dataBase/members/gridder.Json";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + choice);
        }
        if (registerUser) {
            showAlert("错误",
                    "用户已经存在",
                    AlertUtils.AlertType.ERROR,
                    registerStage);
        } else {
            writer(ownFilePath, account);
            showAlert("成功",
                    "恭喜您成功注册",
                    AlertUtils.AlertType.SUCCESS,
                    registerStage);
            closeRegister();
        }
    }

}