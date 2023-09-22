package com.example.diaapp.user_fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.example.diaapp.MainActivity;
import com.example.diaapp.R;
import com.example.diaapp.database.RecordDAO;
import com.example.diaapp.database.DiaDataBase;
import com.example.diaapp.database.UserDAO;
import com.example.diaapp.utility.Crypt;

public class LoginFragment extends Fragment {
    private EditText edtName;
    private EditText edtPassword;
    private Button btnLog;
    private TextView tvReg;

    DiaDataBase db;
    UserDAO data;
    RecordDAO recordDao;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment1, container, false);
        db = DiaDataBase.getDatabase(getActivity());
        data = db.dataDao();
        recordDao = db.diaDao();
        init(view);
        return view;
    }

    private void init(View view) {
        edtName = view.findViewById(R.id.edt_username);
        edtPassword = view.findViewById(R.id.edt_password);
        btnLog = view.findViewById(R.id.btn_sign_in);
        tvReg = view.findViewById(R.id.tv_registration);

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtName.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                try {
                    password = Crypt.encrypt(password);
                } catch (Exception e) {

                }
                Toast.makeText(getActivity(), password, Toast.LENGTH_SHORT).show();
                MainActivity.user = data.verify(email, password);

                if(MainActivity.user != null){

                    MainActivity.sharedPreferencesEmail = getContext().getSharedPreferences(MainActivity.PREFERENCES_EMAIL,
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = MainActivity.sharedPreferencesEmail.edit();
                    editor.putString(MainActivity.PREFERENCES_EMAIL, email);
                    editor.apply();

                    MainActivity.sharedPreferencesPassword = getContext().getSharedPreferences(MainActivity.PREFERENCES_PASSWORD,
                            Context.MODE_PRIVATE);
                    editor = MainActivity.sharedPreferencesPassword.edit();
                    editor.putString(MainActivity.PREFERENCES_PASSWORD, password);
                    editor.apply();

                    Toast.makeText(getActivity(), MainActivity.user.toString(), Toast.LENGTH_SHORT).show();

                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("b").
                            replace(R.id.fragment_container, new DiaryFragment()).commit();
                }
                else {
                        Toast.makeText(getActivity(), "Нет такого пользователя", Toast.LENGTH_SHORT).show();
                }

            }
        });

        tvReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("b").
                        replace(R.id.fragment_container, new RegistrationFragment()).commit();
            }
        });

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().hide();
    }
}
