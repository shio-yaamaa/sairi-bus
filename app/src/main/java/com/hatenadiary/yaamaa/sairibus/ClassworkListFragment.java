package com.hatenadiary.yaamaa.sairibus;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

public class ClassworkListFragment extends ListFragment {

    ClassworkListListAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listAdapter = new ClassworkListListAdapter(getActivity(), 0, ClassworkManager.classworkList);

        this.setListAdapter(listAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        this.setEmptyText("+ボタンをタップして、新しい授業を追加してください。");

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassworkDialog dialog = new ClassworkDialog();

                // setup Bundle
                Bundle args = new Bundle();
                args.putLong("classwork_id", ClassworkManager.classworkList.get(position).id);
                dialog.setArguments(args);

                dialog.show(((ClassworkListActivity) getContext()).getSupportFragmentManager(), "dialog");
            }
        });
    }
}