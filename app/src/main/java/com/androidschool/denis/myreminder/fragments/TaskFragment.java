package com.androidschool.denis.myreminder.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.androidschool.denis.myreminder.MainActivity;
import com.androidschool.denis.myreminder.adapters.CurrentTasksAdapter;
import com.androidschool.denis.myreminder.adapters.TaskAdapter;
import com.androidschool.denis.myreminder.model.ModelTask;

public abstract class TaskFragment extends Fragment {
    //базовый абстрактный класс для CurrentTaskFragment и DoneTaskFragment

    //protected - чтобы иметь доступ из классов-наследников
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    protected TaskAdapter adapter;

    public MainActivity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            activity = (MainActivity) getActivity();
        }

        addTaskFromDB();
    }

    public void addTask(ModelTask newTask, boolean saveToDB) {

        int position = -1;

        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.getItem(i).isTask()) {
                ModelTask task = (ModelTask) adapter.getItem(i);

                //добавляем таски отсортированными по дате.
                if (newTask.getDate() < task.getDate()) {
                    position = i;
                    break;//?цикл прерывается при нахождении первого элемента с бОльшей датой.
                }
            }
        }

        if (position != -1) {
            adapter.addItem(position, newTask);
        } else {
            adapter.addItem(newTask);
        }

        //для новых тасков (saveToDB==true) будем выполнять сохраниние в БД
        if (saveToDB) {
            activity.dbHelper.saveTask(newTask);
        }
    }

    public abstract void addTaskFromDB();

    public abstract void moveTask(ModelTask task);
}
