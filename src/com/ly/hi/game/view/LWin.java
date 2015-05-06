package com.ly.hi.game.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.ly.hi.R;

public class LWin extends View {

	private Paint mPaint;

	private Rect mBounds;

	public LWin(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBounds = new Rect();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		mPaint.setAlpha(0);
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
		mPaint.setTextSize(30);
		Bitmap  bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.game_l );
			float textwidth = mBounds.width();
			float texthight = mBounds.height();
			//canvas.drawText(text, getWidth() / 2 - textwidth / 2, getHeight()
				//	/ 2 + texthight / 2, mPaint);
			//canvasdrawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint)
			canvas.drawBitmap(bitmap, 0, 0, null);
	}

}
