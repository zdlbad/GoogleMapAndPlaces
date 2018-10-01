package com.example.zhangdonglin.googlemapsandplace;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListAdapter extends ArrayAdapter<Object>
{
    private final List<Object> mItems;
    private final Context mContext;
    private final LayoutInflater mInflater;

    public ListAdapter(Context context, ArrayList<Object> items)
    {
        super(context, R.layout.list_item_iterator, items);
        mContext = context;
        mItems = items;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        final View rowView= mInflater.inflate(R.layout.list_item_iterator, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.table_row_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.table_row_image);
        ImageView navigationImage = (ImageView) rowView.findViewById(R.id.table_row_navigation_image);
        if (mItems.get(position) instanceof Toilet){
            imageView.setImageResource(R.drawable.ic_toilet_3);
            txtTitle.setText(mItems.get(position).toString());
            final LatLng selectedPostion = new LatLng(((Toilet) mItems.get(position)).getLat(), ((Toilet) mItems.get(position)).getLon());
            navigationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapActivity mapActivity = (MapActivity) mContext;
                    mapActivity.setDestLatlng(selectedPostion.latitude, selectedPostion.longitude);
                    mapActivity.navigationToGoogle();
                }
            });
        }
        if (mItems.get(position) instanceof ParkingSpot){
            imageView.setImageResource(R.drawable.ic_parking_2);
            txtTitle.setText(mItems.get(position).toString());
            final LatLng selectedPostion = new LatLng(((ParkingSpot) mItems.get(position)).getLat(), ((ParkingSpot) mItems.get(position)).getLon());
            navigationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapActivity mapActivity = (MapActivity) mContext;
                    mapActivity.setDestLatlng(selectedPostion.latitude, selectedPostion.longitude);
                    mapActivity.navigationToGoogle();
                }
            });
        }
        if (mItems.get(position) instanceof MetroStation){
            imageView.setImageResource(R.drawable.ic_train);
            txtTitle.setText(mItems.get(position).toString());
            final LatLng selectedPostion = new LatLng(((MetroStation) mItems.get(position)).getLat(), ((MetroStation) mItems.get(position)).getLon());
            navigationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapActivity mapActivity = (MapActivity) mContext;
                    mapActivity.setDestLatlng(selectedPostion.latitude, selectedPostion.longitude);
                    mapActivity.navigationToGoogle();
                }
            });
        }

        if (mItems.get(position) instanceof Garden){
            imageView.setImageResource(R.drawable.ic_garden);
            txtTitle.setText(mItems.get(position).toString());
            final LatLng selectedPostion = new LatLng(((Garden) mItems.get(position)).getLat(), ((Garden) mItems.get(position)).getLon());
            navigationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapActivity mapActivity = (MapActivity) mContext;
                    mapActivity.setDestLatlng(selectedPostion.latitude, selectedPostion.longitude);
                    mapActivity.navigationToGoogle();
                }
            });
        }

        return rowView;
//        final LatLng selectedPostion = new LatLng(((MetroStation) mItems.get(position)).getLat(), ((MetroStation) mItems.get(position)).getLon());
//        navigationImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MapActivity mapActivity = (MapActivity) mContext;
//                mapActivity.setDestLatlng(selectedPostion.latitude, selectedPostion.longitude);
//                mapActivity.navigationToGoogle();
//            }
//        });
    }

    @Override
    public int getCount()
    {
        return mItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mItems.get(position);
    }

}