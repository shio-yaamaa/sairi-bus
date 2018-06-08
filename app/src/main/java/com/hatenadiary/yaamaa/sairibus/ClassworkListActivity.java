package com.hatenadiary.yaamaa.sairibus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class ClassworkListActivity extends AppCompatActivity {

    int resultCode = RESULT_CANCELED;

    ClassworkListListAdapter classworkListListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classwork_list_activity);

        classworkListListAdapter = (ClassworkListListAdapter) ((ClassworkListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_fragment)).getListAdapter();

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("授業の編集");
        toolbar.setNavigationIcon(R.drawable.ic_appbar_close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivityWithResult();
            }
        });

        // Floating Action Button
        findViewById(R.id.floating_action_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassworkDialog dialog = new ClassworkDialog();

                // setup Bundle
                Bundle args = new Bundle();
                args.putLong("classwork_id", -1);
                dialog.setArguments(args);

                dialog.show(getSupportFragmentManager(), "dialog");
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishActivityWithResult();
    }

    private void finishActivityWithResult() {
        setResult(resultCode);
        finish();
    }
}