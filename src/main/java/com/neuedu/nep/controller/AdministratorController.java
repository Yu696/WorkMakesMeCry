package com.neuedu.nep.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.neuedu.nep.entity.AQIData;
import com.neuedu.nep.entity.Gridder;
import com.neuedu.nep.entity.Member;
import com.neuedu.nep.entity.Supervisor;
import com.neuedu.nep.io.JsonIO;
import com.neuedu.nep.util.AlertUtils;
import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;



import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.*;

import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


import java.io.File;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;


import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.w3c.dom.ls.LSInput;

import static com.neuedu.nep.io.JsonIO.*;
import static com.neuedu.nep.util.FindUtil.findItAndGetIt;
import static com.neuedu.nep.util.FindUtil.getThisPerson;


public class AdministratorController {
    @FXML
    private Button flushButton;

    @FXML
    private Label wordsText;

    @FXML
    public AnchorPane admPaneRoot;

    @FXML
    private TreeView<String> departmentText;

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
    private Label timeText;

    @FXML
    private TableView<AQIData> reportDetailTableView;

    @FXML
    private Button deleteButton;

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
    public void setDialogStage(Stage stage){
        this.AQIStage=stage;
    }
    @FXML
    private  void initialize(){

        //设置实时时间
        timeText.setFont(new Font("Arial",36));
        AnimationTimer animationTimer=new AnimationTimer() {
            @Override
            public void handle(long l) {
                updateTime(timeText);
            }
        };
        animationTimer.start();


        //鼓励标语
        String[] strings={"键盘敲得响，打工人的梦想在闪光。","周一综合征？不存在的！打工人已上线。","工作使我充实，钱包却日渐消瘦。","到点收工，快乐起飞~明天继续搬砖。","打工人的日常：不是在加班，就是在去加班的路上。","生活不易，且行且珍惜，尤其珍惜发工资那天。"};
        Random random=new Random();
        wordsText.setText(strings[random.nextInt(6)]);

        //员工树
        ObjectMapper objectMapper =new ObjectMapper();
        TreeItem<String> rootTree=new TreeItem<>("公司员工结构");
        TreeItem<String> supervisorTree=new TreeItem<>("监督员部");
        TreeItem<String> gridderTree=new TreeItem<>("网格员部");
        rootTree.getChildren().addAll(supervisorTree,gridderTree);
        List<Supervisor> supervisorShowList=read("/dataBase/members/supervisor.Json",new Supervisor());
        for (Supervisor a : supervisorShowList){
            supervisorTree.getChildren().add(new TreeItem<>(a.showInfo()));

        }
        List<Gridder> gridderShowList=read("/dataBase/members/gridder.Json",new Gridder());
        for (Gridder a : gridderShowList){
            gridderTree.getChildren().add(new TreeItem<>(a.showInfo()));
        }
        departmentText.setRoot(rootTree);
        departmentText.setShowRoot(true);
        supervisorTree.setExpanded(true);
        gridderTree.setExpanded(true);
        rootTree.setExpanded(true);
        setupDragAndDrop(departmentText);
        deleteButton.setOnAction(e->{
            if (departmentText.getSelectionModel().getSelectedItem()==null) {
                Stage stage = (Stage) departmentText.getScene().getWindow();
                AlertUtils.showAlert("错误", "请选择要删除的员工", AlertUtils.AlertType.ERROR, stage);
            }
            if(departmentText.getSelectionModel().getSelectedItem()!=null){
            String type=departmentText.getSelectionModel().getSelectedItem().getParent().getValue();
            String memberLine=departmentText.getSelectionModel().getSelectedItem().getValue();
            String accountLine=memberLine.split(" ")[2];
            String account=accountLine.split(":")[1];
            if(type.equals("监督员部")){
                List<Supervisor> supervisorList=read("/dataBase/members/supervisor.Json",new Supervisor());
                List<Supervisor> newSupervisorList=supervisorList.stream().filter(supervisor -> !supervisor.getAccount().equals(account)).toList();
                System.out.println(newSupervisorList);
                try {
                    writerArray("/dataBase/members/supervisor.Json",newSupervisorList);
                    departmentText.getSelectionModel().getSelectedItem().getParent().getChildren().remove(0,departmentText.getSelectionModel().getSelectedItem().getParent().getChildren().size());
                    for(Supervisor a : newSupervisorList){
                        supervisorTree.getChildren().add(new TreeItem<>(a.showInfo()));
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if (type.equals("网格员部")){
                List<Gridder> gridderList=read("/dataBase/members/gridder.json",new Gridder());
                List<Gridder> newGridderList=gridderList.stream().filter(gridder -> !gridder.getAccount().equals(account)).toList();
                System.out.println(newGridderList);
                try {
                    writerArray("/dataBase/members/gridder.json",newGridderList);
                    departmentText.getSelectionModel().getSelectedItem().getParent().getChildren().remove(0,departmentText.getSelectionModel().getSelectedItem().getParent().getChildren().size());
                    for(Gridder a : newGridderList){
                        gridderTree.getChildren().add(new TreeItem<>(a.showInfo()));
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            }
        });
        flushButton.setOnAction(e->{
            supervisorTree.getChildren().remove(0,supervisorTree.getChildren().size());
            gridderTree.getChildren().remove(0,gridderTree.getChildren().size());
            List<Supervisor> newSupervisorShowList=read("/dataBase/members/supervisor.Json",new Supervisor());
            for (Supervisor a : newSupervisorShowList){
                supervisorTree.getChildren().add(new TreeItem<>(a.showInfo()));

            }
            List<Gridder> newGridderShowList=read("/dataBase/members/gridder.Json",new Gridder());
            for (Gridder a : newGridderShowList){
                gridderTree.getChildren().add(new TreeItem<>(a.showInfo()));
            }
        });


        //表格
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
        ObservableList<AQIData> aqiDataList = parseJSONData("/dataBase/members/AQIDataBaseCreatedBySup.Json",new AQIData());
        ObservableList<AQIData> handledList=parseJSONData("/dataBase/members/AQIDataBaseCreatedByAdm.Json",new AQIData());
        ObservableList<AQIData> showList=FXCollections.observableArrayList();
        List<String> handledNum=new ArrayList<>();
        for(AQIData a : handledList){
            handledNum.add(a.getNum());
        }
        for (AQIData a : aqiDataList){
            if (!handledNum.contains(a.getNum())){
                showList.add(a);
            }
        }
        // 将数据设置到TableView
        reportDetailTableView.setItems(showList);

        reportDetailTableView.getSelectionModel().selectedItemProperty().addListener(((observableValue, aqiData, t1) -> {
            if(t1!=null){
                detailedInfoTextArea.setText(t1.toString());
            }
        }));
        //初始化下拉框选项
        List<Gridder> list=read("/dataBase/members/gridder.json",new Gridder());
        List<String> gridderList= new ArrayList<>();
        for(Gridder a : list){
            gridderList.add(a.getName());
        }
        GridderComboBox.getItems().addAll(gridderList);
        //给查询按钮加一个事件监听器
        queryButton.setOnAction(event ->handleQuery());
        //给分配按钮加一个事件监听器
        assignButton.setOnAction(event ->handleAssign());
        turnBackButton.setOnAction(e-> {
            if(reportDetailTableView.getSelectionModel().isEmpty()){
                showAlert("错误","请先选择报告");
            }
           else {
                try {
                    handleDismiss();
                } catch (IOException | URISyntaxException ex) {
                    throw new RuntimeException(ex);
                }
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

    private void updateTime(Label timeText){
        LocalDateTime localDateTime=LocalDateTime.now();
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss");
        String formatTime=localDateTime.format(formatter);
        timeText.setText("现在是 "+ formatTime+ " ");
    }


    private void handleBack(){
        ObservableList<AQIData> aqiDataList = parseJSONData("/dataBase/members/AQIDataBaseCreatedBySup.Json",new AQIData());
        ObservableList<AQIData> handledList=parseJSONData("/dataBase/members/AQIDataBaseCreatedByAdm.Json",new AQIData());
        ObservableList<AQIData> showList=FXCollections.observableArrayList();
        List<String> handledNum=new ArrayList<>();
        for(AQIData a : handledList){
            handledNum.add(a.getNum());
        }
        for (AQIData a : aqiDataList){
            if (!handledNum.contains(a.getNum())){
                showList.add(a);
            }
        }
        // 将数据设置到TableView
        reportDetailTableView.getItems().clear();
        reportDetailTableView.setItems(showList);
    }

    private void handleDismiss() throws IOException, URISyntaxException {
        AQIData aqiData=reportDetailTableView.getSelectionModel().getSelectedItem();
        ObjectMapper objectMapper=new ObjectMapper();
        List<AQIData> list=objectMapper.readValue(JsonIO.class.getResource("/dataBase/members/AQIDataBaseCreatedBySup.Json"),objectMapper.getTypeFactory().constructCollectionType(List.class,AQIData.class));
        List<AQIData> modelList=new ArrayList<>(list);
        list.forEach(aqiData1 -> {
            if(aqiData1.getNum().equals(aqiData.getNum())) {
                aqiData1.setState("打回");
                String mem = aqiData1.getPublisher();
                List<Supervisor> supervisorList = read("/dataBase/members/supervisor.Json", new Supervisor());
                List<Supervisor> s= supervisorList.stream().map(supervisor -> {
                    if (supervisor.getName().equals(mem)) {
                        supervisor.setState("修改 " + aqiData1.getNum() + " 报告");
                    }
                    return supervisor;
                }).toList();
                try {
                    writerArray("/dataBase/members/supervisor.Json",s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(JsonIO.class.getResource("/dataBase/members/AQIDataBaseCreatedBySup.Json").toURI()),list);
        Stage stage=(Stage) backButton.getScene().getWindow();
        AlertUtils.showAlert("成功","打回报告成功，监督员正在全力更改...", AlertUtils.AlertType.SUCCESS,stage);
    }



    private void handleQuery() {
        String aqiReportId = aqiReportIdTextField.getText();
        if (aqiReportId.isEmpty()) {
            showAlert("错误", "请输入AQI报告ID");
            return;
        }
        ObservableList<AQIData> list=parseJSONData("/dataBase/members/AQIDataBaseCreatedBySup.Json",new AQIData());
        ObservableList<AQIData> showList = FXCollections.observableArrayList();
        for(AQIData a : list){
            if(a.getNum().equals(aqiReportId)){
                showList.add(a);
                reportDetailTableView.getItems().remove(0,list.size()-1);
                reportDetailTableView.setItems(showList);
            }
        }

    }


    private String getReportDetailById(String reportId) {
        List<AQIData> list=read("/dataBase/members/AQIDataBaseCreatedBySup.Json",new AQIData());
        for(AQIData data : list){
            if(data.getNum().equals(reportId)){
                return data.toString();
            }
        }
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
        List<AQIData> list=read("/dataBase/members/AQIDataBaseCreatedBySup.Json",new AQIData());
        list.forEach(aqiData -> {
            if (aqiData.getNum().equals(selectedData.getNum())){
                aqiData.setGridder(selectedGridder);
            }
        });
        try {
            writerArray("/dataBase/members/AQIDataBaseCreatedBySup.Json",list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        selectedData.setState("已检阅");
        writer("/dataBase/members/AQIDataBaseCreatedByAdm.json",selectedData);
        List<Gridder> gridderList=read("/dataBase/members/gridder.Json",new Gridder());
        gridderList.forEach(gridder -> {
            if (gridder.getName().equals(selectedGridder)){
                gridder.setState(selectedData.getNum());
            }
        });
        try {
            writerArray("/dataBase/members/gridder.Json",gridderList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        showAlert("成功", "已将报告 " + reportId + " 分配给 " + selectedGridder);
        handleBack();
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

    // 设置拖拽功能
    private void setupDragAndDrop(TreeView<String> tree) {
        // 1. 设置单元格工厂
        tree.setCellFactory(tv -> new TreeCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    // 只为员工节点设置图标
                    if (getTreeItem().getParent() != null &&
                            getTreeItem().getParent().getParent() != null) {
                        setGraphic(createEmployeeIcon());
                    }
                }
            }

            private Circle createEmployeeIcon() {
                Circle icon = new Circle(8);
                icon.setFill(Color.web("#3498db"));
                icon.setStroke(Color.web("#2980b9"));
                return icon;
            }

            // 2. 拖拽开始事件
            {
                setOnDragDetected(event -> {
                    if (getItem() == null || getTreeItem() == null ||
                            getTreeItem().getParent() == null ||
                            getTreeItem().getParent().getParent() == null) {
                        return;
                    }

                    // 创建拖拽板
                    Dragboard db = startDragAndDrop(TransferMode.MOVE);

                    // 设置拖拽内容
                    ClipboardContent content = new ClipboardContent();
                    content.putString(getItem());
                    db.setContent(content);

                    // 设置拖拽视图（可选）
                    db.setDragView(snapshot(null, null));

                    event.consume();
                });

                // 3. 拖拽完成事件
                setOnDragDone(event -> {
                    if (event.getTransferMode() == TransferMode.MOVE) {
                    }
                });
            }
        });

        tree.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) {
                // 使用鼠标位置获取目标项
                TreeItem<String> targetItem = getTreeItemAtPosition(tree, event.getX(), event.getY());

                // 允许拖拽到部门节点
                if (targetItem != null &&
                        (targetItem.getValue().equals("监督员部") ||
                                targetItem.getValue().equals("网格员部"))) {

                    // 接受移动操作
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                    return;
                }
            }
            event.consume();
        });

        // 拖拽放置事件 - 修复版
        tree.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasString()) {
                // 使用鼠标位置获取目标项
                TreeItem<String> targetItem = getTreeItemAtPosition(tree, event.getX(), event.getY());

                if (targetItem != null &&
                        (targetItem.getValue().equals("监督员部") ||
                                targetItem.getValue().equals("网格员部"))) {

                    String draggedEmployee = db.getString();
                    TreeItem<String> draggedItem = findTreeItemByValue(tree.getRoot(), draggedEmployee);

                    if (draggedItem != null) {
                        // 从原部门移除
                        draggedItem.getParent().getChildren().remove(draggedItem);

                        // 添加到新部门
                        targetItem.getChildren().add(draggedItem);

                        // 展开目标部门
                        targetItem.setExpanded(true);

                        try {
                            // 更新员工数据
                            updateEmployeeDepartment(draggedEmployee, targetItem.getValue());
                            success = true;

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        // 视觉反馈
        tree.setOnDragEntered(event -> {
            TreeItem<String> targetItem = getTreeItemAtPosition(tree, event.getX(), event.getY());
            if (targetItem != null && isValidTarget(targetItem)) {
                tree.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
            }
        });

        tree.setOnDragExited(event -> {
            tree.setStyle("");
        });
    }

    // 检查是否是有效的目标部门
    private boolean isValidTarget(TreeItem<String> item) {
        return item != null &&
                ("监督员部".equals(item.getValue()) ||
                        "网格员部".equals(item.getValue()));
    }

    // 根据坐标获取树节点
    private TreeItem<String> getTreeItemAtPosition(TreeView<String> tree, double x, double y) {
        // 确保 TreeView 已经布局
        tree.applyCss();
        tree.layout();

        // 获取第一个可见项作为参考
        if (tree.getExpandedItemCount() == 0) return null;

        // 获取实际行高（从第一个单元格）
        double rowHeight = 24.0; // 默认值
        TreeCell<?> sampleCell = (TreeCell<?>) tree.lookup(".tree-cell");
        if (sampleCell != null && sampleCell.getHeight() > 0) {
            rowHeight = sampleCell.getHeight();
        }

        // 获取滚动位置
        double scrollY = getTreeViewScrollY(tree);

        // 计算总偏移量
        double totalY = y + scrollY;

        // 计算行索引
        int rowIndex = (int) (totalY / rowHeight);

        // 确保行索引在有效范围内
        if (rowIndex >= 0 && rowIndex < tree.getExpandedItemCount()) {
            return tree.getTreeItem(rowIndex);
        }

        return null;
    }

    // 获取 TreeView 的垂直滚动位置
    private double getTreeViewScrollY(TreeView<?> treeView) {
        // 查找垂直滚动条
        for (Node node : treeView.lookupAll(".scroll-bar")) {
            if (node instanceof ScrollBar) {
                ScrollBar scrollBar = (ScrollBar) node;
                if (scrollBar.getOrientation() == Orientation.VERTICAL) {
                    return scrollBar.getValue();
                }
            }
        }
        backButton.setOnAction(e->{
            Stage stage=new Stage();
            handleAssign();
        });
        return 0.0;
    }



    private void updateEmployeeDepartment(String draggedEmployee, String newDepartment) throws IOException {
        String account=draggedEmployee.split(" ")[2];
        String name=draggedEmployee.split( " ")[0];
        String finalAccount=account.split(":")[1];
        ObjectMapper objectMapper=new ObjectMapper();
        if(newDepartment.equals("监督员部")){
            try {
                Member member = getThisPerson("/dataBase/members/gridder.json", finalAccount);
                List<Gridder> gridderList = read("/dataBase/members/gridder.json",new Gridder());
                gridderList.removeIf(gridder -> gridder.getAccount().equals(finalAccount));
                writerArray("/dataBase/members/gridder.json",gridderList);
                //这里加和其他子类不一样的属性
                Supervisor supervisor=new Supervisor(member.getName(),member.getSex(),member.getAccount(),member.getPassWord(),"free");
                writer("/dataBase/members/supervisor.Json", supervisor);
                Stage stage=(Stage) backButton.getScene().getWindow();
                AlertUtils.showAlert("成功","部员已更改", AlertUtils.AlertType.SUCCESS,stage);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

        }
        if(newDepartment.equals("网格员部")){
            try {
                Member member = getThisPerson("/dataBase/members/supervisor.json", finalAccount);
                List<Supervisor> supervisorList =read("/dataBase/members/supervisor.Json",new Supervisor());
                supervisorList.removeIf(supervisor -> supervisor.getAccount().equals(finalAccount));
                if(!supervisorList.equals("[]")){
                    writerArray("/dataBase/members/supervisor.json",supervisorList);
                }
                writerArray("/dataBase/members/supervisor.Json",supervisorList);
                //这里加和其他子类不一样的属性
                Gridder gridder=new Gridder(member.getName(),member.getSex(),member.getAccount(),member.getPassWord(),"free");
                writer("/dataBase/members/gridder.Json", gridder);
                Stage stage=(Stage) backButton.getScene().getWindow();
                AlertUtils.showAlert("成功","部员已更改", AlertUtils.AlertType.SUCCESS,stage);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private TreeItem<String> findTreeItemByValue(TreeItem<String> root, String value) {
        if (root.getValue().equals(value)) {
            return root;
        }

        for (TreeItem<String> child : root.getChildren()) {
            TreeItem<String> found = findTreeItemByValue(child, value);
            if (found != null) {
                return found;
            }
        }

        return null;
    }
}
