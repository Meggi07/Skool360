package com.anandniketanshilaj.skool360.skool360.AsyncTasks;

import android.os.AsyncTask;

import com.anandniketanshilaj.skool360.skool360.Adapter.ExpandableListAdapter;
import com.anandniketanshilaj.skool360.skool360.Models.CanteenModel;
import com.anandniketanshilaj.skool360.skool360.Utility.AppConfiguration;
import com.anandniketanshilaj.skool360.skool360.Utility.ParseJSON;
import com.anandniketanshilaj.skool360.skool360.WebServicesCall.WebServicesCall;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddDeviceDetailAsyncTask extends AsyncTask<Void, Void, Boolean> {
    HashMap<String, String> param = new HashMap<String, String>();

    public AddDeviceDetailAsyncTask(HashMap<String, String> param) {
        this.param = param;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String responseString = null;
        boolean success = false;
        try {
            responseString = WebServicesCall.RunScript(AppConfiguration.getUrl(AppConfiguration.AddDeviceDetail), param);
            success = parseJson(responseString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }

    public boolean parseJson(String responseString) {
        try {
            JSONObject reader = new JSONObject(responseString);
            String readerString = reader.getString("Success");
            if (readerString.equalsIgnoreCase("True")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}