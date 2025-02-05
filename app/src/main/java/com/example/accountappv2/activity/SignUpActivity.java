package com.example.accountappv2.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.accountappv2.data.UserInfo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {
    private String TAG = "SignUpActivity";
    private EditText edit_signUp_account,edit_signUp_password,edit_signUp_checkPassword,edit_signUp_name;
    private TextView txt_signUp_tip;
    private MaterialButton btn_signUp_ok;
    private String account,password,name;
    private int userCunt;
    private boolean flag_ok;
    private String[] accountBuf = new String[100];
    private String[] nameBuf = new String[100];
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        flag_ok = true;
        userCunt = 0;
        readExistingData();
    }

    ///畫面元件初始化
    private void initView(){
        userCunt = 0;
        for (int i=0; i<100;i++){
            accountBuf[i] = "";
        }
        flag_ok = true;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserInfo");

        edit_signUp_account = (EditText) findViewById(R.id.edit_signUp_account);
        edit_signUp_password = (EditText) findViewById(R.id.edit_signUp_password);
        edit_signUp_checkPassword = (EditText) findViewById(R.id.edit_signUp_checkPassword);
        edit_signUp_name = (EditText) findViewById(R.id.edit_signUp_name);
        txt_signUp_tip = (TextView) findViewById(R.id.txt_signUp_tip);
        btn_signUp_ok = (MaterialButton) findViewById(R.id.btn_signUp_ok);
        btn_signUp_ok.setOnClickListener(onClick);

        /////////////
        /*edit_signUp_account.setText("abc7@test.com");
        edit_signUp_password.setText("123456");
        edit_signUp_checkPassword.setText("123456");
        edit_signUp_name.setText("Mars");*/
    }

    ///各項點擊事件
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btn_signUp_ok){///註冊按鈕
                Log.d(TAG, "onClick: btn_signUp_ok");
                account = edit_signUp_account.getText().toString();
                password = edit_signUp_password.getText().toString();
                name = edit_signUp_name.getText().toString();
                if (checkAccount()){
                    txt_signUp_tip.setText("帳號已被使用");
                }else {
                    txt_signUp_tip.setText("");
                    if(password.equals(edit_signUp_checkPassword.getText().toString())){
                        txt_signUp_tip.setText("");
                        addNewUser();
                        finish();
                    }else {
                        txt_signUp_tip.setText("請檢查輸入的兩次密碼");
                    }
                }
            }
        }
    };

    ///檢查現有帳號是否重複
    private boolean checkAccount(){
        for (int i=0; i<userCunt ; i++){
            if (accountBuf[i].equals(account)){
                return true;
            }
        }
        return false;
    }

    ///註冊新增新帳號
    private void addNewUser(){
        Log.d(TAG, "addNewUser: ");
        UserInfo userInfo = new UserInfo(account, password,name);
        myRef.child("user"+userCunt).setValue(userInfo);
        flag_ok = false;
    }

    ///讀取現有帳密資料
    private void readExistingData(){
        Log.d(TAG, "readExistingData: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d(TAG, "onDataChange: user001 ="+dataSnapshot.child("user001").child("account").getValue().toString());
                //Log.d(TAG, "onDataChange: user002 ="+dataSnapshot.child("user002").child("account").getValue().toString());
                if (flag_ok){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Log.d(TAG, "onDataChange: "+snapshot.toString());
                        Log.d(TAG, "onDataChange: userCunt = " + userCunt);
                        accountBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("account").getValue().toString();
                        nameBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("name").getValue().toString();
                        userCunt++;
                    }
                    Log.d(TAG, "onDataChange: ---------userCunt = " + userCunt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}