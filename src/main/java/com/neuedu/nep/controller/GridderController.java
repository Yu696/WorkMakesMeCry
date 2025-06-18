package com.neuedu.nep.controller;

import com.neuedu.nep.entity.AQIData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.neuedu.nep.io.JsonIO.*;

public class GridderController {

    @FXML
    private TableView<AQIData> dataTableView;

    @FXML
    private TextField dataIdField;
    @FXML
    private TextField so2Field;
    @FXML
    private TextField coField;
    @FXML
    private TextField pm25Field;
    @FXML
    private Label currentGridderLabel;
    @FXML
    private Label assignedGridderLabel;
    @FXML
    private Label so2LevelLabel;
    @FXML
    private Label so2PollutionLabel;
    @FXML
    private Label coLevelLabel;
    @FXML
    private Label coPollutionLabel;
    @FXML
    private Label pm25LevelLabel;
    @FXML
    private Label pm25PollutionLabel;
    @FXML
    private Label finalLevelLabel;
    @FXML
    private Label finalPollutionLabel;

    private ObservableList<AQIData> dataList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        setupTableSelectionListener();
        setupConcentrationListeners();
        // 设置当前登录的网格员
        Platform.runLater(() -> {
            // 现在组件已附加到场景
            Stage stage=(Stage) dataIdField.getScene().getWindow();
            String nameLine=stage.getTitle();
            String name=nameLine.split(":")[1];
            currentGridderLabel.setText(name);
            loadAssignedData();
        });


    }

    private void loadAssignedData() {
        // 从数据库加载分配给当前网格员的数据
        // 这里假设有一个方法可以获取当前登录的网格员ID
        Stage stage=(Stage) dataIdField.getScene().getWindow();
        String nameLine=stage.getTitle();
        String name=nameLine.split(":")[1];
        ObservableList<AQIData> allData = parseJSONData("/dataBase/members/AQIDataBaseCreatedByAdm.json",new AQIData());

        for (AQIData data : allData) {
            if (name.equals(data.getGridder()) && !"已处理".equals(data.getState())) {
                dataList.add(data);
            }
        }

        dataTableView.setItems(dataList);
    }

    private void setupTable() {
        // 初始化表格列
        TableColumn<AQIData, String> numCol = new TableColumn<>("编号");
        numCol.setCellValueFactory(new PropertyValueFactory<>("num"));

        TableColumn<AQIData, String> publisherCol = new TableColumn<>("反馈者");
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        TableColumn<AQIData, String> dateCol = new TableColumn<>("反馈时间");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<AQIData, String> levelCol = new TableColumn<>("预估等级");
        levelCol.setCellValueFactory(new PropertyValueFactory<>("AQILevel"));

        TableColumn<AQIData, String> provinceCol = new TableColumn<>("省区域");
        provinceCol.setCellValueFactory(new PropertyValueFactory<>("province"));

        TableColumn<AQIData, String> cityCol = new TableColumn<>("市区域");
        cityCol.setCellValueFactory(new PropertyValueFactory<>("city"));

        TableColumn<AQIData, String> addressCol = new TableColumn<>("具体地址");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("detailedAddress"));

        TableColumn<AQIData, String> infoCol = new TableColumn<>("反馈信息");
        infoCol.setCellValueFactory(new PropertyValueFactory<>("detailedInfo"));

        TableColumn<AQIData, String> stateCol = new TableColumn<>("状态");
        stateCol.setCellValueFactory(new PropertyValueFactory<>("state"));

        dataTableView.getColumns().addAll(numCol, publisherCol, dateCol, levelCol,
                provinceCol, cityCol, addressCol, infoCol, stateCol);
    }

    private void setupTableSelectionListener() {
        dataTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        updateFormWithSelectedData(newValue);
                    }
                });
    }

    private void setupConcentrationListeners() {
        so2Field.textProperty().addListener((obs, oldVal, newVal) -> updatePollutionLevels());
        coField.textProperty().addListener((obs, oldVal, newVal) -> updatePollutionLevels());
        pm25Field.textProperty().addListener((obs, oldVal, newVal) -> updatePollutionLevels());
    }

    // 修改updateFormWithSelectedData方法
    private void updateFormWithSelectedData(AQIData data) {
        dataIdField.setText(data.getNum());


        // 如果已有实测数据，则填充表单
        if (data.getSo2() != null) {
            so2Field.setText(String.valueOf(data.getSo2()));
            so2LevelLabel.setText(data.getSo2Level());
            so2PollutionLabel.setText(getPollutionDescription(data.getSo2Level()));
        }
        if (data.getCo() != null) {
            coField.setText(String.valueOf(data.getCo()));
            coLevelLabel.setText(data.getCoLevel());
            coPollutionLabel.setText(getPollutionDescription(data.getCoLevel()));
        }
        if (data.getPm25() != null) {
            pm25Field.setText(String.valueOf(data.getPm25()));
            pm25LevelLabel.setText(data.getPm25Level());
            pm25PollutionLabel.setText(getPollutionDescription(data.getPm25Level()));
        }

        if (data.getFinalLevel() != null) {
            finalLevelLabel.setText(data.getFinalLevel());
            finalPollutionLabel.setText(data.getFinalPollution());
        }

        // 更新污染等级显示
        updatePollutionLevels();
    }

    private String getPollutionDescription(String level) {
        switch (level) {
            case "一级": return "优";
            case "二级": return "良";
            case "三级": return "轻度污染";
            case "四级": return "中度污染";
            case "五级": return "重度污染";
            case "六级": return "严重污染";
            default: return "未评定";
        }
    }

    private void updatePollutionLevels() {
        try {
            // 更新SO2等级
            if (!so2Field.getText().isEmpty()) {
                double so2 = Double.parseDouble(so2Field.getText());
                String[] so2Result = calculatePollutionLevel(so2,
                        new double[]{0, 50, 150, 475, 800, 1600, 2100, 2620});
                so2LevelLabel.setText(so2Result[0]);
                so2PollutionLabel.setText(so2Result[1]);
                setLevelLabelStyle(so2LevelLabel, so2Result[0]);
            }

            // 更新CO等级
            if (!coField.getText().isEmpty()) {
                double co = Double.parseDouble(coField.getText());
                String[] coResult = calculatePollutionLevel(co,
                        new double[]{0, 2, 4, 14, 24, 36, 48, 60});
                coLevelLabel.setText(coResult[0]);
                coPollutionLabel.setText(coResult[1]);
                setLevelLabelStyle(coLevelLabel, coResult[0]);
            }

            // 更新PM2.5等级
            if (!pm25Field.getText().isEmpty()) {
                double pm25 = Double.parseDouble(pm25Field.getText());
                String[] pm25Result = calculatePollutionLevel(pm25,
                        new double[]{0, 35, 75, 115, 150, 250, 350, 500});
                pm25LevelLabel.setText(pm25Result[0]);
                pm25PollutionLabel.setText(pm25Result[1]);
                setLevelLabelStyle(pm25LevelLabel, pm25Result[0]);
            }

            // 计算最终AQI等级
            updateFinalAQILevel();

        } catch (NumberFormatException e) {
            // 处理无效输入
        }
    }

    private String[] calculatePollutionLevel(double value, double[] thresholds) {
        int index = 0;
        for (int i = 0; i < thresholds.length - 1; i++) {
            if (value >= thresholds[i] && value < thresholds[i + 1]) {
                index = i;
                break;
            }
            if (i == thresholds.length - 2 && value >= thresholds[i + 1]) {
                index = i + 1;
            }
        }

        String[] levels = {"一级", "二级", "三级", "四级", "五级", "六级"};
        String[] pollutions = {"优", "良", "轻度污染", "中度污染", "重度污染", "严重污染"};

        index = Math.min(index, levels.length - 1);
        return new String[]{levels[index], pollutions[index]};
    }

    private void setLevelLabelStyle(Label label, String level) {
        switch (level) {
            case "一级": label.setStyle("-fx-text-fill: green;"); break;
            case "二级": label.setStyle("-fx-text-fill: #99cc00;"); break;
            case "三级": label.setStyle("-fx-text-fill: orange;"); break;
            case "四级": label.setStyle("-fx-text-fill: #ff6600;"); break;
            case "五级": label.setStyle("-fx-text-fill: red;"); break;
            case "六级": label.setStyle("-fx-text-fill: purple;"); break;
        }
    }

    private void updateFinalAQILevel() {
        String so2Level = so2LevelLabel.getText();
        String coLevel = coLevelLabel.getText();
        String pm25Level = pm25LevelLabel.getText();

        // 获取各污染物的等级索引
        int so2Index = getLevelIndex(so2Level);
        int coIndex = getLevelIndex(coLevel);
        int pm25Index = getLevelIndex(pm25Level);

        // 取最高等级
        int finalLevelIndex = Math.max(so2Index, Math.max(coIndex, pm25Index));

        String[] levels = {"一级", "二级", "三级", "四级", "五级", "六级"};
        String[] pollutions = {"优", "良", "轻度污染", "中度污染", "重度污染", "严重污染"};

        if (finalLevelIndex >= 0 && finalLevelIndex < levels.length) {
            finalLevelLabel.setText(levels[finalLevelIndex]);
            finalPollutionLabel.setText(pollutions[finalLevelIndex]);
            setLevelLabelStyle(finalLevelLabel, levels[finalLevelIndex]);
        }
    }

    private int getLevelIndex(String level) {
        switch (level) {
            case "一级": return 0;
            case "二级": return 1;
            case "三级": return 2;
            case "四级": return 3;
            case "五级": return 4;
            case "六级": return 5;
            default: return 0;
        }
    }

    @FXML
    private void handleSubmit() {
        AQIData selectedData = dataTableView.getSelectionModel().getSelectedItem();
        if (selectedData == null) {
            showAlert("错误", "请先选择一条数据");
            return;
        }

        try {
            // 获取并验证实测数据
            double so2 = validateAndGetValue(so2Field.getText(), "SO2浓度");
            double co = validateAndGetValue(coField.getText(), "CO浓度");
            double pm25 = validateAndGetValue(pm25Field.getText(), "PM2.5浓度");

            // 计算各污染物等级
            String[] so2Result = calculatePollutionLevel(so2, new double[]{0, 50, 150, 475, 800, 1600, 2100, 2620});
            String[] coResult = calculatePollutionLevel(co, new double[]{0, 2, 4, 14, 24, 36, 48, 60});
            String[] pm25Result = calculatePollutionLevel(pm25, new double[]{0, 35, 75, 115, 150, 250, 350, 500});

            // 更新数据对象
            selectedData.setSo2(so2);
            selectedData.setCo(co);
            selectedData.setPm25(pm25);
            selectedData.setSo2Level(so2Result[0]);
            selectedData.setCoLevel(coResult[0]);
            selectedData.setPm25Level(pm25Result[0]);
            selectedData.setFinalLevel(finalLevelLabel.getText());
            selectedData.setFinalPollution(finalPollutionLabel.getText());
            selectedData.setState("已处理");

            // 保存到数据库
            writer("/dataBase/members/AQIDataBaseCreatedByGri.Json", selectedData);

            // 更新表格显示
            dataTableView.refresh();
            showAlert("成功", "数据提交成功");
            clearForm();

        } catch (IllegalArgumentException e) {
            showAlert("错误", e.getMessage());
        }
    }

    private double validateAndGetValue(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + "不能为空");
        }
        try {
            double value = Double.parseDouble(input);
            if (value < 0) {
                throw new IllegalArgumentException(fieldName + "不能为负数");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + "必须是有效的数字");
        }
    }

    private void clearForm() {
        dataIdField.clear();
        so2Field.clear();
        coField.clear();
        pm25Field.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // 新增：创建新数据并添加到表格的方法
    private void createNewDataAndAddToTable() {
        try {
            // 验证必填字段
            if (so2Field.getText().isEmpty() || coField.getText().isEmpty() || pm25Field.getText().isEmpty()) {
                throw new IllegalArgumentException("SO2、CO和PM2.5浓度均为必填项");
            }

            // 获取当前网格员
            String currentGridder = currentGridderLabel.getText();
            if ("未分配".equals(currentGridder) || currentGridder == null || currentGridder.trim().isEmpty()) {
                throw new IllegalArgumentException("当前网格员信息缺失，请确认登录状态");
            }

            // 生成唯一编号
            String dataId = "AQI-" + UUID.randomUUID().toString().substring(0, 8);

            // 获取并验证实测数据
            double so2 = validateAndGetValue(so2Field.getText(), "SO2浓度");
            double co = validateAndGetValue(coField.getText(), "CO浓度");
            double pm25 = validateAndGetValue(pm25Field.getText(), "PM2.5浓度");

            // 计算各污染物等级
            String[] so2Result = calculatePollutionLevel(so2, new double[]{0, 50, 150, 475, 800, 1600, 2100, 2620});
            String[] coResult = calculatePollutionLevel(co, new double[]{0, 2, 4, 14, 24, 36, 48, 60});
            String[] pm25Result = calculatePollutionLevel(pm25, new double[]{0, 35, 75, 115, 150, 250, 350, 500});

            // 计算最终AQI等级
            updateFinalAQILevel();
            String finalLevel = finalLevelLabel.getText();
            String finalPollution = finalPollutionLabel.getText();

            // 创建新的AQIData对象
            AQIData newData = new AQIData();
            newData.setNum(dataId);
            newData.setPublisher(currentGridder); // 反馈者为当前网格员
            newData.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            newData.setAQILevel(finalLevel);
            newData.setProvince("江苏省"); // 示例省份
            newData.setCity("南京市"); // 示例城市
            newData.setDetailedAddress("请填写具体地址"); // 可改为从表单获取
            newData.setDetailedInfo("SO2: " + so2 + "ug/m3, CO: " + co + "ug/m3, PM2.5: " + pm25 + "ug/m3");
            newData.setState("已提交");
            newData.setGridder(currentGridder); // 指派的网格员为当前登录者

            // 设置实测数据
            newData.setSo2(so2);
            newData.setCo(co);
            newData.setPm25(pm25);
            newData.setSo2Level(so2Result[0]);
            newData.setCoLevel(coResult[0]);
            newData.setPm25Level(pm25Result[0]);
            newData.setFinalLevel(finalLevel);
            newData.setFinalPollution(finalPollution);

            // 添加到表格数据源
            dataList.add(newData);

            // 保存到数据库
            writer("/dataBase/members/AQIDataBaseCreatedByAdm.json", newData);

            // 刷新表格
            dataTableView.refresh();
            showAlert("成功", "新数据提交成功并添加到表格");
            clearForm();

        } catch (IllegalArgumentException e) {
            showAlert("错误", e.getMessage());
        }
        // 添加缺失的右花括号
    }
}