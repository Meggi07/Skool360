package com.anandniketanshilaj.skool360.skool360.AsyncTasks;

import android.os.AsyncTask;

import com.anandniketanshilaj.skool360.skool360.Interfacess.OnCompletionListner;
import com.anandniketanshilaj.skool360.skool360.Models.DeviceVersionModel;
import com.anandniketanshilaj.skool360.skool360.Models.PTMTeacherResponse.PTMStudentWiseTeacher;
import com.anandniketanshilaj.skool360.skool360.Utility.AppConfiguration;
import com.anandniketanshilaj.skool360.skool360.Utility.ParseJSON;
import com.anandniketanshilaj.skool360.skool360.WebServicesCall.WebServicesCall;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by admsandroid on 11/2/2017.
 */

public class DeviceVersionAsyncTask extends AsyncTask<Void, Void, DeviceVersionModel> {
    HashMap<String, String> param = new HashMap<String, String>();

    public DeviceVersionAsyncTask(HashMap<String, String> param) {
        this.param = param;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected DeviceVersionModel doInBackground(Void... params) {
        String responseString = null;
        DeviceVersionModel deviceVersionModel = null;
        try {
            responseString = WebServicesCall.RunScript(AppConfiguration.getUrl(AppConfiguration.DeviceVersion), param);
            Gson gson = new Gson();
            deviceVersionModel = gson.fromJson(responseString, DeviceVersionModel.class);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return deviceVersionModel;
    }

    @Override
    protected void onPostExecute(DeviceVersionModel result) {
        super.onPostExecute(result);
    }
}
