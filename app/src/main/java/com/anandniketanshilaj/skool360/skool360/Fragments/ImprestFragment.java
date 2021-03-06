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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.anandniketanshilaj.skool360.R;
import com.anandniketanshilaj.skool360.skool360.Activities.DashBoardActivity;
import com.anandniketanshilaj.skool360.skool360.Adapter.ImprestListAdapter;
import com.anandniketanshilaj.skool360.skool360.AsyncTasks.GetImprestDataAsyncTask;
import com.anandniketanshilaj.skool360.skool360.AsyncTasks.GetTermAsyncTask;
import com.anandniketanshilaj.skool360.skool360.Models.ImprestDataModel;
import com.anandniketanshilaj.skool360.skool360.Models.ImprestResponse.Datum;
import com.anandniketanshilaj.skool360.skool360.Models.ImprestResponse.GetImprestDataModel;
import com.anandniketanshilaj.skool360.skool360.Models.TermModel;
import com.anandniketanshilaj.skool360.skool360.Utility.Utility;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Harsh on 04-Aug-16.
 */
public class ImprestFragment extends Fragment {
    private View rootView;
    private Button btnMenu, btnBackImprest;
    private TextView txtMyBalance, txtOpeningBalaceTop, txtNoRecordsImprest;
    private Spinner spinYear;
    private TableRow tblRowBalance, tblRowOpeningBalance;
    private ExpandableListView listImprestData;
    //    private LinearLayout llListTitle;
    private Context mContext;
    private GetTermAsyncTask getTermAsyncTask = null;
    private GetImprestDataAsyncTask getImprestDataAsyncTask = null;
    private ArrayList<TermModel> termModels = new ArrayList<>();
    GetImprestDataModel getImprestResponse;
    private ImprestListAdapter imprestListAdapter = null;
    private ProgressDialog progressDialog = null;
    List<String> listDataHeader;
    HashMap<String, ArrayList<Datum>> listDataChild;
    private int lastExpandedPosition = -1;
    public ImprestFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.imprest_fragment, container, false);
        mContext = getActivity();

        initViews();
        setListners();
        fillspinYear();

        return rootView;
    }

    public void initViews() {
        btnMenu = (Button) rootView.findViewById(R.id.btnMenu);
        btnBackImprest = (Button) rootView.findViewById(R.id.btnBackImprest);
        spinYear = (Spinner) rootView.findViewById(R.id.spinYear);
        tblRowBalance = (TableRow) rootView.findViewById(R.id.tblRowBalance);
        tblRowOpeningBalance = (TableRow) rootView.findViewById(R.id.tblRowOpeningBalance);
        txtMyBalance = (TextView) rootView.findViewById(R.id.txtMyBalance);
        txtOpeningBalaceTop = (TextView) rootView.findViewById(R.id.txtOpeningBalaceTop);
        txtNoRecordsImprest = (TextView) rootView.findViewById(R.id.txtNoRecordsImprest);
        listImprestData = (ExpandableListView) rootView.findViewById(R.id.listImprestData);
//        llListTitle = (LinearLayout) rootView.findViewById(R.id.llListTitle);
    }

    public void setListners() {
        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getImprestData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashBoardActivity.onLeft();
            }
        });

        btnBackImprest.setOnClickListener(new View.OnClickListener() {
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

    public void fillspinYear() {
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> params = new HashMap<String, String>();
                    getTermAsyncTask = new GetTermAsyncTask(params);
                    termModels = getTermAsyncTask.execute().get();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (termModels.size() > 0) {
                                ArrayList<String> termText = new ArrayList<String>();
                                for (int i = 0; i < termModels.size(); i++) {
                                    termText.add(termModels.get(i).getTerm());
                                }
                                final Calendar calendar = Calendar.getInstance();
                                int yy = calendar.get(Calendar.YEAR);
                                int mm = calendar.get(Calendar.MONTH) + 1;
                                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                                int yy1 =calendar.get(Calendar.YEAR)-1;

                                        System.out.print("year:" + yy);
                                String year, yearnotmatch;
                                year = String.valueOf(yy);
                                yearnotmatch=String.valueOf(yy1);
                                Collections.sort(termText);
                                System.out.println("Sorted ArrayList in Java - Ascending order : " + termText);
                                try {
                                    Field popup = Spinner.class.getDeclaredField("mPopup");
                                    popup.setAccessible(true);

                                    // Get private mPopup member variable and try cast to ListPopupWindow
                                    android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinYear);

                                    popupWindow.setHeight(termText.size() > 5 ? 500 : termText.size() * 100);
                                } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
                                    // silently fail...
                                }
                                ArrayAdapter<String> adapterSpinYear = new ArrayAdapter<String>(mContext, R.layout.spinner_layout, termText);
                                spinYear.setAdapter(adapterSpinYear);
                                for (int m = 0; m < termText.size(); m++) {
                                    String[] str = termText.get(m).split("\\-");
                                    if (year.equalsIgnoreCase(str[0])) {
                                        Log.d("yearValue", termText.get(m));
                                        int index = m;
                                        Log.d("indexOf", String.valueOf(index));
                                        spinYear.setSelection(index);

                                    } else if(yearnotmatch.equalsIgnoreCase(str[0])) {
                                        Log.d("yearValue", termText.get(m));
                                        int index = m;
                                        Log.d("indexOf", String.valueOf(index));
                                        spinYear.setSelection(index);
                                    }
                                }
                            } else {
                                progressDialog.dismiss();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void getImprestData() {
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
                        String text = spinYear.getSelectedItem().toString();
                        String id = "";
                        for (int i = 0; i < termModels.size(); i++) {
                            if (text.equals(termModels.get(i).getTerm())) {
                                id = termModels.get(i).getTermId();
                            }
                        }
                        params.put("Term", id);
                        params.put("StudentID", Utility.getPref(mContext, "studid"));
                        getImprestDataAsyncTask = new GetImprestDataAsyncTask(params);
                        getImprestResponse = getImprestDataAsyncTask.execute().get();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                if (getImprestResponse.getFinalArray().size() > 0) {
                                    txtNoRecordsImprest.setVisibility(View.GONE);
                                    tblRowBalance.setVisibility(View.VISIBLE);
                                    tblRowOpeningBalance.setVisibility(View.VISIBLE);
                                    txtMyBalance.setText(getImprestResponse.getMyBalance());
                                    txtOpeningBalaceTop.setText(getImprestResponse.getOpeningBalance());
                                    listImprestData.setVisibility(View.VISIBLE);
                                    if (getImprestResponse.getFinalArray().size() > 0 && getImprestResponse.getMyBalance() != null) {
                                        prepaareList();
                                        imprestListAdapter = new ImprestListAdapter(mContext, listDataHeader, listDataChild);
                                        listImprestData.setAdapter(imprestListAdapter);
                                    }
                                } else {
                                    progressDialog.dismiss();
                                    tblRowBalance.setVisibility(View.GONE);
                                    tblRowOpeningBalance.setVisibility(View.GONE);
                                    txtNoRecordsImprest.setVisibility(View.VISIBLE);
                                    listImprestData.setVisibility(View.GONE);
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
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, ArrayList<Datum>>();

        for (int i = 0; i < getImprestResponse.getFinalArray().size(); i++) {
            listDataHeader.add(getImprestResponse.getFinalArray().get(i).getDate());
            ArrayList<Datum> rows = new ArrayList<Datum>();
            for (int j = 0; j < getImprestResponse.getFinalArray().get(i).getData().size(); j++) {
                rows.add(getImprestResponse.getFinalArray().get(i).getData().get(j));
            }
            listDataChild.put(listDataHeader.get(i), rows);
        }
    }
}
