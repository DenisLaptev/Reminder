package com.androidschool.denis.myreminder.fragments;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.androidschool.denis.myreminder.MainActivity;
import com.androidschool.denis.myreminder.R;
import com.androidschool.denis.myreminder.adapters.CurrentTasksAdapter;
import com.androidschool.denis.myreminder.adapters.TaskAdapter;
import com.androidschool.denis.myreminder.alarm.AlarmHelper;
import com.androidschool.denis.myreminder.dialog.EditTaskDialogFragment;
import com.androidschool.denis.myreminder.model.Item;
import com.androidschool.denis.myreminder.model.ModelTask;

public abstract class TaskFragment extends Fragment {
    //базовый абстрактный класс для CurrentTaskFragment и DoneTaskFragment

    //protected - чтобы иметь доступ из классов-наследников
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;

    protected TaskAdapter adapter;

    public MainActivity activity;

    public AlarmHelper alarmHelper;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            activity = (MainActivity) getActivity();
        }

        alarmHelper = AlarmHelper.getInstance();

        addTaskFromDB();
    }

    public abstract void addTask(ModelTask newTask, boolean saveToDB);
    //На вкладке выполненных задач не будет сепараторов.


    public void updateTask(ModelTask task) {
        adapter.updateTask(task);
    }

    //вызов диалогового окна для удаления таска
    public void removeTaskDialog(final int location) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setMessage(R.string.dialog_removing_message);

        Item item = adapter.getItem(location);

        if (item.isTask()) {

            ModelTask removingTask = (ModelTask) item;
            final long timeStamp = removingTask.getTimeStamp();
            final boolean[] isRemoved = {false};

            dialogBuilder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {

                    adapter.removeItem(location);
                    isRemoved[0] = true;
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator), R.string.removed, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.dialog_cancel, new View.OnClickListener() {
                        //по нажатию на отмену в снекбаре, таск восстанавливается в БД
                        @Override
                        public void onClick(View view) {
                            addTask(activity.dbHelper.query().getTask(timeStamp), false);
                            isRemoved[0] = false;
                        }
                    });
                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View view) {
                            //метод срабатывает, когда снекбар появляется на экране

                        }

                        @Override
                        public void onViewDetachedFromWindow(View view) {
                            //метод срабатывает, когда снекбар исчезает с экрана
                            if (isRemoved[0]) {
                                alarmHelper.removeAlarm(timeStamp);
                                //если не была нажата кнопка отмены удаления, то таск удаляется окончательно из БД
                                activity.dbHelper.removeTask(timeStamp);
                            }
                        }
                    });
                    snackbar.show();

                    dialogInterface.dismiss();
                }
            });
            dialogBuilder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {

                }
            });
        }
        dialogBuilder.show();
    }

    public void showTaskEditDialog(ModelTask task) {
        DialogFragment editingTaskDialog = EditTaskDialogFragment.newInstance(task);
        editingTaskDialog.show(getActivity().getFragmentManager(), "EditingTaskDialogFragment");
    }

    public void removeAllTasks() {
        adapter.removeAllItems();

    }

    public abstract void findTasks(String title);

    //Чтобы не вылетало приложение при возобновлении работы из свёрнутого состояния
    public abstract void checkAdapter();

    public abstract void addTaskFromDB();

    public abstract void moveTask(ModelTask task);
}
