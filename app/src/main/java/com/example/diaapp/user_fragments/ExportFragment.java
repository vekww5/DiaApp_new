package com.example.diaapp.user_fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.diaapp.R;
import com.example.diaapp.database.DiaDataBase;
import com.example.diaapp.database.RecordDIA;
import com.example.diaapp.datapickers.DatePickerFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

//создание фрагмента экспорта
public class ExportFragment  extends Fragment {

    private RadioGroup rGroupPeriod, rGroupFormat;
    private RadioButton rbSelectDate;
    private Button btnExport;
    private TextView tvEndDate, tvStartDate;
    private LinearLayout llStartDate, llEndDate, llSelectDate;

    private Calendar calendarStart;
    private Calendar calendarEnd;
    boolean boolDateStartOrEnd;

    long _currentTime;
    long _7days;
    long _1month;

    long _3months;

    SimpleDateFormat dateFormat;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.export_fragment, container, false);

        //создание фомата отображении даты
        dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        //получение текущей даты
        calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.MONTH, -1);
        calendarEnd = Calendar.getInstance();

        //иниц. переменных tvStartDate и tvEndDate
        tvStartDate = view.findViewById(R.id.textViewExportStartDate);
        tvEndDate = view.findViewById(R.id.textViewExportEndDate);

        tvStartDate.setText(dateFormat.format(calendarStart.getTime()));
        tvEndDate.setText(dateFormat.format(calendarEnd.getTime()));

        Calendar calendar = Calendar.getInstance();

        _currentTime = calendar.getTimeInMillis();
        //данные за 7
        _7days = _currentTime - (7 * 24 * 60 * 60 * 1000L);
        //данные за месяц
        calendar.add(Calendar.MONTH, -1);
        _1month = calendar.getTimeInMillis();
        //данные за 3 месяца
        calendar.add(Calendar.MONTH, -2);
        _3months = calendar.getTimeInMillis();

        //иниц. переменных и установка обработчиков
        btnExport = view.findViewById(R.id.btn_export);
        rGroupPeriod = view.findViewById(R.id.radioGroupPeriod);
        rGroupFormat = view.findViewById(R.id.radioGroupFormatExport);

        llSelectDate = view.findViewById(R.id.ll_select_date);
        llSelectDate.setVisibility(View.GONE);

        rbSelectDate = view.findViewById(R.id.radioButtonSelectDate);

        rGroupPeriod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rbSelectDate.getId() == checkedId){
                    llSelectDate.setVisibility(View.VISIBLE);
                }else {
                    llSelectDate.setVisibility(View.GONE);
                }
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectPeriod = rGroupPeriod.getCheckedRadioButtonId();
                int selectFormat = rGroupFormat.getCheckedRadioButtonId();

                String del;
                if (selectFormat == R.id.radioButtonExportCsv) {
                    del = ",";
                } else {
                    del = "\t" ;
                }
                switch (selectPeriod) {
                    case R.id.radioButtonWeak:
                        export(view, _7days, _currentTime, del);
                        break;
                    case R.id.radioButtonMonth:
                        export(view, _1month, _currentTime, del);
                        break;
                    case R.id.radioButtonThreeMonths:
                        export(view, _3months, _currentTime, del);
                        break;
                    case R.id.radioButtonSelectDate:
                        export(view, calendarStart.getTimeInMillis(), calendarEnd.getTimeInMillis(), del);
                        break;
                }
            }
        });

        llStartDate = view.findViewById(R.id.ll_start_date);
        llEndDate = view.findViewById(R.id.ll_end_date);

        llStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(calendarStart);
                boolDateStartOrEnd = true;
            }
        });
        llEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(calendarEnd);
                boolDateStartOrEnd = false;
            }
        });

        return view;
    }

    // метод эспорта данных в файл
    public void export (final View view, final long startDate, final long endDate, final String del){

        // запрос на получение данных за выбранный период
        DiaDataBase db = DiaDataBase.getDatabase(getActivity().getApplicationContext());
        List<RecordDIA> listDiaData = db.diaDao().getDiaForPeriod(startDate, endDate);

        // формирование заголовка данных
        StringBuilder exportData = new StringBuilder();
        exportData.append('"' + "Продленный инсулин" + '"').append(del).append('"')
                .append("Короткий инсулин").append('"').append(del).append('"')
                .append("Сахар").append('"').append(del).append('"')
                .append("Хлебные единицы").append('"').append(del).append('"')
                .append("Дата").append('"').append(del).append('"').append("Время")
                .append('"');

        // Создание объекта SimpleDateFormat для формата даты и времени
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        // заполнение данными
        for (RecordDIA user : listDiaData) {
            exportData.append("\n").append('"'+user.injectLong+'"')
                    .append(del).append('"'+user.injectShort+'"').append(del)
                    .append('"'+user.glucose+'"').append(del).append('"'+user.xe+'"')
                    .append(del).append('"'+dateFormat.format(user.timestamp)+'"').append(del)
                    .append('"'+timeFormat.format(user.timestamp)+'"');
        }

        sendDataFromSelectPeriod(view, exportData, startDate, endDate);
    }

    // метод отправки файла
    private void sendDataFromSelectPeriod(View view, StringBuilder eDBuilder, long startDate, long endDate){
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(startDate);
        String startDateString = dateFormat.format(calendar.getTime());

        calendar.setTimeInMillis(endDate);
        String endDateString = dateFormat.format(calendar.getTime());

        String fileNameExport = "Export " + startDateString + "-" + endDateString + ".csv";

        try {
            FileOutputStream out = view.getContext().openFileOutput(fileNameExport, Context.MODE_PRIVATE);
            out.write((eDBuilder.toString()).getBytes());
            out.close();

            Context context = view.getContext();
            File filelocation = new File(context.getFilesDir(), fileNameExport);
            Uri path = FileProvider.getUriForFile(context, "com.example.diaapp.fileprovider", filelocation);

            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/scv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Экспорт данных DiaApp: " + startDateString + "-" + endDateString);
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent,"Send email"));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // создание диалога для выюора даты
    private void showDatePicker(Calendar calender) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Задаем текущую дату в диалоговом окне
         */
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Задаем обратный вызов для получение выбранной даты
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // определяем поле для вывода и сохранения даты, начала или конца
            if (boolDateStartOrEnd){
                calendarStart.set(Calendar.YEAR, year);
                calendarStart.set(Calendar.MONTH, monthOfYear);
                calendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tvStartDate.setText(dateFormat.format(calendarStart.getTime()));
            }else {
                calendarEnd.set(Calendar.YEAR, year);
                calendarEnd.set(Calendar.MONTH, monthOfYear);
                calendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tvEndDate.setText(dateFormat.format(calendarEnd.getTime()));
            }
        }
    };
}
