package com.cavaliers.mylocalbartender.menu.adapter;

/**
 * Team Cavaliers
 */

import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cavaliers.mylocalbartender.R;
import com.cavaliers.mylocalbartender.menu.model.ItemSlideMenu;

import android.content.Context;

import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *
 */
public class SlidingMenuAdapter extends BaseAdapter {

    private Context context;
    private List<ItemSlideMenu> lstItem;

    public SlidingMenuAdapter(Context context, List<ItemSlideMenu> lstItem) {
        this.context = context;
        this.lstItem = lstItem;
    }

    @Override
    public int getCount() {
        return lstItem.size();
    }

    @Override
    public Object getItem(int position) {
        return lstItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = View.inflate(context, R.layout.item_sliding_menu, null);
        ImageView img = (ImageView) v.findViewById(R.id.item_image);
        TextView tv = (TextView) v.findViewById(R.id.item_title);
        ItemSlideMenu item = lstItem.get(position);
        img.setImageResource(item.getImgId());
        tv.setText(item.getTitle());

        return v;
    }

}
