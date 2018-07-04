package com.cavaliers.mylocalbartender.home.search.helper;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by DrenediesInk on 01/03/2017.
 */

public class HistoryProvider  extends SearchRecentSuggestionsProvider{
    public final static String AUTHORITY = "com.cavaliers.mylocalbartender.home.search.helper.HistoryProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public HistoryProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}