package com.example.accountappv2.activity;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.accountappv2.R;
import com.example.accountappv2.data.BillData;
import com.example.accountappv2.data.UserInfo;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNewItemActivity extends AppCompatActivity {
    private String TAG = "AddNewItemActivity";
    private ImageView img_addNewItem_back;
    private CardView cv_addNewItem_income,cv_addNewItem_expenditure;
    private TextView txt_addNewItem_date;
    private EditText edit_addNewItem_price,edit_addNewItem_detail;
    private MaterialButton btn_addNewItem_add;
    private DatePickerDialog.OnDateSetListener datePicker;
    private Calendar calendar;
    private Spinner sp_addNewItem_spin;
    private String year,month,day;
    private String typeResult;
    private int incomeFlag;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SharedPreferences getPrefs;
    String userAccount,str;
    int itemCunt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_item);
        initView();
    }

    ///畫面元件初始化
    private void initView(){
        incomeFlag = 0;
        getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        userAccount = "";
        userAccount = getPrefs.getString("userAccount", "");
        Log.d(TAG, "initView: userAccount = "+userAccount);
        str = userAccount.substring(0, userAccount.indexOf("@"));
        Log.d(TAG, "initView: str = "+str);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("BillDataInfo");

        img_addNewItem_back = (ImageView) findViewById(R.id.img_addNewItem_back);
        img_addNewItem_back.setOnClickListener(onClick);

        cv_addNewItem_income = (CardView) findViewById(R.id.cv_addNewItem_income);
        cv_addNewItem_expenditure = (CardView) findViewById(R.id.cv_addNewItem_expenditure);
        txt_addNewItem_date = (TextView)findViewById(R.id.txt_addNewItem_date);
        sp_addNewItem_spin = (Spinner) findViewById(R.id.sp_addNewItem_spin);
        btn_addNewItem_add = (MaterialButton) findViewById(R.id.btn_addNewItem_add);
        edit_addNewItem_price = (EditText)findViewById(R.id.edit_addNewItem_price);
        edit_addNewItem_detail = (EditText)findViewById(R.id.edit_addNewItem_detail);
        cv_addNewItem_income.setOnClickListener(onClick);
        cv_addNewItem_expenditure.setOnClickListener(onClick);
        txt_addNewItem_date.setOnClickListener(onClick);
        btn_addNewItem_add.setOnClickListener(onClick);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        year = String.valueOf(calendar.get(Calendar.YEAR));
        month = String.valueOf(calendar.get(Calendar.MONTH)+1);
        day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        Log.d(TAG, "initView = "+year+"/"+month+"/"+day);
        txt_addNewItem_date.setText(year+"/"+month+"/"+day);

        datePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH , i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                String myFormat = "yyyy/MM/dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
                txt_addNewItem_date.setText(sdf.format(calendar.getTime()));

                myFormat = "yyyy";
                sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
                year = sdf.format(calendar.getTime());
                Log.d(TAG, "onDateSet: year = "+year);

                myFormat = "MM";
                sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
                month = sdf.format(calendar.getTime());
                Log.d(TAG, "onDateSet: month = "+month);

                myFormat = "dd";
                sdf = new SimpleDateFormat(myFormat, Locale.TAIWAN);
                day = sdf.format(calendar.getTime());
                Log.d(TAG, "onDateSet: day = "+day);
            }
        };

        // 設定 sp 元件 ItemSelected 事件的 listener
        sp_addNewItem_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {
                typeResult = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "onItemSelected: result = "+typeResult);
            }

            @Override
            public void onNothingSelected(AdapterView parent) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        itemCunt = 0;
        readExistingData();
    }

    ///各項點擊事件
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.img_addNewItem_back){
                finish();
            }else if(view.getId() == R.id.cv_addNewItem_income){
                Log.d(TAG, "onClick: cv_addNewItem_income");
                cv_addNewItem_income.setCardBackgroundColor(Color.rgb(255,190,158));
                cv_addNewItem_expenditure.setCardBackgroundColor(Color.rgb(209,209,209));
                incomeFlag = 1;
            }else if(view.getId() == R.id.cv_addNewItem_expenditure){
                Log.d(TAG, "onClick: cv_addNewItem_expenditure");
                cv_addNewItem_expenditure.setCardBackgroundColor(Color.rgb(255,190,158));
                cv_addNewItem_income.setCardBackgroundColor(Color.rgb(209,209,209));
                incomeFlag = -1;
            }else if(view.getId() == R.id.txt_addNewItem_date){
                Log.d(TAG, "onClick: txt_addNewItem_date");
                DatePickerDialog dialog = new DatePickerDialog(AddNewItemActivity.this,
                        datePicker,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }else if(view.getId() == R.id.btn_addNewItem_add){
                Log.d(TAG, "onClick: btn_addNewItem_add");
                //addSampleData();
                if(addNewItem()){
                    finish();
                }
            }

        }
    };

    ///添加新記帳資料
    private boolean addNewItem(){
        Log.d(TAG, "addNewItem: ");
        if (incomeFlag == 0){
            Toast.makeText(getApplicationContext(),"請選擇收入或支出",Toast.LENGTH_SHORT).show();
            return false;
        }

        if((edit_addNewItem_price.getText().toString().equals("")) || (edit_addNewItem_detail.getText().toString().equals(""))){
            Toast.makeText(getApplicationContext(),"請填寫完整訊息",Toast.LENGTH_SHORT).show();
            return false;
        }else {
            String price = "0";
            if(incomeFlag>0){
                price = String.valueOf(Integer.parseInt(edit_addNewItem_price.getText().toString())*1);
            }else if(incomeFlag<0){
                price = String.valueOf(Integer.parseInt(edit_addNewItem_price.getText().toString())*-1);
            }

            String detail = edit_addNewItem_detail.getText().toString();
            BillData billData = new BillData("item"+itemCunt,year,month,day,typeResult,detail,price);
            myRef.child(str).child("item"+itemCunt).setValue(billData);
            return true;
        }
    }

    ///範例資料
    private void addSampleData(){
        BillData billData = new BillData("item0","2024","11","05","交通","悠遊卡儲值","-1000");
        myRef.child(str).child("item0").setValue(billData);
        billData = new BillData("item1","2024","11","06","消費","購買書籍","-960");
        myRef.child(str).child("item1").setValue(billData);
        billData = new BillData("item2","2024","11","07","食物","火鍋","-500");
        myRef.child(str).child("item2").setValue(billData);
        billData = new BillData("item3","2024","11","08","薪水","家教收入","2000");
        myRef.child(str).child("item3").setValue(billData);

    }

    ///讀取現有資料
    private void readExistingData(){
        Log.d(TAG, "readExistingData: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: dataSnapshot = "+dataSnapshot.toString());
                for (DataSnapshot snapshot : dataSnapshot.child(str).getChildren()){
                    itemCunt++;
                }
                Log.d(TAG, "onDataChange: itemCunt = "+itemCunt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}