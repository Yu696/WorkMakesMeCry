package com.neuedu.nep.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.*;

import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;

public class AQIReportAssignController {
    @FXML
    private TextField aqiReportIdTextField;
    @FXML
    private Button queryButton;

    @FXML
    private TableView<ReportDetailItem> reportDetailTableView;
    @FXML
    private TableColumn<ReportDetailItem, String> propertyColumn;
    @FXML
    private TableColumn<ReportDetailItem, String> valueColumn;
    @FXML
    private ComboBox<String> GridderComboBox;
    @FXML
    private Button assignButton;
    @FXML
    private  void initialize(){
        // 初始化表格视图
        propertyColumn.setCellValueFactory(new PropertyValueFactory<>("property"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        // 设置列宽
        propertyColumn.setPrefWidth(150);
        valueColumn.setPrefWidth(300);
        //初始化下拉框选项
        List<String>gridderList= Arrays.asList("李晓旭","张大千","王力");
        GridderComboBox.getItems().addAll(gridderList);
        //给查询按钮加一个事件监听器
        queryButton.setOnAction(event ->handleQuery());
        //给分配按钮加一个事件监听器
        assignButton.setOnAction(event ->handleAssign());



    }
    private void handleQuery() {
        String aqiReportId = aqiReportIdTextField.getText();
        if (aqiReportId.isEmpty()) {
            showAlert("错误", "请输入AQI报告ID");
            return;
        }

        // 模拟根据报告 ID 查询报告详情
        String reportDetail = getReportDetailById(aqiReportId);
        if (reportDetail == null) {
            showAlert("错误", "未找到对应 AQI 报告");
            return;
        }
        ObservableList<ReportDetailItem> tableData = parseReportDetail(reportDetail);
        reportDetailTableView.setItems(tableData);


    }
    private ObservableList<ReportDetailItem> parseReportDetail(String reportDetail) {
        ObservableList<ReportDetailItem> data = FXCollections.observableArrayList();
        String[] lines = reportDetail.split("\n");

        for (String line : lines) {
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                data.add(new ReportDetailItem(parts[0].trim(), parts[1].trim()));
            }
        }

        return data;
    }

    private String getReportDetailById(String reportId) {
        // 这里模拟数据，实际应从数据库或 API 获取
        if ("1".equals(reportId)) {
            return "AQI反馈数据编号: 001\n" +
                    "所在省区域: 广东省\n" +
                    "所在市区域: 广州市\n" +
                    "详细地址: 天河区XX路XX号\n" +
                    "预估AQI等级: 良\n" +
                    "反馈日期: 2025-06-10\n" +
                    "反馈信息详情: 空气质量良好，无明显污染\n" +
                    "反馈者姓名: 张三";
        } else if ("2".equals(reportId)) {
            return "AQI反馈数据编号: 002\n" +
                    "所在省区域: 江苏省\n" +
                    "所在市区域: 南京市\n" +
                    "详细地址: 鼓楼区XX街XX号\n" +
                    "预估AQI等级: 轻度污染\n" +
                    "反馈日期: 2025-06-09\n" +
                    "反馈信息详情: 空气中有轻微异味\n" +
                    "反馈者姓名: 李四";
        }
        return null;
    }

    private void handleAssign() {
        String reportId = aqiReportIdTextField.getText();
        String selectedGridder = GridderComboBox.getValue();

        if (reportId.isEmpty()) {
            showAlert("错误", "请输入报告ID");
            return;
        }
        if (selectedGridder == null) {
            showAlert("错误", "请选择网格员");
            return;
        }
        // 模拟分配报告
        showAlert("成功", "已将报告 " + reportId + " 分配给 " + selectedGridder);
    }

    /**
     * 显示提示框
     * @param title 提示框标题
     * @param content 提示框内容
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    // 表格数据模型类
    public static class ReportDetailItem {
        private final String property;
        private final String value;

        public ReportDetailItem(String property, String value) {
            this.property = property;
            this.value = value;
        }

        public String getProperty() {
            return property;
        }

        public String getValue() {
            return value;
        }
    }




}
