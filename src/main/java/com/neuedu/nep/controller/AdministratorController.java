package com.neuedu.nep.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuedu.nep.entity.AQIData;
import com.neuedu.nep.entity.Gridder;
import com.neuedu.nep.io.JsonIO;
import com.neuedu.nep.util.AlertUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;


import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.*;

import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import java.util.List;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;


import javafx.stage.Stage;

import static com.neuedu.nep.io.JsonIO.*;
import static com.neuedu.nep.util.FindUtil.findItAndGetIt;


public class AdministratorController {
    @FXML
    private Button backButton;


    @FXML
    private Button turnBackButton;

    @FXML
    private TextArea detailedInfoTextArea;

    @FXML
    private TextField aqiReportIdTextField;

    @FXML
    private Button queryButton;

    @FXML
    private TableColumn<ReportDetailItem,String> numColumn;

    @FXML
    private TableColumn<ReportDetailItem,String> provinceColumn;

    @FXML
    private TableView<AQIData> reportDetailTableView;

    @FXML
    private TableColumn<AQIData,String> AQIColumn;

    @FXML
    private TableColumn<AQIData,String> dataColumn;

    @FXML
    private TableColumn<AQIData,String> infoColumn;

    @FXML
    private TableColumn<AQIData,String> nameColumn;

    @FXML
    private TableColumn<AQIData,String> cityColumn;

    @FXML
    private TableColumn<AQIData,String> detailedAddress;

    @FXML
    private ComboBox<String> GridderComboBox;

    @FXML
    private Button assignButton;

    @FXML
    private Stage AQIStage;

    @FXML
    public Stage getAQIStage(){
        return AQIStage;
    }

    @FXML
    public void setAdministratorStage(Stage stage){
        this.AQIStage=stage;
    }
    @FXML
    private  void initialize(){
        TableColumn<AQIData, String> numColumn = new TableColumn<>("编号");
        numColumn.setCellValueFactory(new PropertyValueFactory<>("num"));

        TableColumn<AQIData, String> provinceColumn = new TableColumn<>("省份");
        provinceColumn.setCellValueFactory(new PropertyValueFactory<>("province"));

        TableColumn<AQIData, String> cityColumn = new TableColumn<>("城市");
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));

        TableColumn<AQIData, String> addressColumn = new TableColumn<>("详细地址");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("detailedAddress"));

        TableColumn<AQIData, String> levelColumn = new TableColumn<>("AQI等级");
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("AQILevel"));

        TableColumn<AQIData, String> dateColumn = new TableColumn<>("日期");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<AQIData, String> infoColumn = new TableColumn<>("详细信息");
        infoColumn.setCellValueFactory(new PropertyValueFactory<>("detailedInfo"));

        TableColumn<AQIData, String> publisherColumn = new TableColumn<>("发布者");
        publisherColumn.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        // 添加列到TableView
        reportDetailTableView.getColumns().addAll(
                numColumn, provinceColumn, cityColumn,
                addressColumn, levelColumn, dateColumn,
                infoColumn, publisherColumn
        );
        ObservableList<AQIData> aqiDataList = parseJSONData("/dataBase/members/AQIDataBaseCreatedBySup.Json");

        // 将数据设置到TableView
        reportDetailTableView.setItems(aqiDataList);
        reportDetailTableView.getSelectionModel().selectedItemProperty().addListener(((observableValue, aqiData, t1) -> {
            if(t1!=null){
                detailedInfoTextArea.setText(t1.toString());
            }
        }));
        //初始化下拉框选项
        List<Gridder> list=read("/dataBase/members/gridder.json",new Gridder());
        List<String>gridderList= new ArrayList<>();
        for(Gridder a : list){
            gridderList.add(a.getName());
            System.out.println("已添加网格员"+a.getName()+"进入菜单");
        }
        GridderComboBox.getItems().addAll(gridderList);
        //给查询按钮加一个事件监听器
        queryButton.setOnAction(event ->handleQuery());
        //给分配按钮加一个事件监听器
        assignButton.setOnAction(event ->handleAssign());
        turnBackButton.setOnAction(e-> {
            try {
                handleDismiss();
            } catch (IOException | URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        });
        backButton.setOnAction(e->handleBack());
        // 创建标题标签
        Label titleLabel = new Label("公众监督AQI反馈数据指派");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // 创建输入编号相关控件
        Label inputIdLabel = new Label("请输入AQI反馈数据编号：");
        TextField idTextField = new TextField();

        Button queryButton = new Button("查询");

        // 创建指派相关控件
        Label assignLabel = new Label("公众监督AQI反馈数据指派：");
        Label GridderLabel = new Label("请选择你要指派的网格员：");
        ComboBox<String> GridderComboBox = new ComboBox<>();
        // 可通过网格员ComboBox.getItems().add("网格员姓名1"); 等方式预先添加可选网格员
        Button assignButton = new Button("立即指派");



    }
    private void handleBack(){
        ObservableList<AQIData> aqiDataList = parseJSONData("/dataBase/members/AQIDataBaseCreatedBySup.Json");
        reportDetailTableView.setItems(aqiDataList);
    }

    private void handleDismiss() throws IOException, URISyntaxException {
        AQIData aqiData=reportDetailTableView.getSelectionModel().getSelectedItem();
        ObjectMapper objectMapper=new ObjectMapper();
        List<AQIData> list=objectMapper.readValue(JsonIO.class.getResource("/dataBase/members/AQIDataBaseCreatedBySup.Json"),objectMapper.getTypeFactory().constructCollectionType(List.class,AQIData.class));
        List<AQIData> modelList=new ArrayList<>(list);
        list.forEach(aqiData1 -> {
            if(aqiData1.getNum().equals(aqiData.getNum())){
                aqiData1.setState("打回");
                modelList.remove(aqiData1);
            }
        });
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(JsonIO.class.getResource("/dataBase/members/AQIDataBaseCreatedBySup.Json").toURI()),list);
        Stage stage=(Stage) backButton.getScene().getWindow();
        AlertUtils.showAlert("成功","打回报告成功，监督员正在全力更改...", AlertUtils.AlertType.SUCCESS,stage);
    }



    private void handleQuery() {
        String aqiReportId = aqiReportIdTextField.getText();
//        System.out.println(aqiReportId);
        if (aqiReportId.isEmpty()) {
            showAlert("错误", "请输入AQI报告ID");
            return;
        }
        ObservableList<AQIData> list=parseJSONData("/dataBase/members/AQIDataBaseCreatedBySup.Json");
        ObservableList<AQIData> showList = FXCollections.observableArrayList();
        for(AQIData a : list){
            if(a.getNum().equals(aqiReportId)){
                showList.add(a);
                reportDetailTableView.getItems().remove(0,list.size()-1);
                reportDetailTableView.setItems(showList);
            }
        }

    }

