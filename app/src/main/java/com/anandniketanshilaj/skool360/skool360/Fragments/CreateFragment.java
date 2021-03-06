package com.anandniketanshilaj.skool360.skool360.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.anandniketanshilaj.skool360.R;
import com.anandniketanshilaj.skool360.skool360.Adapter.ExpandableListAdapterInbox;
import com.anandniketanshilaj.skool360.skool360.AsyncTasks.PTMStudentWiseTeacherAsyncTask;
import com.anandniketanshilaj.skool360.skool360.AsyncTasks.PTMTeacherStudentGetDetailAsyncTask;
import com.anandniketanshilaj.skool360.skool360.AsyncTasks.PTMTeacherStudentInsertDetailAsyncTask;
import com.anandniketanshilaj.skool360.skool360.Models.MainPtmSentMessageResponse;
import com.anandniketanshilaj.skool360.skool360.Models.PTMCreateResponse.FinalArrayStudentForCreate;
import com.anandniketanshilaj.skool360.skool360.Models.PTMCreateResponse.MainResponseDisplayStudent;
import com.anandniketanshilaj.skool360.skool360.Models.PTMCreateResponse.StudentDatum;
import com.anandniketanshilaj.skool360.skool360.Models.PTMTeacherResponse.PTMStudentWiseTeacher;
import com.anandniketanshilaj.skool360.skool360.Utility.Utility;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admsandroid on 10/30/2017.
 */

public class CreateFragment extends Fragment {
    private View rootView;
    private Context mContext;
    private static TextView txtDate;
    private EditText edtPurpose, edtDescription;
    private Spinner spinRequestFor;
    private Button btnSave, btnCancel;
    private PTMStudentWiseTeacherAsyncTask ptmStudentWiseTeacherAsyncTask = null;
    private ProgressDialog progressDialog = null;
    PTMStudentWiseTeacher responseTeacher;
    private PTMTeacherStudentInsertDetailAsyncTask getPTMTeacherStudentInsertDetailAsyncTask = null;
    MainPtmSentMessageResponse mainPtmSentMessageResponse;
    private static String dateFinal;
    String Date, purpose, requestfor, description;
    ArrayList<String> Teacherfield;
    HashMap<Integer, String> spinnerMap;
    String[] spinnerteacherIdArray;

