package com.hatenadiary.yaamaa.sairibus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ClassworkListListAdapter extends ArrayAdapter<Classwork> {

    LayoutInflater layoutInflater;

    public ClassworkListListAdapter(Context context, int textViewResourceId, List<Classwork> objects) {
        super(context, textViewResourceId, objects);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ClassworkManager.classworkList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Classwork classwork = ClassworkManager.classworkList.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.classwork_list_list_item, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.name)).setText(classwork.name);
        ((TextView) convertView.findViewById(R.id.time))
                .setText(Utility.convertTimes(classwork.times, true));

        return convertView;
    }
}