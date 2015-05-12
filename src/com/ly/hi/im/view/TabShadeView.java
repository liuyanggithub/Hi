package com.ly.hi.im.view;

import com.ly.hi.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 左右滑动tab图标和文字渐变view
 */
public class TabShadeView extends FrameLayout {
    private final String TAG = TabShadeView.class.getSimpleName();

    private ImageView mImageView;
    private TextView mTextView;

    private Drawable normalDrawableSrc;
    private Drawable selectDrawableSrc;
    private int normalTextColor = 0xff999999;
    private int selectTextColor = 0xff45C01A;
    private String text = "TAB";
    private int textSize = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());

    /* 透明度 0.0-1.0*/
    private float mAlpha = 1f;

    private static final String INSTANCE_STATE = "instance_state";
    private static final String STATE_ALPHA = "state_alpha";

    public TabShadeView(Context context) {
        super(context);
    }

    public TabShadeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_tab_shade, this);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mTextView = (TextView) findViewById(R.id.textView);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.TabShadeView);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.TabShadeView_normalDrawableSrc:
                    normalDrawableSrc = typedArray.getDrawable(attr);
                    break;
                case R.styleable.TabShadeView_selectDrawableSrc:
                    selectDrawableSrc = typedArray.getDrawable(attr);
                    break;
                case R.styleable.TabShadeView_normalTextColor:
                    normalTextColor = typedArray.getColor(attr, 0xff999999);
                    break;
                case R.styleable.TabShadeView_selectTextColor:
                    selectTextColor = typedArray.getColor(attr, 0xff45C01A);
                    break;
                case R.styleable.TabShadeView_text:
                    text = typedArray.getString(attr);
                    break;
                case R.styleable.TabShadeView_textSize:
                    textSize = (int) typedArray.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    break;
            }
        }
        setImageView(0);
        mTextView.setText(text);
//        Log.d(TAG, "textSize=" + textSize);
//        mTextView.setTextSize(textSize);
        setTextView(0);
        typedArray.recycle();
    }

    public void setImageView(float alpha) {
        if (mImageView != null) {
            mImageView.setBackgroundDrawable(normalDrawableSrc);
            mImageView.setImageDrawable(selectDrawableSrc);
            int a = (int) Math.ceil((255 * alpha));
            mImageView.getBackground().setAlpha(255 - a);
            mImageView.setAlpha(a);
        }

    }

    public void setTextView(float alpha) {
        if (mTextView != null) {
            String nTextColor = Integer.toHexString(normalTextColor);
            int R1 = 0, G1 = 0, B1 = 0;
            if (!TextUtils.isEmpty(nTextColor) && nTextColor.length() >= 6) {
                R1 = (int) (Integer.parseInt(nTextColor.substring(nTextColor.length() - 6, nTextColor.length() - 4), 16) * (1 - alpha));
                G1 = (int) (Integer.parseInt(nTextColor.substring(nTextColor.length() - 4, nTextColor.length() - 2), 16) * (1 - alpha));
                B1 = (int) (Integer.parseInt(nTextColor.substring(nTextColor.length() - 2, nTextColor.length()), 16) * (1 - alpha));
            }
            String sTextColor = Integer.toHexString(selectTextColor);
            int R2 = 0, G2 = 0, B2 = 0;
            if (!TextUtils.isEmpty(sTextColor) && sTextColor.length() >= 6) {
                R2 = (int) (Integer.parseInt(sTextColor.substring(sTextColor.length() - 6, sTextColor.length() - 4), 16) * alpha);
                G2 = (int) (Integer.parseInt(sTextColor.substring(sTextColor.length() - 4, sTextColor.length() - 2), 16) * alpha);
                B2 = (int) (Integer.parseInt(sTextColor.substring(sTextColor.length() - 2, sTextColor.length()), 16) * alpha);
            }
            int R3, G3, B3;
            R3 = R1 + R2;
            G3 = G1 + G2;
            B3 = B1 + B2;
            String R, G, B;
            if (R3 < 16) {
                R = "0" + Integer.toHexString(R3);
            } else {
                R = Integer.toHexString(R3);
            }
            if (G3 < 16) {
                G = "0" + Integer.toHexString(G3);
            } else {
                G = Integer.toHexString(G3);
            }
            if (B3 < 16) {
                B = "0" + Integer.toHexString(B3);
            } else {
                B = Integer.toHexString(B3);
            }
            String rgb = "#" + R + G + B;
//            Log.d(TAG, rgb);
            mTextView.setTextColor(Color.parseColor(rgb));
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(STATE_ALPHA, mAlpha);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        try {
            super.onRestoreInstanceState(state);
        } catch (Exception e) {
        }
        state = null;
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATE_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
