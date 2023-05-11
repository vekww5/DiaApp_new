package com.example.diaapp.user_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaapp.ListAdapter;
import com.example.diaapp.R;
import com.example.diaapp.database.DiaDataBase;
import com.example.diaapp.database.RecordDIA;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class DiaryFragment extends Fragment {
    private ListAdapter diaListAdapter;
    private FloatingActionButton fabInjectShort, fabInjectLong, fabGlucose, fabXE;
    private FloatingActionsMenu floatingActionsMenu;
    private RecyclerView recyclerView;
    DiaDataBase db;
    List<RecordDIA> diaList;

    // TODO список с записями пользователя: инсулин, хлебные единицы и т.д.
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.diary_fragment, container, false);

        // Синглтонное создание БД
        db = DiaDataBase.getDatabase(view.getContext());

        //указываем, что будем заполнять параметры меню
        setHasOptionsMenu(true);

        // иниц. списка RecyclerView
        initRecyclerView(view);

        // загрузка данных
        loadUserList(view);

        // иниц. свайпов RecyclerView
        setupRecyclerView();

        //иниц. меню floatingActionsMenu
        initFloatButtonMenuAction(view);

        return view;
    }

    private void initFloatButtonMenuAction(View view) {
        floatingActionsMenu = view.findViewById(R.id.floatingActionsMenu);

        //иниц. кнопки меню fabInjectLong и задание обработчика
        fabInjectLong = view.findViewById(R.id.inject_long);
        fabInjectLong.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AddFragment()).commit();
            floatingActionsMenu.collapse();
        });

        //иниц. кнопки меню fabInjectShort и задание обработчика
        fabInjectShort = view.findViewById(R.id.inject_short);
        fabInjectShort.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AddFragment()).commit();
            floatingActionsMenu.collapse();
        });

        //иниц. кнопки меню fabGlucose и задание обработчика
        fabGlucose = view.findViewById(R.id.drop_blood);
        fabGlucose.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AddFragment()).commit();
            floatingActionsMenu.collapse();
        });

        //иниц. кнопки меню fabXE и задание обработчика
        fabXE = view.findViewById(R.id.xe);
        fabXE.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AddFragment()).commit();
            floatingActionsMenu.collapse();
        });
    }

    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Create  тире между записями
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        //recyclerView.addItemDecoration(dividerItemDecoration);

        diaListAdapter = new ListAdapter(view.getContext());
        recyclerView.setAdapter(diaListAdapter);
    }

    private void loadUserList(View view) {
        diaList = db.diaDao().getAll();
        diaListAdapter.setUserList(diaList);
    }

    private void setupRecyclerView() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            //обработка сдвига элемента recyclerView
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                showSnackbar(position);
            }

        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showSnackbar(int position) {

        RecordDIA dia = diaListAdapter.getDiaAtPosition(position);
        db.diaDao().deleteID(dia.getUid());
        diaListAdapter.notifyItemRemoved(position);
        diaList.remove(position);

        Snackbar snackbar = Snackbar.make(getView(), "Запись удалена", Snackbar.LENGTH_LONG)
                .setAction("Отменить", v -> {
                    db.diaDao().insert(dia);
                    diaList.add(position, dia);
                    diaListAdapter.notifyItemInserted(position);
                }).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        if (event != DISMISS_EVENT_ACTION) {
                            // Snackbar закрыт без отмены удаления, не нужно восстанавливать удаленную запись
                        }
                    }
                });
        snackbar.show();
    }

}