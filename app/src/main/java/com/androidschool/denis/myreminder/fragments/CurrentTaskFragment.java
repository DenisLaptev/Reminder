package com.androidschool.denis.myreminder.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidschool.denis.myreminder.R;
import com.androidschool.denis.myreminder.adapters.CurrentTasksAdapter;
import com.androidschool.denis.myreminder.database.DBHelper;
import com.androidschool.denis.myreminder.model.ModelSeparator;
import com.androidschool.denis.myreminder.model.ModelTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentTaskFragment extends TaskFragment {


    public CurrentTaskFragment() {
        // Required empty public constructor
    }

    OnTaskDoneListener onTaskDoneListener;

    public interface OnTaskDoneListener {
        void onTaskDone(ModelTask task);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onTaskDoneListener = (OnTaskDoneListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTaskDoneListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_current_task, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.rvCurrentTasks);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        //В аргументе ссылаемся на текущий фрагмент
        adapter = new CurrentTasksAdapter(this);
        recyclerView.setAdapter(adapter);

        return rootView;
    }


    @Override
    public void findTasks(String title) {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        //добавление тасков, если они текущие или просроченные
        tasks.addAll(activity.dbHelper.query().getTasks(
                DBHelper.SELECTION_LIKE_TITLE + " AND "
                        + DBHelper.SELECTION_STATUS + " OR "
                        + DBHelper.SELECTION_STATUS,
                new String[]{
                        "%" + title + "%",
                        Integer.toString(ModelTask.STATUS_CURRENT),
                        Integer.toString(ModelTask.STATUS_OVERDUE)
                }, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }

    @Override
    public void addTaskFromDB() {
        adapter.removeAllItems();
        List<ModelTask> tasks = new ArrayList<>();
        //добавление тасков, если они текущие или просроченные
        tasks.addAll(activity.dbHelper.query().getTasks(
                DBHelper.SELECTION_STATUS + " OR " + DBHelper.SELECTION_STATUS,
                new String[]{
                        Integer.toString(ModelTask.STATUS_CURRENT),
                        Integer.toString(ModelTask.STATUS_OVERDUE)
                }, DBHelper.TASK_DATE_COLUMN));
        for (int i = 0; i < tasks.size(); i++) {
            addTask(tasks.get(i), false);
        }
    }


    @Override
    public void addTask(ModelTask newTask, boolean saveToDB) {
        int position = -1;
        ModelSeparator separator = null;

        checkAdapter();
        //Цикл определяет позицию таска
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isTask()) {
                ModelTask task = (ModelTask) adapter.getItem(i);
                if (newTask.getDate() < task.getDate()) {
                    position = i;
                    break;
                }
            }
        }

        if (newTask.getDate() != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(newTask.getDate());


            if (calendar.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                    && calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {

                newTask.setDateStatus(ModelSeparator.TYPE_OVERDUE);

                if (!adapter.containsSeparatorOverdue) {
                    adapter.containsSeparatorOverdue = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_OVERDUE);
                }


            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                    && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {

                newTask.setDateStatus(ModelSeparator.TYPE_TODAY);

                if (!adapter.containsSeparatorToday) {
                    adapter.containsSeparatorToday = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TODAY);
                }

            } else if (calendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1
                    && calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {

                newTask.setDateStatus(ModelSeparator.TYPE_TOMORROW);

                if (!adapter.containsSeparatorTomorrow) {
                    adapter.containsSeparatorTomorrow = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_TOMORROW);
                }
            } else if (calendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 1
                    || calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {

                newTask.setDateStatus(ModelSeparator.TYPE_FUTURE);

                if (!adapter.containsSeparatorFuture) {
                    adapter.containsSeparatorFuture = true;
                    separator = new ModelSeparator(ModelSeparator.TYPE_FUTURE);
                }
            }
        }


        //Условия предотвращают некорректное размещение задач внутри сепараторов
        if (position != -1) {


            if (!adapter.getItem(position - 1).isTask()) {
                if (position - 2 >= 0 && adapter.getItem(position - 2).isTask()) {
                    ModelTask task = (ModelTask) adapter.getItem(position - 2);
                    if (task.getDateStatus() == newTask.getDateStatus()) {
                        position -= 1;
                    }
                } else if (position - 2 < 0 && newTask.getDate() == 0) {
                    position -= 1;
                }
            }
            if (separator != null) {
                adapter.addItem(position - 1, separator);
            }

            adapter.addItem(position, newTask);
        } else {

            if (separator != null) {
                adapter.addItem(separator);
            }

            adapter.addItem(newTask);
        }

        if (saveToDB) {
            activity.dbHelper.saveTask(newTask);
        }

    }


    @Override
    public void moveTask(ModelTask task) {
        alarmHelper.removeAlarm(task.getTimeStamp());
        onTaskDoneListener.onTaskDone(task);
    }

    //Если адаптер пустой, то пересоздаём адаптер
    @Override
    public void checkAdapter() {
        if (adapter == null) {
            adapter = new CurrentTasksAdapter(this);
            addTaskFromDB();
        }
    }
}
