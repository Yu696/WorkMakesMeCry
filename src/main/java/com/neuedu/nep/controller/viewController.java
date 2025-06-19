package com.neuedu.nep.controller;

import com.neuedu.nep.entity.AQIData;
import com.neuedu.nep.io.JsonIO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class viewController implements Initializable {

    @FXML private LineChart<String, Number> LineChartUp;
    @FXML private PieChart PieChart;
    @FXML private BubbleChart<Number, Number> BubbleChart;
    @FXML private TextArea detailTextArea;
    @FXML private LineChart<String, Number> LineChartDown;
    @FXML private StackedAreaChart<String, Number> stackedAreaChart;

    // JSON文件路径
    private static final String GRI_DATA_PATH = "/dataBase/members/AQIDataBaseCreatedByGri.Json";

    // 合并后的AQI数据列表
    private final ObservableList<AQIData> aqiDataList = FXCollections.observableArrayList();

    // 存储等级到饼图数据的映射
    private final Map<String, PieChart.Data> levelToPieDataMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 从JSON文件加载数据
        loadDataFromJson();

        // 初始化文本区域（设置为空）
        initTextArea();

        // 初始化图表
        initPieChart();
        initLineCharts();
        initBubbleChart();
        initializeStackedChart();
    }

    // 从JSON文件加载数据
    private void loadDataFromJson() {
        try {
            // 读取网格员处理后的数据
            List<AQIData> griData = JsonIO.read(GRI_DATA_PATH, new AQIData());

            // 合并数据
            aqiDataList.addAll(griData);

        } catch (Exception e) {
            e.printStackTrace();
            // 如果读取失败，使用模拟数据作为后备
            aqiDataList.addAll(
                    new AQIData("001", "广东省", "广州市", "天河区XX路XX号", "良", "2025-06-10",
                            "空气质量良好，无明显污染", "张三", null, "未检阅"),
                    new AQIData("002", "浙江省", "杭州市", "西湖区XX路XX号", "优", "2025-06-12",
                            "空气质量优秀，适合户外活动", "李四", "王五", "已处理")
            );
        }
    }

    // 初始化饼图（AQI等级分布）
    private void initPieChart() {
        Map<String, Integer> levelCount = new HashMap<>();

        for (AQIData data : aqiDataList) {
            String level = data.getAQILevel();
            levelCount.put(level, levelCount.getOrDefault(level, 0) + 1);
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        levelCount.forEach((level, count) -> {
            PieChart.Data data = new PieChart.Data(level + " (" + count + ")", count);
            pieData.add(data);
            levelToPieDataMap.put(level, data);
        });

        PieChart.setData(pieData);
        PieChart.setTitle("AQI等级分布");

        // 添加悬停提示
        pieData.forEach(data -> {
            Tooltip tooltip = new Tooltip(String.format("%s: %.0f条",
                    data.getName().replaceAll("\\s*\\(\\d+\\)", ""),
                    data.getPieValue()));
            tooltip.setShowDelay(Duration.ZERO);
            Tooltip.install(data.getNode(), tooltip);

            // 添加点击事件
            data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                String level = data.getName().split("\\s+")[0]; // 提取等级名称
                List<AQIData> filteredList=showAQIDataByLevel(level);
                if (filteredList != null) {
                    updateStackedAreaChart(filteredList);
                    updateBubbleChart(filteredList);
                }
            });
        });
    }

    // 初始化折线图（时间趋势）
    private void initLineCharts() {
        // 上线折线图（按日期统计）
        XYChart.Series<String, Number> seriesUp = new XYChart.Series<>();
        seriesUp.setName("AQI报告数量");

        Map<String, Integer> dailyCount = new TreeMap<>();
        for (AQIData data : aqiDataList) {
            String date = data.getDate();
            dailyCount.put(date, dailyCount.getOrDefault(date, 0) + 1);
        }

        dailyCount.forEach((date, count) ->
                seriesUp.getData().add(new XYChart.Data<>(date, count))
        );

        LineChartUp.getData().add(seriesUp);
        LineChartUp.setTitle("每日报告趋势");

        // 下线折线图（按省份统计）
        XYChart.Series<String, Number> seriesDown = new XYChart.Series<>();
        seriesDown.setName("地区报告量");

        Map<String, Integer> provinceCount = new HashMap<>();
        for (AQIData data : aqiDataList) {
            String province = data.getProvince();
            provinceCount.put(province, provinceCount.getOrDefault(province, 0) + 1);
        }

        provinceCount.forEach((province, count) ->
                seriesDown.getData().add(new XYChart.Data<>(province, count))
        );

        LineChartDown.getData().add(seriesDown);
        LineChartDown.setTitle("地区报告统计");
    }

    // 初始化气泡图（污染物关系）
    private void initBubbleChart() {
        BubbleChart.setTitle("污染物浓度关系");
        ((NumberAxis)BubbleChart.getXAxis()).setLabel("SO₂ (μg/m³)");
        ((NumberAxis)BubbleChart.getYAxis()).setLabel("CO (μg/m³)");
    }

    private void updateBubbleChart(List<AQIData> aqiDataList){
        BubbleChart.getData().clear();
        // 使用实际污染物数据
        for (AQIData data : aqiDataList) {
            // 跳过没有实测数据的记录
            if (data.getSo2() == null || data.getCo() == null || data.getPm25() == null)
                continue;

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(data.getCity());
            // 气泡大小基于PM2.5浓度（除以10进行缩放）
            series.getData().add(new XYChart.Data<>(data.getSo2(), data.getCo(), data.getPm25()/10));
            BubbleChart.getData().add(series);
        }

        // 如果没有实测数据，添加示例数据
        if (BubbleChart.getData().isEmpty()) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("示例");
            series.getData().add(new XYChart.Data<>(5.0, 1.0, 15.0));
            BubbleChart.getData().add(series);
        }

        BubbleChart.setTitle("污染物浓度关系");
        ((NumberAxis)BubbleChart.getXAxis()).setLabel("SO₂ (μg/m³)");
        ((NumberAxis)BubbleChart.getYAxis()).setLabel("CO (μg/m³)");
    }

    // 在初始化方法中创建图表（例如initialize()方法）
    private void initializeStackedChart() {
        stackedAreaChart.getXAxis().setLabel("城市");
        stackedAreaChart.getYAxis().setLabel("浓度 (μg/m³)");
        stackedAreaChart.getXAxis().setTickLabelRotation(-30);
        stackedAreaChart.setTitle("地区污染物浓度");
    }

    // 更新图表数据的方法
    private void updateStackedAreaChart(List<AQIData> filteredList) {
        // 按城市分组数据（保持有序）
        Map<String, List<AQIData>> dataByCity = new LinkedHashMap<>();
        for (AQIData data : filteredList) {
            String city = data.getCity();
            dataByCity.computeIfAbsent(city, k -> new ArrayList<>()).add(data);
        }
        // 创建系列
        XYChart.Series<String, Number> so2Series = new XYChart.Series<>();
        so2Series.setName("SO₂");
        XYChart.Series<String, Number> coSeries = new XYChart.Series<>();
        coSeries.setName("CO");
        XYChart.Series<String, Number> pm25Series = new XYChart.Series<>();
        pm25Series.setName("PM2.5");

        // 为每个城市计算平均浓度
        for (Map.Entry<String, List<AQIData>> entry : dataByCity.entrySet()) {
            String city = entry.getKey();
            List<AQIData> cityData = entry.getValue();

            double so2Sum = 0, coSum = 0, pm25Sum = 0;
            int count = 0;

            for (AQIData data : cityData) {
                if (data.getSo2() != null) so2Sum += data.getSo2();
                if (data.getCo() != null) coSum += data.getCo();
                if (data.getPm25() != null) pm25Sum += data.getPm25();
                count++;
            }

            if (count > 0) {
                double so2Avg = so2Sum / count;
                double coAvg = coSum / count;
                double pm25Avg = pm25Sum / count;
                System.out.println(so2Avg);
                so2Series.getData().add(new XYChart.Data<>(city, so2Avg));
                coSeries.getData().add(new XYChart.Data<>(city, coAvg));
                pm25Series.getData().add(new XYChart.Data<>(city, pm25Avg));
            }
        }
        // 清除旧数据并添加新系列
        stackedAreaChart.getData().clear();
        stackedAreaChart.getData().addAll(so2Series, coSeries, pm25Series);
    }

    // 初始化文本区域（设置为空）
    private void initTextArea() {
        detailTextArea.setText("");
        detailTextArea.setFont(Font.font("Monospaced", 12));
    }

    // 显示指定等级的所有AQI数据
    private List<AQIData> showAQIDataByLevel(String level) {
        // 过滤出指定等级的数据
        List<AQIData> filteredData = aqiDataList.stream()
                .filter(data -> level.equals(data.getAQILevel()))
                .collect(Collectors.toList());

        if (filteredData.isEmpty()) {
            detailTextArea.setText("没有找到 " + level + " 等级的AQI数据");
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(level).append(" 等级AQI数据 (").append(filteredData.size()).append("条) ===\n\n");
        sb.append(String.format("%-6s %-8s %-8s %-12s %-15s %s\n",
                "编号", "省份", "城市", "状态", "日期", "发布人"));

        for (AQIData data : filteredData) {
            sb.append(String.format("%-6s %-8s %-8s %-12s %-15s %s\n",
                    data.getNum(),
                    data.getProvince(),
                    data.getCity(),
                    data.getState(),
                    data.getDate(),
                    data.getPublisher()
            ));
        }

        // 添加第一条记录的详细信息
        sb.append("\n=== 详细信息（第一条记录） ===\n");
        sb.append(filteredData.get(0).toString());

        detailTextArea.setText(sb.toString());
        return filteredData;
    }
}