package com.example.athulk.hajarpattika;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class UpdateLoader extends AsyncTaskLoader<UpdateInfo>{
    private String mUrl;

    public UpdateLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public UpdateInfo loadInBackground() {
        if (mUrl == null){
            return null;
        }
        UpdateInfo updateInfo = UpdateQuery.fetchData(mUrl);
        return updateInfo;
    }
}
