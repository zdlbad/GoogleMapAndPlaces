package com.example.zhangdonglin.googlemapsandplace;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class SpotInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public SpotInfoWindowAdapter(Context context){
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.spot_info,null);
    }

    public void renderWindoText(Marker marker, View view){
        String title = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.spot_info_title);

        if (!title.equals("")) {
            if (title != null){
                tvTitle.setText(title);
            }else{
                tvTitle.setText("Unknown Title");
            }
        }

        String snippet = marker.getSnippet();
        TextView tvSnippet = (TextView) view.findViewById(R.id.spot_info_content);

        if (!snippet.equals("")){
            if (snippet != null){
                tvSnippet.setText(snippet);
            }else{
                tvSnippet.setText("Unknown Content");
            }
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindoText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindoText(marker, mWindow);
        return mWindow;
    }
}
