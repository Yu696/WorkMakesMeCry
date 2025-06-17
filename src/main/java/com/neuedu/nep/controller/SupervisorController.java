package com.neuedu.nep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuedu.nep.entity.*;
import com.neuedu.nep.io.JsonIO;
import com.neuedu.nep.util.AlertUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import com.neuedu.nep.util.FileUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.neuedu.nep.io.JsonIO.read;
import static com.neuedu.nep.io.JsonIO.writer;
import static com.neuedu.nep.util.AlertUtils.showAlert;
import static com.neuedu.nep.util.FileUtils.getMaxIdFromJson;
import static com.neuedu.nep.util.FileUtils.getThisPerson;


public class SupervisorController implements Initializable {
    @FXML
    private TabPane Tab;

    @FXML
    public Label title2;

    @FXML
    private Label supervisorName;      // 监督员姓名

    @FXML
    private AnchorPane paneRoot;

    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;

    @FXML
    private TextField address;         // 地址输入框

    @FXML
    private ChoiceBox<String> GradeChoice; // AQI等级选择框

    @FXML
    private TableView<AqiLevel> Aqilevel; // AQI等级表格

    @FXML
    private TableColumn<AqiLevel, String> Grade;  // AQI等级表

    @FXML
    private TableColumn<AqiLevel, String> description;

    @FXML
    private TableColumn<AqiLevel, String> influence;

    @FXML
    private ChoiceBox<String> provinceChoice; // 省份选择框

    @FXML
    private ChoiceBox<String> cityChoice;     // 城市选择框

    @FXML
    private TextField information;

    @FXML
    private DatePicker time;

    @FXML
    private TableView<AQIData> showDetail;

    @FXML
    private TableColumn<AqiLevel, String> num1;

    @FXML
    private TableColumn<AqiLevel, String> time1;

    @FXML
    private TableColumn<AqiLevel, String> state;

    @FXML
    private Button confirmButton;

    @FXML
    private TextArea infoText;

    @FXML
    private Button flushButton;

    private static int currentId = 1;

    // 反馈文本域
    //关闭
    public void closeFeedback() {
        if (FeedbackStage != null) {
            FeedbackStage.close();
        }
    }

    //生成时间
    public LocalDate getSelectedDate() {
        return time.getValue();
    }

    //生成编号
    public static synchronized String generateSequentialId() {
//        return String.valueOf(currentId++);
        currentId = getMaxIdFromJson() + 1;
        return String.format("%03d", currentId);

    }

    // AQI等级枚举
    private enum AqiLevelEnum {
        一级("优", "空气质量令人满意,基本无空气污染"),
        二级("良", "空气质量可接受,但某些污染物可能对极少数异常敏感人群健康有较弱的影响"),
        三级("轻度污染", "易感人群症状有轻度加剧,健康人群出现刺激症状"),
        四级("中度污染", "进一步加剧易感人群症状,可能对健康人群心脏、呼吸系统有影响"),
        五级("重度污染", "心脏病和肺病患者症状显著加剧,运动耐受力降低,健康人群普遍出现症状"),
        六级("严重污染", "健康人群耐受力降低,有明显强烈症状,提前出现某些疾病");

        private final String description;
        private final String influence;

        AqiLevelEnum(String description, String influence) {
            this.description = description;
            this.influence = influence;
        }
    }

    // 表格行数据类（使用JavaFX属性）
    public static class AqiLevel {
        private final SimpleStringProperty level;
        private final SimpleStringProperty description;
        private final SimpleStringProperty influence;

        public AqiLevel(String level, String description, String influence) {
            this.level = new SimpleStringProperty(level);
            this.description = new SimpleStringProperty(description);
            this.influence = new SimpleStringProperty(influence);
        }

        public SimpleStringProperty levelProperty() {
            return level;
        }

        public SimpleStringProperty descriptionProperty() {
            return description;
        }

        public SimpleStringProperty influenceProperty() {
            return influence;
        }
    }

    private void setupAqiTable() {
        ObservableList<AqiLevel> levels = FXCollections.observableArrayList();
        for (AqiLevelEnum level : AqiLevelEnum.values()) {
            levels.add(new AqiLevel(
                    level.name(),
                    level.description,
                    level.influence
            ));
        }
        Aqilevel.setItems(levels);
        Grade.setCellValueFactory(cellData -> cellData.getValue().levelProperty());
        Grade.setPrefWidth(212);
        description.setCellValueFactory(cell -> cell.getValue().descriptionProperty());
        description.setPrefWidth(236);
        influence.setCellValueFactory(cell -> cell.getValue().influenceProperty());
        influence.setPrefWidth(488);
    }

