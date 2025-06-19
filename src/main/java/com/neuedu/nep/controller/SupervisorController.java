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

import static com.neuedu.nep.io.JsonIO.*;
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
        provinceCities.put("北京市", List.of("北京市"));
        provinceCities.put("天津市", List.of("天津市"));
        provinceCities.put("河北省", List.of("石家庄市", "唐山市", "秦皇岛市", "邯郸市", "邢台市", "保定市", "张家口市", "承德市", "沧州市", "廊坊市", "衡水市"));
        provinceCities.put("山西省", List.of("太原市", "大同市", "阳泉市", "长治市", "晋城市", "朔州市", "晋中市", "运城市", "忻州市", "临汾市", "吕梁市"));
        provinceCities.put("内蒙古自治区", List.of("呼和浩特市", "包头市", "乌海市", "赤峰市", "通辽市", "鄂尔多斯市", "呼伦贝尔市", "巴彦淖尔市", "乌兰察布市", "兴安盟", "锡林郭勒盟", "阿拉善盟"));
        provinceCities.put("辽宁省", List.of("沈阳市", "大连市", "鞍山市", "抚顺市", "本溪市", "丹东市", "锦州市", "营口市", "阜新市", "辽阳市", "盘锦市", "铁岭市", "朝阳市", "葫芦岛市"));
        provinceCities.put("吉林省", List.of("长春市", "吉林市", "四平市", "辽源市", "通化市", "白山市", "松原市", "白城市", "延边朝鲜族自治州"));
        provinceCities.put("黑龙江省", List.of("哈尔滨市", "齐齐哈尔市", "鸡西市", "鹤岗市", "双鸭山市", "大庆市", "伊春市", "佳木斯市", "七台河市", "牡丹江市", "黑河市", "绥化市", "大兴安岭地区"));
        provinceCities.put("上海市", List.of("上海市"));
        provinceCities.put("江苏省", List.of("南京市", "无锡市", "徐州市", "常州市", "苏州市", "南通市", "连云港市", "淮安市", "盐城市", "扬州市", "镇江市", "泰州市", "宿迁市"));
        provinceCities.put("浙江省", List.of("杭州市", "宁波市", "温州市", "嘉兴市", "湖州市", "绍兴市", "金华市", "衢州市", "舟山市", "台州市", "丽水市"));
        provinceCities.put("安徽省", List.of("合肥市", "芜湖市", "蚌埠市", "淮南市", "马鞍山市", "淮北市", "铜陵市", "安庆市", "黄山市", "滁州市", "阜阳市", "宿州市", "六安市", "亳州市", "池州市", "宣城市"));
        provinceCities.put("福建省", List.of("福州市", "厦门市", "莆田市", "三明市", "泉州市", "漳州市", "南平市", "龙岩市", "宁德市"));
        provinceCities.put("江西省", List.of("南昌市", "景德镇市", "萍乡市", "九江市", "新余市", "鹰潭市", "赣州市", "吉安市", "宜春市", "抚州市", "上饶市"));
        provinceCities.put("山东省", List.of("济南市", "青岛市", "淄博市", "枣庄市", "东营市", "烟台市", "潍坊市", "济宁市", "泰安市", "威海市", "日照市", "临沂市", "德州市", "聊城市", "滨州市", "菏泽市"));
        provinceCities.put("河南省", List.of("郑州市", "开封市", "洛阳市", "平顶山市", "安阳市", "鹤壁市", "新乡市", "焦作市", "濮阳市", "许昌市", "漯河市", "三门峡市", "南阳市", "商丘市", "信阳市", "周口市", "驻马店市", "济源市"));
        provinceCities.put("湖北省", List.of("武汉市", "黄石市", "十堰市", "宜昌市", "襄阳市", "鄂州市", "荆门市", "孝感市", "荆州市", "黄冈市", "咸宁市", "随州市", "恩施土家族苗族自治州"));
        provinceCities.put("湖南省", List.of("长沙市", "株洲市", "湘潭市", "衡阳市", "邵阳市", "岳阳市", "常德市", "张家界市", "益阳市", "郴州市", "永州市", "怀化市", "娄底市", "湘西土家族苗族自治州"));
        provinceCities.put("广东省", List.of("广州市", "韶关市", "深圳市", "珠海市", "汕头市", "佛山市", "江门市", "湛江市", "茂名市", "肇庆市", "惠州市", "梅州市", "汕尾市", "河源市", "阳江市", "清远市", "东莞市", "中山市", "潮州市", "揭阳市", "云浮市"));
        provinceCities.put("广西壮族自治区", List.of("南宁市", "柳州市", "桂林市", "梧州市", "北海市", "防城港市", "钦州市", "贵港市", "玉林市", "百色市", "贺州市", "河池市", "来宾市", "崇左市"));
        provinceCities.put("海南省", List.of("海口市", "三亚市", "三沙市", "儋州市", "五指山市", "琼海市", "文昌市", "万宁市", "东方市", "定安县", "屯昌县", "澄迈县", "临高县", "白沙黎族自治县", "昌江黎族自治县", "乐东黎族自治县", "陵水黎族自治县", "保亭黎族苗族自治县", "琼中黎族苗族自治县"));
        provinceCities.put("重庆市", List.of("重庆市"));
        provinceCities.put("四川省", List.of("成都市", "自贡市", "攀枝花市", "泸州市", "德阳市", "绵阳市", "广元市", "遂宁市", "内江市", "乐山市", "南充市", "眉山市", "宜宾市", "广安市", "达州市", "雅安市", "巴中市", "资阳市", "阿坝藏族羌族自治州", "甘孜藏族自治州", "凉山彝族自治州"));
        provinceCities.put("贵州省", List.of("贵阳市", "六盘水市", "遵义市", "安顺市", "毕节市", "铜仁市", "黔西南布依族苗族自治州", "黔东南苗族侗族自治州", "黔南布依族苗族自治州"));
        provinceCities.put("云南省", List.of("昆明市", "曲靖市", "玉溪市", "保山市", "昭通市", "丽江市", "普洱市", "临沧市", "楚雄彝族自治州", "红河哈尼族彝族自治州", "文山壮族苗族自治州", "西双版纳傣族自治州", "大理白族自治州", "德宏傣族景颇族自治州", "怒江傈僳族自治州", "迪庆藏族自治州"));
        provinceCities.put("西藏自治区", List.of("拉萨市", "日喀则市", "昌都市", "林芝市", "山南市", "那曲市", "阿里地区"));
        provinceCities.put("陕西省", List.of("西安市", "铜川市", "宝鸡市", "咸阳市", "渭南市", "延安市", "汉中市", "榆林市", "安康市", "商洛市"));
        provinceCities.put("甘肃省", List.of("兰州市", "嘉峪关市", "金昌市", "白银市", "天水市", "武威市", "张掖市", "平凉市", "酒泉市", "庆阳市", "定西市", "陇南市", "临夏回族自治州", "甘南藏族自治州"));
        provinceCities.put("青海省", List.of("西宁市", "海东市", "海北藏族自治州", "黄南藏族自治州", "海南藏族自治州", "果洛藏族自治州", "玉树藏族自治州", "海西蒙古族藏族自治州"));
        provinceCities.put("宁夏回族自治区", List.of("银川市", "石嘴山市", "吴忠市", "固原市", "中卫市"));
        provinceCities.put("新疆维吾尔自治区", List.of("乌鲁木齐市", "克拉玛依市", "吐鲁番市", "哈密市", "昌吉回族自治州", "博尔塔拉蒙古自治州", "巴音郭楞蒙古自治州", "阿克苏地区", "克孜勒苏柯尔克孜自治州", "喀什地区", "和田地区", "伊犁哈萨克自治州", "塔城地区", "阿勒泰地区", "石河子市", "阿拉尔市", "图木舒克市", "五家渠市", "北屯市", "铁门关市", "双河市", "可克达拉市", "昆玉市"));
        provinceCities.put("台湾省", List.of("台北市", "新北市", "桃园市", "台中市", "台南市", "高雄市", "基隆市", "新竹市", "嘉义市", "新竹县", "苗栗县", "彰化县", "南投县", "云林县", "嘉义县", "屏东县", "宜兰县", "花莲县", "台东县", "澎湖县"));
        provinceCities.put("香港特别行政区", List.of("香港岛", "九龙", "新界"));
        provinceCities.put("澳门特别行政区", List.of("花地玛堂区", "圣安多尼堂区", "大堂区", "望德堂区", "风顺堂区", "嘉模堂区", "圣方济各堂区"));
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
            String nameA=nameLine.split(":")[1];
            String name=nameA.split(",")[1];
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
                infoText.setStyle("-fx-font-size: 16pt;");
                infoText.setText(t1.toString());
                confirmButton.setOnAction(e->{
                    String result=infoText.getText();
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
                    ObjectMapper objectMapper=new ObjectMapper();
                    try {
                        File file=new File(JsonIO.class.getResource("/dataBase/members/AQIDataBaseCreatedBySup.Json").toURI());
                        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file,list);
                        Stage stage=(Stage) confirmButton.getScene().getWindow();
                        List<Supervisor> supervisorList = read("/dataBase/members/supervisor.Json", new Supervisor());
                        List<Supervisor> s= supervisorList.stream().map(supervisor -> {
                            if (supervisor.getName().equals(publisher)) {
                                supervisor.setState("free");
                            }
                            return supervisor;
                        }).toList();
                        try {
                            writerArray("/dataBase/members/supervisor.Json",s);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        showAlert("成功","成功修改了此报告，正在接受监督员的审核", AlertUtils.AlertType.SUCCESS,stage);
                    } catch (URISyntaxException | IOException ex) {
                        throw new RuntimeException(ex);
                    }


                });
            }
        });
    }



}