//    //  对数据进行处理来将其拆分成表格能读取的形式
//    private ObservableList<AQIData> parseJSONData(String dataPath) {
//            ObservableList<AQIData> data = FXCollections.observableArrayList();
//            List<AQIData> aqiDataList=read(dataPath,new AQIData());
//            for(AQIData a :aqiDataList ){
//                System.out.println(a.toString());
//            }
//            data.addAll(aqiDataList);
//            return data;
//    }

    private String getReportDetailById(String reportId) {
        // 这里模拟数据，实际应从数据库或 API 获取
        List<AQIData> list=read("/dataBase/members/AQIDataBaseCreatedBySup.Json",new AQIData());
        for(AQIData data : list){
            if(data.getNum().equals(reportId)){
                return data.toString();
            }
        }
//        if ("1".equals(reportId)) {
//            return "AQI反馈数据编号: 001\n" +
//                    "所在省区域: 广东省\n" +
//                    "所在市区域: 广州市\n" +
//                    "详细地址: 天河区XX路XX号\n" +
//                    "预估AQI等级: 良\n" +
//                    "反馈日期: 2025-06-10\n" +
//                    "反馈信息详情: 空气质量良好，无明显污染\n" +
//                    "反馈者姓名: 张三";
//        } else if ("2".equals(reportId)) {
//            return "AQI反馈数据编号: 002\n" +
//                    "所在省区域: 江苏省\n" +
//                    "所在市区域: 南京市\n" +
//                    "详细地址: 鼓楼区XX街XX号\n" +
//                    "预估AQI等级: 轻度污染\n" +
//                    "反馈日期: 2025-06-09\n" +
//                    "反馈信息详情: 空气中有轻微异味\n" +
//                    "反馈者姓名: 李四";
//        }
        return null;
    }

    private void handleAssign() {
        String reportId = aqiReportIdTextField.getText();
        AQIData selectedData=reportDetailTableView.getSelectionModel().getSelectedItem();
        String selectedGridder = GridderComboBox.getValue();
        if (reportId.isEmpty() && selectedData==null) {
            showAlert("错误", "请输入报告ID或选中一个报告");
            return;
        }
        if (selectedGridder == null) {
            showAlert("错误", "请选择网格员");
            return;
        }
        if(selectedData==null) {
             AQIData aqiData= findItAndGetIt("/dataBase/members/AQIDataBaseCreatedBySup.Json", reportId);
             aqiData.setGridder(selectedGridder);
             writer("/dataBase/members/AQIDataBaseCreatedByAdm.json",aqiData);
        }
        selectedData.setGridder(selectedGridder);
        writer("/dataBase/members/AQIDataBaseCreatedByAdm.json",selectedData);
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
