package com.pingu.semiowl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {

    private ArrayList<User> list = new ArrayList<>();

    public UserAdapter() {

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
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_users, viewGroup, false);
        }
        ImageView ivImage = (ImageView)view.findViewById(R.id.ivImage);
        TextView tvName = (TextView)view.findViewById(R.id.tvName);

        User user = list.get(i);

        ivImage.setImageDrawable(user.getImage());
        tvName.setText(user.getName());

        return view;
    }

    public void addUser(Drawable image, String name) {
        User user = new User();
        user.setImage(image);
        user.setName(name);

        list.add(user);

        notifyDataSetChanged();
    }
}
