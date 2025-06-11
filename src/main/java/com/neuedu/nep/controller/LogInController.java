package com.neuedu.nep.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuedu.nep.entity.Member;
import com.neuedu.nep.io.JsonIO;
import com.neuedu.nep.util.AlertUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import static com.neuedu.nep.io.JsonIO.read;
import static com.neuedu.nep.util.AlertUtils.registeredOrNot;
import static com.neuedu.nep.util.AlertUtils.showAlert;


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
                if(registeredOrNot("/dataBase/members/supervisor.Json",account)) {
                    System.out.println("找到了！");
                    Member supervisor = getThisPerson("/dataBase/members/supervisor.Json", account);
                    System.out.println("该人已在我手中");
                    handleUserView(supervisor);
                }
                else{
                    showAlert("错误","您并没有注册过", AlertUtils.AlertType.ERROR, logInStage);
                    closeThisPage();
                }

                break;

            case "administrator":
                if(registeredOrNot("/dataBase/members/administrator.json",account)) {
                    Member administrator = getThisPerson("/dataBase/members/administrator.Json", account);
                    handleUserView(administrator);
                }else{
                    showAlert("错误","您并没有注册过", AlertUtils.AlertType.ERROR,
                            logInStage);
                    closeThisPage();
                }


                break;

            case "gridder":
                if(registeredOrNot("/dataBase/members/gridder.json",account)) {
                    Member gridder= getThisPerson("/dataBase/members/gridder.Json", account);
                    handleUserView(gridder);
                }else{
                    showAlert("错误","您并没有注册过", AlertUtils.AlertType.ERROR,
                            logInStage);
                    closeThisPage();
                }



                break;
        }

    }

    public Member getThisPerson(String filePath,String account) throws URISyntaxException {
        Member member=new Member("余润东","男","111","123");
        List<Member> list=read(filePath,member);
        for(Member a : list){
            if(a.getAccount().equals(account)){
                return a;
            }
        }
        System.out.println("没这个人--来自getthisperson");
        return null;
    }

    public void handleUserView(Member member){

    }
}
