package com.example.diaapp.user_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaapp.R;
import com.example.diaapp.database.DiaDataBase;
import com.example.diaapp.database.User;
import com.example.diaapp.database.UserDAO;
import com.example.diaapp.utility.Crypt;


public class RegistrationFragment extends Fragment {

    private EditText regPassword;
    private EditText regEmail;
    private Button btnReg;

    private DiaDataBase db;
    private UserDAO data;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration_fragment1, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        db = DiaDataBase.getDatabase(getActivity());
        data = db.dataDao();
        regPassword = view.findViewById(R.id.reg_password);
        regEmail = view.findViewById(R.id.reg_email);
        btnReg = view.findViewById(R.id.btn_reg);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = regPassword.getText().toString().trim();
                String email = regEmail.getText().toString().trim();

                try {
                    password =  Crypt.encrypt(password);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                User user = new User(email, password);
                Toast.makeText(getActivity(), password, Toast.LENGTH_SHORT).show();
                data.add(user);
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new LoginFragment()).commit();
            }
        });
    }


}
