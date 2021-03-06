package com.anandniketanshilaj.skool360.skool360.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.anandniketanshilaj.skool360.R;
import com.anandniketanshilaj.skool360.skool360.Activities.DashBoardActivity;
import com.anandniketanshilaj.skool360.skool360.Adapter.ExpandableListAdapterCircular;
import com.anandniketanshilaj.skool360.skool360.AsyncTasks.GetCircularAsyncTask;
import com.anandniketanshilaj.skool360.skool360.Models.CircularModel;
import com.anandniketanshilaj.skool360.skool360.Models.PaymentLedgerModel;
import com.anandniketanshilaj.skool360.skool360.Utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Harsh on 04-Aug-16.
 */
public class CircularFragment extends Fragment {
    private View rootView;
    private Button btnMenu, btnBackCircular;
    private ExpandableListView listCircular;
    private TextView txtNoRecordsCircular;
    private Context mContext;
    private GetCircularAsyncTask getCircularAsyncTask = null;
    private ExpandableListAdapterCircular circularListAdapter = null;
    private ProgressDialog progressDialog = null;
    private ArrayList<CircularModel> circularModels = new ArrayList<>();
    ArrayList<String> listDataHeader;
    HashMap<String, ArrayList<CircularModel>> listDataChildCircular;
    private int lastExpandedPosition = -1;
    public CircularFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.circular_fragment, container, false);
        mContext = getActivity();

        initViews();
        setListners();
        getCircularData();

        return rootView;
    }

    public void initViews() {
        btnMenu = (Button) rootView.findViewById(R.id.btnMenu);
        txtNoRecordsCircular = (TextView) rootView.findViewById(R.id.txtNoRecordsCircular);
        btnBackCircular = (Button) rootView.findViewById(R.id.btnBackCircular);
        listCircular = (ExpandableListView) rootView.findViewById(R.id.listCircular);

    }

    public void setListners() {

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashBoardActivity.onLeft();
            }
        });

        listCircular.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    listCircular.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
        btnBackCircular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.frame_container, fragment).commit();
            }
        });

    }

    public void getCircularData(){
        if(Utility.isNetworkConnected(mContext)) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HashMap<String, String> params = new HashMap<String, String>();
                        getCircularAsyncTask = new GetCircularAsyncTask(params);
                        circularModels = getCircularAsyncTask.execute().get();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                if (circularModels.size() > 0) {
                                    txtNoRecordsCircular.setVisibility(View.GONE);
                                    prepaareList();
                                    circularListAdapter = new ExpandableListAdapterCircular(getActivity(),listDataHeader,listDataChildCircular);
                                    listCircular.setAdapter(circularListAdapter);

                                } else {
                                    progressDialog.dismiss();
                                    txtNoRecordsCircular.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else{
            Utility.ping(mContext,"Network not avialable");
        }
    }
    public void prepaareList() {
        listDataHeader = new ArrayList<>();
        listDataChildCircular = new HashMap<String, ArrayList<CircularModel>>();

        for (int i = 0; i < circularModels.size(); i++) {
            Circulardemo cdemo = new Circulardemo();
            cdemo.Date = circularModels.get(i).getDate().toString();
            cdemo.Subject = circularModels.get(i).getSubject().toString();
            listDataHeader.add(cdemo.Subject.toString() + "|" + cdemo.Date);
            Log.d("displaypositiondata", listDataHeader.get(0));

            ArrayList<CircularModel> rows = new ArrayList<CircularModel>();
//            for (int j = 0; j < circularModels.size(); j++) {
                rows.add(circularModels.get(i));

//            }
            listDataChildCircular.put(listDataHeader.get(i), rows);
        }
    }

    public class Circulardemo {
        private String Date;
        private String Subject;
    }
}
