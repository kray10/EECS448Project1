package wubbalubbadubdub.eecs448project1;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import wubbalubbadubdub.eecs448project1.R;
import wubbalubbadubdub.eecs448project1.data.Task;

/**
 * Created by martin on 10/5/2017.
 */

class Task_adapter extends BaseAdapter {

    List<Task> list;
    private LayoutInflater mInflater;



    public Task_adapter( LayoutInflater inflater, List<Task> items) {
        list=items;
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewInfromation = mInflater.inflate(R.layout.task_adapter, null);
        Task Item = list.get(position);
        TextView txt = viewInfromation.findViewById(R.id.tasktext);
        ImageButton deletedate = viewInfromation.findViewById(R.id.deleteButton);
        deletedate.setTag(position);
        deletedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                removeItem(position);
                notifyDataSetChanged();
            }
            });
            txt.setText(getname(position));
        return viewInfromation;
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyDataSetChanged(); //refresh your listview based on new data
    }

    public String getname(int position) {
       String name =list.get(position).getTaskName();
        return name;
    }


}