    //模拟登录用户
    private void initUI(String name) {
        supervisorName.setText(name);

    }

    // 省份-城市数据
    private final Map<String, List<String>> provinceCities = new LinkedHashMap<>();

    private void loadProvinces() {
        provinceCities.put("辽宁省", Arrays.asList("沈阳市", "大连市", "鞍山市"));
        provinceCities.put("广东省", Arrays.asList("广州市", "深圳市", "珠海市"));
        provinceChoice.getItems().addAll(provinceCities.keySet());
        //provinceChoice.getSelectionModel().selectFirst();
    }

    private void bindCityToProvince() {
        provinceChoice.setOnAction(e -> {
            String province = provinceChoice.getValue();
            cityChoice.getItems().setAll(provinceCities.getOrDefault(province, Collections.emptyList()));
            cityChoice.getSelectionModel().selectFirst();
        });
    }

    //等级选择框
    private void initGradeChoices() {
        GradeChoice.getItems().addAll(
                Arrays.stream(AqiLevelEnum.values())
                        .map(Enum::name)
                        .toList()
        );
        GradeChoice.getSelectionModel().selectFirst();
    }


    @FXML
    private Stage FeedbackStage;

    @FXML
    public void setDialogStage(Stage dialogStage) {
        this.FeedbackStage = dialogStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            // 现在组件已附加到场景
            Stage stage=(Stage) Tab.getScene().getWindow();
            String nameLine=stage.getTitle();
            String name=nameLine.split(":")[1];
            initUI(name);
            filterAndShowData(name);
        });

        loadProvinces();
        setupAqiTable();
        initGradeChoices();
        bindCityToProvince();
        cityChoice.getSelectionModel().clearSelection();
        GradeChoice.getSelectionModel().clearSelection();

    }


    @FXML
    private void handleSubmit() {
        Stage stage=(Stage) submitButton.getScene().getWindow();
        String province = provinceChoice.getValue();
        String city = cityChoice.getValue();
        String detailedAddress = address.getText();
        String AQILevel = GradeChoice.getValue();
        String detailedInfo = information.getText();
        String publisher = supervisorName.getText();
        String gridder = null;
        String state = null;
        String date;
        // 检查 DatePicker 的值是否为 null
        if (time.getValue() == null) {
            showAlert("错误", "请选择提交时间", AlertUtils.AlertType.ERROR, stage);
            return;
        }
        date = time.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // 使用新的顺序编号生成方法

        String num = generateSequentialId();
        AQIData aqiData = new AQIData(num, province, detailedAddress, city, AQILevel, date, detailedInfo, publisher);
        //判定错误信息
        if (provinceChoice.getSelectionModel().isEmpty()) {
            showAlert("错误",
                    "未选择省份信息",
                    AlertUtils.AlertType.ERROR,
                    stage);
            return;
        }
        if (cityChoice.getSelectionModel().isEmpty()) {
            showAlert("错误",
                    "未选择市级信息",
                    AlertUtils.AlertType.ERROR,
                    stage);
            return;
        }
        if (address.getText().isEmpty()) {
            showAlert("错误",
                    "未填写详细地址",
                    AlertUtils.AlertType.ERROR,
                    stage);
            return;
        }
        if (GradeChoice.getSelectionModel().isEmpty()) {
            showAlert("错误",
                    "未选择等级",
                    AlertUtils.AlertType.ERROR,
                    stage);
            return;
        }

        if (information.getText().isEmpty()) {
            showAlert("错误",
                    "未填写详细信息",
                    AlertUtils.AlertType.ERROR,
                    stage);
            return;
        }

        showSuccess();
        clearForm();

        // 调用写入文件的方法
        String filePath = "/dataBase/members/AQIDataBaseCreatedBySup.Json";
        writer(filePath, aqiData);
    }

    @FXML
    private void backButtonClicked() {
        closeFeedback();
    }

    // 添加 submitButtonClicked 方法
    @FXML
    private void submitButtonClicked() {
        // 调用原有的提交处理方法
        handleSubmit();
    }

    //清楚原有表格信息
    private void clearForm() {
        address.clear();
        information.clear();
        GradeChoice.getSelectionModel().clearSelection();
        provinceChoice.getSelectionModel().clearSelection();
        cityChoice.getSelectionModel().clearSelection();
        time.getEditor().clear();
    }

    private void showSuccess() {
        Stage stage=(Stage) submitButton.getScene().getWindow();
        showAlert("成功",
                "恭喜您成功反馈数据",
                AlertUtils.AlertType.SUCCESS,
                stage);
    }

    //展示历史记录
    private void filterAndShowData(String name) {
        // 初始化第二个表格列
        num1.setCellValueFactory(new PropertyValueFactory<>("num"));
        num1.setPrefWidth(100);
        time1.setCellValueFactory(new PropertyValueFactory<>("date"));
        time1.setPrefWidth(150);
        state.setCellValueFactory(new PropertyValueFactory<>("state"));
        state.setPrefWidth(80);
        // 读取文件中的所有 AQI 数据
        List<AQIData> allData = FileUtils.readAllAqiData();
        List<AQIData> filteredData = new ArrayList<>();

        // 筛选出与 supervisorName 名字一样的数据
        for (AQIData data : allData) {
            if (name.equals(data.getPublisher())) {
                filteredData.add(data);
            }
        }
        ObservableList<AQIData> observableData = FXCollections.observableArrayList(filteredData);
        flushButton.setOnAction(e->{
            // 读取文件中的所有 AQI 数据
            List<AQIData> allData1 = FileUtils.readAllAqiData();
            List<AQIData> filteredData1 = new ArrayList<>();
            List<AQIData> submittedData = read("/dataBase/members/AQIDataBaseCreatedByAdm.json",new AQIData());
            // 筛选出与 supervisorName 名字一样的数据 并且 如果administrator已经通过该报告，将不会再出现在表格上
            for (AQIData data : allData1) {
                if (name.equals(data.getPublisher()) ) {
                    int count=0;
                    for(AQIData data1 : submittedData){
                        if(data1.getNum().equals(data.getNum())){
                            count++;
                        }
                    }
                    if(count==0){
                        filteredData1.add(data);
                    }
                }
            }
            ObservableList<AQIData> observableData1 = FXCollections.observableArrayList(filteredData1);
            showDetail.setItems(observableData1);
        });
        // 将筛选后的数据设置到表格中
        showDetail.setItems(observableData);
        showDetail.getSelectionModel().selectedItemProperty().addListener((observableValue, aqiData, t1) -> {
            if(t1!=null){
                infoText.setText(t1.toString());
                confirmButton.setOnAction(e->{
                    String result=infoText.getText();
                    System.out.println(result);
                    String numLine=result.split("\n")[0];
                    String num=numLine.split(":")[1];
                    String provinceLine=result.split("\n")[1];
                    String province=provinceLine.split(":")[1];
                    String cityLine=result.split("\n")[2];
                    String city=cityLine.split(":")[1];
                    String addressLine=result.split("\n")[3];
                    String address=addressLine.split(":")[1];
                    String AQILevelLine=result.split("\n")[4];
                    String AQILevel=AQILevelLine.split(":")[1];
                    String dateLine=result.split("\n")[5];
                    String date=dateLine.split(":")[1];
                    String infoLine=result.split("\n")[6];
                    String info=infoLine.split(":")[1];
                    String publisherLine=result.split("\n")[7];
                    String publisher=publisherLine.split(":")[1];
                    //不能手动更改这两个，权限不够
//                    String gridderLine=result.split("\n")[8];
//                    String gridder=gridderLine.split(":")[1];
//                    String stateLine=result.split("\n")[9];
//                    String state=stateLine.split(":")[1];
                    List<AQIData> list=read("/dataBase/members/AQIDataBaseCreatedBySup.Json",new AQIData());
                    list.removeIf(aqiData1 -> aqiData1.getNum().equals(t1.getNum()));
                    list.add(new AQIData(num,province,city,address,AQILevel,date,info,publisher,null,"未检阅"));
                    System.out.println(list);
                    ObjectMapper objectMapper=new ObjectMapper();
                    try {
                        File file=new File(JsonIO.class.getResource("/dataBase/members/AQIDataBaseCreatedBySup.Json").toURI());
                        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file,list);
                        Stage stage=(Stage) confirmButton.getScene().getWindow();
                        showAlert("成功","成功修改了此报告，正在接受监督员的审核", AlertUtils.AlertType.SUCCESS,stage);
                        System.out.println("成功改变数据");
                    } catch (URISyntaxException | IOException ex) {
                        throw new RuntimeException(ex);
                    }


                });
            }
        });
    }



}
