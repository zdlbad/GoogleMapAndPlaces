package com.example.zhangdonglin.googlemapsandplace.View;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zhangdonglin.googlemapsandplace.R;

public class SliderAdapter extends PagerAdapter{

    Context context;
    LayoutInflater layoutInflater;

    public int[] slide_images = {
            R.drawable.swivel_find_toilet,
            R.drawable.swivel_find_parking,
            R.drawable.swivel_building_rating_intro,
            R.drawable.swivel_make_report,
            R.drawable.swivel_check_report,
    };

    public String[] slide_headings = {
            "Find wheelchair accessible public toilets.",
            "Find available parking places with wanted duration.",
            "Find building accessiblilty before you go.",
            "Make building accessiblilty report to help improve.",
            "Scan building information made by others.",
    };

    public SliderAdapter(Context context) {
        Log.d("SliderAdapter", "===== new a sliderAdapter");
        this.context = context;
    }




    @Override
    public int getCount() {
        return slide_images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Log.d("SliderAdapter", "===== new a item");
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_adapter_layout, container, false);

        ImageView sliderImageView = (ImageView) view.findViewById(R.id.slide_image);
        TextView sliderHeading = (TextView) view.findViewById(R.id.slide_heading);

        sliderImageView.setImageResource(slide_images[position]);
        sliderHeading.setText(slide_headings[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout)object);
    }
}
