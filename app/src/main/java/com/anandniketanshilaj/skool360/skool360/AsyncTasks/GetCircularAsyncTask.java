package com.anandniketanshilaj.skool360.skool360.AsyncTasks;

import android.os.AsyncTask;

import com.anandniketanshilaj.skool360.skool360.Models.AnnouncementModel;
import com.anandniketanshilaj.skool360.skool360.Models.CircularModel;
import com.anandniketanshilaj.skool360.skool360.Utility.AppConfiguration;
import com.anandniketanshilaj.skool360.skool360.Utility.ParseJSON;
import com.anandniketanshilaj.skool360.skool360.WebServicesCall.WebServicesCall;

import java.util.ArrayList;
import java.util.HashMap;

public class GetCircularAsyncTask extends AsyncTask<Void, Void, ArrayList<CircularModel>> {
    HashMap<String, String> param = new HashMap<String, String>();

    public GetCircularAsyncTask(HashMap<String, String> param) {
        this.param = param;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
        protected ArrayList<CircularModel> doInBackground(Void... params) {
            String responseString = null;
            ArrayList<CircularModel> result = null;
            try {
                responseString = WebServicesCall.RunScript(AppConfiguration.getUrl(AppConfiguration.GetCircular), param);
                result = ParseJSON.parseCircularJson(responseString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<CircularModel> result) {
            super.onPostExecute(result);
        }
    }