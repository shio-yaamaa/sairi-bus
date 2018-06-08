package com.hatenadiary.yaamaa.sairibus;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class BusDataLoader extends AsyncTask<Boolean, Void, Boolean> {

    SQLiteDatabase db = BusManager.db;

    @Override
    public Boolean doInBackground(Boolean... args) {

        boolean isDataValid = args[0];
        boolean errorFlag = false;

        if (!isDataValid) {
            errorFlag = parseHtml();
        }

        createInstance();
        assignLane();

        return errorFlag;
    }

    @Override
    public void onPostExecute(Boolean errorFlag) {
        BusManager.activity.readyBus(errorFlag);
    }

    private boolean parseHtml() {

        Pattern splitPattern = Pattern.compile("[ 　.．]");

        Boolean errorFlag = false;

        int year = MainActivity.currentDate.get(Calendar.YEAR);
        int month = MainActivity.currentDate.get(Calendar.MONTH);
        int date = MainActivity.currentDate.get(Calendar.DATE);

        List<String> sectionList = new ArrayList<>();
        List<List<String>> directionList = new ArrayList<>();
        List<List<List<String>>> nameList = new ArrayList<>();
        List<List<List<String>>> timesList = new ArrayList<>();
        List<List<List<Boolean>>> twoBusesList = new ArrayList<>();
        List<List<List<Boolean>>> microDiseaseList = new ArrayList<>();

        try {

            Document doc = Jsoup.connect(Constants.BUS_URL).timeout(0).get();
            Element body = doc.body();

            // dateTableのclassを持つテーブルを取得
            Elements tables = body.getElementsByClass("dataTable");

            for (int i = 0; i < tables.size(); i++) {
                List<List<String>> nameListOfSection = new ArrayList<>();
                List<List<String>> timesListOfSection = new ArrayList<>();
                List<List<Boolean>> twoBusesListOfSection = new ArrayList<>();
                List<List<Boolean>> microDiseaseListOfSection = new ArrayList<>();

                // sectionを取得
                String fullSectionString = tables.get(i).previousElementSibling().text();
                String[] sectionStrings = splitPattern.split(fullSectionString);
                String section = "";
                for (String sectionString : sectionStrings) {
                    if (sectionString.contains("間") && section.equals("")) {
                        section = sectionString;
                    }
                }
                if (section.contains("地区間")) {
                    section = section.replace("地区間", "");
                }
                sectionList.add(section);

                // すべてのTRをbusTableRowsに格納し、そのうちヘッダーのみをbusTableRowsから取り出してbusHeaderRowに格納
                Elements busTableRows = tables.get(i).child(0).children();
                Element busHeaderRow = busTableRows.remove(0);

                // directionの数(directionCount)や、便名カラムの位置(nameColumnIndices)、それぞれのdirectionのtimesカラムの数(timesColumnCounts)を取得
                Elements busNameTableData = busTableRows.get(0).getElementsByClass("tableName");
                int directionCount = busNameTableData.size();
                int columnCount = busHeaderRow.children().size();
                int[] nameColumnIndices = new int[directionCount];
                for (int j = 0; j < directionCount; j++) {
                    nameColumnIndices[j] = busNameTableData.get(j).elementSiblingIndex();
                }
                int[] timesColumnCounts = new int[directionCount];
                for (int j = 0; j < directionCount; j++) {
                    if (j < directionCount - 1) {
                        timesColumnCounts[j] = nameColumnIndices[j + 1] - nameColumnIndices[j] - 1;
                    } else {
                        timesColumnCounts[j] = columnCount - nameColumnIndices[j] - 1;
                    }
                }

                // directionを取得
                Elements busHeaderTableData = busHeaderRow.children();
                String[] busHeaderTexts = new String[busHeaderTableData.size()];
                for (int j = 0; j < busHeaderTexts.length; j++) {
                    busHeaderTexts[j] = busHeaderTableData.get(j).text().replace(" ", "");
                }
                String[] directions = new String[directionCount];
                for (int j = 0; j < directionCount; j++) {
                    String[] directionTexts = new String[timesColumnCounts[j]];
                    System.arraycopy(busHeaderTexts, nameColumnIndices[j] + 1, directionTexts, 0, timesColumnCounts[j]);
                    directions[j] = TextUtils.join(",", directionTexts);
                }
                directionList.add(new ArrayList<>(Arrays.asList(directions)));

                // directionごとに受け皿を用意
                for (int j = 0; j < directionCount; j++) {
                    nameListOfSection.add(new ArrayList<String>());
                    timesListOfSection.add(new ArrayList<String>());
                    twoBusesListOfSection.add(new ArrayList<Boolean>());
                    microDiseaseListOfSection.add(new ArrayList<Boolean>());
                }

                for (int j = 0; j < busTableRows.size(); j++) {
                    Elements tableData = busTableRows.get(j).children();

                    for (int k = 0; k < directionCount; k++) {
                        String name = tableData.get(nameColumnIndices[k]).text().replace(" ", "");
                        if (Pattern.matches(".*[0-9].*", name)) {
                            // nameを取得
                            microDiseaseListOfSection.get(k).add(name.contains("※"));
                            nameListOfSection.get(k).add(Utility.normalize(name.replace("※", "")));

                            // timesを取得
                            String[] timesArray = new String[timesColumnCounts[k]];
                            Boolean twoBuses = false;
                            for (int l = 0; l < timesColumnCounts[k]; l++) {
                                String timeText = tableData.get(nameColumnIndices[k] + l + 1).text();
                                if (timeText.contains("*")) {
                                    twoBuses = true;
                                }
                                timesArray[l] = String.valueOf(Utility.convertTime(timeText.replace("*", "")));
                            }
                            String times = TextUtils.join(",", timesArray);
                            timesListOfSection.get(k).add(times);
                            twoBusesListOfSection.get(k).add(twoBuses);
                        }
                    }
                }

                nameList.add(nameListOfSection);
                timesList.add(timesListOfSection);
                twoBusesList.add(twoBusesListOfSection);
                microDiseaseList.add(microDiseaseListOfSection);

            }

        } catch (Exception e) {
            errorFlag = true;
            e.printStackTrace();
        }

        if (!errorFlag) {
            db.beginTransaction();
            db.delete(SQLiteDBHelper.TABLE_BUSES, null, null);

            // insert data into database
            for (int i = 0; i < sectionList.size(); i++) {
                String section = sectionList.get(i);
                for (int j = 0; j < directionList.get(i).size(); j++) {
                    String direction = directionList.get(i).get(j);
                    for (int k = 0; k < nameList.get(i).get(j).size(); k++) {
                        String name = nameList.get(i).get(j).get(k);
                        String times = timesList.get(i).get(j).get(k);
                        Boolean twoBuses = twoBusesList.get(i).get(j).get(k);
                        Boolean microDisease = microDiseaseList.get(i).get(j).get(k);

                        ContentValues contentValues = new ContentValues();
                        contentValues.put("name", name);
                        contentValues.put("year", year);
                        contentValues.put("month", month);
                        contentValues.put("date", date);
                        contentValues.put("section", section);
                        contentValues.put("direction", direction);
                        contentValues.put("times", times);
                        contentValues.put("two_buses", twoBuses ? 1 : 0);
                        contentValues.put("micro_disease", microDisease ? 1 : 0);
                        contentValues.put("lane", -1);

                        db.insert(SQLiteDBHelper.TABLE_BUSES, null, contentValues);
                    }
                }
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        }

        return errorFlag;
    }

    private void createInstance() {
        Cursor cursor = db.query(SQLiteDBHelper.TABLE_BUSES, null, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {

            // create Bus instance from database
            do {
                // add to sectionList
                String section = cursor.getString(cursor.getColumnIndex("section"));
                int sectionIndex = BusManager.sectionList.indexOf(section);
                if (sectionIndex == -1) {
                    BusManager.sectionList.add(section);
                    sectionIndex = BusManager.sectionList.size() - 1;
                    BusManager.directionList.add(new ArrayList<ArrayList<String>>());
                }

                // add to directionList
                int directionIndex = -1;
                String[] locations = cursor.getString(cursor.getColumnIndex("direction")).split(",");
                for (int i = 0; i < BusManager.directionList.get(sectionIndex).size(); i++) {
                    if (Utility.isSameDirection(BusManager.directionList.get(sectionIndex).get(i), locations)) {
                        directionIndex = i;
                        break;
                    }
                }
                if (directionIndex == -1) {
                    BusManager.directionList.get(sectionIndex).add(new ArrayList<>(Arrays.asList(locations)));
                    directionIndex = BusManager.directionList.get(sectionIndex).size() - 1;
                }

                // make times
                String[] stringTimes = cursor.getString(cursor.getColumnIndex("times")).split(",");
                int times[] = new int[stringTimes.length];
                for (int i = 0; i < stringTimes.length; i++) {
                    times[i] = Integer.parseInt(stringTimes[i]);
                }

                new Bus(
                        cursor.getLong(cursor.getColumnIndex("_id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        sectionIndex,
                        directionIndex,
                        times,
                        cursor.getInt(cursor.getColumnIndex("two_buses")) == 1,
                        cursor.getInt(cursor.getColumnIndex("micro_disease")) == 1,
                        cursor.getInt(cursor.getColumnIndex("lane"))
                );

            } while (cursor.moveToNext());

        }

        cursor.close();
    }

    private void assignLane() {

        // return if the lane is already assigned
        if (BusManager.busList.size() == 0 || BusManager.busList.get(0).lane != -1) {
            return;
        }

        List<Bus> busListOfSameDirection = new ArrayList<>();
        Bus busOfSameLane;
        int difference;
        for (int section = 0; section < BusManager.sectionList.size(); section++) {
            for (int direction = 0; direction < BusManager.directionList.get(section).size(); direction++) {
                busListOfSameDirection.clear();

                // collect Bus instances of same section & direction
                for (Bus bus : BusManager.busList) {
                    if (bus.section == section && bus.direction == direction) {
                        busListOfSameDirection.add(bus);
                    }
                }

                // assign lane numbers to busListOfSameDirection
                for (Bus busToAssignLane : busListOfSameDirection) {
                    for (int lane = 0; true; lane++) {
                        busOfSameLane = null;

                        // search busServiceOfSameLane(the last BusService of the same lane)
                        for (int i = busListOfSameDirection.indexOf(busToAssignLane) - 1; i >= 0; i--) {
                            if (busListOfSameDirection.get(i).lane == lane) {
                                busOfSameLane = busListOfSameDirection.get(i);
                                break;
                            }
                        }

                        if (busOfSameLane == null) {
                            busToAssignLane.lane = lane;
                            break;
                        } else {
                            difference = busToAssignLane.times[0] - busOfSameLane.times[busOfSameLane.times.length - 1];
                            if (difference >= Constants.BUS_MIN_MARGIN) {
                                busToAssignLane.lane = lane;
                                break;
                            }
                        }
                    }
                }
            }
        }

        // save the lane into the database
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();

        for (Bus bus : BusManager.busList) {
            contentValues.put("lane", bus.lane);
            db.update(SQLiteDBHelper.TABLE_BUSES, contentValues, "_id = " + bus.id, null);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

}