package com.example.accountappv2.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.accountappv2.R;
import com.example.accountappv2.adapter.BillItemAdapter;
import com.example.accountappv2.adapter.ReceiptItemAdapter;
import com.example.accountappv2.data.BillData;
import com.example.accountappv2.data.ReceiptItem;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiptFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String TAG = "ReceiptFragment";
    private MaterialButton mabtn_receipt_scan,mabtn_receipt_save;
    private TextView txt_receipt_scanResult;
    private GridView gv_receipt_list;
    private ReceiptItemAdapter receiptItemAdapter;
    private List<ReceiptItem> list;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private SharedPreferences getPrefs;
    private String userAccount,str;
    private int itemCunt;
    private String scanResult;

    public ReceiptFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiptFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReceiptFragment newInstance(String param1, String param2) {
        ReceiptFragment fragment = new ReceiptFragment();
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
        View view = inflater.inflate(R.layout.fragment_receipt, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        readExistingData();
    }

    ///發票紀錄畫面元件初始化
    private void initView(View v){
        itemCunt = 0;
        scanResult = "null";
        getPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        userAccount = "";
        userAccount = getPrefs.getString("userAccount", "");
        Log.d(TAG, "initView: userAccount = "+userAccount);
        str = userAccount.substring(0, userAccount.indexOf("@"));
        Log.d(TAG, "initView: str = "+str);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("ReceiptDataInfo");

        mabtn_receipt_scan = (MaterialButton) v.findViewById(R.id.mabtn_receipt_scan);
        mabtn_receipt_save = (MaterialButton) v.findViewById(R.id.mabtn_receipt_save);
        txt_receipt_scanResult = (TextView) v.findViewById(R.id.txt_receipt_scanResult);
        mabtn_receipt_scan.setOnClickListener(onClick);
        mabtn_receipt_save.setOnClickListener(onClick);

        gv_receipt_list = (GridView) v.findViewById(R.id.gv_receipt_list);
        list = new ArrayList<>();
        setData();
        receiptItemAdapter = new ReceiptItemAdapter(getContext(),list);
        gv_receipt_list.setAdapter(receiptItemAdapter);
        gv_receipt_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemLongClick: index = "+i);
                AlertDialog.Builder alertDialog =
                        new AlertDialog.Builder(getContext());
                alertDialog.setTitle("刪除資料");
                alertDialog.setMessage("是否刪除此筆資料資訊");
                alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: OK");
                        myRef.child(str).child(String.valueOf(list.get(i).getId())).removeValue();
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

    private void setData(){
        for (int i=0; i< 1; i++){
            list.add(new ReceiptItem(1,"11"));
        }
    }

    ///各項點擊事件
    private View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.mabtn_receipt_scan){
                Log.d(TAG, "onClick: mabtn_receipt_scan");
                //new IntentIntegrator(getActivity()).initiateScan(); // 启动扫描

                IntentIntegrator integrator = new IntentIntegrator(getActivity());
                integrator.forSupportFragment(ReceiptFragment.this).initiateScan();
            }else if(view.getId() == R.id.mabtn_receipt_save){
                Log.d(TAG, "onClick: mabtn_receipt_save");
                AlertDialog.Builder alertDialog =
                        new AlertDialog.Builder(getContext());
                alertDialog.setTitle("儲存發票資訊");
                alertDialog.setMessage("是否儲存發票資訊");
                alertDialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: OK");
                        addNewItem();
                    }
                });
                alertDialog.setNeutralButton("取消",(dialog, which) -> {
                    Log.d(TAG, "onClick: NO");
                });
                alertDialog.setCancelable(false);
                alertDialog.show();
            }

        }
    };

    private void addNewItem(){
        ReceiptItem receiptItem = new ReceiptItem(itemCunt,scanResult);
        myRef.child(str).child(""+itemCunt).setValue(receiptItem);
    }


    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: in");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                // 用戶取消了掃描
            } else {
                // 取得掃描結果
                //String scanResult = result.getContents();
                scanResult = result.getContents();
                // 在這裡處理掃描結果，例如顯示到介面上或發送到伺服器等
                Log.d(TAG, "onActivityResult: "+scanResult);
                txt_receipt_scanResult.setText(scanResult);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    ///讀取現有資料
    private void readExistingData(){
        Log.d(TAG, "readExistingData: ");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: dataSnapshot = "+dataSnapshot.toString());
                list.clear();
                itemCunt = 1;
                for (DataSnapshot snapshot : dataSnapshot.child(str).getChildren()){
                    Log.d(TAG, "onDataChange: snapshot.getRef().toString() = "+snapshot.getRef().toString());
                    Log.d(TAG, "onDataChange: "+snapshot);
                    String key = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: key = "+key);
                    String detail = dataSnapshot.child(str).child(key).child("detail").getValue().toString();
                    list.add(new ReceiptItem(Integer.parseInt(key),detail));
                    itemCunt++;
                }
                receiptItemAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}