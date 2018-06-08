package com.hatenadiary.yaamaa.sairibus;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

public class DetailDialog extends DialogFragment {

    String title;
    List<String> nameList;
    List<Integer> timeList;
    boolean twoBuses = false;
    boolean microDisease = false;
    boolean busHoliday = false;

    TextView[] timeLeftViews;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        // find Views
        final View contentView = inflater.inflate(R.layout.detail_dialog_content, null);
        final TableLayout tableLayout = (TableLayout) contentView.findViewById(R.id.table);

        // get values for the table
        title = getArguments().getString("title");
        nameList = getArguments().getStringArrayList("name_list");
        timeList = getArguments().getIntegerArrayList("time_list");
        twoBuses = getArguments().getBoolean("two_buses");
        microDisease = getArguments().getBoolean("micro_disease");
        busHoliday = getArguments().getBoolean("bus_holiday");

        timeLeftViews = new TextView[nameList.size()];

        // create and add TableRows
        for (int i = 0; i < nameList.size(); i++) {

            TableRow tableRow = (TableRow) inflater.inflate(R.layout.detail_dialog_table_item, null);

            ((TextView) tableRow.findViewById(R.id.name)).setText(nameList.get(i));
            ((TextView) tableRow.findViewById(R.id.time)).setText(Utility.convertTime(timeList.get(i), false));
            timeLeftViews[i] = (TextView) tableRow.findViewById(R.id.time_left);

            final MapCoordinate mapCoordinate = MapCoordinateManager.getMapCoordinateByName(nameList.get(i));
            if (mapCoordinate != null) {
                tableRow.getChildAt(0).setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(Utility.makeMapUriString(mapCoordinate.latitude, mapCoordinate.longitude))
                        );
                        intent.setPackage(Constants.GOOGLE_MAP_PACKAGE_NAME);
                        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                        return true;
                    }
                });
            }

            tableLayout.addView(tableRow);
        }

        contentView.findViewById(R.id.two_buses).setVisibility(twoBuses ? View.VISIBLE : View.GONE);
        contentView.findViewById(R.id.micro_disease).setVisibility(microDisease ? View.VISIBLE : View.GONE);
        contentView.findViewById(R.id.holiday).setVisibility(busHoliday ? View.VISIBLE : View.GONE);

        updateTimeLeft();

        // create Dialog
        return new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setView(contentView)
                .setNegativeButton("CLOSE", null)
                .create();
    }

    public void invalidate() {
        updateTimeLeft();
    }

    public void updateTimeLeft() {
        for (int i = 0; i < timeLeftViews.length; i++) {
            timeLeftViews[i].setText(
                    timeList.get(i) >= Utility.getCurrentTime()
                            ? Utility.convertTime(timeList.get(i) - Utility.getCurrentTime(), true)
                            : "-"
            );
        }
    }
}
