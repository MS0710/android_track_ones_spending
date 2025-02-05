package com.example.accountappv2.Fragment;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;

import com.example.accountappv2.R;
import com.example.accountappv2.data.BillData;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG = "StatisticFragment";
    private PieChart pieChart;
    private ArrayList<PieEntry> entries;
    private TextView txt_statistic_selectDate;
    private String currentSelectYear,currentSelectMonth;
    private int[] itemData = new int[6];
    private String[] itemTitle = {"食物","交通","消費","娛樂","居家","額外",};

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SharedPreferences getPrefs;
    String userAccount,str;

    public StatisticFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
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
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        readExistingData();
    }

    ///支出紀錄統計畫面元件初始化
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


        pieChart = view.findViewById(R.id.pieChart);
        txt_statistic_selectDate = (TextView)view.findViewById(R.id.txt_statistic_selectDate);
        txt_statistic_selectDate.setOnClickListener(onClick);
        currentSelectYear = String.valueOf(cal.get(Calendar.YEAR));
        currentSelectMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
        txt_statistic_selectDate.setText(currentSelectYear+"年"+currentSelectMonth+"月支出");
    }

    ///各項點擊事件
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.txt_statistic_selectDate){
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
                    txt_statistic_selectDate.setText(""+year+"年"+month+"月支出");
                    dialog.dismiss();
                    readExistingData();
                }));
            }

        }
    };

    ///讀取現有資料
    private void readExistingData(){
        Log.d(TAG, "readExistingData: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: dataSnapshot = "+dataSnapshot.toString());
                /*<item>食物</item>
                <item>交通</item>
                <item>消費</item>
                <item>娛樂</item>
                <item>居家</item>
                <item>額外</item>*/
                for(int i=0; i<6; i++){
                    itemData[i] = 0;
                }
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
                        switch (type){
                            case "食物":
                                itemData[0] += Integer.parseInt(cost);
                                break;
                            case "交通":
                                itemData[1] += Integer.parseInt(cost);
                                break;
                            case "消費":
                                itemData[2] += Integer.parseInt(cost);
                                break;
                            case "娛樂":
                                itemData[3] += Integer.parseInt(cost);
                                break;
                            case "居家":
                                itemData[4] += Integer.parseInt(cost);
                                break;
                            case "額外":
                                itemData[5] += Integer.parseInt(cost);
                                break;
                        }
                    }
                }
                setPieData();
                setPieChart();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setPieData(){
        entries = new ArrayList<>();
        for(int i=0; i<6; i++){
            if(itemData[i] !=0){
                Log.d(TAG, "setPieData: itemTitle[i] = "+itemTitle[i]);
                Log.d(TAG, "setPieData: itemData[i] = "+(itemData[i]*-1));
                entries.add(new PieEntry((itemData[i]*-1), itemTitle[i]));
            }
        }
    }

    private void setPieChart(){

        PieDataSet dataSet = new PieDataSet(entries, "");
        //dataSet.setColors(ColorTemplate.COLORFUL_COLORS);//自動隨機選色

        ArrayList<Integer> colors = new ArrayList<>();
        // 手動分配圓餅顏色
        colors.add(Color.rgb(0, 229, 230));//藍
        colors.add(Color.rgb(255, 221, 51));//黃
        colors.add(Color.rgb(255, 117, 117));//紅
        colors.add(Color.rgb(201, 141, 193));//紫
        colors.add(Color.rgb(255, 171, 133));//橘
        colors.add(Color.rgb(163, 209, 209));//淡藍
        dataSet.setColors(colors);
        dataSet.setValueTextSize(16f);

        pieChart.setUsePercentValues(true); //設定為顯示百分比
        //Description pieChart.setDescription("當天時間分配表");//設定描述
        //pieChart.setDescriptionTextSize(20f);
        pieChart.setCenterText("PieChart1234");
        pieChart.setDrawCenterText(false); //設定可以繪製中間的文字
        pieChart.setCenterTextColor(Color.RED); //中間的文字顏色

        pieChart.setHoleColor(Color.WHITE);//餅狀圖中間的圓的繪製顏色
        pieChart.setHoleRadius(30f);//餅狀圖中間的圓的半徑大小
        pieChart.setTransparentCircleColor(Color.RED);//設定圓環的顏色
        pieChart.setTransparentCircleAlpha(0);//設定圓環的透明度[0,255]
        //pieChart.setTransparentCircleRadius(40f);//設定圓環的半徑值

        //餅狀圖上字體的設置
        pieChart.setDrawEntryLabels(true);//設定是否繪製Label
        pieChart.setEntryLabelColor(Color.rgb(0,0,0));//設定繪製Label的顏色
        pieChart.setEntryLabelTextSize(20f);//設定繪製Label的字體大小

        //餅狀圖下方文字大小設定
        Legend l = pieChart.getLegend();
        l.setTextSize(20f);

        //各個餅狀圖所佔比例數字的設置
        //dataSet.setValueFormatter(new PercentFormatter());//設定%
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                float YHAmount = Math.round(value * 100) / 100f;
                return YHAmount+"%";
            }
        });//設定%
        dataSet.setValueTextSize(18f);
        dataSet.setValueTextColor(Color.BLUE);



        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.getDescription().setEnabled(false);
        //pieChart.setCenterText("PieChart");
        pieChart.animateY(1000);
    }

    private void hideDay(DatePicker mDatePicker) {
        try {
            /* 處理android5.0以上的特殊情況 */
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
}