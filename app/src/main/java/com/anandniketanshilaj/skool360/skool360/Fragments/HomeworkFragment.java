package com.anandniketanshilaj.skool360.skool360.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.anandniketanshilaj.skool360.R;
import com.anandniketanshilaj.skool360.skool360.Activities.DashBoardActivity;
import com.anandniketanshilaj.skool360.skool360.Adapter.ExpandableListAdapterHomework;
import com.anandniketanshilaj.skool360.skool360.AsyncTasks.GetStudHomeworkAsyncTask;
import com.anandniketanshilaj.skool360.skool360.Models.HomeWorkResponse.FinalArrayHomeWork;
import com.anandniketanshilaj.skool360.skool360.Models.HomeWorkResponse.GetHomeWorkModel;
import com.anandniketanshilaj.skool360.skool360.Models.HomeWorkResponse.HomeWorkInfo;
import com.anandniketanshilaj.skool360.skool360.Utility.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Harsh on 04-Aug-16.
 */
public class HomeworkFragment extends Fragment {
    private View rootView;
    private Button btnMenu, btnFilterHomework, btnBackHomework;
    private static TextView fromDate, toDate;
    private TextView txtNoRecordsHomework;
    private static String dateFinal;
    private Context mContext;
    private GetStudHomeworkAsyncTask getStudHomeworkAsyncTask = null;
    GetHomeWorkModel getHomeWorkModelResponse;
    private ProgressDialog progressDialog = null;
    private static boolean isFromDate = false;
    private int lastExpandedPosition = -1;

    ExpandableListAdapterHomework listAdapter;
    ExpandableListView lvExpHomework;
    List<String> listDataHeader;
    HashMap<String, ArrayList<HomeWorkInfo>> listDataChild;


    public HomeworkFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.homework_fragment, container, false);
        mContext = getActivity();

        initViews();
        setListners();

        return rootView;
    }

    public void initViews() {
        btnMenu = (Button) rootView.findViewById(R.id.btnMenu);
        fromDate = (TextView) rootView.findViewById(R.id.fromDate);
        toDate = (TextView) rootView.findViewById(R.id.toDate);
        btnFilterHomework = (Button) rootView.findViewById(R.id.btnFilterHomework);
        txtNoRecordsHomework = (TextView) rootView.findViewById(R.id.txtNoRecordsHomework);
        btnBackHomework = (Button) rootView.findViewById(R.id.btnBackHomework);
        lvExpHomework = (ExpandableListView) rootView.findViewById(R.id.lvExpHomework);


        //load today's data first
        fromDate.setText(Utility.getTodaysDate());
        toDate.setText(Utility.getTodaysDate());
        getHomeworkData(fromDate.getText().toString(), toDate.getText().toString());
    }

    public void setListners() {
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashBoardActivity.onLeft();
            }
        });

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromDate = true;
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromDate = false;
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        btnFilterHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fromDate.getText().toString().equalsIgnoreCase("")) {
                    if (!toDate.getText().toString().equalsIgnoreCase("")) {

                        getHomeworkData(fromDate.getText().toString(), toDate.getText().toString());

                    } else {
                        Utility.pong(mContext, "You need to select a to date");
                    }
                } else {
                    Utility.pong(mContext, "You need to select a from date");
                }
            }
        });

        btnBackHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.frame_container, fragment).commit();
            }
        });
        lvExpHomework.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    lvExpHomework.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    public void getHomeworkData(final String fromDate, final String toDate) {
        if (Utility.isNetworkConnected(mContext)) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("StudentID", Utility.getPref(mContext, "studid"));//
                        params.put("HomeWorkFromDate", fromDate);
                        params.put("HomeWorkToDate", toDate);

                        getStudHomeworkAsyncTask = new GetStudHomeworkAsyncTask(params);
                        getHomeWorkModelResponse = getStudHomeworkAsyncTask.execute().get();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                if (getHomeWorkModelResponse.getFinalArray().size() > 0) {
                                    txtNoRecordsHomework.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                    prepaareList();
                                    listAdapter = new ExpandableListAdapterHomework(getActivity(), listDataHeader, listDataChild);
                                    lvExpHomework.setAdapter(listAdapter);
                                } else {
                                    progressDialog.dismiss();
                                    txtNoRecordsHomework.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            Utility.ping(mContext, "Network not available");
        }
    }

    public void prepaareList() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<String, ArrayList<HomeWorkInfo>>();

        for (int i = 0; i < getHomeWorkModelResponse.getFinalArray().size(); i++) {
            FinalArrayHomeWork date = getHomeWorkModelResponse.getFinalArray().get(i);
            String header = date.getHomeWorkDate();
            listDataHeader.add(header);
            listDataChild.put(header,date.getData());
        }
    }


    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }

        public void populateSetDate(int year, int month, int day) {
            String d, m, y;
            d = Integer.toString(day);
            m = Integer.toString(month);
            y = Integer.toString(year);

            if (day < 10) {
                d = "0" + d;
            }
            if (month < 10) {
                m = "0" + m;
            }
            dateFinal = d + "/" + m + "/" + y;

            if (isFromDate) {
                fromDate.setText(dateFinal);
            } else {
                toDate.setText(dateFinal);
            }
        }
    }
}
