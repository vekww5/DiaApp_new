package com.example.diaapp.user_fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaapp.AlertPlayer;
import com.example.diaapp.MainActivity;
import com.example.diaapp.R;
import com.example.diaapp.database.DiaDataBase;
import com.example.diaapp.database.Record;
import com.example.diaapp.network.TCPConnection;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;


public class StatisticsFragment extends Fragment {

    //TODO: График

    private List<Record> _1dayData;
    private List<Record> _7daysData;
    private List<Record> _30daysData;

    private long currentTime;
    private long _1day;
    private long _7days;
    private long _30days;

    DiaDataBase db;

    private LineChartView chartHello;
    private LineChartData chartDataHello;

    private LineChartData data;

    private LineChartData previewData;
    private PreviewLineChartView previewChart;

    private List<Line> lines; // главный лист линий для отрисовки графика
    private List<Line> predictLines;

    private Button btnSendData;
    private TextView tvGlucoseLastRecord, tvTimeLastRecord, tvGlucoseLevelRate;

    private ImageView ivArrow;


    private boolean updatingPreviewViewport = false;
    private boolean updatingChartViewport = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.statistics_fragment, container, false);

        //указываем, что будем заполнять параметры меню
        setHasOptionsMenu(true);

        //получаем обьект бд
        db = DiaDataBase.getDatabase(getActivity().getApplicationContext());

        //инициализация переченных для работы с базой данных
        _1dayData = new ArrayList<>();
        _7daysData = new ArrayList<>();
        _30daysData = new ArrayList<>();

        // иниц. графиков
        chartHello = view.findViewById(R.id.chart_hello);
        previewChart = (PreviewLineChartView) view.findViewById(R.id.chart_preview);

        // Generate data for previewed chart and copy of that data for preview chart.
        //generateDefaultData();
        //chartHello.setLineChartData(data);
        // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
        // zoom/scroll is unnecessary.
        //chartHello.setZoomEnabled(false);
        //chartHello.setScrollEnabled(false);
        //previewChart.setLineChartData(previewData);
        //previewChart.setViewportChangeListener(new ViewportListener());
        //previewX(false);

        // начальная настройка графика
        setupDataChart();


        // начальная настройка панели уровня сахара
        setGlucosePanel(view);


        AlertPlayer  player = new AlertPlayer();

        // насройка кнопки для отправки
        btnSendData = view.findViewById(R.id.btn_send_data);
        btnSendData.setOnClickListener(view1 -> {

            //player.startAlert(this.getContext(), 0);

            //получение текущей даты
            currentTime = Calendar.getInstance().getTimeInMillis();

            // Получить время 30 минут назад в миллисекундах
            long start = currentTime - 18000000; // -0

            // запрос на получение данных за выбранный период
            List<Record> listDia = db.diaDao().getDiaForPeriod(_1day, currentTime, MainActivity.user.getId());

            // формируем данные для отправки в json
            Gson gson = new Gson();
            String jsonArray = gson.toJson(listDia);

            //отправка в потоке
            Thread thread = new Thread(() -> {

                TCPConnection tcpConnection = null;
                try {
                    tcpConnection = new TCPConnection("192.168.1.35", 5000);
                    tcpConnection.sendString(jsonArray);
                    String pred = tcpConnection.getMassege();
                    if (pred != null) {
                        Type type = new TypeToken<List<Record>>() {}.getType();
                        List<Record> dia = gson.fromJson(pred, type);
                        addDataChart(dia);
                    }
                    tcpConnection.disconnect();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
            thread.start();

        });

        return view;
    }

    private void setGlucosePanel(View view) {
        final DecimalFormat df = new DecimalFormat("+0.0;-0.0");

        tvGlucoseLastRecord = view.findViewById(R.id.glucose_field);
        tvTimeLastRecord = view.findViewById(R.id.time_field);
        tvGlucoseLevelRate = view.findViewById(R.id.glucose_level_rate);
        ivArrow = view.findViewById(R.id.arrow_image);

        if (_1dayData.size() > 0) {

            int indexLastRecord = _1dayData.size() - 1;
            Record DialastRec = _1dayData.get(indexLastRecord);

            // Преобразование timestamp в объект Date
            Date date = new Date(DialastRec.getTimestamp());

            // Создание объекта SimpleDateFormat для формата даты и времени
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            // Получение даты и времени отдельно
            String dateString = dateFormat.format(date);
            String timeString = timeFormat.format(date);

            tvTimeLastRecord.setText(timeString);
            tvGlucoseLastRecord.setText(DialastRec.getGlucoseMmolString());

            if (_1dayData.size() > 1) {

                tvGlucoseLevelRate.setText(String.valueOf( df.format(DialastRec.getGlucoseMmol()
                        - _1dayData.get(indexLastRecord - 1).getGlucoseMmol()) + " mmol/l"));

                // изменение положения стрелки
                setArrowSide(DialastRec.getGlucoseMmol() - _1dayData.get(indexLastRecord - 1).getGlucoseMmol());
            }

        }


    }

    void setArrowSide (float number){

        float n = (float) (Math.round(number * 100.0)/100.0);

        // положение  и знак
        int i = 1;
        if (number > 0) i = -1;

        // поворот стрелки
        if (n < 0.2) {
            ivArrow.setRotation(0);
        } else if (n < 0.5){
            ivArrow.setRotation(45*i);
        }else {
            ivArrow.setRotation(90*i);
        }
    }

    // добавленое новых данных (предсказаний) в график
    private void addDataChart(List<Record> listDiaData){
        Line line;
        List<PointValue> values = new ArrayList<PointValue>();
        predictLines = new ArrayList<>();

        // заполнение данными
        for (Record dia : listDiaData) {
            float y = dia.getGlucoseMmol();
            long x = dia.getTimestamp();

            PointValue point = new PointValue(x, y);
            point.setLabel(dia.getGlucoseMmolString());

            values.add(point);
        }

        //---------------------------// рисует Points
        line = new Line(values);
        line.setColor(Color.BLUE);
        line.setHasLines(false); // скрыть/показать линии между точками
        line.setPointRadius(4);
        line.setHasPoints(true);
        line.setHasLabels(false);
        predictLines.add(line);

        setupAsisChart();
    }

    // начальная настройка графика
    private void setupDataChart(){

        //получение текущей даты
        currentTime = Calendar.getInstance().getTimeInMillis();

        //данные за 1, 7, 30 дней
        _1day = currentTime - (24 * 60 * 60 * 1000L);
        _7days = currentTime - (7 * 24 * 60 * 60 * 1000L);
        _30days = currentTime - (30 * 24 * 60 * 60 * 1000L);

        //получение данных за заданный переод времени и настройка графика
        setupQueryRoom(_1day, currentTime);

    }

    // получение данных за заданный переод времени
    private void setupQueryRoom(final long startDate, final long endDate){

        // запрос на получение данных за выбранный период
        _1dayData = db.diaDao().getDiaForPeriod(startDate, endDate, MainActivity.user.getId());

        // заполнение графика данными
        setupChartHello(_1dayData, startDate, endDate);
    }

    //метод для заполнение и настройка графика
    private void setupChartHello(List<Record> listDiaData, long startDate, long endDate){
        Line line;
        List<PointValue> values = new ArrayList<PointValue>();

        // заполнение данными
        for (Record dia : listDiaData) {
            float y = dia.getGlucoseMmol();
            long x = dia.getTimestamp();

            PointValue point = new PointValue(x, y);
            point.setLabel(dia.getGlucoseMmolString());

            values.add(point);
        }

        //---------------------------// рисует Points
        lines = new ArrayList<>();
        line = new Line(values);
        line.setColor(Color. rgb(255, 165, 0));
        line.setHasLines(false); // скрыть/показать линии между точками
        line.setPointRadius(4);
        line.setHasPoints(true);
        line.setHasLabels(false);

        lines.add(line);

        //--------------------------// рисует RED LINE
        List<PointValue> lineValuesRed = new ArrayList<>();
        lineValuesRed.add(new PointValue(startDate, 4));
        lineValuesRed.add(new PointValue(endDate, 4));

        Line lineRed  = new Line(lineValuesRed);
        lineRed.setColor(Color.RED);
        lineRed.setHasLines(true);
        lineRed.setStrokeWidth(2);
        lineRed.setHasPoints(false);
        lineRed.setHasLabels(false);
        lineRed.setFilled(true);

        lines.add(lineRed);

        //--------------------------// рисует START LINE
        List<PointValue> lineValuesRed1 = new ArrayList<>();
        lineValuesRed1.add(new PointValue(startDate, 0));
        lineValuesRed1.add(new PointValue(startDate, 0));

        Line lineRed1  = new Line(lineValuesRed1);
        lineRed1.setColor(Color.RED);
        lineRed1.setHasLines(true);
        lineRed1.setStrokeWidth(2);
        lineRed1.setHasPoints(false);
        lineRed1.setHasLabels(false);
        lineRed1.setFilled(true);

        lines.add(lineRed1);

        lines.add(minShowLine());

        //---------------------------// рисует Yellow LINE
        List<PointValue> lineValuesYellow = new ArrayList<>();
        lineValuesYellow.add(new PointValue(startDate, 11));
        lineValuesYellow.add(new PointValue(endDate, 11));

        Line LineYellow  = new Line(lineValuesYellow);
        LineYellow.setColor(Color.YELLOW);
        LineYellow.setHasLines(true);
        LineYellow.setStrokeWidth(2);
        LineYellow.setHasPoints(false);
        LineYellow.setHasLabels(false);
        lines.add(LineYellow);
//-------------------------------------------------
        setupAsisChart();
    }

    private void setupAsisChart() {

        List<Line> newLines = new ArrayList<>();
        if(lines != null) {
            newLines.addAll(lines);
        }
        if (predictLines != null){
            newLines.addAll(predictLines);
        }

        chartDataHello = new LineChartData(newLines);

        // заполняем оси Х значения
        Axis axisX = xAxis();
        axisX.setMaxLabelChars(2);

        // заполняем оси У значения
        Axis axisY = yAxis();

        // задание осей Х и У
        chartDataHello.setAxisYLeft(axisY);
        chartDataHello.setAxisXBottom(axisX);

        // задание режима отображения
        chartHello.setLineChartData(chartDataHello);
        previewChart.setLineChartData(chartHello.getLineChartData());

        chartHello.setZoomType(ZoomType.HORIZONTAL);
        previewChart.setZoomType(ZoomType.HORIZONTAL);

        chartHello.setViewportCalculationEnabled(true);
        previewChart.setViewportCalculationEnabled(true);

        previewChart.setViewportChangeListener(new ViewportListener());
        chartHello.setViewportChangeListener(new ChartViewPortListener());

        //previewX(true);


        // задание области видимоти
        //Viewport v = chartHello.getMaximumViewport();
        //v.set(_1day, v.top, currentTime, 0);

        //chartHello.setMaximumViewport(v);
        //chartHello.setCurrentViewport(v);
    }

    private void generateDefaultData() {
        int numValues = 50;

        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            values.add(new PointValue(i, (float) Math.random() * 100f));
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN);
        line.setHasPoints(false);// too many values so don't draw points.

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        data = new LineChartData(lines);
        data.setAxisXBottom(new Axis());
        data.setAxisYLeft(new Axis().setHasLines(true));

        // prepare preview data, is better to use separate deep copy for preview chart.
        // Set color to grey to make preview area more visible.
        previewData = new LineChartData(data);
        previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);

    }


    private class ViewportListener implements ViewportChangeListener {
        @Override
        public void onViewportChanged(Viewport newViewport) {
            if (!updatingChartViewport) {
                updatingPreviewViewport = true;
                chartHello.setZoomType(ZoomType.HORIZONTAL);
                chartHello.setCurrentViewport(newViewport);
                updatingPreviewViewport = false;
            }
        }
    }

    private class ChartViewPortListener implements ViewportChangeListener {
        @Override
        public void onViewportChanged(Viewport newViewport) {
            if (!updatingPreviewViewport) {
                updatingChartViewport = true;
                previewChart.setZoomType(ZoomType.HORIZONTAL);
                previewChart.setCurrentViewport(newViewport);
                updatingChartViewport = false;
            }
        }
    }


    //
    private void previewX(boolean animate) {
        Viewport tempViewport = new Viewport(chartHello.getMaximumViewport());
        float dx = tempViewport.width() / 4;
        tempViewport.inset(dx, 0);
        if (animate) {
            previewChart.setCurrentViewportWithAnimation(tempViewport);
        } else {
            previewChart.setCurrentViewport(tempViewport);
        }
        previewChart.setZoomType(ZoomType.HORIZONTAL);
    }

    private void previewXY() {
        // Better to not modify viewport of any chart directly so create a copy.
        Viewport tempViewport = new Viewport(chartHello.getMaximumViewport());
        // Make temp viewport smaller.
        float dx = tempViewport.width() / 4;
        float dy = tempViewport.height() / 4;
        tempViewport.inset(dx, dy);
        previewChart.setCurrentViewportWithAnimation(tempViewport);
    }

    // настройка оси х
    private Axis xAxis() {
        int periodInt = 0;

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        DateFormat timeFormat = new SimpleDateFormat("HH", Locale.getDefault());

        Calendar calendarEndTime = Calendar.getInstance();
        Calendar calendarStartTime = Calendar.getInstance();
        calendarStartTime.setTimeInMillis(_1day);
        calendarStartTime.set(Calendar.MINUTE, 0);
        calendarStartTime.set(Calendar.SECOND, 0);
        calendarStartTime.set(Calendar.MILLISECOND, 0);

        periodInt = Calendar.HOUR;
        calendarStartTime.add(periodInt, 1);

        while (calendarStartTime.getTimeInMillis() < calendarEndTime.getTimeInMillis()) {
            axisValues.add(new AxisValue((calendarStartTime.getTimeInMillis()),
                    (timeFormat.format(calendarStartTime.getTimeInMillis())).toCharArray()));
            calendarStartTime.add(periodInt, 1);
        }
        Axis axis = new Axis();
        axis.setAutoGenerated(false);
        axis.setValues(axisValues);
        axis.setHasLines(true);
        axis.setTextColor(Color.BLACK);
        //axis.setMaxLabelChars(1);
        return axis;
    }

    // метод для заполнение оси У данными
    public Axis yAxis() {
        Axis yAxis = new Axis();
        yAxis.setAutoGenerated(false);
        List<AxisValue> axisValues = new ArrayList<AxisValue>();

        for(int j = 1; j <= 10; j += 1) {
            axisValues.add(new AxisValue(j*2));
        }
        yAxis.setValues(axisValues);
        yAxis.setHasLines(true);
        //yAxis.setMaxLabelChars(5);
        //yAxis.setTextSize(10);
        yAxis.setTextColor(Color.BLACK);
        yAxis.setInside(true); // сделать внутри графика числа
        return yAxis;
    }

    // создание previewXAxis
    public Axis previewXAxis() {
        Axis previewXaxis = xAxis();
        previewXaxis.setTextSize(4);
        previewXaxis.setHasLines(true);
        return previewXaxis;
    }

    public Line minShowLine() {
        List<PointValue> minShowValues = new ArrayList<PointValue>();
        minShowValues.add( new PointValue(_1day, (float) 4.0));
        minShowValues.add(new PointValue(currentTime, (float) 4.0));
        Line minShowLine = new Line(minShowValues);
        minShowLine.setHasPoints(false);
        minShowLine.setHasLines(false);
        return minShowLine;
    }
}

