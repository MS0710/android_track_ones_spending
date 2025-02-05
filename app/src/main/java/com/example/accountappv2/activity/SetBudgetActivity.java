package com.example.accountappv2.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.accountappv2.R;
import com.google.android.material.button.MaterialButton;

public class SetBudgetActivity extends AppCompatActivity {
    private String TAG = "SetBudgetActivity";
    private ImageView img_setBudget_back;
    private EditText edit_setBudget_price;
    private MaterialButton btn_setBudget_set;

    private SharedPreferences getPrefs;
    private SharedPreferences.Editor editor;
    private int userSetBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_budget);
        initView();
    }

    ///畫面元件初始化
    private void initView(){
        getPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userSetBudget = getPrefs.getInt("userSetBudget", 10000);

        img_setBudget_back = (ImageView) findViewById(R.id.img_setBudget_back);
        edit_setBudget_price = (EditText) findViewById(R.id.edit_setBudget_price);
        btn_setBudget_set = (MaterialButton) findViewById(R.id.btn_setBudget_set);
        img_setBudget_back.setOnClickListener(onClick);
        btn_setBudget_set.setOnClickListener(onClick);
        edit_setBudget_price.setText(""+userSetBudget);
    }

    ///各項點擊事件
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.img_setBudget_back){///返回按鍵
                Log.d(TAG, "onClick: img_setBudget_back");
                finish();
            }else if(view.getId() == R.id.btn_setBudget_set){///修改預算
                Log.d(TAG, "onClick: btn_setBudget_set");

                if(edit_setBudget_price.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"請輸入預算",Toast.LENGTH_SHORT).show();
                }else {
                    int budgetNum = Integer.parseInt(edit_setBudget_price.getText().toString());
                    if(budgetNum == 0){
                        Toast.makeText(getApplicationContext(),"請輸入預算",Toast.LENGTH_SHORT).show();
                    }else {
                        editor = getPrefs.edit();
                        editor.putInt("userSetBudget", budgetNum);
                        editor.apply();
                        finish();
                    }

                }
            }

        }
    };

}