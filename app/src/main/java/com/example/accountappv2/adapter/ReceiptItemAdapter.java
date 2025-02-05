package com.example.accountappv2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.accountappv2.R;
import com.example.accountappv2.data.BillData;
import com.example.accountappv2.data.ReceiptItem;

import java.util.List;

public class ReceiptItemAdapter extends BaseAdapter {
    private String TAG = "ReceiptItemAdapter";
    private Context context;
    private List<ReceiptItem> list;
    public ReceiptItemAdapter(Context _context,List<ReceiptItem> _list){
        this.context = _context;
        this.list = _list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 第一次載入建立View，其餘復用 View
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.receipt_item, null);
            holder = new ViewHolder();
            holder.txt_receiptItem_id = (TextView) convertView.findViewById(R.id.txt_receiptItem_id);
            holder.txt_receiptItem_detail = (TextView) convertView.findViewById(R.id.txt_receiptItem_detail);
            // 打標籤
            convertView.setTag(holder);

        } else {
            // 從標籤中取得數據
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txt_receiptItem_id.setText(""+list.get(position).getId());
        holder.txt_receiptItem_detail.setText(list.get(position).getDetail());

        return convertView;
    }

    class ViewHolder {
        TextView txt_receiptItem_id,txt_receiptItem_detail;
    }
}
