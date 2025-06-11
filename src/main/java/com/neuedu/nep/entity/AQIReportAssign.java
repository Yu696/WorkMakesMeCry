package com.neuedu.nep.entity;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class AQIReportAssign extends  Application {
    public class ReportDetailItem {
        private String reportId;
        private String area;
        private String aqiLevel;

        public ReportDetailItem(String reportId, String area, String aqiLevel) {
            this.reportId = reportId;
            this.area = area;
            this.aqiLevel = aqiLevel;
        }

        public String getReportId() {
            return reportId;
        }

        public void setReportId(String reportId) {
            this.reportId = reportId;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getAqiLevel() {
            return aqiLevel;
        }

        public void setAqiLevel(String aqiLevel) {
            this.aqiLevel = aqiLevel;
        }
    }
    @Override
    public void start(Stage primaryStage) {
        // 创建标题标签
        Label titleLabel = new Label("公众监督AQI反馈数据指派");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

      // 创建输入编号相关控件
        Label inputIdLabel = new Label("请输入AQI反馈数据编号：");
        TextField idTextField = new TextField();

        Button queryButton = new Button("查询");
        // 创建表格视图替代TextArea
        TableView<ReportDetailItem> tableView = new TableView<>();
        tableView.setPrefHeight(200);
        // 定义表格列
        TableColumn<ReportDetailItem, String> propertyColumn = new TableColumn<>("属性");
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("property"));
        propertyColumn.setPrefWidth(150);

        TableColumn<ReportDetailItem, String> valueColumn = new TableColumn<>("值");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setPrefWidth(350);

        tableView.getColumns().addAll(propertyColumn, valueColumn);
        // 创建指派相关控件
        Label assignLabel = new Label("公众监督AQI反馈数据指派：");
        Label GridderLabel = new Label("请选择你要指派的网格员：");
        ComboBox<String> GridderComboBox = new ComboBox<>();
        // 可通过网格员ComboBox.getItems().add("网格员姓名1"); 等方式预先添加可选网格员
        Button assignButton = new Button("立即指派");

        // 布局设置，使用VBox垂直布局
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));
        vbox.getChildren().addAll(
                titleLabel,
                new VBox(5, inputIdLabel, idTextField, queryButton),
                tableView,
                assignLabel,
                new VBox(5,GridderLabel, GridderComboBox, assignButton)
        );
        FXMLLoader fxmlLoader = new FXMLLoader(AQIReportAssign.class.getResource("/com/neuedu/nep/aqiReportAssign.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 600, 400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 创建场景并设置到舞台

        primaryStage.setTitle("AQI反馈数据指派界面");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    }
