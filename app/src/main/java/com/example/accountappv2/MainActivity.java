package com.example.accountappv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.accountappv2.activity.ForgetPasswordActivity;
import com.example.accountappv2.activity.HomeActivity;
import com.example.accountappv2.activity.SignUpActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private MaterialButton btn_main_signUp,btn_main_signIn;
    private TextInputLayout textInput_main_account_layout,textInput_main_password_layout;
    private TextView txt_main_forgetPassword;
    private EditText edit_main_account,edit_main_password;
    private ImageView img_main_watchPassword;
    private String account,password;
    private int userCunt;
    private int correctCunt;
    private String[] IDBuf = new String[100];
    private String[] accountBuf = new String[100];
    private String[] passwordBuf = new String[100];
    private String[] nameBuf = new String[100];
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SharedPreferences getPrefs;
    private SharedPreferences.Editor editor;
    private boolean flag_password;

    String userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //取得相機權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this
                        , android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                    100);
            Log.d(TAG, "onCreate: OK");
        }else{
            Log.d(TAG, "onCreate: No");
            //Toast.makeText(this, "請開啟相機權限", Toast.LENGTH_SHORT).show();
        }
        initView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] ==0){
            //openQRCamera();
            Log.d(TAG, "onRequestPermissionsResult: 1");
        }else{
            //Toast.makeText(this, "請開啟相機權限", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onRequestPermissionsResult: 2");
        }
    }

    ///畫面元件初始化
    private void initView(){
        flag_password = false;
        account = "";
        password = "";
        userCunt = 0;
        correctCunt = 0;

        getPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getPrefs.getString("userID", "");
        getPrefs.getString("userName", "");
        userAccount = getPrefs.getString("userAccount", "");

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserInfo");
        edit_main_account = (EditText)findViewById(R.id.edit_main_account);
        edit_main_password = (EditText)findViewById(R.id.edit_main_password);
        textInput_main_account_layout = (TextInputLayout) findViewById(R.id.textInput_main_account_layout);
        textInput_main_password_layout = (TextInputLayout) findViewById(R.id.textInput_main_password_layout);
        img_main_watchPassword = (ImageView)findViewById(R.id.img_main_watchPassword);
        txt_main_forgetPassword = (TextView) findViewById(R.id.txt_main_forgetPassword);

        btn_main_signUp = (MaterialButton) findViewById(R.id.btn_main_signUp);
        btn_main_signIn = (MaterialButton) findViewById(R.id.btn_main_signIn);
        btn_main_signUp.setOnClickListener(onClick);
        btn_main_signIn.setOnClickListener(onClick);
        img_main_watchPassword.setOnClickListener(onClick);
        txt_main_forgetPassword.setOnClickListener(onClick);
        ////////////
        //edit_main_account.setText("abc7@test.com");
        //edit_main_password.setText("123456");
        if(!userAccount.equals("")){
            Log.d(TAG, "initView: userAccount = "+userAccount);
            edit_main_account.setText(userAccount);
        }
    }

    ///各項點擊事件
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.btn_main_signUp){ ///跳轉註冊畫面
                Log.d(TAG, "onClick: btn_main_signUp");
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }else if(view.getId() == R.id.btn_main_signIn){///跳轉登入畫面
                Log.d(TAG, "onClick: btn_main_signIn");
                account = edit_main_account.getText().toString();
                password = edit_main_password.getText().toString();
                if(checkEditViewEmpty()){///檢查帳號密碼是否輸入為空
                    if(checkAccount() && checkPassword()){///檢查帳號密碼是否正確
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        editor = getPrefs.edit();
                        editor.putString("userID", IDBuf[correctCunt]);
                        editor.putString("userName", nameBuf[correctCunt]);
                        editor.putString("userAccount", accountBuf[correctCunt]);
                        editor.putString("userPassword", passwordBuf[correctCunt]);
                        Log.d(TAG, "onClick: userAccount = " +accountBuf[correctCunt]);
                        editor.apply();
                        startActivity(intent);
                    }else {
                        Toast.makeText(getApplicationContext(),"帳號密碼錯誤",Toast.LENGTH_SHORT).show();
                    }
                }
            }else if(view.getId() == R.id.img_main_watchPassword){///觀看密碼
                Log.d(TAG, "onClick: img_main_watchPassword");
                if(flag_password){
                    edit_main_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    flag_password = false;
                }else {
                    edit_main_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    flag_password = true;
                }
            }else if(view.getId() == R.id.txt_main_forgetPassword){///跳轉忘記密碼畫面
                Log.d(TAG, "onClick: txt_main_forgetPassword");
                Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
                startActivity(intent);
            }

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        readExistingData();
    }

    ///禁止回上頁
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
    }

    ///確認帳號
    private boolean checkAccount(){
        for (int i=0; i<userCunt ; i++){
            if(account.equals(accountBuf[i])){
                correctCunt = i;
                return true;
            }
        }
        return false;
    }

    ///確認密碼
    private boolean checkPassword(){
        for (int i=0; i<userCunt ; i++){
            if(password.equals(passwordBuf[i])){
                return true;
            }
        }
        return false;
    }

    ///檢查帳號密碼是否輸入為空
    private boolean checkEditViewEmpty(){
        if(TextUtils.isEmpty(account)){
            textInput_main_account_layout.setError("請輸入帳號");
            textInput_main_password_layout.setError("");
            return false;
        }else if(TextUtils.isEmpty(password)){
            textInput_main_account_layout.setError("");
            textInput_main_password_layout.setError("請輸入密碼");
            return false;
        }else {
            textInput_main_account_layout.setError("");
            textInput_main_password_layout.setError("");
            return true;
        }
    }

    ///讀取現有帳密資料
    private void readExistingData(){
        Log.d(TAG, "readExistingData: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userCunt = 0;
                //Log.d(TAG, "onDataChange: user001 ="+dataSnapshot.child("user001").child("account").getValue().toString());
                //Log.d(TAG, "onDataChange: user002 ="+dataSnapshot.child("user002").child("account").getValue().toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+snapshot.toString());
                    Log.d(TAG, "onDataChange: userCunt = " + userCunt);
                    IDBuf[userCunt] = "user"+userCunt;
                    accountBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("account").getValue().toString();
                    passwordBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("password").getValue().toString();
                    nameBuf[userCunt] = dataSnapshot.child("user"+userCunt).child("name").getValue().toString();
                    userCunt++;
                }
                Log.d(TAG, "onDataChange: ---------userCunt = " + userCunt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}