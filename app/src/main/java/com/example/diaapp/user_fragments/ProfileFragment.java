package com.example.diaapp.user_fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaapp.MainActivity;
import com.example.diaapp.R;
import com.example.diaapp.database.DiaDataBase;
import com.example.diaapp.database.Profile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    private EditText etWeight, etHeight, etName, etLastName;
    private TextView tvDate, tvEmail, tvPassword;
    private Button btnSaveUserInfo;
    private ToggleButton tbDiaType, tbPol;
    private RelativeLayout rl_date_birth;

    DiaDataBase db;


    private Calendar calendar;
    private String TAG = "dialog";

    // создание фрагмента, иниц. элеметов

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        //иницыализация переченных дл работы с базой данных
        db = DiaDataBase.getDatabase(getActivity().getApplicationContext());


        //иницыализация переченных имени, фамилии
        etName = view.findViewById(R.id.edit_text_name_value);
        etLastName = view.findViewById(R.id.edit_text_name_last_value);

        tbDiaType = view.findViewById(R.id.toggle_button_dia);
        tbPol = view.findViewById(R.id.toggle_button_pol);

        tvPassword = view.findViewById(R.id.text_view_change_password);

        tvPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvEmail = view.findViewById(R.id.text_view_email);

        calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        tvDate = view.findViewById(R.id.text_view_date);

        etWeight = view.findViewById(R.id.edit_text_weight_value);
        etHeight = view.findViewById(R.id.edit_text_height_value);

        etWeight.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});
        etHeight.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(4,2)});

        //иниц. кнопки btnSaveUserInfo и задание обработчика
        btnSaveUserInfo = view.findViewById(R.id.button_save_user_info);
        btnSaveUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eHeight = etHeight.getText().toString().trim();
                String eWeight = etWeight.getText().toString().trim();

                float eh = Float.parseFloat(eHeight);
                float ew = Float.parseFloat(eWeight);

                //save data user

                Profile iu = db.infoDao().getInfo( MainActivity.user.getId());
                List<Profile> iuall = db.infoDao().getInfoUsers();

                if (iu != null){
                    iu.setFirstName(etName.getText().toString().trim());
                    iu.setLastName(etLastName.getText().toString().trim());
                    iu.setHeight(eh);
                    iu.setWeight(ew);
                    iu.setTypeDia(tbDiaType.getText().toString().trim());
                    iu.setGender(tbPol.getText().toString().trim());
                    iu.setBirthday(calendar.getTimeInMillis());

                    db.infoDao().updateInfoUser(iu);
                }else {

                    Profile profile = new Profile(
                            etName.getText().toString().trim(),
                            etLastName.getText().toString().trim(),
                            eh,
                            ew,
                            tbDiaType.getText().toString().trim(),
                            tbPol.getText().toString().trim(),
                            calendar.getTimeInMillis(),
                            MainActivity.user.getId());

                    db.infoDao().insert(profile);
                }

            }
        });

        rl_date_birth = view.findViewById(R.id.rl_date_birth);
        rl_date_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int myYear = calendar.get(Calendar.YEAR);
                int myMonth = calendar.get(Calendar.MONTH);
                int myDay = calendar.get(Calendar.DAY_OF_MONTH);

                // инициализируем диалог выбора даты текущими значениями
                DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calendar.set(year, monthOfYear, dayOfMonth);

                                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                                String currentDateString = dateFormat.format(calendar.getTime());

                                tvDate.setText(currentDateString);

                            }
                        }, myYear, myMonth, myDay);
                datePickerDialog.show();
            }
        });

        // загрузка профиля
        setupUserInfo(view);

        return view;
    }

    private void setupUserInfo(View view) {
            Profile user = db.infoDao().getInfo(MainActivity.user.getId());
            if (user != null) {
                tvEmail.setText(MainActivity.user.getEmail());
                etName.setText(user.getFirstName());
                etLastName.setText(user.getLastName());
                etHeight.setText(String.valueOf(user.getHeight()));
                etWeight.setText(String.valueOf(user.getWeight()));

                calendar.setTimeInMillis(user.getBirthday());

                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String currentDateString = dateFormat.format(calendar.getTime());
                tvDate.setText(currentDateString);

                if(user.getTypeDia().equals("II")) {
                    tbDiaType.setChecked(true);
                }

                if(user.getGender().equals("Ж")) {
                    tbPol.setChecked(true);
                }
            }
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }
    }
}
