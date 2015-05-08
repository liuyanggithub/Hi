package com.ly.hi.game.ui;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ly.hi.R;

/**
 * 猜拳游戏activity
 * @author liuyang 
 */
public class GameActivity extends Activity {
	private ImageView p1;
	private ImageView p2;
	private long beginTime = 0;
	private long endTime = 0;
	private long sumTime = 15000;
	private LinearLayout l1;
	private LinearLayout l2;
	private LinearLayout dj1;
	private LinearLayout dj2;
	private ImageView img;
	private TextView fs;
	private TextView sj;
	private long gameTime = 1000;
	private int mark = 0;
	private ImageView markImg;
	public int number1 = -1;
	public int number2 = -1;
	private Animation myAnimation_Alpha;
	private long mExitTime = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_main);
		initView();
	}

	public void initView() {
		myAnimation_Alpha = AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.game_alpha);
		p1 = (ImageView) findViewById(R.id.view1);
		p2 = (ImageView) findViewById(R.id.view2);
		l1 = (LinearLayout) findViewById(R.id.l1);
		l2 = (LinearLayout) findViewById(R.id.l2);
		dj1 = (LinearLayout) findViewById(R.id.dj1);
		dj2 = (LinearLayout) findViewById(R.id.dj2);
		img = (ImageView) findViewById(R.id.img);
		markImg = (ImageView) findViewById(R.id.mark);
		markImg.setVisibility(View.GONE);
		fs = (TextView) findViewById(R.id.js);
		sj = (TextView) findViewById(R.id.sj);
		l1.setVisibility(View.GONE);
		l2.setVisibility(View.VISIBLE);

	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			while (sumTime >= 100) {
				handle.sendEmptyMessage(11);
				sumTime = sumTime - 100;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	};
	Runnable game = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (sumTime > 100) {
				while (gameTime >= 50) {
					handle.sendEmptyMessage(12);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					gameTime = gameTime - 50;
				}
				beginTime = System.currentTimeMillis();
			}
		}
	};

	public void kaishi() {
		l2.setVisibility(View.GONE);
		l1.setVisibility(View.VISIBLE);
		dj2.setVisibility(View.GONE);
		dj1.setVisibility(View.VISIBLE);
		new Thread(runnable).start();
		new Thread(game).start();
		beginTime = System.currentTimeMillis();

	}

	public void next() {
		beginTime = System.currentTimeMillis();
		new Thread(game).start();
	}

	Runnable r = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(800);
				handle.sendEmptyMessage(13);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	public void getMark() {

		if (endTime - beginTime <= 1000) {
			mark = mark + 3;
			markImg.setVisibility(View.VISIBLE);
			markImg.setBackgroundResource(R.drawable.game_j3);
			markImg.startAnimation(myAnimation_Alpha);
			new Thread(r).start();
		} else if (endTime - beginTime > 1000 && endTime - beginTime <= 1500) {
			mark = mark + 2;
			markImg.setVisibility(View.VISIBLE);
			markImg.setBackgroundResource(R.drawable.game_j2);
			markImg.startAnimation(myAnimation_Alpha);
			new Thread(r).start();
		} else if (endTime - beginTime >= 1500) {
			mark = mark + 1;
			markImg.setVisibility(View.VISIBLE);
			markImg.setBackgroundResource(R.drawable.game_j1);
			markImg.startAnimation(myAnimation_Alpha);
			new Thread(r).start();
		}
		fs.setText("分数:" + mark);
	}

	public void getJMark() {
		if (mark > 0) {
			mark = mark - 1;
			markImg.setVisibility(View.VISIBLE);
			markImg.setBackgroundResource(R.drawable.game_jian1);
			markImg.startAnimation(myAnimation_Alpha);
			new Thread(r).start();
		}
		fs.setText("分数:" + mark);
	}

	public void zuo(View v) {
		if (gameTime <= 50) {
			endTime = System.currentTimeMillis();
			if (number1 == 1) {
				if (number2 == 3) {

					getMark();
				} else {

					getJMark();
				}
			} else if (number1 == 2) {
				if (number2 == 1) {

					getMark();
				} else {
					getJMark();
				}

			} else if (number1 == 3) {
				if (number2 == 2) {
					getMark();
				} else {
					getJMark();
				}
			}
			gameTime = 1000;
			next();
		}
	}

	public void zhong(View v) {
		if (gameTime <= 50) {
			endTime = System.currentTimeMillis();
			if (number1 == number2) {
				getMark();
			} else {
				getJMark();
			}
			gameTime = 1000;
			next();
		}
	}

	public void you(View v) {
		if (gameTime <= 50) {
			endTime = System.currentTimeMillis();
			if (number2 == 1) {
				if (number1 == 3) {
					getMark();
				} else {
					getJMark();
				}
			} else if (number2 == 2) {
				if (number1 == 1) {
					getMark();
				} else {
					getJMark();
				}

			} else if (number2 == 3) {
				if (number1 == 2) {
					getMark();
				} else {
					getJMark();
				}
			}
			gameTime = 1000;
			next();
		}
	}

	public void restart(View v) {
		dj2.setVisibility(View.GONE);
		new Thread(re).start();
		sumTime = 15000;
		mark = 0;
		fs.setText("分数:" + mark);
	}

	Handler handle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 4:
				img.setBackgroundResource(R.drawable.game_s3);
				break;
			case 3:
				img.setBackgroundResource(R.drawable.game_s2);
				break;
			case 2:
				img.setBackgroundResource(R.drawable.game_s1);
				break;
			case 1:
				img.setBackgroundResource(R.drawable.game_play);
				break;
			case 0:
				kaishi();
				break;
			case 11:
				if (sumTime != 0) {
					sj.setText(((sumTime) / 1000 + "." + (sumTime) % 1000 / 10)
							+ "秒");
				} else {
					sj.setText("时间到");
					l1.setVisibility(View.GONE);
					l2.setVisibility(View.VISIBLE);
					if (25 <= mark) {
						img.setBackgroundResource(R.drawable.game_a);
					} else if (20 <= mark && mark < 25) {
						img.setBackgroundResource(R.drawable.game_b);
					} else if (15 <= mark && mark < 20) {
						img.setBackgroundResource(R.drawable.game_c);
					} else if (10 <= mark && mark < 15) {
						img.setBackgroundResource(R.drawable.game_d);
					} else if (5 <= mark && mark < 10) {
						img.setBackgroundResource(R.drawable.game_e);
					} else if (mark < 5) {
						img.setBackgroundResource(R.drawable.game_f);
					}
					dj2.setVisibility(View.VISIBLE);
					dj1.setVisibility(View.GONE);

				}
				break;
			case 12:
				changeImg();
				break;
			case 13:

				markImg.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};

	public void changeImg() {
		getRandomNumber();
		if (number1 == 1) {
			p1.setBackgroundResource(R.drawable.game_jd);
		} else if (number1 == 2) {
			p1.setBackgroundResource(R.drawable.game_st);
		} else if (number1 == 3) {
			p1.setBackgroundResource(R.drawable.game_bu);
		}
		if (number2 == 1) {
			p2.setBackgroundResource(R.drawable.game_zjd);
		} else if (number2 == 2) {
			p2.setBackgroundResource(R.drawable.game_zst);
		} else if (number2 == 3) {
			p2.setBackgroundResource(R.drawable.game_zbu);
		}
	}

	Runnable re = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			for (int i = 4; i >= 0; i--) {
				handle.sendEmptyMessage(i);
				if (i != 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Thread(re).start();
	}

	private void getRandomNumber() {
		Random r = new Random();
		number1 = Math.abs(r.nextInt(3) + 1);
		number2 = Math.abs(r.nextInt(3) + 1);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - mExitTime > 2000) {
				Toast.makeText(this, "再按一次退出游戏", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {

				// for (int i = 0; i < activityList.size(); i++) {
				// if (null != activityList.get(i)) {
				// activityList.get(i).finish();
				// }
				// }
				System.exit(0);// 否则退出程序
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void jk() {
		mExitTime = System.currentTimeMillis();
	}
}
