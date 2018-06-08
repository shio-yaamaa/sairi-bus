package com.hatenadiary.yaamaa.sairibus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Utility utility;

    // show/hide
    SharedPreferences sharedPreferences;
    List<String> keyNames = new ArrayList<>();
    Map<String, String> keyNameAndShownName = new HashMap<>();
    Map<String, View> rowTitleViewToShow = new HashMap<>();
    Map<String, View> rowViewToShow = new HashMap<>();

    RuledView ruledView;
    CurrentTimeView currentTimeView;

    ProgressDialog progressDialog;

    static Calendar currentDate;

    boolean errorFlag = false;
    boolean isBusReady = false;
    boolean isLibraryReady = false;
    boolean isRestaurantReady = false;
    boolean isHolidayReady = false;

    LinearLayout rowTitleContainer;
    LinearLayout rowContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        utility = new Utility(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constants.clearTimeRange();

        // set width of row title
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        Constants.setRowTitleWidth(utility.pxToDp(point.x));

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(Utility.getAppbarTitle());

        ruledView = (RuledView) findViewById(R.id.ruled_view);
        currentTimeView = (CurrentTimeView) findViewById(R.id.current_time_view);

        // set fading edge
        View horizontalScroll = findViewById(R.id.horizontal_scroll);
        horizontalScroll.setHorizontalFadingEdgeEnabled(true);
        horizontalScroll.setFadingEdgeLength((int) utility.dpToPx(5));

        // ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("データを取得しています");
        progressDialog.show();

        currentDate = Calendar.getInstance();

        Initializer.initialize(this);
    }

    public void readyBus(boolean errorFlag) { isBusReady = true; setupRows(errorFlag); }

    public void readyLibrary(boolean errorFlag) { isLibraryReady = true; setupRows(errorFlag); }

    public void readyRestaurant(boolean errorFlag) { isRestaurantReady = true; setupRows(errorFlag); }

    public void readyHoliday(boolean errorFlag) { isHolidayReady = true; setupRows(errorFlag); }

    private void setupRows(boolean errorFlag) {

        this.errorFlag = this.errorFlag || errorFlag;

        if (isBusReady && isLibraryReady && isRestaurantReady && isHolidayReady) {

            Constants.settleTimeRange();

            ruledView.adjustSize();
            ruledView.invalidate();
            currentTimeView.adjustSize();
            currentTimeView.invalidate();

            // find parents
            rowTitleContainer = (LinearLayout) findViewById(R.id.row_title_container);
            rowTitleContainer.removeAllViews();
            rowContainer = (LinearLayout) findViewById(R.id.row_container);
            rowContainer.getLayoutParams().width = (int) utility.dpToPx(Constants.CONTENT_WIDTH);
            rowContainer.removeAllViews();

            setupClassworkRow();
            setupBusRow();
            setupLibraryRow();
            setupRestaurantRow();

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (errorFlag) {
                new AlertDialog.Builder(this)
                        .setTitle("エラー")
                        .setMessage("情報の取得中にエラーが発生しました。ネット接続を見直し、右上のメニューから再読み込みをしてみてください。")
                        .setPositiveButton("OK", null)
                        .show();
            }

        }
    }

    private void setupClassworkRow() {

        String keyName = "isClassworkShown";
        keyNames.add(keyName);
        keyNameAndShownName.put(keyName, "授業");

        // setup row title
        View rowTitleView = RowTitleMaker
                .make("授業", Constants.ROW_TITLE_DEFAULT_COLOR, Constants.CLASSWORK_COLOR, (int) utility.dpToPx(Constants.PERIOD_VIEW_HEIGHT));
        rowTitleViewToShow.put(keyName, rowTitleView);
        rowTitleContainer.addView(rowTitleView);

        // create the parent
        FrameLayout rowView = new FrameLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) utility.dpToPx(Constants.PERIOD_VIEW_HEIGHT));
        layoutParams.setMargins(0, (int) utility.dpToPx(Constants.MARGIN_BETWEEN_ROWS), 0, 0);
        rowView.setLayoutParams(layoutParams);
        rowContainer.addView(rowView);
        rowViewToShow.put(keyName, rowView);

        for (Classwork classwork : ClassworkManager.classworkList) {
            PeriodView classView = new PeriodView(this);
            classView.initialize(
                    rowView,
                    classwork.name,
                    PeriodView.PeriodType.CLASSWORK,
                    classwork.times,
                    Constants.CLASSWORK_COLOR
            );
        }

        updateVisibility(keyName, sharedPreferences.getBoolean(keyName, true) ? View.VISIBLE : View.GONE);
    }

    private void setupBusRow() {

        for (int section = 0; section < BusManager.sectionList.size(); section++) {

            String keyName = "is" + BusManager.sectionList.get(section) + "Shown";
            keyNames.add(keyName);
            keyNameAndShownName.put(keyName, "バス(" + BusManager.sectionList.get(section) + ")");

            LinearLayout sectionLayout = new LinearLayout(this);
            rowViewToShow.put(keyName, sectionLayout);
            sectionLayout.setOrientation(LinearLayout.VERTICAL);
            rowContainer.addView(sectionLayout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            ((LinearLayout.LayoutParams) sectionLayout.getLayoutParams())
                    .setMargins(0, (int) utility.dpToPx(Constants.ROW_MAIN_TITLE_HEIGHT + Constants.MARGIN_BETWEEN_ROWS), 0, 0);

            int[] directionHeights = new int[BusManager.directionList.get(section).size()];

            for (int direction = 0; direction < BusManager.directionList.get(section).size(); direction++) {

                int directionColor = Constants.busDirectionColors[section % 2][direction % 2];

                LinearLayout directionLayout = new LinearLayout(this);
                directionLayout.setOrientation(LinearLayout.VERTICAL);
                sectionLayout.addView(directionLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                for (Bus bus : BusManager.busList) {
                    if (bus.section == section && direction == bus.direction) {

                        // add FrameLayout if it doesn't exist
                        if (directionLayout.getChildCount() <= bus.lane) {
                            FrameLayout frameLayout = new FrameLayout(this);
                            directionLayout.addView(
                                    frameLayout,
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    (int) utility.dpToPx(Constants.BUS_VIEW_HEIGHT)
                            );
                        }

                        // add BusView
                        BusView busView = new BusView(this);
                        busView.initialize(
                                (FrameLayout) directionLayout.getChildAt(bus.lane),
                                bus,
                                directionColor
                        );

                    }
                }

                directionHeights[direction] = (int) utility.dpToPx(Constants.BUS_VIEW_HEIGHT) * directionLayout.getChildCount();
            }

            // make row title
            CharSequence[] subtitles = new CharSequence[BusManager.directionList.get(section).size()];
            CharSequence[] fullLengthSubtitles = new CharSequence[subtitles.length];
            for (int i = 0; i < subtitles.length; i++) {
                subtitles[i] = Utility.makeDirectionText(BusManager.directionList.get(section).get(i), false);
                fullLengthSubtitles[i] = Utility.makeDirectionText(BusManager.directionList.get(section).get(i), true);
            }
            int[] subtitleColors = new int[subtitles.length];
            Arrays.fill(subtitleColors, Color.argb(255, 50, 50, 50));

            View rowTitle = RowTitleMaker.make(
                    Utility.shortenSectionText(BusManager.sectionList.get(section)),
                    Constants.ROW_TITLE_DEFAULT_COLOR,
                    (int) utility.dpToPx(Constants.ROW_MAIN_TITLE_HEIGHT),
                    Constants.busSectionColors[section % 2],
                    subtitles,
                    fullLengthSubtitles,
                    directionHeights,
                    Constants.busDirectionColors[section % 2],
                    Gravity.TOP
            );
            rowTitleContainer.addView(rowTitle);
            rowTitleViewToShow.put(keyName, rowTitle);

            updateVisibility(keyName, sharedPreferences.getBoolean(keyName, true) ? View.VISIBLE : View.GONE);
        }
    }

    private void setupLibraryRow() {

        String keyName = "isLibraryShown";
        keyNames.add(keyName);
        keyNameAndShownName.put(keyName, "図書館");

        // create row title
        String[] names = new String[LibraryManager.libraryList.size()];
        String[] fullLengthNames = new String[LibraryManager.libraryList.size()];
        int[][] times = new int[LibraryManager.libraryList.size()][2];
        for (int i = 0; i < names.length; i++) {
            Library library = LibraryManager.libraryList.get(i);
            names[i] = library.name;
            fullLengthNames[i] = Utility.makeSnackbarText(library.name, library.times, true);
            times[i] = new int[]{library.times[0], library.times[1]};
        }
        int[] subtitleHeights = new int[LibraryManager.libraryList.size()];
        Arrays.fill(subtitleHeights, (int) utility.dpToPx(Constants.PERIOD_VIEW_HEIGHT));
        int[] subtitleColors = new int[LibraryManager.libraryList.size()];
        Arrays.fill(subtitleColors, Constants.ROW_TITLE_DEFAULT_COLOR);
        View rowTitle = RowTitleMaker.make(
                "図書館",
                Constants.LIBRARY_COLOR,
                (int) utility.dpToPx(Constants.ROW_MAIN_TITLE_HEIGHT),
                Constants.LIBRARY_COLOR,
                names,
                fullLengthNames,
                subtitleHeights,
                subtitleColors,
                Gravity.CENTER_VERTICAL
        );
        rowTitleContainer.addView(rowTitle);
        rowTitleViewToShow.put(keyName, rowTitle);

        // create row
        int[] periodViewColors = new int[LibraryManager.libraryList.size()];
        Arrays.fill(periodViewColors, Constants.LIBRARY_COLOR);
        View rowView = PeriodViewGroupMaker.make(
                names, PeriodView.PeriodType.LIBRARY, times, periodViewColors
        );
        rowContainer.addView(rowView);
        rowViewToShow.put(keyName, rowView);

        updateVisibility(keyName, sharedPreferences.getBoolean(keyName, true) ? View.VISIBLE : View.GONE);
    }

    private void setupRestaurantRow() {
        for (int i = 0; i < RestaurantManager.Campus.values().length; i++) {

            String keyName = "is" + RestaurantManager.Campus.values()[i].name + "Shown";
            keyNames.add(keyName);
            keyNameAndShownName.put(keyName, "食堂(" + RestaurantManager.Campus.values()[i].name + ")");

            // create data for row title
            String[] names = new String[RestaurantManager.Campus.values()[i].restaurantList.size()];
            String[] fullLengthNames = new String[names.length];
            int[] subtitleHeights = new int[names.length];
            int[] subtitleColors = new int[names.length];
            int[][] times = new int[names.length][2];   // PeriodViewGroupMakerのため
            for (int j = 0; j < names.length; j++) {
                Restaurant restaurant = RestaurantManager.Campus.values()[i].restaurantList.get(j);
                names[j] = restaurant.name;
                fullLengthNames[j] = Utility.makeSnackbarText(restaurant.name, restaurant.times, false);
                subtitleHeights[j] = (int) utility.dpToPx(Constants.PERIOD_VIEW_HEIGHT);
                subtitleColors[j] = Constants.ROW_TITLE_DEFAULT_COLOR;
                times[j] = restaurant.times;
            }

            // create row title
            View rowTitleView = RowTitleMaker.make(
                    RestaurantManager.Campus.values()[i].name,
                    Constants.restaurantColors[i],
                    (int) utility.dpToPx(Constants.ROW_MAIN_TITLE_HEIGHT),
                    Constants.restaurantColors[i],
                    names,
                    fullLengthNames,
                    subtitleHeights,
                    subtitleColors,
                    Gravity.CENTER_VERTICAL
            );
            rowTitleContainer.addView(rowTitleView);
            rowTitleViewToShow.put(keyName, rowTitleView);

            // create row
            int[] periodViewColors = new int[names.length];
            Arrays.fill(periodViewColors, Constants.restaurantColors[i]);
            View rowView = PeriodViewGroupMaker.make(names, PeriodView.PeriodType.RESTAURANT, times, periodViewColors);
            rowContainer.addView(rowView);
            rowViewToShow.put(keyName, rowView);

            updateVisibility(keyName, sharedPreferences.getBoolean(keyName, true) ? View.VISIBLE : View.GONE);
        }
    }

    public void updateVisibility(String key, int visibility) {
        rowTitleViewToShow.get(key).setVisibility(visibility);
        rowViewToShow.get(key).setVisibility(visibility);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.current_time:
                currentTimeView.scrollToCurrentTime();
                return true;
            case R.id.show_hide:
                String[] multiChoiceItems = new String[keyNames.size()];
                boolean[] multiChoiceItemsSelected = new boolean[keyNames.size()];
                for (int i = 0; i < keyNames.size(); i++) {
                    multiChoiceItems[i] = keyNameAndShownName.get(keyNames.get(i));
                    multiChoiceItemsSelected[i] = sharedPreferences.getBoolean(keyNames.get(i), true);
                }
                new AlertDialog.Builder(this)
                        .setTitle("表示/非表示")
                        .setMultiChoiceItems(
                                multiChoiceItems,
                                multiChoiceItemsSelected,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        SharedPreferences.Editor editor =  sharedPreferences.edit();
                                        editor.putBoolean(keyNames.get(which), isChecked);
                                        editor.apply();
                                        updateVisibility(keyNames.get(which), isChecked ? View.VISIBLE : View.GONE);
                                    }
                                })
                        .setPositiveButton("CLOSE", null)
                        .show();
                return true;
            case R.id.edit_classwork:
                startActivityForResult(new Intent(getApplicationContext(), ClassworkListActivity.class), Constants.EDIT_CLASSWORK_REQUEST_CODE);
                return true;
            case R.id.reload:
                new AlertDialog.Builder(this)
                        .setTitle("再読み込みしますか？")
                        .setMessage("インターネットから情報を取得するので、少し時間がかかります。")
                        .setPositiveButton("RELOAD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDBHelper.clear();
                                MainActivity.this.recreate();
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .show();
                return true;
            case R.id.browser:
                new AlertDialog.Builder(this)
                        .setTitle("閲覧するページを選択")
                        .setItems(Constants.linkNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.linkUrls[which])));
                            }
                        })
                        .setPositiveButton("CLOSE", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.EDIT_CLASSWORK_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    this.recreate();
                }
                break;

            default:
                break;
        }

        // in case onResume() is not called
        RedrawTimer.updateInstance();
        RedrawTimer.redraw();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (RedrawTimer.getInstance() != null) {
            RedrawTimer.getInstance().cancel();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        RedrawTimer.updateInstance();
        RedrawTimer.redraw();
    }
}
