package com.example.diaapp.user_fragments;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaapp.R;
import com.example.diaapp.database.DiaDataBase;
import com.example.diaapp.database.RecordDIA;

import java.util.HashMap;
import java.util.List;

import io.feeeei.circleseekbar.CircleSeekBar;

public class AddFragment extends Fragment {

    Button btn;
    EditText edtLong;
    EditText edtShort;
    EditText edtGlucose;
    EditText edtXE;
    EditText edtTime;

    private CircleSeekBar mHalfEdSeekbar;
    private CircleSeekBar mEdSeekbar;
    private TextView mEdView;

    // Метки для хранения данных dia
    HashMap<String, Float> diaData;
    String markGlucose = "glucose";
    String markInjlong = "injlong";
    String markInjshort = "injshort";
    String markXe = "xe";
    String curentMark = markGlucose;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_fragment, container, false);

        // создаем карту для хранения данных по ключу
        diaData = new HashMap<String, Float>();
        diaData.put(markGlucose, 0.0F);
        diaData.put(markInjlong, 0.0F);
        diaData.put(markInjshort, 0.0F);
        diaData.put(markXe, 0.0F);

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

        btn = view.findViewById(R.id.btn_ok);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                diaData.put(curentMark, (float) (mEdSeekbar.getCurProcess() + (mHalfEdSeekbar.getCurProcess()*0.1)));

                float inject_long = diaData.get(markInjlong);
                float inject_short = diaData.get(markInjshort);
                float glucose = diaData.get(markGlucose);
                float xe = diaData.get(markXe);
                long time = System.currentTimeMillis();
                RecordDIA recordDIA = new RecordDIA(inject_long, inject_short, glucose, xe, time);

                DiaDataBase db = DiaDataBase.getDatabase(getActivity().getApplicationContext());
                db.diaDao().insert(recordDIA);
            }
        });

        return view;
    }

    private void changeText(int hour, int minute) {
        mEdView.setText(String.valueOf(hour + "." + minute));
    }
}
