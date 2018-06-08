package com.hatenadiary.yaamaa.sairibus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClassworkDialogListAdapter extends BaseAdapter {

    LayoutInflater layoutInflater;
    Classwork classwork;
    ClassworkDialog classworkDialog;

    public ClassworkDialogListAdapter(Context context, Classwork classwork, ClassworkDialog classworkDialog) {
        super();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.classwork = classwork;
        this.classworkDialog = classworkDialog;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.classwork_dialog_list_item, parent, false);
        }

        int time = position == 0 ? classworkDialog.start : classworkDialog.end;

        ((TextView) convertView.findViewById(R.id.name)).setText(position == 0 ? "開始" : "終了");
        ((TextView) convertView.findViewById(R.id.time))
                .setText(time == -1 ? "タップして設定" : Utility.convertTime(time, false));

        return convertView;
    }

}