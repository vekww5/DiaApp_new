package com.example.diaapp.user_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.diaapp.R;


public class MainFragment extends Fragment {
    Button stat;
    Button dairy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        stat = (Button) view.findViewById(R.id.statistics_fragment);
        dairy = (Button) view.findViewById(R.id.diary_fragment);
        return view;
    }

}