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

import com.example.diaapp.MainActivity;
import com.example.diaapp.R;
import com.example.diaapp.database.DiaDataBase;
import com.example.diaapp.database.Record;
import com.example.diaapp.database.User;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

public class HistoryFragment extends Fragment {
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

    private ColumnChartView chartHelloColumn;
    private ColumnChartData chartHelloData;

    private LineChartData data;
    private List<Line> lines; // главный лист линий для отрисовки графика

    private Button btnSendData;
    private TextView tvAverageDay, tvAverageWeek, tvAverageMonth;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.history_fragment, container, false);

        //указываем, что будем заполнять параметры меню
        setHasOptionsMenu(true);

        //получаем обьект бд
        db = DiaDataBase.getDatabase(getActivity().getApplicationContext());

        //инициализация переченных для работы с базой данных
        _1dayData = new ArrayList<>();
        _7daysData = new ArrayList<>();
        _30daysData = new ArrayList<>();

        //инициализация переченных
        tvAverageDay = view.findViewById(R.id.text_view_text_average_day);
        tvAverageWeek = view.findViewById(R.id.text_view_text_average_week);
        tvAverageMonth = view.findViewById(R.id.text_view_text_average_month);

        // иниц. графиков
        chartHello = view.findViewById(R.id.chart_hello);

        // начальная настройка графика
        setupDataChart();

        return view;
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
        _1dayData = setupQueryRoom(_1day, currentTime);
        _7daysData = setupQueryRoom(_7days, currentTime);
        _30daysData = setupQueryRoom(_30days, currentTime);

        // расчет среднего
        calcAverange(_1dayData, tvAverageDay);
        calcAverange(_7daysData, tvAverageWeek);
        calcAverange(_30daysData, tvAverageMonth);

        // заполнение графика данными
        //setupChartHello(_1dayData, _1day, currentTime);
        setupChartHello7days (_1dayData);
    }

    // получение данных за заданный переод времени
    private List<Record> setupQueryRoom(final long startDate, final long endDate){
        // запрос на получение данных за выбранный период
        return db.diaDao().getDiaForPeriod(startDate, endDate, MainActivity.user.getId());
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

    // метод расчета среднего
    private void calcAverange(List<Record> dataList, TextView textViewDate) {
        float averange_float = 0;

        for (Record du : dataList) {
            averange_float += du.getGlucoseMmol();
        }

        float af = averange_float/dataList.size();
        if ((Double.isNaN(af)) || (Double.isInfinite(af))) af = 0;

        DecimalFormat f = new DecimalFormat("#0.0");
        textViewDate.setText(f.format(af) + " ммоль");
    }

    /**
     * Метод для расчета среднего за каждый день
     * @param dataUserList измерения показателей
     * @return сркднее за каждый лень
     */
    private HashMap<Integer, Float> getMapAverangForDays(List<Record> dataUserList){

        HashMap<Integer, Integer> mapCount = new HashMap<Integer, Integer>();
        HashMap<Integer, Float> mapValue = new HashMap<Integer, Float>();

        Calendar calendar = Calendar.getInstance();

        for (Record dul : dataUserList) {

            calendar.setTimeInMillis(dul.getTimestamp());
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            Float aFloat = mapValue.get(day);
            Integer integer = mapCount.get(day);

            if (aFloat != null)
                mapValue.put(day, aFloat + dul.getGlucoseMmol());
            else
                mapValue.put(day, dul.getGlucoseMmol());

            if (integer !=null)
                mapCount.put(day,  integer + 1);
            else
                mapCount.put(day, 1);
        }

        for(Map.Entry<Integer, Integer> entry : mapCount.entrySet()){
            Integer x = entry.getKey();
            float y = mapValue.get(entry.getKey()) / entry.getValue();

            mapValue.put(x, y);
        }

        return mapValue;
    }


    private void setupAsisChart() {

        List<Line> newLines = new ArrayList<>();
        if(lines != null) {
            newLines.addAll(lines);
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
        chartHello.setZoomType(ZoomType.HORIZONTAL);
        chartHello.setViewportCalculationEnabled(true);

        //previewX(true);

        // задание области видимоти
        //Viewport v = chartHello.getMaximumViewport();
        //v.set(_1day, v.top, currentTime, 0);

        //chartHello.setMaximumViewport(v);
        //chartHello.setCurrentViewport(v);
    }


    // метод для заполнение и настройка графика 7 дней
    private void setupChartHello7days(List<Record> dataUser){
        int numValues = 7;

        Line line;
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();

        HashMap<Integer, Float> mapReturn = getMapAverangForDays(dataUser);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_WEEK, -numValues);

        DecimalFormat f = new DecimalFormat("##.0");
        for (int i = 0; i <= numValues; i++){

            int day = c.get(Calendar.DAY_OF_MONTH);
            Float y = mapReturn.get(day);

            if (y != null){
                PointValue point = new PointValue(i, y);
                point.setLabel(f.format(y));
                values.add(point);
            }
            axisValues.add(new AxisValue(i).setLabel(c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())));
            c.add(Calendar.DAY_OF_WEEK, 1);
        }

//---------------------------------------------------- GRAY LINE
        line = new Line(values);
        line.setColor(Color.GRAY);
        line.setHasLines(true);
        line.setPointRadius(4);
        line.setHasPoints(true);
        line.setHasLabels(true);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);
//-------------------------------------------------Yellow LINE
        List<PointValue> lineValuesYellow = new ArrayList<PointValue>();
        lineValuesYellow.add(new PointValue(0, 10));
        lineValuesYellow.add(new PointValue(numValues, 10));

        Line LineYellow  = new Line(lineValuesYellow);
        LineYellow.setColor(Color.GRAY);
        LineYellow.setHasLines(true);
        LineYellow.setStrokeWidth(1);
        LineYellow.setHasPoints(false);
        LineYellow.setHasLabels(false);
        lines.add(LineYellow);
//-------------------------------------------------
        chartDataHello = new LineChartData(lines);

        Axis axisX = new Axis(axisValues).setHasLines(true);
        axisX.setMaxLabelChars(2);
        axisX.setTextColor(Color.BLACK);

        Axis axisY = yAxis();

        chartDataHello.setAxisXBottom(axisX);
        chartDataHello.setAxisYLeft(axisY);

        chartHello.setLineChartData(chartDataHello);


        Viewport v = chartHello.getMaximumViewport();
        v.set(1, v.top, numValues, 0);

        chartHello.setMaximumViewport(v);
        chartHello.setCurrentViewport(v);
        chartHello.setZoomType(ZoomType.HORIZONTAL);
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

}