    public CreateFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create, container, false);
        mContext = getActivity();

        initViews();
        setListners();

        return rootView;
    }

    public void initViews() {
        txtDate = (TextView) rootView.findViewById(R.id.txtDate);
        edtPurpose = (EditText) rootView.findViewById(R.id.edtPurpose);
        edtDescription = (EditText) rootView.findViewById(R.id.edtDescription);
        spinRequestFor = (Spinner) rootView.findViewById(R.id.spinRequestFor);
        btnSave = (Button) rootView.findViewById(R.id.btnSave);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);

        setUserVisibleHint(true);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && rootView != null) {
            getSpinnerData();

            //load today's data first
            txtDate.setText("DD/MM/YYYY");
        }
        // execute your data loading logic.
    }

    public void setListners() {
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        spinRequestFor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String name = spinRequestFor.getSelectedItem().toString();
                String getid = spinnerMap.get(spinRequestFor.getSelectedItemPosition());

                Log.d("value", name + " " + getid);
                requestfor = getid.toString();
                Log.d("requestfor", requestfor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getsendAppoimentData();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtDate.setText("DD/MM/YYYY");
                edtPurpose.setText("");
                edtDescription.setText("");
                setSelection();
            }
        });
    }

    public void getsendAppoimentData() {
        Date = txtDate.getText().toString();
        purpose = edtPurpose.getText().toString();
        description = edtDescription.getText().toString();

        if (Utility.isNetworkConnected(mContext)) {
            if (!requestfor.equalsIgnoreCase("") && !Date.equalsIgnoreCase("") &&
                    !purpose.equalsIgnoreCase("") && !description.equalsIgnoreCase("")) {
                progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Please Wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("MessageID", "0");
                            params.put("FromID", Utility.getPref(mContext, "studid"));
                            params.put("ToID", requestfor);
                            params.put("MeetingDate", Date);
                            params.put("SubjectLine", purpose);
                            params.put("Description", description);
                            params.put("Flag", "Student");

                            getPTMTeacherStudentInsertDetailAsyncTask = new PTMTeacherStudentInsertDetailAsyncTask(params);
                            mainPtmSentMessageResponse = getPTMTeacherStudentInsertDetailAsyncTask.execute().get();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    if (mainPtmSentMessageResponse.getSuccess().equalsIgnoreCase("True")) {
                                        txtDate.setText("DD/MM/YYYY");
                                        edtPurpose.setText("");
                                        edtDescription.setText("");
                                        setSelection();
                                        Utility.ping(mContext, "Appointment Book Successfully.");
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
            } else {
                Utility.ping(mContext, "Blank field not allowed.");
            }
        } else {
            Utility.ping(mContext, "Network not available");
        }
    }

    public void getSpinnerData() {
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
                        params.put("StudentID", Utility.getPref(mContext, "studid"));
                        ptmStudentWiseTeacherAsyncTask = new PTMStudentWiseTeacherAsyncTask(params);
                        responseTeacher = ptmStudentWiseTeacherAsyncTask.execute().get();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                if (responseTeacher.getFinalArray().size() > 0) {
                                    fillspinner();
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
        } else {
            Utility.ping(mContext, "Network not available");
        }
    }

    public void fillspinner() {
        ArrayList<Integer> staffId = new ArrayList<>();
        for (int i = 0; i < responseTeacher.getFinalArray().size(); i++) {
            if (responseTeacher.getFinalArray().get(i).getTeacherName().contains("ClassTeacher")) {
                staffId.add(responseTeacher.getFinalArray().get(i).getStaffID());
            }
        }
        ArrayList<Integer> TeacherId = new ArrayList<>();
        for (int i = 0; i < responseTeacher.getFinalArray().size(); i++) {
            if (responseTeacher.getFinalArray().get(i).getTeacherName().contains("ClassTeacher")) {
                TeacherId.add(responseTeacher.getFinalArray().get(i).getStaffID());
            } else {
                TeacherId.add(responseTeacher.getFinalArray().get(i).getTeacherID());
            }
        }
        ArrayList<String> TeacherName = new ArrayList<String>();
        for (int i = 0; i < responseTeacher.getFinalArray().size(); i++) {
            TeacherName.add(responseTeacher.getFinalArray().get(i).getTeacherName());

        }
        String[] spinnerstaffIdArray = new String[staffId.size()];
        spinnerteacherIdArray = new String[TeacherId.size()];

        spinnerMap = new HashMap<Integer, String>();
        for (int i = 0; i < TeacherId.size(); i++) {
            spinnerMap.put(i, String.valueOf(TeacherId.get(i)));
            spinnerteacherIdArray[i] = TeacherName.get(i).trim();
        }
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinRequestFor);

            popupWindow.setHeight(spinnerteacherIdArray.length > 5 ? 500 : spinnerteacherIdArray.length * 100);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(mContext, R.layout.spinner_layout, spinnerteacherIdArray);
        spinRequestFor.setAdapter(adapterYear);
        setSelection();
    }

    public void setSelection() {
        for (int m = 0; m < spinnerteacherIdArray.length; m++) {
            if (spinnerteacherIdArray[m].contains("(ClassTeacher)")) {
                Log.d("spinnerValue", spinnerteacherIdArray[m]);
                int index = m;
                Log.d("indexOf", String.valueOf(index));
                spinRequestFor.setSelection(index);
            }
        }
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, yy, mm, dd);
            dialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);

            return dialog;
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

            txtDate.setText(dateFinal);
        }
    }

}
