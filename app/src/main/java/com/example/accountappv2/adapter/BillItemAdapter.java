package com.example.accountappv2.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.accountappv2.R;
import com.example.accountappv2.data.BillData;

import java.util.List;

public class BillItemAdapter extends BaseAdapter {
    private Context context;
    private List<BillData> list;
    public BillItemAdapter(Context _context,List<BillData> _list){
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
                    R.layout.bill_item, null);
            holder = new ViewHolder();
            holder.cv_billItem_item = (CardView) convertView.findViewById(R.id.cv_billItem_item);
            holder.txt_billItem_day = (TextView) convertView.findViewById(R.id.txt_billItem_day);
            holder.txt_billItem_date = (TextView) convertView.findViewById(R.id.txt_billItem_date);
            holder.txt_billItem_type = (TextView) convertView.findViewById(R.id.txt_billItem_type);
            holder.txt_billItem_detail = (TextView) convertView.findViewById(R.id.txt_billItem_detail);
            holder.txt_billItem_cost = (TextView) convertView.findViewById(R.id.txt_billItem_cost);
            // 打標籤
            convertView.setTag(holder);

        } else {
            // 從標籤中取得數據
            holder = (ViewHolder) convertView.getTag();
        }

        String year = list.get(position).getYear();
        String month = list.get(position).getMonth();
        String day = list.get(position).getDay();
        int cost = Integer.parseInt(list.get(position).getCost());
        if(cost >0){
            holder.cv_billItem_item.setCardBackgroundColor(Color.rgb(255,231,112));
        }else {
            holder.cv_billItem_item.setCardBackgroundColor(Color.rgb(119,202,177));
        }

        holder.txt_billItem_day.setText(day);
        holder.txt_billItem_date.setText(year+"/"+month+"/"+day);
        holder.txt_billItem_type.setText(list.get(position).getType());
        holder.txt_billItem_detail.setText(list.get(position).getDetail());
        holder.txt_billItem_cost.setText(""+cost);


        return convertView;
    }

    class ViewHolder {
        CardView cv_billItem_item;
        TextView txt_billItem_day,txt_billItem_date,txt_billItem_type,txt_billItem_detail,txt_billItem_cost;
    }
}
