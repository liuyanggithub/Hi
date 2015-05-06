package com.ly.hi.game.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

public class CounterView extends View implements OnClickListener {

	private Paint mPaint;

	private Rect mBounds;

	private int mCount;

	public CounterView(Context context) {
		super(context);
	}

	public CounterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBounds = new Rect(0,0, 50, 50);
		setOnClickListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//∏¯ª≠± …Ë÷√
           mPaint.setColor(Color.BLUE);
           canvas.drawRect(0,1 , getWidth(),getHeight() , mPaint);
           mPaint.setColor(Color.YELLOW);
           mPaint.setTextSize(30);
           String text = String.valueOf(mCount);
           mPaint.getTextBounds(text, 0, text.length(), mBounds);
           float  textwidth=mBounds.width();
           float  texthight=mBounds.height();
           canvas.drawText(text, getWidth() / 2 - textwidth / 2, getHeight() / 2
                   + texthight / 2, mPaint);
	}
	public void onClick(View arg0) {
		mCount++;
		invalidate();
	}
}
