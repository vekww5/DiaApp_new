package com.example.diaapp.user_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diaapp.ListAdapter;
import com.example.diaapp.MainActivity;
import com.example.diaapp.R;
import com.example.diaapp.database.DiaDataBase;
import com.example.diaapp.database.Record;
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
    List<Record> diaList;

    // Cписок с записями пользователя: инсулин, хлебные единицы и т.д.
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

    void getFragment(){
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("d").replace(R.id.fragment_container,
                new AddFragment()).commit();
    }

    private void initFloatButtonMenuAction(View view) {
        floatingActionsMenu = view.findViewById(R.id.floatingActionsMenu);

        //иниц. кнопки меню fabInjectLong и задание обработчика
        fabInjectLong = view.findViewById(R.id.inject_long);
        fabInjectLong.setOnClickListener(v -> {
            getFragment();
            floatingActionsMenu.collapse();
        });

        //иниц. кнопки меню fabInjectShort и задание обработчика
        fabInjectShort = view.findViewById(R.id.inject_short);
        fabInjectShort.setOnClickListener(v -> {
            getFragment();
            floatingActionsMenu.collapse();
        });

        //иниц. кнопки меню fabGlucose и задание обработчика
        fabGlucose = view.findViewById(R.id.drop_blood);
        fabGlucose.setOnClickListener(v -> {
            getFragment();
            floatingActionsMenu.collapse();
        });

        //иниц. кнопки меню fabXE и задание обработчика
        fabXE = view.findViewById(R.id.xe);
        fabXE.setOnClickListener(v -> {
            getFragment();
            floatingActionsMenu.collapse();
        });
    }

    //инициализация списка
    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Create  тире между записями
        //DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        //recyclerView.addItemDecoration(dividerItemDecoration);

        diaListAdapter = new ListAdapter(view.getContext());
        recyclerView.setAdapter(diaListAdapter);
    }

    //загрузка записей пользователя
    private void loadUserList(View view) {
        if (MainActivity.user != null) {
            diaList = db.diaDao().getAllSortedTime(MainActivity.user.getId());
            String str = String.valueOf(MainActivity.user.getId());
            Toast.makeText(getActivity(), str, Toast.LENGTH_LONG).show();
            diaListAdapter.setUserList(diaList);
        }
    }

    // настройка списка recyclerView
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

    // создание и настройка Snackbar
    private void showSnackbar(int position) {
        Record dia = diaListAdapter.getDiaAtPosition(position);
        db.diaDao().deleteID(dia.getRecordId());
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
    }

}