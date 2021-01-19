package com.example.countryapp.view;

import android.content.Context;
import android.widget.ImageView;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.countryapp.R;

public class Util {

    public static void loadImage(ImageView view, String url, CircularProgressDrawable progressDrawable){
        // 이미지 로드할 때 옵션 설정
        RequestOptions options = new RequestOptions()
                .placeholder(progressDrawable) // 이미지 로딩하는 동안 보여줄 원형 프로그레스
                .error(R.mipmap.ic_launcher_round); // url 로드할 때 error 발생시 보여줄 이미지
        Glide.with(view.getContext())
                .setDefaultRequestOptions(options)
                .load(url)
                .into(view);
    }

    // 이미지 로딩 중에 보여줄 원형 프로그레스 만들기
    public static CircularProgressDrawable getProgressDrawable(Context context){
        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(context);
        progressDrawable.setStrokeWidth(10f);
        progressDrawable.setCenterRadius(50f);
        progressDrawable.start();
        return progressDrawable;
    }
}
