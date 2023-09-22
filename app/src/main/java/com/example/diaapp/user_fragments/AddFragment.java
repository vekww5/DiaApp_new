package com.example.diaapp.user_fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaapp.MainActivity;
import com.example.diaapp.R;
import com.example.diaapp.database.DiaDataBase;
import com.example.diaapp.database.Record;
import com.example.diaapp.datapickers.DatePickerFragment;
import com.example.diaapp.datapickers.TimePickerFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import io.feeeei.circleseekbar.CircleSeekBar;

public class AddFragment extends Fragment {

    Button btn_ok, btn_exit;

    private CircleSeekBar mHalfEdSeekbar;
    private CircleSeekBar mEdSeekbar;
    private TextView mEdView, mTimeView, mDateView;

    // Метки для хранения данных dia
    HashMap<String, Float> diaData;
    String markGlucose = "glucose";
    String markInjlong = "injlong";
    String markInjshort = "injshort";
    String markXe = "xe";
    String curentMark = markGlucose;

    private Calendar calendar;

    int hours;
    int minutes;
    int years;
    int months;
    int days;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_fragment, container, false);

        // Create calendar
        calendar = Calendar.getInstance();
        years = calendar.get(Calendar.YEAR);
        months = calendar.get(Calendar.MONTH);
        days = calendar.get(Calendar.DAY_OF_MONTH);
        hours = calendar.get(Calendar.HOUR);
        minutes = calendar.get(Calendar.MINUTE);

        // создаем карту для хранения данных по ключу
        diaData = new HashMap<String, Float>();
        diaData.put(markGlucose, 0.0F);
        diaData.put(markInjlong, 0.0F);
        diaData.put(markInjshort, 0.0F);
        diaData.put(markXe, 0.0F);

        // установка текущей даты

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM", Locale.getDefault());
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());

        mTimeView = view.findViewById(R.id.text_view_time);
        mDateView = view.findViewById(R.id.text_view_date);
        mDateView.setText(dateFormatter.format(calendar.getTime()));
        mTimeView.setText(timeFormatter.format(calendar.getTime()));

        //Seekbars
        mHalfEdSeekbar = (CircleSeekBar) view.findViewById(R.id.seek_ed_half);
        mEdSeekbar = (CircleSeekBar) view.findViewById(R.id.seek_ed);
        mEdView = (TextView) view.findViewById(R.id.textview);

        mEdSeekbar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, int curValue) {
                changeText(curValue, mHalfEdSeekbar.getCurProcess());
            }
        });

        mHalfEdSeekbar.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onChanged(CircleSeekBar seekbar, int curValue) {
                changeText(mEdSeekbar.getCurProcess(), curValue);
            }
        });

        //radiogroup
        RadioGroup radioGroup = view.findViewById(R.id.radio_group_dia);
        radioGroup.check(R.id.rb_glucose);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                diaData.put(curentMark, (float) (mEdSeekbar.getCurProcess() + (mHalfEdSeekbar.getCurProcess()*0.1)));
                switch (checkedId) {
                    case -1:
                        break;
                    case R.id.rb_inject_long:
                        curentMark = markInjlong;
                        break;
                    case R.id.rb_inject_short:
                        curentMark = markInjshort;
                        break;
                    case R.id.rb_glucose:
                        curentMark = markGlucose;
                        break;
                    case R.id.rb_xe:
                        curentMark = markXe;
                        break;
                }
                Float temp = diaData.get(curentMark);
                int temp_unit = temp.intValue();

                float fPart = Math.round((temp - Math.floor(temp)) * 10);

                int temp_half_unit = (int) fPart;
                mEdSeekbar.setCurProcess(temp_unit);
                mHalfEdSeekbar.setCurProcess(temp_half_unit);
            }
        });

        //отмена сохранения
        btn_exit = view.findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // (переходим к другому фрагменту)
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DiaryFragment()).commit();
            }
        });

        //сохраняем данные в базу данных
        btn_ok = view.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaData.put(curentMark, (float) (mEdSeekbar.getCurProcess() + (mHalfEdSeekbar.getCurProcess()*0.1)));

                float inject_long = diaData.get(markInjlong);
                float inject_short = diaData.get(markInjshort);
                float glucose = Math.round(diaData.get(markGlucose) * 18);
                float xe = diaData.get(markXe);

                Calendar cldr = Calendar.getInstance();

                cldr.set(Calendar.YEAR, years);
                cldr.set(Calendar.MONTH, months);
                cldr.set(Calendar.DAY_OF_MONTH, days);
                cldr.set(Calendar.HOUR_OF_DAY, hours);
                cldr.set(Calendar.MINUTE, minutes);

                long time = cldr.getTimeInMillis();

                Record record = new Record(inject_long, inject_short, glucose, xe, time,
                        MainActivity.user.getId());

                DiaDataBase db = DiaDataBase.getDatabase(getActivity().getApplicationContext());

                db.diaDao().insert(record);
                Toast.makeText(view.getContext(), "Запись добавлена.", Toast.LENGTH_SHORT).show();

                // (переходим к другому фрагменту)
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DiaryFragment()).commit();
            }
        });

        //дата и время
        mDateView = view.findViewById(R.id.text_view_date);
        mTimeView = view.findViewById(R.id.text_view_time);

        mDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(calendar);
            }
        });

        mTimeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker(calendar);
            }
        });

        return view;
    }

    private void changeText(int hour, int minute) {
        mEdView.setText(String.valueOf(hour + "." + minute));
    }

    // pickers для даты и времени

    private void showDatePicker(Calendar calender) {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    private void showTimePicker(Calendar calender) {
        TimePickerFragment time = new TimePickerFragment(calender);
        time.setCallBack(ontime);
        time.show(getFragmentManager(), "Time Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            DateFormat dateFormat = new SimpleDateFormat("d MMM", Locale.getDefault());
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            years = year;
            months = monthOfYear;
            days = dayOfMonth;

            mDateView.setText(dateFormat.format(calendar.getTime()));
        }
    };

    TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            hours = hourOfDay;
            minutes = minute;

            mTimeView.setText(String.format("%02d:%02d", hourOfDay, minute));
        }
    };

}
