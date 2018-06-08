package com.hatenadiary.yaamaa.sairibus;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

public class ClassworkDialog extends DialogFragment {

    int start;
    int end;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final LayoutInflater inflater = LayoutInflater.from(getContext());
        final ClassworkListActivity classworkListActivity = (ClassworkListActivity) getActivity();
        final ClassworkListListAdapter classworkListListAdapter = classworkListActivity.classworkListListAdapter;

        final View contentView = inflater.inflate(R.layout.classwork_dialog_content, null);

        // get the classwork
        final Classwork classwork = ClassworkManager.getClassworkById(getArguments().getLong("classwork_id"));

        start = classwork == null ? -1 : classwork.times[0];
        end = classwork == null ? -1 : classwork.times[1];

        final EditText nameEditText = (EditText) contentView.findViewById(R.id.classwork_name);
        nameEditText.setText(classwork == null ? "" : classwork.name);

        // setup ListView
        ListView listView = (ListView) contentView.findViewById(R.id.list_view);
        final ClassworkDialogListAdapter classworkDialogListAdapter = new ClassworkDialogListAdapter(getContext(), classwork, this);
        listView.setAdapter(classworkDialogListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (position == 0) {
                                    start = Utility.convertTimeFromCalendar(new int[]{hourOfDay, minute});
                                } else {
                                    end = Utility.convertTimeFromCalendar(new int[]{hourOfDay, minute});
                                }
                                classworkDialogListAdapter.notifyDataSetChanged();
                            }
                        },
                        Utility.convertTimeForCalendar(position == 0 ? start : end)[0],
                        Utility.convertTimeForCalendar(position == 0 ? start : end)[1],
                        true
                ).show();
            }
        });

        // create Dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext())
                .setView(contentView)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null);

        if (classwork != null) {
            dialogBuilder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClassworkManager.deleteClasswork(classworkListListAdapter, classwork);
                    Snackbar
                            .make(getActivity().findViewById(R.id.coordinator_layout), "授業を削除しました", Snackbar.LENGTH_INDEFINITE)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ClassworkManager.undoDeleteClasswork(classworkListListAdapter);
                                }
                            })
                            .show();
                    classworkListActivity.resultCode = Activity.RESULT_OK;
                }
            });
        }

        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name = nameEditText.getText().toString();

                        // 授業名が設定されているか
                        if (name.equals("")) {
                            Snackbar.make(alertDialog.getButton(DialogInterface.BUTTON_POSITIVE), "授業名が空欄です", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        // 開始・終了時間が設定されているか
                        if (start == -1 || end == -1) {
                            Snackbar.make(alertDialog.getButton(DialogInterface.BUTTON_POSITIVE), "時間が設定されていません", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        // 開始・終了時間の順序が正しいか
                        if (start >= end) {
                            Snackbar.make(alertDialog.getButton(DialogInterface.BUTTON_POSITIVE), "開始時間と終了時間の順序が正しくありません", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        if (classwork == null) {
                            // 新規登録
                            ClassworkManager.addClasswork(classworkListListAdapter, name, new int[]{start, end});
                        } else {
                            // 編集
                            ClassworkManager.editClasswork(classworkListListAdapter, classwork, name, new int[]{start, end});
                        }
                        classworkListActivity.resultCode = Activity.RESULT_OK;
                        alertDialog.dismiss();
                    }
                });
            }
        });

        return alertDialog;
    }

}