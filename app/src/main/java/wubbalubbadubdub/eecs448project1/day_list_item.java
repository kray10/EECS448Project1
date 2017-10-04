package wubbalubbadubdub.eecs448project1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

/**
 * Created by simonyang on 2017/9/29.
 */

public class day_list_item extends BaseAdapter {
    private List<dayitem> mitem;
    private LayoutInflater mInflater;

    public day_list_item(LayoutInflater inflater, List<dayitem> items) {
        mitem = items;
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        return mitem.size();
    }

    @Override
    public Object getItem(int i) {
        return mitem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View viewInfromation = mInflater.inflate(R.layout.day_list_item, null);
        dayitem Item = mitem.get(i);
        TextView date = viewInfromation.findViewById(R.id.daytext);
//        ImageButton deletedate = viewInfromation.findViewById(R.id.deleteButton);
//        deletedate.setTag(i);
//        deletedate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                int position = (int) view.getTag();
//                removeItem(position);
//                notifyDataSetChanged();
//            }
//        });
//        ImageButton copytimeslot = viewInfromation.findViewById(R.id.copytimeslot);
//        copytimeslot.setTag(i);
//        copytimeslot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //PopupWindow alldate = (PopupWindow) new
//
//                //notifyDataSetChanged();
//            }
//        });

        date.setText(getdate(Item.getDay(), Item.getMonth(), Item.getYear()));
        return viewInfromation;
    }

    public String getdate(int day, int month, int year) {
        String d = Integer.toString(day);
        String m = Integer.toString(month);
        String y = Integer.toString(year);
        return ( m + "/" + d + "/" +y);
    }

    public void removeItem(int position) {
        mitem.remove(position);
        notifyDataSetChanged(); //refresh your listview based on new data

    }
}