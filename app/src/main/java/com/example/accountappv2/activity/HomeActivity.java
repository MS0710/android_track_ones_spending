package com.example.accountappv2.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.accountappv2.Fragment.BillFragment;
import com.example.accountappv2.Fragment.ReceiptFragment;
import com.example.accountappv2.Fragment.StatisticFragment;
import com.example.accountappv2.R;
import com.example.accountappv2.adapter.MainPagerAdapter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class HomeActivity extends AppCompatActivity {
    private String TAG = "HomeActivity";
    private SmartTabLayout smartTabLayout ;
    private ViewPager viewpager;
    private MainPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    ///主畫面元件初始化
    private void initView(){
        smartTabLayout = (SmartTabLayout)findViewById(R.id.viewpagertab);
        viewpager = (ViewPager)findViewById(R.id.viewpager);

        //Use for Activity
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        //Use for Fragment
        adapter.addFragment(new BillFragment(),"billPage");
        adapter.addFragment(new StatisticFragment(),"statisticPage");
        adapter.addFragment(new ReceiptFragment(),"receiptPage");

        viewpager.setOffscreenPageLimit(3);
        final int[] tabIcons = {R.drawable.tab_ic_note,R.drawable.tab_ic_pie_chart,R.drawable.tab_ic_receipt};
        final int[] tabTitles = {R.string.tab_bill,R.string.tab_statistic, R.string.tab_receipt};

        smartTabLayout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                //View view = inflater.inflate(R.layout.layout_navigation_bottom_item, container, false);
                View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.layout_navigation_bottom_item, container, false);
                ImageView iconView = (ImageView) view.findViewById(R.id.img_icon);
                Log.d(TAG, "createTabView: position = "+position);
                Log.d(TAG, "createTabView: tabIcons.length = "+tabIcons.length);
                Log.d(TAG, "createTabView: position % tabIcons.length = "+(position % tabIcons.length));
                iconView.setBackgroundResource(tabIcons[position % tabIcons.length]);
                TextView titleView = (TextView) view.findViewById(R.id.txt_title);
                titleView.setText(tabTitles[position % tabTitles.length]);
                return view;
            }
        });

        viewpager.setAdapter(adapter);
        smartTabLayout.setViewPager(viewpager);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}