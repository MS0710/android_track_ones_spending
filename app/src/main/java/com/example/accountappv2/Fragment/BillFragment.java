package com.example.accountappv2.Fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.accountappv2.R;
import com.example.accountappv2.activity.AddNewItemActivity;
import com.example.accountappv2.activity.SetBudgetActivity;
import com.example.accountappv2.adapter.BillItemAdapter;
import com.example.accountappv2.data.BillData;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BillFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String TAG = "BillFragment";
    private TextView txt_bill_selectDate;
    private TextView txt_bill_cost,txt_bill_income,txt_bill_total,txt_bill_remainingBudget,txt_bill_budget;
    private FloatingActionButton btn_bill_add;
    private ImageView img_bill_setting;
    private GridView gv_bill_list;
    private BillItemAdapter billItemAdapter;
    private List<BillData> list;
    private String currentSelectYear,currentSelectMonth;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SharedPreferences getPrefs;
    private String userAccount,str;
    private int userSetBudget;

    public BillFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BillFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillFragment newInstance(String param1, String param2) {
        BillFragment fragment = new BillFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bill, container, false);
        initView(view);
        return view;
    }

    ///記帳紀錄畫面元件初始化
    private void initView(View view){

        getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        userAccount = "";
        userAccount = getPrefs.getString("userAccount", "");
        Log.d(TAG, "initView: userAccount = "+userAccount);
        str = userAccount.substring(0, userAccount.indexOf("@"));
        Log.d(TAG, "initView: str = "+str);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("BillDataInfo");

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        Log.d(TAG, "initView = "+cal.get(Calendar.YEAR)+"/"+cal.get(Calendar.MONTH));

        txt_bill_selectDate = (TextView) view.findViewById(R.id.txt_bill_selectDate);
        txt_bill_cost = (TextView) view.findViewById(R.id.txt_bill_cost);
        txt_bill_income = (TextView) view.findViewById(R.id.txt_bill_income);
        txt_bill_total = (TextView) view.findViewById(R.id.txt_bill_total);
        txt_bill_remainingBudget = (TextView) view.findViewById(R.id.txt_bill_remainingBudget);
        txt_bill_budget = (TextView) view.findViewById(R.id.txt_bill_budget);
        btn_bill_add = (FloatingActionButton) view.findViewById(R.id.btn_bill_add);
        img_bill_setting = (ImageView) view.findViewById(R.id.img_bill_setting);
        currentSelectYear = String.valueOf(cal.get(Calendar.YEAR));
        currentSelectMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
        txt_bill_selectDate.setText(currentSelectYear+"年"+currentSelectMonth+"月");
        txt_bill_selectDate.setOnClickListener(onClick);
        btn_bill_add.setOnClickListener(onClick);
        img_bill_setting.setOnClickListener(onClick);

        gv_bill_list = (GridView) view.findViewById(R.id.gv_bill_list);
        list = new ArrayList<>();
        //setData();
        billItemAdapter = new BillItemAdapter(getContext(),list);
        gv_bill_list.setAdapter(billItemAdapter);
        gv_bill_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemLongClick: index = "+i);
                AlertDialog.Builder alertDialog =
                        new AlertDialog.Builder(getContext());
                alertDialog.setTitle("刪除資料");
                alertDialog.setMessage("是否刪除此筆資料紀錄");
                alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: OK");
                        myRef.child(str).child(String.valueOf(list.get(i).getKeyId())).removeValue();
                    }
                });
                alertDialog.setNeutralButton("取消",(dialog, which) -> {
                    Log.d(TAG, "onClick: NO");
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
                return false;
            }
        });

    }

    ///各項點擊事件
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.txt_bill_selectDate){
                Log.d(TAG, "onClick: txt_bill_selectDate");
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                View v = getLayoutInflater().inflate(R.layout.date_select_dialog,null);
                alertDialog.setTitle("請選擇月份");
                alertDialog.setView(v);
                alertDialog.setPositiveButton("確定",(((dialog, which) -> {})));

                AlertDialog dialog = alertDialog.create();
                dialog.show();

                final DatePicker datePicker = (DatePicker) v.findViewById(R.id.date_picker_his);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                datePicker.init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
                hideDay(datePicker);

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener((v1 -> {
                    int year = datePicker.getYear();
                    int month = datePicker.getMonth()+1;
                    currentSelectYear = String.valueOf(year);
                    currentSelectMonth = String.valueOf(month);
                    Log.d(TAG, "onClick"+year+"年"+month+"月");
                    txt_bill_selectDate.setText(""+year+"年"+month+"月");
                    dialog.dismiss();
                    readExistingData();
                }));
            }else if(view.getId() == R.id.btn_bill_add){
                Log.d(TAG, "onClick: btn_bill_add");
                Intent intent = new Intent(getContext(), AddNewItemActivity.class);
                getContext().startActivity(intent);
            }else if(view.getId() == R.id.img_bill_setting){
                Log.d(TAG, "onClick: img_bill_setting");
                Intent intent = new Intent(getContext(), SetBudgetActivity.class);
                getContext().startActivity(intent);

            }

        }
    };

    private void hideDay(DatePicker mDatePicker) {
        try {
            /* 处理android5.0以上的特殊情况 */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                if (daySpinnerId != 0) {
                    View daySpinner = mDatePicker.findViewById(daySpinnerId);
                    if (daySpinner != null) {
                        daySpinner.setVisibility(View.GONE);
                    }
                }
            } else {
                Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
                for (Field datePickerField : datePickerfFields) {
                    if ("mDaySpinner".equals(datePickerField.getName()) || ("mDayPicker").equals(datePickerField.getName())) {
                        datePickerField.setAccessible(true);
                        Object dayPicker = new Object();
                        try {
                            dayPicker = datePickerField.get(mDatePicker);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        ((View) dayPicker).setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData(){
        for (int i=0; i< 1; i++){
            list.add(new BillData("item0","2024","11","05","交通","悠遊卡儲值","-1000"));
            list.add(new BillData("item1","2024","11","06","教育","購買書籍","-960"));
            list.add(new BillData("item2","2024","11","07","食物","火鍋","-500"));
            list.add(new BillData("item3","2024","11","08","薪水","家教收入","2000"));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        userSetBudget = getPrefs.getInt("userSetBudget", 10000);
        txt_bill_budget.setText(""+userSetBudget);
        readExistingData();
    }

    ///讀取現有資料
    private void readExistingData(){
        Log.d(TAG, "readExistingData: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: dataSnapshot = "+dataSnapshot.toString());
                int calculateCost = 0;
                int calculateIncome = 0;

                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.child(str).getChildren()){
                    Log.d(TAG, "onDataChange: "+snapshot);
                    String key = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: "+key);
                    String year = dataSnapshot.child(str).child(key).child("year").getValue().toString();
                    String month = dataSnapshot.child(str).child(key).child("month").getValue().toString();
                    String day = dataSnapshot.child(str).child(key).child("day").getValue().toString();
                    String type = dataSnapshot.child(str).child(key).child("type").getValue().toString();
                    String cost = dataSnapshot.child(str).child(key).child("cost").getValue().toString();
                    String detail = dataSnapshot.child(str).child(key).child("detail").getValue().toString();
                    Log.d(TAG, "onDataChange: year = "+year);
                    Log.d(TAG, "onDataChange: month = "+month);
                    Log.d(TAG, "onDataChange: day = "+day);
                    Log.d(TAG, "onDataChange: type = "+type);
                    Log.d(TAG, "onDataChange: cost = "+cost);
                    Log.d(TAG, "onDataChange: detail = "+detail);
                    if(year.equals(currentSelectYear) && month.equals(currentSelectMonth)){
                        if(Integer.parseInt(cost) > 0){
                            calculateIncome += Integer.parseInt(cost);
                        }else {
                            calculateCost += Integer.parseInt(cost);
                        }
                        list.add(new BillData(key,year,month,day,type,detail,cost));
                    }
                }
                billItemAdapter.notifyDataSetChanged();
                txt_bill_cost.setText(""+calculateCost);
                txt_bill_income.setText(""+calculateIncome);
                txt_bill_total.setText(""+(calculateCost+calculateIncome));
                txt_bill_remainingBudget.setText(""+(userSetBudget+(calculateCost+calculateIncome)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}