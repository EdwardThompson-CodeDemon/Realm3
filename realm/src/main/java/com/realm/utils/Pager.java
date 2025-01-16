package com.realm.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.realm.Models.Query;
import com.realm.Realm;
import com.realm.Services.DatabaseManager;

public class Pager<RM> {
    public interface PagerCallback<RM> {
        default void onDataRefreshed(ArrayList<RM> data, int from, int to, int total) {

        }
    }

    Class<RM> realm_model;
    int pageSize;
    PagerCallback pagerCallback;
    String[] searchFields;
    ArrayList<RM> allRecords = new ArrayList<>();
    int pagerEventId;

    public Pager(Class<RM> realm_model, int pagerEventId, int pageSize, PagerCallback pagerCallback, String... searchFields) {
        this.realm_model = realm_model;
        this.pagerEventId = pagerEventId;
        this.pageSize = pageSize;
        this.pagerCallback = pagerCallback;
        this.searchFields = searchFields;

    }

    View prev, next;
    EditText search_text;
    TextView position_indicator;
    ProgressBar loading_bar;

    //    public Pager(Class<RM> realm_model, int pagerEventId, int pageSize, PagerCallback pagerCallback, EditText searchTextInput, View prev, View next, TextView positionIndicator, ProgressBar loadingBar, String... searchFields) {
//    public Pager(Class<RM> realm_model, int pagerEventId, int pageSize, PagerCallback pagerCallback, EditText searchTextInput, View prev, View next, TextView positionIndicator, ProgressBar loadingBar, String customQuery, String[] tableFilters, String... searchFields) {
    public Pager(Class<RM> realm_model, int pagerEventId, int pageSize, PagerCallback pagerCallback, EditText searchTextInput, View prev, View next, TextView positionIndicator, ProgressBar loadingBar, String customQuery, String[] tableFilters, String[] columns, String... searchFields) {
        this.realm_model = realm_model;
        this.pagerEventId = pagerEventId;
        this.pageSize = pageSize;
        this.pagerCallback = pagerCallback;
        this.prev = prev;
        this.next = next;
        this.search_text = searchTextInput;
        this.position_indicator = positionIndicator;
        this.loading_bar = loadingBar;
        this.customQuery = customQuery;
        this.tableFilters = tableFilters;
        this.columns = columns;
        this.searchFields = searchFields;
        initUi();

    }

    String searchTerm = "";

    public void search(String searchTerm) {
        this.searchTerm = searchTerm;
        reset_list();
    }

    public void next() {
        offset = ((pageSize + offset) < total) ? offset + pageSize : offset;
        reset_list();
    }

    public void previous() {
        offset = (offset != 0) ? offset - pageSize : offset;
        reset_list();
    }


    void initUi() {


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                offset = 0;
                pageSize = 100;
                search(search_text.getText().toString());

            }
        });
        reset_list();

    }


    public static int search_counter = 0;

    Thread search_thread;
    int offset = 0, total = 0;


    public void setFilters(LinkedHashMap<String, Boolean> orderFilters,String... tableFilters) {
        this.orderFilters = orderFilters == null ? new LinkedHashMap<>() : orderFilters;
        this.tableFilters = tableFilters == null ? new String[0] : tableFilters;
        reset_list();
    }
    LinkedHashMap<String, Boolean> orderFilters = new LinkedHashMap<>();
    public void setOrderFilters(LinkedHashMap<String, Boolean> orderFilters) {
        this.orderFilters = orderFilters == null ? new LinkedHashMap<>() : orderFilters;
        reset_list();
    }

    String[] tableFilters = new String[0];

    public void setTableFilters(String... tableFilters) {
        this.tableFilters = tableFilters == null ? new String[0] : tableFilters;
        reset_list();
    }

    String customQuery;

    public void setCustomQuery(String customQuery) {
        this.customQuery = customQuery;
        reset_list();
    }

    String[] columns;

    public void setColumns(String[] columns) {
        this.columns = columns;
        reset_list();
    }

    void reset_list() {
        String search_tearm = searchTerm;
        search_tearm = search_tearm == null ? "" : search_tearm.toUpperCase();
        loading_bar.setVisibility(View.VISIBLE);
        search_counter++;
        DatabaseManager.pagerEventMap.put(pagerEventId, search_counter);
        String finalSearch_tearm = search_tearm;
        search_thread = new Thread(() -> {

            int int_counter = search_counter;
            Query query=new Query()
                    .setColumns(columns)
                    .setCustomQuery(customQuery)
                    .setTableFilters(DatabaseManager.concatenate(tableFilters, new String[]{conccat_sql_or_like_filters(searchFields, finalSearch_tearm)}))
                    .setOffset(offset)
                    .setLimit(pageSize);
            if(orderFilters.isEmpty()){
                allRecords = Realm.databaseManager.loadObjectArray(realm_model, pagerEventId, search_counter, query);
            }else{
                for(Map.Entry<String,Boolean> entry:orderFilters.entrySet()){
                    query=query.addOrderFilters(entry.getKey(), entry.getValue());
                }
                allRecords = Realm.databaseManager.loadObjectArray(realm_model, pagerEventId, search_counter,query);
            }

            if (search_counter == int_counter && allRecords != null) {
                total = Realm.databaseManager.getRecordCount(realm_model, new Query()
                        .setColumns(columns)
                        .setCustomQuery(customQuery)
                        .setTableFilters(DatabaseManager.concatenate(tableFilters, new String[]{conccat_sql_or_like_filters(searchFields, finalSearch_tearm)})));


                loading_bar.post(() -> {

                    try {
                        pagerCallback.onDataRefreshed(allRecords, offset, offset + allRecords.size(), total);
                        position_indicator.setText(offset + " - " + (offset + allRecords.size()) + " of " + total);
                        if(total<pageSize){
                            prev.setVisibility(View.GONE);
                            position_indicator.setVisibility(View.GONE);
                            next.setVisibility(View.GONE);
                        }else{
                            prev.setVisibility(View.VISIBLE);
                            position_indicator.setVisibility(View.VISIBLE);
                            next.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception ex) {

                    }

                    loading_bar.setVisibility(View.GONE);

                });
                Runtime.getRuntime().gc();

            }

        });

        search_thread.start();
    }


    public String conccat_sql_or_filters(String[] str_to_join, String searchTerm) {
        String result = "";
        for (int i = 0; i < str_to_join.length; i++) {
            result = result + (i == 0 ? "WHERE " : " OR ") + str_to_join[i] + "='" + searchTerm + "'";
        }
        return result;

    }

    public String conccat_sql_or_like_filters(String[] str_to_join, String searchTerm) {
        String result = "";
        for (int i = 0; i < str_to_join.length; i++) {
            result = result + (i == 0 ? " UPPER(" : " OR UPPER(") + str_to_join[i] + ") LIKE '%" + searchTerm + "%'";
        }
        return result;

    }


}
