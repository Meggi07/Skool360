package com.anandniketanshilaj.skool360.skool360.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.anandniketanshilaj.skool360.R;
import com.anandniketanshilaj.skool360.skool360.Activities.DashBoardActivity;
import com.anandniketanshilaj.skool360.skool360.Adapter.ExpandableListAdapterResult;
import com.anandniketanshilaj.skool360.skool360.AsyncTasks.GetStudentResultAsyncTask;
import com.anandniketanshilaj.skool360.skool360.Models.ResultModel;
import com.anandniketanshilaj.skool360.skool360.Utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ResultFragment extends Fragment {
    private View rootView;
    private Button btnMenu, btnBackUnitTest;
    private TextView txtNoRecordsUnitTest;
    private Context mContext;
    private ProgressDialog progressDialog = null;
    private GetStudentResultAsyncTask getStudentResultAsyncTask = null;
    private ArrayList<ResultModel> resultModels = new ArrayList<>();
    private int lastExpandedPosition = -1;

    ExpandableListAdapterResult expandableListAdapterResult;
    ExpandableListView lvExpResult;
    List<String> listDataHeader;
    HashMap<String, ArrayList<ResultModel.Data>> listDataChild;


    public ResultFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_result, container, false);
        mContext = getActivity();

        initViews();
        setListners();

        return rootView;
    }

    public void initViews() {
        btnMenu = (Button) rootView.findViewById(R.id.btnMenu);
        txtNoRecordsUnitTest = (TextView) rootView.findViewById(R.id.txtNoRecordsUnitTest);
        btnBackUnitTest = (Button) rootView.findViewById(R.id.btnBackUnitTest);
        lvExpResult = (ExpandableListView) rootView.findViewById(R.id.lvExpResult);

        getUnitTestData();
    }

    public void setListners() {
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashBoardActivity.onLeft();
            }
        });

        btnBackUnitTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.frame_container, fragment).commit();
            }
        });
        lvExpResult.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    lvExpResult.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    public void getUnitTestData() {
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
                        params.put("StudentID", Utility.getPref(mContext, "studid"));
                        params.put("TermID", "4"); //Utility.getPref(mContext, "TermID")
                        getStudentResultAsyncTask = new GetStudentResultAsyncTask(params);
                        resultModels = getStudentResultAsyncTask.execute().get();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (resultModels.size() > 0) {
                                    txtNoRecordsUnitTest.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                    prepaareList();
                                    expandableListAdapterResult = new ExpandableListAdapterResult(getActivity(), listDataHeader, listDataChild);
                                    lvExpResult.setAdapter(expandableListAdapterResult);
                                } else {
                                    progressDialog.dismiss();
                                    txtNoRecordsUnitTest.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else{
            Utility.ping(mContext,"Network not available");
        }
    }

    public void prepaareList() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, ArrayList<ResultModel.Data>>();

        for (int i = 0; i < resultModels.size(); i++) {
            listDataHeader.add(resultModels.get(i).getTestName() + "|" + resultModels.get(i).getTotalMarksGained() + "|" +
                    resultModels.get(i).getTotal_Marks() + "|" + resultModels.get(i).getTotalPercentage());

            ArrayList<ResultModel.Data> rows = new ArrayList<ResultModel.Data>();
            for (int j = 0; j < resultModels.get(i).getDataArrayList().size(); j++) {
                rows.add(resultModels.get(i).getDataArrayList().get(j));

            }
            listDataChild.put(listDataHeader.get(i), rows);
        }
    }
}
