package com.example.zhangdonglin.googlemapsandplace;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

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
    public View getView(final int position, View view, ViewGroup parent)
    {
        final View rowView= mInflater.inflate(R.layout.list_item_iterator, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.table_row_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.table_row_image);
        ImageView navigationImage = (ImageView) rowView.findViewById(R.id.table_row_navigation_image);
        ImageView reportImage = (ImageView) rowView.findViewById(R.id.table_row_report_image);

        if (mItems.get(position) instanceof Toilet){
            imageView.setImageResource(R.drawable.ic_toilet_3);
            txtTitle.setText(mItems.get(position).toString());
            final MapActivity mapActivity = (MapActivity) mContext;
            final LatLng selectedPostion = new LatLng(((Toilet) mItems.get(position)).getLat(), ((Toilet) mItems.get(position)).getLon());
            navigationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapActivity.setDestLatlng(selectedPostion.latitude, selectedPostion.longitude);
                    mapActivity.navigationToGoogle();
                }
            });
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapActivity.moveToMarker(
                            mapActivity.toiletManager.markerList.get(position)
                    );
                }
            });
        }

        if (mItems.get(position) instanceof ParkingSpot){
            imageView.setImageResource(R.drawable.ic_parking_2);
            txtTitle.setText(mItems.get(position).toString());
            final LatLng selectedPostion = new LatLng(((ParkingSpot) mItems.get(position)).getLat(), ((ParkingSpot) mItems.get(position)).getLon());
            final MapActivity mapActivity = (MapActivity) mContext;
            navigationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapActivity.setDestLatlng(selectedPostion.latitude, selectedPostion.longitude);
                    mapActivity.navigationToGoogle();
                }
            });
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapActivity.moveToMarker(
                            mapActivity.parkingManager.markerList.get(position)
                    );
                }
            });
        }

        if (mItems.get(position) instanceof MetroStation){
            imageView.setImageResource(R.drawable.ic_train);
            txtTitle.setText(mItems.get(position).toString());
            final LatLng selectedPostion = new LatLng(((MetroStation) mItems.get(position)).getLat(), ((MetroStation) mItems.get(position)).getLon());
            final MapActivity mapActivity = (MapActivity) mContext;
            navigationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapActivity.setDestLatlng(selectedPostion.latitude, selectedPostion.longitude);
                    mapActivity.navigationToGoogle();
                }
            });
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapActivity.moveToMarker(
                            mapActivity.metroStationManager.markerList.get(position)
                    );
                }
            });
        }

        if (mItems.get(position) instanceof Garden){
            imageView.setImageResource(R.drawable.ic_garden);
            txtTitle.setText(mItems.get(position).toString());
            final LatLng selectedPostion = new LatLng(((Garden) mItems.get(position)).getLat(), ((Garden) mItems.get(position)).getLon());
            final MapActivity mapActivity = (MapActivity) mContext;
            navigationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapActivity.setDestLatlng(selectedPostion.latitude, selectedPostion.longitude);
                    mapActivity.navigationToGoogle();
                }
            });
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapActivity.moveToMarker(
                            mapActivity.gardenManager.markerList.get(position)
                    );
                }
            });
        }

        if (mItems.get(position) instanceof BuildingSpot){
            imageView.setImageResource(R.drawable.ic_building_2);
            txtTitle.setText(mItems.get(position).toString());
            reportImage.setVisibility(View.VISIBLE);
            final LatLng selectedPostion = new LatLng(((BuildingSpot) mItems.get(position)).getY_coordinate(), ((BuildingSpot) mItems.get(position)).getX_coordinate());
            final MapActivity mapActivity = (MapActivity) mContext;
            navigationImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapActivity.setDestLatlng(selectedPostion.latitude, selectedPostion.longitude);
                    mapActivity.navigationToGoogle();
                }
            });
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapActivity.buildingManager.reportBuildingSpot = (BuildingSpot) mItems.get(position);
                    mapActivity.moveToMarker(
                            mapActivity.buildingManager.markerList.get(position)
                    );
                    mapActivity.updateInfoDialog();
                    mapActivity.buildingInfoDialog.show();
                }
            });
            reportImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapActivity.buildingManager.reportBuildingSpot = (BuildingSpot) mItems.get(position);
                    mapActivity.resetReportDialog();
                    mapActivity.buildingReportDialog.show();
                }
            });
        }

        return rowView;
//        final LatLng selectedPostion = new LatLng(((MetroStation) mItems.get(position)).getY_coordinate(), ((MetroStation) mItems.get(position)).getLon());
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