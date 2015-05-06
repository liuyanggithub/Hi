package com.ly.hi.game.view;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.ly.hi.R;

public class LView extends View {

	public int number=-1;

	private Paint mPaint;

	private Rect mBounds;

	public LView(Context context, AttributeSet attrs) {
		super(context, attrs);
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
		if (number != 0) {
			getRandomNumber();
			 Bitmap bitmap=null;
			if(number==1){
				bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.game_jd );
			}else if(number==2){
				bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.game_st );
			}else if(number==3){
				bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.game_bu );
			}
			
			//canvas.drawText(text, getWidth() / 2 - textwidth / 2, getHeight()
				//	/ 2 + texthight / 2, mPaint);
			//canvasdrawBitmap(Bitmap bitmap, Rect src, Rect dst, Paint paint)
			canvas.drawBitmap(bitmap, 0, 0, null);
		}
	}
	public void begin() {
		number=-1;
		invalidate();
	}

	private void getRandomNumber() {
		Random r = new Random();
		number = Math.abs(r.nextInt(3)+1);  
	}

	public void next() {
		invalidate();
	}
	public int getNumber(){
		return number;
	}
	

}
