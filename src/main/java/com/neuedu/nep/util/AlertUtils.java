package com.neuedu.nep.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.neuedu.nep.entity.Member;
import com.neuedu.nep.io.JsonIO;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.neuedu.nep.io.JsonIO.read;

/*
* 错误提醒工具类，用于在JavaFX应用程序中显示各种类型的提醒
*/
public class AlertUtils {
    // 提醒类型枚举
    public enum AlertType {
        ERROR, WARNING, SUCCESS
    }

    // 显示提醒
    public static void showAlert(String title, String message, com.neuedu.nep.util.AlertUtils.AlertType type, Stage ownerStage) {
        // 创建舞台
        Stage alertStage = new Stage();
        alertStage.initStyle(StageStyle.TRANSPARENT);
        alertStage.setAlwaysOnTop(true);
        System.out.println("成功");
        // 如果有主窗口，将提醒窗口定位在主窗口上方
        if (ownerStage != null && ownerStage.getScene() != null) {
            Scene ownerScene = ownerStage.getScene();
            double x = ownerStage.getX() + ownerScene.getWidth() / 2 - 200;
            double y = ownerStage.getY() + 20;
            alertStage.setX(x);
            alertStage.setY(y);
        } else {
            // 默认位置
            alertStage.setX(200);
            alertStage.setY(20);
        }

        // 创建内容
        VBox contentBox = createAlertContent(title, message, type);

        // 创建场景
        Scene scene = new Scene(contentBox);
        scene.setFill(Color.TRANSPARENT);
        alertStage.setScene(scene);

        // 设置动画
        setupAlertAnimation(contentBox, alertStage);

        // 显示提醒
        alertStage.show();

        // 设置自动关闭
        if (type != AlertType.ERROR) { // 错误类型不自动关闭
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // 2秒后自动关闭
                    javafx.application.Platform.runLater(alertStage::close);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // 创建提醒内容
    private static VBox createAlertContent(String title, String message, AlertType type) {
        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(15));
        vbox.setPrefWidth(400);
        vbox.setMaxWidth(400);

        // 设置背景和边框
        switch (type) {
            case ERROR:
                vbox.setStyle("-fx-background-color: rgba(239, 68, 68, 0.9); " +
                        "-fx-background-radius: 5px; " +
                        "-fx-border-color: rgba(185, 28, 28, 0.9); " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 5px;");
                break;
            case WARNING:
                vbox.setStyle("-fx-background-color: rgba(245, 158, 11, 0.9); " +
                        "-fx-background-radius: 5px; " +
                        "-fx-border-color: rgba(180, 83, 9, 0.9); " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 5px;");
                break;
            case SUCCESS:
                vbox.setStyle("-fx-background-color: rgba(16, 185, 129, 0.9); " +
                        "-fx-background-radius: 5px; " +
                        "-fx-border-color: rgba(4, 120, 87, 0.9); " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 5px;");
                break;
        }

        // 创建标题和图标
        HBox titleBox = new HBox(10);

        // 添加图标
        ImageView icon = new ImageView();
        icon.setFitHeight(24);
        icon.setFitWidth(24);

        switch (type) {
            case ERROR:
                icon.setImage(new Image(AlertUtils.class.getResourceAsStream("/image/error.jpg")));
                break;
            case WARNING:
                icon.setImage(new Image(AlertUtils.class.getResourceAsStream("/image/warning.jpg")));
                break;
            case SUCCESS:
                icon.setImage(new Image(AlertUtils.class.getResourceAsStream("/image/success.jpg")));
                break;
        }

        // 添加标题
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", javafx.scene.text.FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.WHITE);

        titleBox.getChildren().addAll(icon, titleLabel);

        // 添加消息
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("System", 12));
        messageLabel.setTextFill(Color.WHITE);
        messageLabel.setWrapText(true);

        vbox.getChildren().addAll(titleBox, messageLabel);

        return vbox;
    }

    // 设置动画效果
    private static void setupAlertAnimation(VBox contentBox, Stage alertStage) {
        // 淡入动画
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), contentBox);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // 从上方滑入动画
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), contentBox);
        slideIn.setFromY(-50);
        slideIn.setToY(0);

        // 淡出动画
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), contentBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> alertStage.close());

        // 鼠标悬停时暂停淡出
        contentBox.setOnMouseEntered(e -> {
            fadeOut.stop();
            contentBox.setOpacity(1.0);
        });

        // 鼠标离开时继续淡出
        contentBox.setOnMouseExited(e -> {
            if (alertStage.isShowing()) {
                fadeOut.playFromStart();
            }
        });

        // 点击关闭
        contentBox.setOnMouseClicked(e -> {
            fadeOut.playFromStart();
        });

        // 播放入场动画
        fadeIn.play();
        slideIn.play();
    }

    //对用户信息进行比对
    public static boolean registeredOrNot(String filePath, String memberAccount) {
            Member member=new Member("余润东","男","111","123");
            List<Member> memberList=read(filePath,member);
            System.out.println("名单读取成功");
            for(Member a : memberList){
                System.out.println(a.toString());
                if(a.getAccount().equals(memberAccount)){
                    return true;
                }
            }
            return false;
            //            JsonMapper jsonMapper = new JsonMapper();
//            JsonNode jsonNode = jsonMapper.readTree(JsonIO.class.getResource(filePath));
//            System.out.println(jsonNode.asText());
//            if (jsonNode.isArray()) {
//                for (JsonNode jsonItem : jsonNode) {
//                    if (jsonItem.get("account").asText().equals(memberAccount)) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//            if (jsonNode.isObject()) {
//                if (jsonNode.get("account").asText().equals(memberAccount)) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//            if (jsonNode.isEmpty()){
//                return false;
//            }
//            else {
//                System.out.println("数据格式不正确");
//                return true;
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
}

