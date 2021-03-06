package com.anandniketanshilaj.skool360.skool360.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anandniketanshilaj.skool360.R;
import com.anandniketanshilaj.skool360.skool360.Activities.DashBoardActivity;
import com.anandniketanshilaj.skool360.skool360.AsyncTasks.FeesAsyncTask;
import com.anandniketanshilaj.skool360.skool360.Models.FeesModel;
import com.anandniketanshilaj.skool360.skool360.Utility.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FeesFragment extends Fragment {
    private View rootView;
    private Button btnMenu, btnBackUnitTest, more_detail_btn;
    private TextView txtNoRecordsUnitTest, payment_total_amount_txt,
            payment_total_amount_status_txt, total_fee_txt, due_fee_txt, discount_fee_txt;
    private Context mContext;
    private FragmentManager fragmentManager = null;
    private ProgressDialog progressDialog = null;
    private FeesAsyncTask getFeesAsyncTask = null;
    private ArrayList<FeesModel> feesdetailModels = new ArrayList<>();
    private LinearLayout linear,linear_right;

    public FeesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fees, container, false);
        mContext = getActivity();

        initViews();
        setListners();
        getFeesData();
        return rootView;
    }

    public void initViews() {
        btnMenu = (Button) rootView.findViewById(R.id.btnMenu);
        txtNoRecordsUnitTest = (TextView) rootView.findViewById(R.id.txtNoRecordsUnitTest);
        btnBackUnitTest = (Button) rootView.findViewById(R.id.btnBackUnitTest);
        payment_total_amount_txt = (TextView) rootView.findViewById(R.id.payment_total_amount_txt);
        payment_total_amount_status_txt = (TextView) rootView.findViewById(R.id.payment_total_amount_status_txt);
        total_fee_txt = (TextView) rootView.findViewById(R.id.total_fee_txt);
        due_fee_txt = (TextView) rootView.findViewById(R.id.due_fee_txt);
        discount_fee_txt = (TextView) rootView.findViewById(R.id.discount_fee_txt);
        more_detail_btn = (Button) rootView.findViewById(R.id.more_detail_btn);
        linear_right=(LinearLayout)rootView.findViewById(R.id.linear_right);
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
        more_detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PaymentFragment();
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out)
                        .replace(R.id.frame_container, fragment).commit();
            }
        });

    }

    public void getFeesData() {
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
                        params.put("studentid", Utility.getPref(mContext, "studid"));

                        getFeesAsyncTask = new FeesAsyncTask(params);
                        feesdetailModels = getFeesAsyncTask.execute().get();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (feesdetailModels.size() > 0) {
                                    txtNoRecordsUnitTest.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                    payment_total_amount_txt.setText("₹" + " " + feesdetailModels.get(0).getTermPaid());
                                    total_fee_txt.setText("Total" + "\n" + "₹" + " " + Html.fromHtml(feesdetailModels.get(0).getTermTotal()));
                                    due_fee_txt.setText("Due" + "\n" + "₹" + " " + Html.fromHtml(feesdetailModels.get(0).getTermDuePay()));
                                    discount_fee_txt.setText("Discount" + "\n" + "₹" + " " + Html.fromHtml(feesdetailModels.get(0).getTermDiscount()));
                                    linear_right.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.right2));

                                } else {
                                    progressDialog.dismiss();
                                    Utility.ping(mContext,"No Record Found");
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


}
