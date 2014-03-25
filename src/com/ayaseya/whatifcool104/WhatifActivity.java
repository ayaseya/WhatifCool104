package com.ayaseya.whatifcool104;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.leaderboard.Leaderboards.SubmitScoreResult;
import com.google.android.gms.games.leaderboard.ScoreSubmissionData;
import com.google.example.games.basegameutils.BaseGameActivity;

public class WhatifActivity extends BaseGameActivity
		implements ViewFactory, Leaderboards.SubmitScoreResult {

	// LogCat用のタグを定数で定義する
	public static final String TAG = "Test";

	private TrumpView[] trumpView = new TrumpView[6];
	private RoundedImageView[] trumpBackView = new RoundedImageView[6];

	private Deck deck;
	private Deck record;

	private int statusbarHeight;

	private boolean flipAnimFlag = true;// アニメーション中はfalseとなり画像をクリックできない
	private boolean moveAnimFlag = true;// アニメーション中はfalseとなり画像をクリックできない
	private boolean dealAnimFlag = true;// アニメーション中はfalseとなり画像をクリックできない

	private float centerX;// Y軸回転の中心点(X座標)を設定
	private float centerY;// Y軸回転の中心点(Y座標)を設定

	private boolean replaceFlag = true;// onCreate()後にonWindowFocusChanged()で一度だけ処理を実行するためのフラグ

	private int animCount = 0;// 5枚配るアニメ処理で何枚目であるかカウントする変数
	private int counter = 0;// 

	private TextView guideView;// ガイド
	private TextSwitcher count;// カウンターViewのインスタンス
	private int width = 0;// 端末の横幅
	private int height = 0;// 端末の高さ
	private int txtSwitchFontSize = 20; //カウンターのフォントサイズ

	private int scrollHeight;//スクロールViewの移動量を格納する変数

	private TextView wagerView;
	private TextView winView;
	private TextView paidView;
	private TextView creditView;

	private enum DPI {
		ldpi, mdpi, hdpi, xhdpi, xxhdpi
	}

	private DPI dpi;
	private boolean coinFlag = false;// trueでコインの加算処理中のため入力を受け付けないようにする
	private boolean skipFlag = false;// trueでコインの加算処理をスキップ

	private int timerCounter;// タイマー処理のカウントアップに使用する変数

	private int[] rate52 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3,
			3, 3, 4, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 10, 10, 12, 12, 14, 14, 16,
			18, 20, 22, 25, 30, 35, 40, 45, 50, 55, 60, 80, 100 };
	private int[] ratePoker = { 0, 100, 20, 5, 2, 1 };
	private TextView msg;

	private Coin coin;

	private Resources res;

	private ScrollView bonusScroll1;
	private ScrollView bonusScroll2;
	private ScrollView paysScroll1;
	private ScrollView paysScroll2;
	private ScrollView hitsScroll1;

	private int scrollLines = 3;// スクロールViewで表示させ行数

	private int timeTranslate = 200;//200
	private int timeDeal = 50;//50
	private int timeFlip = 90;//90

	private int scrollSleepTime = 250;//250
	private int msgSleepTime = 500;//500

	private Handler handler = new Handler();

	private int counterWidth = 0;
	private int counterHeight = 0;

	private int margin = 10;

	private Configuration config;

	private TextView counterMsg;

	private TextView txt;

	private int[] idList;

	private int trumpViewWidth = 60;

	private int trumpViewHeight = 90;

	private float ratio;

	private float scale;

	private int fontSize = 16;

	private Deck standard;

	private boolean game = false;//ゲーム中であるか否かの判断

	private SoundPool soundPool;

	private boolean ringerMode = false;

	private boolean isPlugged = false;

	private int se_beep;
	private int se_cancel;
	private int se_clear;
	private int se_coin;
	private int se_enter;
	private int se_even;
	private int se_loser;
	private int se_msg;
	private int se_on;
	private int se_score;
	private int se_peep;
	private int se_trump_flip;
	private int se_trump_select;
	private int se_winner;
	private int streamId;

	/** マナーモード等の状態取得Intent Filter */
	private static IntentFilter ringerModeIntentFilter = new IntentFilter(AudioManager.RINGER_MODE_CHANGED_ACTION);

	/** 指令を飛ばすBroadCastReceiver */
	private static BroadcastReceiver ringerModeStateChangeReceiver = null;

	/** ヘッドセットプラグ状態取得Intent Filter */
	private static IntentFilter plugIntentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);

	/** 指令を飛ばすBroadCastReceiver */
	private static BroadcastReceiver plugStateChangeReceiver = null;

	// ポーカーの役で成立した回数をカウントする変数
	int RFcount = 0;
	int SFcount = 0;
	int FKcount = 0;
	int FHcount = 0;
	int FLcount = 0;

	int pokerPosition = 0;// ポーカーのスクロールビューの現在位置
	int pokerPrevPosition = 0;// ポーカーのスクロールビューの現在位置

	private static final String KEY_CREDIT = "DATA_COIN";
	private Button btn;

	private User user;

	private long start;
	private long stop;
	private Dialog alert;
	private long clearTime;

	private PendingResult<SubmitScoreResult> pr;

	/* ********** ********** ********** ********** */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE); // タイトルバーを非表示にする

		super.onCreate(savedInstanceState);

		Log.v(TAG, "/* ********** ********** ********** ********** */");

		user = new User();
		File file = this.getFileStreamPath("SaveData.dat");
		boolean isExists = file.exists();
		if (isExists) {
			Log.v(TAG, "> SaveData.datが存在しました");
			try {
				FileInputStream fis = openFileInput("SaveData.dat");
				ObjectInputStream ois = new ObjectInputStream(fis);
				user = (User) ois.readObject();
				ois.close();

				// 最後の起動時に横画面だった場合、横画面で起動する
				if (user.rotate == Configuration.ORIENTATION_LANDSCAPE) {
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					user.rotate = 0;
				}

			} catch (Exception e) {
				Log.d(TAG, "Error");
			}
		} else {
			Log.v("Test", "SaveData.datが存在しませんでした");
		}
		setContentView(R.layout.whatif_layout);

		findViewById(R.id.black).bringToFront();

		//端末の画面の向きを取得するためリソースを取得する
		config = getResources().getConfiguration();

		// トランプ(52枚)を管理するDeckクラスのインスタンスの取得
		deck = new Deck(this.getApplicationContext());
		// トランプ(52枚)の生成とシャッフル
		deck.shuffle(this.getApplicationContext());
		// 場札に置いた順番を記録するインスタンスを取得する
		record = new Deck(this.getApplicationContext());

		standard = new Deck(this.getApplicationContext());
		standard.standard(this.getApplicationContext());

		// コイン処理のインスタンスを取得する
		coin = new Coin();

		// ldpi : 120 dpi
		// mdpi : 160 dpi
		// hdpi : 240 dpi
		// xhdpi : 320 dpi
		// xxhdpi : 480 dpi
		// txdpi : 213 dpi (Nexus 7)

		// ldpi : mdpi : hdpi : xdhpi : xxhdpi : tvdpi = 0.75 : 1 : 1.5 : 2 : 3 : 1.33125

		// Activityを継承していないため、getWindowManager()メソッドは利用できない
		// Displayクラスのインスタンスを取得するため
		// 引数のContextを使用してWindowManagerを取得する
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		// Displayインスタンスを取得する
		Display display = wm.getDefaultDisplay();

		// DPI を取得する
		int _dpi = getResources().getDisplayMetrics().densityDpi;

		// dpi を元に比率を計算する ( dpi ÷ 基準値(mdpi) )
		ratio = _dpi / 160f;

		scale = getResources().getDisplayMetrics().scaledDensity;

		width = display.getWidth();
		height = display.getHeight();

		if (ratio == 0.75) {
			dpi = DPI.ldpi;
		} else if (ratio == 1.0) {
			dpi = DPI.mdpi;
		} else if (ratio == 1.5) {
			dpi = DPI.hdpi;
		} else if (ratio == 2.0) {
			dpi = DPI.xhdpi;
		} else if (ratio >= 3.0) {
			dpi = DPI.xxhdpi;
		}
		Log.v(TAG, "> " + dpi + "(" + _dpi + "dpi)" + " density=" + ratio + " w=" + width + " h=" + height + " s=" + scale);

		/* ********** ********** ********** ********** */

		res = getResources();

		for (int i = 0; i <= 5; i++) {
			int id = res.getIdentifier("trumpView" + i, "id", getPackageName());
			trumpView[i] = (TrumpView) findViewById(id);

		}
		for (int i = 0; i <= 5; i++) {
			int id = res.getIdentifier("roundedImageView" + i, "id", getPackageName());
			trumpBackView[i] = (RoundedImageView) findViewById(id);
		}

		// リスナー登録
		trumpView[0].setOnClickListener(layoutListener);// Y軸回転テスト用のクリックリスナー
		trumpView[1].setOnClickListener(hand1Listener);
		trumpView[2].setOnClickListener(hand2Listener);
		trumpView[3].setOnClickListener(hand3Listener);
		trumpView[4].setOnClickListener(hand4Listener);
		trumpView[5].setOnClickListener(hand5Listener);

		findViewById(R.id.collectBtn).setOnClickListener(collectBtnListener);
		//		findViewById(R.id.hdBtn).setOnClickListener(hbBtnListener);
		findViewById(R.id.betBtn).setOnClickListener(betBtnListener);
		findViewById(R.id.repeatBtn).setOnClickListener(repeatBtnListener);
		findViewById(R.id.dealBtn).setOnClickListener(dealBtnListener);

		findViewById(R.id.msgLayout).setOnClickListener(msgListener);

		findViewById(R.id.sign_in_button).setOnClickListener(SignInListener);
		findViewById(R.id.sign_out_button).setOnClickListener(SignOutListener);

		wagerView = (TextView) findViewById(R.id.wager);//
		winView = (TextView) findViewById(R.id.win);//
		paidView = (TextView) findViewById(R.id.paid);//
		creditView = (TextView) findViewById(R.id.credit);//

		bonusScroll2 = (ScrollView) findViewById(R.id.bonusScroll2);
		paysScroll2 = (ScrollView) findViewById(R.id.paysScroll2);
		hitsScroll1 = (ScrollView) findViewById(R.id.hitsScroll1);

		// 非表示
		findViewById(R.id.msgLayout).setVisibility(View.INVISIBLE);

		counterMsg = (TextView) findViewById(R.id.counterMsg);

		ringerModeStateChangeReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// 接続状態を取得
				if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
					if (intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1) == AudioManager.RINGER_MODE_NORMAL) {
						// 通常モード
						ringerMode = true;
						//						Log.v(TAG, "通常モード");
					} else {
						// マナーモードorサイレントモード
						ringerMode = false;
						//						Log.v(TAG, "マナーモードorサイレントモード");
					}
				}
			}
		};

		plugStateChangeReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// 接続状態を取得
				if (intent.getIntExtra("state", 0) > 0) {
					isPlugged = true;
					//					Log.v(TAG, "プラグIN");
				} else {
					isPlugged = false;
					//					Log.v(TAG, "プラグOUT");
				}
			}
		};

		// AdView をリソースとしてルックアップしてリクエストを読み込む
		AdView adView = (AdView) this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		loadCoin();
		fixFont();

		pr = new PendingResult<Leaderboards.SubmitScoreResult>() {

			@Override
			public void setResultCallback(ResultCallback<SubmitScoreResult> arg0) {
				Log.v(TAG, "setResultCallback" + arg0);

			}

			@Override
			public SubmitScoreResult e(Status arg0) {
				Log.v(TAG, "SubmitScoreResult" + arg0);
				return null;
			}

			@Override
			public SubmitScoreResult await(long arg0, TimeUnit arg1) {
				Log.v(TAG, "SubmitScoreResult await" + arg0 + "|" + arg1);
				return null;
			}

			@Override
			public SubmitScoreResult await() {
				Log.v(TAG, "SubmitScoreResult await");
				return null;
			}
		};

		ResultCallback<SubmitScoreResult> rc = new ResultCallback<Leaderboards.SubmitScoreResult>() {

			@Override
			public void onResult(SubmitScoreResult arg0) {
				Log.v(TAG, "TonResult" + arg0);

			}
		};

		//		Log.v(TAG, "onCreate()");

	}// onCreate()

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		// Portrait(縦長)
		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			vertical();

		}
		// Landscape(横長)
		else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			horizontal();
		}

		//		if (coin.getCredit() == 0) {
		//			present();
		//		}

		//		Log.v(TAG, "Counter=" + counter);
		//		Log.v(TAG, "onWindowFocusChanged()");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//		Log.v(TAG, "onRestart()");
	}

	@Override
	protected void onStart() {
		super.onStart();

		//		Log.v(TAG, "onStart()");
	}

	@Override
	protected void onResume() {
		super.onResume();
		// headset plug状態 Broadcast Receiver登録
		registerReceiver(plugStateChangeReceiver, plugIntentFilter);

		registerReceiver(ringerModeStateChangeReceiver, ringerModeIntentFilter);

		soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);

		se_beep = soundPool.load(this, R.raw.se_beep, 1);
		se_cancel = soundPool.load(this, R.raw.se_cancel, 1);
		se_clear = soundPool.load(this, R.raw.se_clear, 1);
		se_coin = soundPool.load(this, R.raw.se_coin, 1);
		se_enter = soundPool.load(this, R.raw.se_enter, 1);
		se_even = soundPool.load(this, R.raw.se_even, 1);
		se_loser = soundPool.load(this, R.raw.se_loser, 1);
		se_msg = soundPool.load(this, R.raw.se_msg, 1);
		se_on = soundPool.load(this, R.raw.se_on, 1);
		se_score = soundPool.load(this, R.raw.se_score, 1);
		se_peep = soundPool.load(this, R.raw.se_peep, 1);
		se_trump_flip = soundPool.load(this, R.raw.se_trump_flip, 1);
		se_trump_select = soundPool.load(this, R.raw.se_trump_select, 1);
		se_winner = soundPool.load(this, R.raw.se_winner, 1);

		//		Log.v(TAG, "onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();

		soundPool.release();

		// headset plug状態 Broadcast Receiver登録解除
		unregisterReceiver(plugStateChangeReceiver);

		unregisterReceiver(ringerModeStateChangeReceiver);

		// 縦画面、横画面のどちらで終了したのか保存する
		user.rotate = config.orientation;

		//		Log.v(TAG, "onPause()");
	}

	@Override
	protected void onStop() {
		super.onStop();
		//		Log.v(TAG, "onStop()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		saveUser();

		saveCoin();
		//		Log.v(TAG, "onDestroy()");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.whatif, menu);

		//		menu.findItem(R.id.rotate).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		return true;
	}// onCreateOptionsMenu()

	/* ********** ********** ********** ********** */

	private void saveUser() {

		try {
			FileOutputStream fos = openFileOutput("SaveData.dat", MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(user);
			oos.close();
		} catch (Exception e) {
			Log.d(TAG, "Error");
		}
	}

	private void resizeTrump() {

		for (int i = 0; i <= 5; i++) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(trumpViewWidth, trumpViewHeight);
			int[] rules = ((LayoutParams) trumpBackView[i].getLayoutParams()).getRules();

			//		Log.v(TAG, "rules[]="
			//				+ rules[0] + ","
			//				+ rules[1] + ","
			//				+ rules[2] + ","
			//				+ rules[3] + ","
			//				+ rules[4] + ","
			//				+ rules[5] + ","
			//				+ rules[6] + ","
			//				+ rules[7] + ","
			//				+ rules[8] + ","
			//				+ rules[9] + ","
			//				+ rules[10] + ","
			//				+ rules[11] + ","
			//				+ rules[12] + ","
			//				+ rules[13] + ","
			//				+ rules[14] + ","
			//				+ rules[15]);

			if (rules[0] != 0) {// -1ならtrue
				lp.addRule(RelativeLayout.LEFT_OF, rules[0]);
			}
			if (rules[1] != 0) {
				lp.addRule(RelativeLayout.RIGHT_OF, rules[1]);
			}
			if (rules[2] != 0) {
				lp.addRule(RelativeLayout.ABOVE, rules[2]);
			}
			if (rules[3] != 0) {
				lp.addRule(RelativeLayout.BELOW, rules[3]);
			}
			if (rules[4] != 0) {
				lp.addRule(RelativeLayout.ALIGN_BASELINE, rules[4]);
			}
			if (rules[5] != 0) {
				lp.addRule(RelativeLayout.ALIGN_LEFT, rules[5]);
			}
			if (rules[6] != 0) {
				lp.addRule(RelativeLayout.ALIGN_TOP, rules[6]);
			}
			if (rules[7] != 0) {
				lp.addRule(RelativeLayout.ALIGN_RIGHT, rules[7]);
			}
			if (rules[8] != 0) {
				lp.addRule(RelativeLayout.ALIGN_BOTTOM, rules[8]);
			}
			if (rules[9] != 0) {
				lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, rules[9]);
			}
			if (rules[10] != 0) {
				lp.addRule(RelativeLayout.ALIGN_PARENT_TOP, rules[10]);
			}
			if (rules[11] != 0) {
				lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, rules[11]);
			}
			if (rules[12] != 0) {
				lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, rules[12]);
			}
			if (rules[13] != 0) {
				lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			}
			if (rules[14] != 0) {
				lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			}
			if (rules[15] != 0) {
				lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
			}

			if (config.orientation == Configuration.ORIENTATION_PORTRAIT) { // 縦画面でのマージンを設定する
				if (i == 0) {
					lp.topMargin = 25;

				} else if (i == 3) {
					lp.topMargin = 15;

					TextView selectAble = (TextView) findViewById(R.id.selectAble1);
					if ((trumpViewHeight + 25 + selectAble.getHeight())
					< findViewById(R.id.ruler).getHeight()) {

						//						Log.v("Test", "ruler=" + findViewById(R.id.ruler).getHeight());
						//						Log.v("Test", "selectable=" + selectAble.getHeight());
						//						Log.v("Test", "trumpViewHeight=" + trumpViewHeight);
						//						Log.v("Test", "margin=" + ((findViewById(R.id.ruler).getHeight() -
						//								(trumpViewHeight + selectAble.getHeight()))));

						lp.topMargin = findViewById(R.id.ruler).getHeight() - trumpViewHeight - selectAble.getHeight();
					}

					if (width == 240) {
						lp.topMargin = 15;
						margin = 5;
					}

					lp.leftMargin = margin;
					lp.rightMargin = margin;
				} else if (i == 1) {
					if (width == 240) {
						margin = 5;
					}
					lp.rightMargin = margin;
				} else if (i == 5) {
					if (width == 240) {
						margin = 5;
					}
					lp.leftMargin = margin;
				}
			} else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {// 横画面でのマージンを設定する
				margin = (width - (trumpViewWidth * 5)) / 6;
				TextView selectAble = (TextView) findViewById(R.id.selectAble1);

				if (i == 0) {
					lp.topMargin = 5;
				} else if (i == 3) {
					lp.topMargin = 10;

					if ((height / 2) < findViewById(R.id.ruler).getHeight()) {
						lp.topMargin = (findViewById(R.id.ruler).getHeight() -
								(trumpViewHeight + 20 + selectAble.getHeight())) / 2;
					}

				} else if (i == 2) {
					lp.rightMargin = margin;
				} else if (i == 1) {
					lp.rightMargin = margin;
				} else if (i == 4) {
					lp.leftMargin = margin;
				} else if (i == 5) {
					lp.leftMargin = margin;
				}

			}
			trumpBackView[i].setLayoutParams(lp);

		}

	}

	private void fixFont() {

		// Portrait(縦長)
		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			fontSize = (int) ((width / scale) / 20);

		}
		// Landscape(横長)
		else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			fontSize = (int) ((width / scale) / 45);
		}

		Log.v(TAG, "> fontSize=" + fontSize + "sp");

		String[] itemList = res.getStringArray(R.array.textView);

		TextView[] textView = new TextView[itemList.length];

		idList = new int[itemList.length];
		//配列データからIDの取得
		for (int i = 0; i < itemList.length; i++) {
			idList[i] = res.getIdentifier(itemList[i], "id", getPackageName());
			textView[i] = (TextView) findViewById(idList[i]);
			if (textView[i] != null) {
				//				textView[i].setTextSize(TypedValue.COMPLEX_UNIT_PX ,fontSize);
				textView[i].setTextSize(fontSize);
			}
		}

		// Portrait(縦長)
		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {

		}
		// Landscape(横長)
		else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {

		}

		wagerView.setTextSize(fontSize - 2);
		winView.setTextSize(fontSize - 2);
		paidView.setTextSize(fontSize - 2);
		creditView.setTextSize(fontSize - 2);
	}

	// トランプ1枚をアニメーションの処理
	private void FlipTrump(final int index) {

		// アニメーション中にクリックできないようfalseに変更する
		flipAnimFlag = false;
		// 現在表示されているトランプ画像を非表示にする
		trumpBackView[index].setVisibility(View.INVISIBLE);

		// Y軸回転(0～90度)
		Rotate3dAnimation rotation = new Rotate3dAnimation(0, 90, centerX, centerY, 0f, true);
		rotation.setDuration(timeFlip);
		trumpBackView[index].startAnimation(rotation);

		if (ringerMode && !isPlugged) {
			soundPool.play(se_trump_flip, 0.5F, 0.5F, 0, 0, 1.0F);
		} else if (isPlugged) {
			soundPool.play(se_trump_flip, 0.1F, 0.1F, 0, 0, 1.0F);
		}

		rotation.setAnimationListener(new TrumpAnimationListener(index) {
			// 裏面が回転し終わり表面が回転し始める
			@Override
			public void onAnimationEnd(Animation animation) {

				// Y軸回転(270～360度)
				Rotate3dAnimation rotation = new Rotate3dAnimation(270, 360, centerX, centerY, 0f, false);

				rotation.setDuration(timeFlip);
				trumpView[index].startAnimation(rotation);
				rotation.setAnimationListener(new TrumpAnimationListener(index) {
					@Override
					public void onAnimationEnd(Animation animation) {
						// アニメーションの終了
						trumpView[index].setVisibility(View.VISIBLE);
						flipAnimFlag = true;
					}
				});
			}
		});

	}

	// トランプ5枚を順番に捲るアニメーションの処理
	private void dealFlipTrump(final int index) {

		// アニメーション中にクリックできないようfalseに変更する
		dealAnimFlag = false;
		// 現在表示されているトランプ画像を非表示にする
		trumpBackView[index].setVisibility(View.INVISIBLE);

		// Y軸回転(0～90度)
		Rotate3dAnimation rotation = new Rotate3dAnimation(0, 90, centerX, centerY, 0f, true);

		rotation.setDuration(timeDeal);
		trumpBackView[index].startAnimation(rotation);

		if (ringerMode && !isPlugged) {
			soundPool.play(se_trump_flip, 0.5F, 0.5F, 0, 0, 0.5F);
		} else if (isPlugged) {
			soundPool.play(se_trump_flip, 0.1F, 0.1F, 0, 0, 0.5F);
		}

		rotation.setAnimationListener(new TrumpAnimationListener(index) {
			// 裏面が回転し終わり表面が回転し始める
			@Override
			public void onAnimationEnd(Animation animation) {

				// Y軸回転(270～360度)
				Rotate3dAnimation rotation = new Rotate3dAnimation(270, 360, centerX, centerY, 0f, false);

				rotation.setDuration(timeDeal);
				trumpView[index].startAnimation(rotation);
				rotation.setAnimationListener(new TrumpAnimationListener(index) {
					@Override
					public void onAnimationEnd(Animation animation) {
						// アニメーションの終了
						trumpView[index].setVisibility(View.VISIBLE);
						yellowNum(trumpView[index].getSerial());
						animCount++;
						dealAnimFlag = true;
						// 手札2～5までのY軸回転処理						
						if (animCount < 5) {
							dealFlipTrump(index + 1);
						} else if (animCount == 5) {
							animCount = 0;
							selectAble();
							checkPoker();
						}

					}
				});
			}
		});

	}

	private void setScrollView() {
		scrollHeight = findViewById(R.id.cChain1).getHeight();

		bonusScroll1 = (ScrollView) findViewById(R.id.bonusScroll1);

		// スクロールViewの高さを変更
		bonusScroll1.getLayoutParams().height = scrollHeight * scrollLines;
		bonusScroll1.requestLayout();

		bonusScroll1.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;//trueでタッチスクロールを禁止する
			}
		});
		bonusScroll1.post(new Runnable() {
			@Override
			public void run() {
				if (counter == 0) {
					bonusScroll1.fullScroll(ScrollView.FOCUS_DOWN);// 一番下までスクロールする
				} else {
					bonusScroll1.scrollBy(0, scrollHeight * (50 - counter));

					int cChainId = res.getIdentifier("cChain" + counter, "id", getPackageName());

					((TextView) findViewById(cChainId)).setBackgroundColor(0xFFFF0000);

				}
			}
		});

		paysScroll1 = (ScrollView) findViewById(R.id.paysScroll1);
		// スクロールViewの高さを変更
		paysScroll1.getLayoutParams().height = scrollHeight * scrollLines;
		paysScroll1.requestLayout();
		paysScroll1.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;//trueでタッチスクロールを禁止する
			}
		});
		paysScroll1.post(new Runnable() {
			@Override
			public void run() {

				if (counter == 0) {
					paysScroll1.fullScroll(ScrollView.FOCUS_DOWN);// 一番下までスクロールする
				} else {
					paysScroll1.scrollBy(0, scrollHeight * (50 - counter));

					int cBonusId = res.getIdentifier("cBonus" + counter, "id", getPackageName());
					((TextView) findViewById(cBonusId)).setTextColor(0xFFFF0000);
					((TextView) findViewById(cBonusId)).setBackgroundColor(0xFFFFFFFF);
				}
			}
		});

		// スクロールViewの高さを変更
		bonusScroll2.getLayoutParams().height = scrollHeight;
		bonusScroll2.requestLayout();
		bonusScroll2.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;//trueでタッチスクロールを禁止する
			}
		});

		bonusScroll2.post(new Runnable() {
			@Override
			public void run() {

				if (counter == 0) {
					bonusScroll2.fullScroll(ScrollView.FOCUS_UP);// 一番上までスクロールする
				}
			}
		});

		// スクロールViewの高さを変更
		paysScroll2.getLayoutParams().height = scrollHeight;
		paysScroll2.requestLayout();
		paysScroll2.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;//trueでタッチスクロールを禁止する
			}
		});
		paysScroll2.post(new Runnable() {
			@Override
			public void run() {

				if (counter == 0) {
					paysScroll2.fullScroll(ScrollView.FOCUS_UP);// 一番上までスクロールする
				}
			}
		});

		// スクロールViewの高さを変更
		hitsScroll1.getLayoutParams().height = scrollHeight;
		hitsScroll1.requestLayout();
		hitsScroll1.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;//trueでタッチスクロールを禁止する
			}
		});
		hitsScroll1.post(new Runnable() {
			@Override
			public void run() {

				if (counter == 0) {
					hitsScroll1.fullScroll(ScrollView.FOCUS_UP);// 一番上までスクロールする
				}
			}
		});

	}

	// 手札から場札へトランプが移動する処理
	private void moveTrump(final int index) {

		// アニメーション中にクリックできないようfalseに変更する
		moveAnimFlag = false;

		// アニメーションするViewを最前面に移動する
		trumpView[index].bringToFront();

		TranslateAnimation translate = new TranslateAnimation(
				0, trumpView[0].getLeft() - trumpView[index].getLeft(),
				0, trumpView[0].getTop() - trumpView[index].getTop());

		translate.setDuration(timeTranslate);
		translate.setFillAfter(true);

		trumpView[index].startAnimation(translate);
		translate.setAnimationListener(new TrumpAnimationListener(index) {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// ////////////////////////////////////////////////
				// トランプクリック後の処理
				// ////////////////////////////////////////////////
				trumpView[index].setAnimation(null);//チラつき防止、移動後のアニメViewが消える前に次のトランプが読み込まれてしまうため

				counter++;
				count.setText(String.valueOf(counter));

				// 場札のビュー(trumpView[0])に手札の情報を移す
				trumpView[0].setTrump(trumpView[index].getNumber(),
						trumpView[index].getSuit(),
						trumpView[index].getSerial(),
						trumpView[index].getColor());

				if (counter == 1) {
					trumpView[0].setVisibility(View.VISIBLE);
				}

				// recordに場札に置く札を記録していく
				record.trump.add(new Trump(
						trumpView[0].getNumber(),
						trumpView[0].getSuit(),
						trumpView[0].getSerial(),
						trumpView[0].getColor()
						));

				// 場札に置いた札を強調表示する
				boldNum(trumpView[0].getSerial());

				// 2枚目以降に強調表示した札の番号を消す
				if (counter > 1) {
					deleteNum(record.trump.get(counter - 2).getSerial());
				}

				// アニメーションのため一時非表示
				trumpView[index].setVisibility(View.INVISIBLE);

				// 手札に新しい札を配る

				if (counter < 48) {
					// 手札のビュー(trumpView[index])に新しい札の情報を移す
					trumpView[index].setTrump(deck.trump.get(counter + 4).getNumber(),
							deck.trump.get(counter + 4).getSuit(),
							deck.trump.get(counter + 4).getSerial(),
							deck.trump.get(counter + 4).getColor());

					FlipTrump(index);
					yellowNum(trumpView[index].getSerial());
					selectAble();
					checkPoker();
					GameOver();

				} else {
					trumpView[index].setVisibility(View.INVISIBLE);
					trumpView[index].setTrump(0, null, 0, 0);// 非表示になった手札には空の情報を入れておく(selectAble()に反応しないよう)

					Resources res = getResources();
					int id = res.getIdentifier("selectAble" + index, "id", getPackageName());
					TextView view = (TextView) findViewById(id);
					view.setTextColor(0x00FFFF00);
					selectAble();
					GameOver();
					GameClear();
				}

				scrollUp();
				moveAnimFlag = true;
			}

		});

	}

	// 場札と同じ数字かスート(図柄)ならtrueを返す処理
	private boolean agreeTrump(int index) {
		if (counter == 0) {
			// 一枚目の場合(どのカードも場札に置ける)
			//			Log.v(TAG, "一枚目");

			return true;
		} else if (trumpView[0].getSuit().equals(trumpView[index].getSuit())) {
			// スート(図柄)が一致した場合
			//			Log.v(TAG, "スート(図柄)が一致");

			return true;
		} else if (trumpView[0].getNumber() == trumpView[index].getNumber()) {
			// 数字が一致した場合
			//			Log.v(TAG, "数字が一致");

			return true;
		}
		return false;
	}

	// RoyalFlushが成立していたならtrueを返す処理
	private boolean checkRF() {

		ArrayList<Integer> tmp = new ArrayList<Integer>();

		tmp.add(trumpView[1].getNumber());
		tmp.add(trumpView[2].getNumber());
		tmp.add(trumpView[3].getNumber());
		tmp.add(trumpView[4].getNumber());
		tmp.add(trumpView[5].getNumber());

		Collections.sort(tmp);

		if (!checkFL()) {
			return false;
		}

		if (tmp.get(0) == (1) &&
				tmp.get(1) == (10) &&
				tmp.get(2) == (11) &&
				tmp.get(3) == (12) &&
				tmp.get(4) == (13)) {
			//			Log.v(TAG, counter + ">>☆☆☆☆☆ROYAL STRAIGHT FLUSH☆☆☆☆☆");
			return true;
		}

		return false;
	}

	// StraightFlushが成立していたならtrueを返す処理
	private boolean checkSF() {

		ArrayList<Integer> tmp = new ArrayList<Integer>();

		tmp.add(trumpView[1].getNumber());
		tmp.add(trumpView[2].getNumber());
		tmp.add(trumpView[3].getNumber());
		tmp.add(trumpView[4].getNumber());
		tmp.add(trumpView[5].getNumber());

		Collections.sort(tmp);

		if (!checkFL()) {
			return false;
		}

		if (tmp.get(1) == (tmp.get(0) + 1) &&
				tmp.get(2) == (tmp.get(0) + 2) &&
				tmp.get(3) == (tmp.get(0) + 3) &&
				tmp.get(4) == (tmp.get(0) + 4)) {
			//			Log.v(TAG, counter + ">>☆☆☆☆☆STRAIGHT FLUSH☆☆☆☆☆");
			return true;
		}

		return false;
	}

	// Four_of_a_Kindが成立していたならtrueを返す処理
	private boolean checkFK() {

		ArrayList<Integer> tmp = new ArrayList<Integer>();

		tmp.add(trumpView[1].getNumber());
		tmp.add(trumpView[2].getNumber());
		tmp.add(trumpView[3].getNumber());
		tmp.add(trumpView[4].getNumber());
		tmp.add(trumpView[5].getNumber());

		Collections.sort(tmp);

		int min = Collections.min(tmp);
		int max = Collections.max(tmp);

		int minCount = 0;
		int maxCount = 0;

		for (int i = 0; i < 5; i++) {
			if (tmp.get(i) == min) {
				minCount++;
			}
			if (tmp.get(i) == max) {
				maxCount++;
			}
		}
		if ((minCount == 1 && maxCount == 4) ||
				(minCount == 4 && maxCount == 1)) {
			//			Log.v(TAG, counter + ">>☆☆☆☆☆FOUR OF A KIND☆☆☆☆☆");
			return true;
		}

		return false;
	}

	// FullHouseが成立していたならtrueを返す処理
	private boolean checkFH() {

		ArrayList<Integer> tmp = new ArrayList<Integer>();

		tmp.add(trumpView[1].getNumber());
		tmp.add(trumpView[2].getNumber());
		tmp.add(trumpView[3].getNumber());
		tmp.add(trumpView[4].getNumber());
		tmp.add(trumpView[5].getNumber());

		Collections.sort(tmp);

		int min = Collections.min(tmp);
		int max = Collections.max(tmp);

		int minCount = 0;
		int maxCount = 0;

		for (int i = 0; i < 5; i++) {
			if (tmp.get(i) == min) {
				minCount++;
			}
			if (tmp.get(i) == max) {
				maxCount++;
			}
		}
		if ((minCount == 2 && maxCount == 3) ||
				(minCount == 3 && maxCount == 2)) {
			//			Log.v(TAG, counter + ">>☆☆☆☆☆FULL HOUSE☆☆☆☆☆");
			return true;
		}

		return false;
	}

	// Flushが成立していたならtrueを返す処理
	private boolean checkFL() {

		for (int i = 1; i <= 4; i++) {
			if (!trumpView[1].getSuit().equals(trumpView[i + 1].getSuit())) {
				return false;
			}
		}
		//		Log.v(TAG, counter + ">>☆☆☆☆☆FLUSH☆☆☆☆☆");
		return true;
	}

	// ポーカーの役が成立していた時の処理
	private void checkPoker() {

		int movement = 0;

		if (counter < 48) {// 手札が5枚存在する間は役の判定を行う

			if (checkRF()) {

				pokerPosition = 1;

				if (pokerPosition < pokerPrevPosition) {// 現在位置が移動先よりも上にある場合
					movement = pokerPosition - pokerPrevPosition;
					if (movement != 0) {// 移動量が0でなければスクロール
						bonusScroll2.smoothScrollBy(0, scrollHeight * movement);
						paysScroll2.smoothScrollBy(0, scrollHeight * movement);
						hitsScroll1.smoothScrollBy(0, scrollHeight * movement);
					}
				} else {// 現在位置が移動先よりも下にある場合
					movement = pokerPrevPosition - pokerPosition;
					if (movement != 0) {// 移動量が0でなければスクロール
						bonusScroll2.smoothScrollBy(0, -scrollHeight * movement);
						paysScroll2.smoothScrollBy(0, -scrollHeight * movement);
						hitsScroll1.smoothScrollBy(0, -scrollHeight * movement);
					}
				}

				RFcount++;
				((TextView) findViewById(R.id.hitsFlag1)).setText(String.valueOf(RFcount));
				((TextView) findViewById(R.id.handBonus1)).setText(String.valueOf(ratePoker[1] * coin.getWager()));

				if (ringerMode && !isPlugged) {
					soundPool.play(se_coin, 0.5F, 0.5F, 0, 0, 1.0F);
				} else if (isPlugged) {
					soundPool.play(se_coin, 0.1F, 0.1F, 0, 0, 1.0F);
				}
				pokerPrevPosition = pokerPosition;
				return;

			} else if (checkSF()) {
				pokerPosition = 2;

				if (pokerPosition < pokerPrevPosition) {// 現在位置が移動先よりも上にある場合
					movement = pokerPosition - pokerPrevPosition;
					if (movement != 0) {// 移動量が0でなければスクロール
						bonusScroll2.smoothScrollBy(0, scrollHeight * movement);
						paysScroll2.smoothScrollBy(0, scrollHeight * movement);
						hitsScroll1.smoothScrollBy(0, scrollHeight * movement);
					}
				} else {// 現在位置が移動先よりも下にある場合
					movement = pokerPrevPosition - pokerPosition;
					if (movement != 0) {// 移動量が0でなければスクロール
						bonusScroll2.smoothScrollBy(0, -scrollHeight * movement);
						paysScroll2.smoothScrollBy(0, -scrollHeight * movement);
						hitsScroll1.smoothScrollBy(0, -scrollHeight * movement);
					}
				}

				SFcount++;
				((TextView) findViewById(R.id.hitsFlag2)).setText(String.valueOf(SFcount));
				((TextView) findViewById(R.id.handBonus2)).setText(String.valueOf(ratePoker[2] * coin.getWager()));

				if (ringerMode && !isPlugged) {
					soundPool.play(se_coin, 0.5F, 0.5F, 0, 0, 1.0F);
				} else if (isPlugged) {
					soundPool.play(se_coin, 0.1F, 0.1F, 0, 0, 1.0F);
				}
				pokerPrevPosition = pokerPosition;
				return;
			} else if (checkFK()) {

				pokerPosition = 3;

				if (pokerPosition < pokerPrevPosition) {// 現在位置が移動先よりも上にある場合
					movement = pokerPosition - pokerPrevPosition;
					if (movement != 0) {// 移動量が0でなければスクロール
						bonusScroll2.smoothScrollBy(0, scrollHeight * movement);
						paysScroll2.smoothScrollBy(0, scrollHeight * movement);
						hitsScroll1.smoothScrollBy(0, scrollHeight * movement);
					}
				} else {// 現在位置が移動先よりも下にある場合
					movement = pokerPrevPosition - pokerPosition;
					if (movement != 0) {// 移動量が0でなければスクロール
						bonusScroll2.smoothScrollBy(0, -scrollHeight * movement);
						paysScroll2.smoothScrollBy(0, -scrollHeight * movement);
						hitsScroll1.smoothScrollBy(0, -scrollHeight * movement);
					}
				}
				FKcount++;
				((TextView) findViewById(R.id.hitsFlag3)).setText(String.valueOf(FKcount));
				((TextView) findViewById(R.id.handBonus3)).setText(String.valueOf(ratePoker[3] * coin.getWager()));

				if (ringerMode && !isPlugged) {
					soundPool.play(se_coin, 0.5F, 0.5F, 0, 0, 1.0F);
				} else if (isPlugged) {
					soundPool.play(se_coin, 0.1F, 0.1F, 0, 0, 1.0F);
				}
				pokerPrevPosition = pokerPosition;
				return;
			} else if (checkFH()) {

				pokerPosition = 4;

				if (pokerPosition < pokerPrevPosition) {// 現在位置が移動先よりも上にある場合
					movement = pokerPosition - pokerPrevPosition;
					if (movement != 0) {// 移動量が0でなければスクロール
						bonusScroll2.smoothScrollBy(0, scrollHeight * movement);
						paysScroll2.smoothScrollBy(0, scrollHeight * movement);
						hitsScroll1.smoothScrollBy(0, scrollHeight * movement);
					}
				} else {// 現在位置が移動先よりも下にある場合
					movement = pokerPrevPosition - pokerPosition;
					if (movement != 0) {// 移動量が0でなければスクロール
						bonusScroll2.smoothScrollBy(0, -scrollHeight * movement);
						paysScroll2.smoothScrollBy(0, -scrollHeight * movement);
						hitsScroll1.smoothScrollBy(0, -scrollHeight * movement);
					}
				}
				FHcount++;
				((TextView) findViewById(R.id.hitsFlag4)).setText(String.valueOf(FHcount));
				((TextView) findViewById(R.id.handBonus4)).setText(String.valueOf(ratePoker[4] * coin.getWager()));

				if (ringerMode && !isPlugged) {
					soundPool.play(se_coin, 0.5F, 0.5F, 0, 0, 1.0F);
				} else if (isPlugged) {
					soundPool.play(se_coin, 0.1F, 0.1F, 0, 0, 1.0F);
				}
				pokerPrevPosition = pokerPosition;
				return;
			} else if (checkFL()) {

				pokerPosition = 5;

				if (pokerPosition < pokerPrevPosition) {// 現在位置が移動先よりも上にある場合
					movement = pokerPosition - pokerPrevPosition;
					if (movement != 0) {// 移動量が0でなければスクロール
						bonusScroll2.smoothScrollBy(0, scrollHeight * movement);
						paysScroll2.smoothScrollBy(0, scrollHeight * movement);
						hitsScroll1.smoothScrollBy(0, scrollHeight * movement);
					}
				} else {// 現在位置が移動先よりも下にある場合
					movement = pokerPrevPosition - pokerPosition;
					if (movement != 0) {// 移動量が0でなければスクロール
						bonusScroll2.smoothScrollBy(0, -scrollHeight * movement);
						paysScroll2.smoothScrollBy(0, -scrollHeight * movement);
						hitsScroll1.smoothScrollBy(0, -scrollHeight * movement);
					}
				}
				FLcount++;
				((TextView) findViewById(R.id.hitsFlag5)).setText(String.valueOf(FLcount));
				((TextView) findViewById(R.id.handBonus5)).setText(String.valueOf(ratePoker[5] * coin.getWager()));

				if (ringerMode && !isPlugged) {
					soundPool.play(se_coin, 0.5F, 0.5F, 0, 0, 1.0F);
				} else if (isPlugged) {
					soundPool.play(se_coin, 0.1F, 0.1F, 0, 0, 1.0F);
				}
				pokerPrevPosition = pokerPosition;
				return;
			}
		}
	}

	// boldNum関数…場札に置いたトランプの数字をガイド上で太字・シアンにする処理
	private void boldNum(int x) {
		Resources res = getResources();
		int guideId = res.getIdentifier("card" + x, "id", getPackageName());
		guideView = (TextView) findViewById(guideId);
		// TextViewの文字色を変更する（16進数で頭の2bitがアルファ値、00で透過率100%）
		guideView.setTextColor(0xFF00BFFF);
		// フォントのスタイル（太字、斜線など）を変更する
		guideView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
	}// Card.BoldNum_**********

	// yellowNum関数…場札に置いたトランプの数字をガイド上で黄色にする処理
	private void yellowNum(int x) {
		Resources res = getResources();
		int guideId = res.getIdentifier("card" + x, "id", getPackageName());
		guideView = (TextView) findViewById(guideId);
		// TextViewの文字色を変更する（16進数で頭の2bitがアルファ値、00で透過率100%）
		guideView.setTextColor(0xFFFFD700);
		// フォントのスタイル（太字、斜線など）を変更する
		guideView.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		// 背景色を変更する
		guideView.setBackgroundColor(0xFF7777FF);
	}// Card.yellowNum_**********

	// deleteNum関数…場札に置いたトランプの数字をガイド上から消す処理
	private void deleteNum(int x) {
		// getResources()でリソースオブジェクトを
		Resources res = getResources();
		// guideIdという変数にリソースの場所を格納する
		// ("card" + x , "id" , getPackageName())
		// ↓
		// (R.id.card***)
		int guideId = res.getIdentifier("card" + x, "id", getPackageName());
		guideView = (TextView) findViewById(guideId);
		guideView.setTextColor(0x00FFFFFF);
		// 背景色を元の青色に戻す
		guideView.setBackgroundColor(0xFF0000FF);
	}// Card.DeleteNum_**********

	private void redrawGuide() {
		Resources res = getResources();
		int guideId;
		for (int i = 0; i < 52; i++) {
			guideId = res.getIdentifier("card" + i, "id", getPackageName());
			guideView = (TextView) findViewById(guideId);
			// 文字色を白に戻す
			guideView.setTextColor(0xFFFFFFFF);
			// 背景色を元の青色に戻す
			guideView.setBackgroundColor(0xFF0000FF);
		}
	}

	private void txtSwitchOn() {

		final Animation in = AnimationUtils.loadAnimation(this, R.anim.down_in);
		final Animation out = AnimationUtils.loadAnimation(this, R.anim.down_out);

		count = (TextSwitcher) findViewById(R.id.counterView);
		counterWidth = count.getWidth();
		counterHeight = count.getHeight();

		count.setInAnimation(in);
		count.setOutAnimation(out);
		count.setFactory(this);
		count.setText(String.valueOf(counter));
	}

	private void setTxtBounus() {

		Resources res = getResources();
		int id;
		TextView view;
		for (int i = 1; i <= 52; i++) {
			id = res.getIdentifier("cChain" + i, "id", getPackageName());
			view = (TextView) findViewById(id);
			view.setText(String.valueOf(i) + " CARDS");
			view.setBackgroundColor(0xFF0000FF);
		}

		for (int i = 1; i <= 52; i++) {
			id = res.getIdentifier("cBonus" + i, "id", getPackageName());
			view = (TextView) findViewById(id);
			view.setText(String.valueOf(rate52[i - 1]));

		}
	}

	private void setTxtBounus(int wager) {

		Resources res = getResources();
		int id;
		TextView view;

		for (int i = 1; i <= 52; i++) {
			id = res.getIdentifier("cChain" + i, "id", getPackageName());
			view = (TextView) findViewById(id);
			view.setText(String.valueOf(i) + " CARDS");
			view.setBackgroundColor(0xFF0000FF);
		}

		for (int i = 1; i <= 52; i++) {
			id = res.getIdentifier("cBonus" + i, "id", getPackageName());
			view = (TextView) findViewById(id);
			view.setText(String.valueOf(rate52[i - 1] * wager));
			view.setTextColor(0xFFFFFFFF);
			view.setBackgroundColor(0xFFFF0000);
		}
	}

	private void scrollUp() {

		final Resources res = getResources();

		final int cChainId = res.getIdentifier("cChain" + counter, "id", getPackageName());
		final int cBonusId = res.getIdentifier("cBonus" + counter, "id", getPackageName());
		if (counter == 1) {

			((TextView) findViewById(cChainId)).setBackgroundColor(0xFFFF0000);

			((TextView) findViewById(cBonusId)).setTextColor(0xFFFF0000);
			((TextView) findViewById(cBonusId)).setBackgroundColor(0xFFFFFFFF);

		} else {
			if (counter <= 50) {

				new Thread(new Runnable() {

					@Override
					public void run() {

						handler.post(new Runnable() {

							@Override
							public void run() {

								bonusScroll1.smoothScrollBy(0, -scrollHeight);
								paysScroll1.smoothScrollBy(0, -scrollHeight);

							}

						});

					}

				}).start();

				new Thread(new Runnable() {

					@Override
					public void run() {

						try {
							Thread.sleep(scrollSleepTime);

							handler.post(new Runnable() {

								@Override
								public void run() {

									// 1つ前のViewの状態を元に戻す
									((TextView) findViewById(res.getIdentifier("cChain" + (counter - 1), "id", getPackageName())))
											.setBackgroundColor(0xFF0000FF);

									((TextView) findViewById(res.getIdentifier("cBonus" + (counter - 1), "id", getPackageName())))
											.setTextColor(0xFFFFFFFF);
									((TextView) findViewById(res.getIdentifier("cBonus" + (counter - 1), "id", getPackageName())))
											.setBackgroundColor(0xFFFF0000);

									((TextView) findViewById(cChainId)).setBackgroundColor(0xFFFF0000);

									((TextView) findViewById(cBonusId)).setTextColor(0xFFFF0000);
									((TextView) findViewById(cBonusId)).setBackgroundColor(0xFFFFFFFF);

								}

							});

						} catch (InterruptedException e1) {
							// TODO 自動生成された catch ブロック
							e1.printStackTrace();
						}

					}

				}).start();

			} else {

				// 1つ前のViewの状態を元に戻す
				((TextView) findViewById(res.getIdentifier("cChain" + (counter - 1), "id", getPackageName())))
						.setBackgroundColor(0xFF0000FF);

				((TextView) findViewById(res.getIdentifier("cBonus" + (counter - 1), "id", getPackageName())))
						.setTextColor(0xFFFFFFFF);
				((TextView) findViewById(res.getIdentifier("cBonus" + (counter - 1), "id", getPackageName())))
						.setBackgroundColor(0xFFFF0000);

				((TextView) findViewById(cChainId)).setBackgroundColor(0xFFFF0000);

				((TextView) findViewById(cBonusId)).setTextColor(0xFFFF0000);
				((TextView) findViewById(cBonusId)).setBackgroundColor(0xFFFFFFFF);
			}

		}

	}

	private void redrawCoin() {
		wagerView.setText(String.valueOf(coin.getWager()));
		winView.setText(String.valueOf(coin.getWin()));
		paidView.setText(String.valueOf(coin.getPaid()));
		creditView.setText(String.valueOf(coin.getCredit()));
	}

	// cuCoin関数…cu = Count Upの略称、払い戻し時にコインの枚数が1枚ずつ
	// 増減する様子を表示する処理、引数は増減する枚数を渡す
	private void countUp(final int x) {
		// コイン増加表示の処理中かフラグ判定
		if (coinFlag == false && (x != 0)) {

			coinFlag = true;// コイン増加表示の処理中というフラグを立てる
			//			beforeCredit = coin.getCredit();// 増加前のコインの枚数を格納
			//			beforeWager = coin.getBeforeWager();
			final Timer timer = new Timer();

			//			Log.v(TAG, "timer_start x=" + x);

			winView.setText(String.valueOf(x));

			if (ringerMode && !isPlugged) {
				streamId = soundPool.play(se_score, 0.5F, 0.5F, 0, -1, 1.0F);
			} else if (isPlugged) {
				streamId = soundPool.play(se_score, 0.1F, 0.1F, 0, -1, 1.0F);
			}

			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// handlerを通じてUI Threadへ処理をキューイング
					handler.post(new Runnable() {
						public void run() {
							paidView.setText(String.valueOf(timerCounter));
							creditView.setText(String.valueOf(coin.getCredit() + timerCounter));

							//											
							timerCounter++;

							if (x == coin.getWager()) {
								//								Log.v(TAG, "timer_stop_MIN");

								coin.setCredit(coin.getCredit() + coin.getWager());
								coin.setWager(0);
								coin.setPaid(0);

								redrawCoin();

								soundPool.stop(streamId);

								coinFlag = false;
								timerCounter = 0;
								timer.cancel();

							} else if (coin.getWager() > 0 && x == timerCounter) {
								//								Log.v(TAG, "timer_stop_WIN");

								coin.setCredit(coin.getCredit() + x);
								coin.setWager(0);
								coin.setPaid(0);

								redrawCoin();

								soundPool.stop(streamId);

								coinFlag = false;
								timerCounter = 0;
								timer.cancel();
							} else if (coin.getWager() == 0 && x == timerCounter) {
								//								Log.v(TAG, "timer_stop_PRESENT");

								coin.setCredit(coin.getCredit() + x);
								coin.setWager(0);
								coin.setPaid(0);

								redrawCoin();

								soundPool.stop(streamId);

								coinFlag = false;
								timerCounter = 0;
								timer.cancel();
							} else if (skipFlag == true) {
								//								Log.v(TAG, "timer_stop_SKIP");

								coin.setCredit(coin.getCredit() + x);
								coin.setWager(0);
								coin.setPaid(0);

								redrawCoin();

								soundPool.stop(streamId);

								coinFlag = false;
								skipFlag = false;
								timerCounter = 0;
								timer.cancel();

							}

						}
					});

				}
			}, 0, 50);

		} else if (x == 0) {
			coin.setWager(0);
			coin.setPaid(0);
			redrawCoin();
		}

	}

	// 払い戻し金を表示する処理
	private void refundCoin() {

		int poker = (ratePoker[1] * coin.getWager()) * RFcount +
				(ratePoker[2] * coin.getWager()) * SFcount +
				(ratePoker[3] * coin.getWager()) * FKcount +
				(ratePoker[4] * coin.getWager()) * FHcount +
				(ratePoker[5] * coin.getWager()) * FLcount;

		countUp(rate52[counter - 1] * coin.getWager() + poker);
		//		Log.v(TAG, "RATE=" + rate52[counter - 1] + "WAGER" + coin.getWager());
		//		Log.v(TAG, "WIN=" + rate52[counter - 1] * coin.getWager());
	}

	// 払い戻し金を計算する処理
	private int calculateCoin() {

		int poker = (ratePoker[1] * coin.getWager()) * RFcount +
				(ratePoker[2] * coin.getWager()) * SFcount +
				(ratePoker[3] * coin.getWager()) * FKcount +
				(ratePoker[4] * coin.getWager()) * FHcount +
				(ratePoker[5] * coin.getWager()) * FLcount;

		int whatif = rate52[counter - 1] * coin.getWager();

		return whatif + poker;
	}

	// 場札と同じ数字かスート(図柄)ならSELECT ABLEを表示する処理
	private void selectAble() {

		Resources res = getResources();
		int id;
		TextView view;

		if (counter == 0) {
			// 一枚目の場合(どのカードも場札に置ける)

			for (int i = 1; i <= 5; i++) {
				id = res.getIdentifier("selectAble" + i, "id", getPackageName());
				view = (TextView) findViewById(id);
				view.setTextColor(0xFFFFFF00);
			}

		} else {

			for (int i = 1; i <= 5; i++) {
				id = res.getIdentifier("selectAble" + i, "id", getPackageName());
				view = (TextView) findViewById(id);

				if (trumpView[0].getSuit().equals(trumpView[i].getSuit())) {// スート(図柄)が一致した場合
					view.setTextColor(0xFFFFFF00);
				} else if (trumpView[0].getNumber() == trumpView[i].getNumber()) {// 数字が一致した場合
					view.setTextColor(0xFFFFFF00);
				} else {// 一致しなかった場合は透明にする
					view.setTextColor(0x00FFFF00);
				}

			}
		}

	}

	private void saveCoin() {

		TextView credit = (TextView) findViewById(R.id.credit);
		int valueInt = Integer.parseInt(credit.getText().toString());

		// プリファレンスを取得する
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);

		// 編集用のeditorインスタンスを取得する
		SharedPreferences.Editor editor = preference.edit();

		// editorを利用してデータをput(書き出す)する
		editor.putInt(KEY_CREDIT, valueInt);

		// commitメソッドを利用して書き込みを確定させる
		editor.commit();

	}

	private void loadCoin() {

		// プリファレンスを取得する
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);

		// プリファレンスに保存したデータを読み取る
		//第2引数は、第1引数が見つからなかった場合に返す値

		int valueInt = preference.getInt(KEY_CREDIT, 100);
		coin.setCredit(valueInt);

	}

	private void callDialog() {
		LayoutInflater inflater = LayoutInflater.from(WhatifActivity.this);
		View dialog = inflater.inflate(R.layout.clear_dialog,
				(ViewGroup) findViewById(R.id.clear_dialog));

		Button closeBtn = (Button) dialog.findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				alert.dismiss();
			}
		});

		Button speedrunBtn = (Button) dialog.findViewById(R.id.SpeedrunBtn);
		speedrunBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isSignedIn() && clearTime != 0) {

					Games.Leaderboards.submitScore(getApiClient(), getString(R.string.lb_id), clearTime);

					Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
				}

			}
		});

		alert = new AlertDialog.Builder(WhatifActivity.this)
				.setTitle("YOUR GAME CLEAR!")
				.setView(dialog)
				.show();

	}

	// ////////////////////////////////////////////////
	// Vertical and Horizontal
	// ////////////////////////////////////////////////

	private void vertical() {
		// 機種によって複数回onWindowFocusChanged()が呼ばれる場合があるので
		// FlagでonCreate()の実行後に一度だけ実行されるように記述する
		if (replaceFlag) {
			// ステータスバーの高さを取得
			//			Rect rect = new Rect();
			//			getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
			//			statusbarHeight = rect.top;
			//			Log.v(TAG, "ステータスバー height=" + statusbarHeight);

			// 画面回転前のガイドの状態を復元する
			if (counter > 1) {
				for (int i = 0; i < counter - 1; i++) {
					deleteNum(record.trump.get(i).getSerial());
				}
			}
			if (counter != 0) {
				yellowNum(trumpView[0].getSerial());
				boldNum(trumpView[0].getSerial());

				yellowNum(trumpView[1].getSerial());
				yellowNum(trumpView[2].getSerial());
				yellowNum(trumpView[3].getSerial());
				yellowNum(trumpView[4].getSerial());
				yellowNum(trumpView[5].getSerial());
			}

			// 縦画面の時のトランプのサイズを決定する
			trumpViewWidth = width / 6;
			trumpViewHeight = (int) (trumpViewWidth * 1.5);

			Log.v(TAG, "> trump w=" + trumpViewWidth + " h=" + trumpViewHeight);

			//トランプの背景画像のサイズを変更する
			resizeTrump();

			// trumpViewの縦横を画像に合わせる
			for (int i = 0; i <= 5; i++) {
				trumpView[i].getLayoutParams().width = trumpViewWidth;
				trumpView[i].getLayoutParams().height = trumpViewHeight;
				trumpView[i].requestLayout();
			}

			// trumpViewのフォントサイズを変更する
			int fontSize = 10;

			fontSize = (int) ((trumpViewHeight / scale) / 4);
			fontSize -= fontSize / 10;

			for (int i = 0; i <= 5; i++) {
				trumpView[i].setFontSize(fontSize, fontSize * 2);
			}

			// 山札から5枚トランプの情報を読み込む
			if (!game) {
				for (int i = 1; i <= 5; i++) {
					trumpView[i].setTrump(deck.trump.get(i - 1).getNumber(),
							deck.trump.get(i - 1).getSuit(),
							deck.trump.get(i - 1).getSerial(),
							deck.trump.get(i - 1).getColor());
				}
			}

			// Y軸回転用の変数を取得する
			centerX = trumpBackView[0].getWidth() / 2;
			centerY = trumpBackView[0].getLayoutParams().height;

			//スクロールViewの設定をする
			setScrollView();
			// ボーナスにテキストを設定する
			if (coin.getWager() == 0) {
				setTxtBounus();
			} else {
				setTxtBounus(coin.getWager());
			}

			// コインの初期値を設定する
			redrawCoin();

			// トランプ画像を非表示にする
			for (int i = 0; i <= 5; i++) {
				trumpView[i].setVisibility(View.INVISIBLE);
				trumpBackView[i].setVisibility(View.INVISIBLE);
			}

			// 最前面の黒画像をフェードアウトする
			new Thread((new Runnable() {

				@Override
				public void run() {
					handler.post(new Runnable() {

						@Override
						public void run() {

							AlphaAnimation alpha = new AlphaAnimation(
									1.0f, // 開始時の透明度（1は全く透過しない）
									0.0f); // 終了時の透明度（0は完全に透過）

							alpha.setDuration(1000);

							// アニメーション終了時の表示状態を維持する
							alpha.setFillEnabled(true);
							alpha.setFillAfter(true);

							// アニメーションを開始
							findViewById(R.id.black).startAnimation(alpha);
						}
					});

				}
			})).start();

			//カウンター処理
			txtSwitchOn();

			// コインの増加処理中に画面の回転が発生した場合
			// 払い戻しの演出をスキップしてCreditに反映させておく
			if (coin.getWin() != 0) {

				coin.setCredit(coin.getCredit() + coin.getWin());

				coin.setWager(0);
				coin.setWin(0);
				coin.setPaid(0);

				redrawCoin();
			}

			if (game) {

				findViewById(R.id.btnLayout).setVisibility(View.INVISIBLE);
				// トランプ画像を非表示にする
				for (int i = 0; i <= 5; i++) {
					trumpView[i].setVisibility(View.VISIBLE);
				}
				selectAble();
			}

			// 起動時のみ必要な処理が終了したのでフラグを変更する
			replaceFlag = false;

		}
	}

	private void horizontal() {
		// 機種によって複数回onWindowFocusChanged()が呼ばれる場合があるので
		// FlagでonCreate()の実行後に一度だけ実行されるように記述する
		if (replaceFlag) {
			// ステータスバーの高さを取得
			//			Rect rect = new Rect();
			//			getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
			//			statusbarHeight = rect.top;
			//			Log.v(TAG, "ステータスバー height=" + statusbarHeight);

			// 画面回転前のガイドの状態を復元する
			if (counter > 1) {
				for (int i = 0; i < counter - 1; i++) {
					deleteNum(record.trump.get(i).getSerial());
				}
			}
			if (counter != 0) {
				yellowNum(trumpView[0].getSerial());
				boldNum(trumpView[0].getSerial());

				yellowNum(trumpView[1].getSerial());
				yellowNum(trumpView[2].getSerial());
				yellowNum(trumpView[3].getSerial());
				yellowNum(trumpView[4].getSerial());
				yellowNum(trumpView[5].getSerial());
			}

			// 横画面の時のトランプのサイズを決定する
			TextView selectAble = (TextView) findViewById(R.id.selectAble1);

			if ((findViewById(R.id.ruler).getHeight()) > height / 2) {

				trumpViewHeight = height
						- (findViewById(R.id.ruler).getHeight())
						- 10 // マージン
						- selectAble.getLineHeight() * 2 //
						- 10; // マージン

			} else {
				trumpViewHeight = (findViewById(R.id.ruler).getHeight())
						- 10 // マージン
						- selectAble.getLineHeight() * 2 //
						- 10;
			}

			trumpViewWidth = (int) (trumpViewHeight / 1.5);
			Log.v(TAG, "> trump w=" + trumpViewWidth + " h=" + trumpViewHeight);
			//トランプの背景画像のサイズを変更する
			resizeTrump();

			// trumpViewの縦横を画像に合わせる
			for (int i = 0; i <= 5; i++) {
				trumpView[i].getLayoutParams().width = trumpViewWidth;
				trumpView[i].getLayoutParams().height = trumpViewHeight;
				trumpView[i].requestLayout();
			}

			// trumpViewのフォントサイズを変更する
			int fontSize = 30;

			fontSize = (int) ((trumpViewHeight / scale) / 4);
			fontSize -= fontSize / 10;

			for (int i = 0; i <= 5; i++) {
				trumpView[i].setFontSize(fontSize, fontSize * 2);
			}

			// 山札から5枚トランプの情報を読み込む
			if (!game) {

				for (int i = 1; i <= 5; i++) {
					trumpView[i].setTrump(deck.trump.get(i - 1).getNumber(),
							deck.trump.get(i - 1).getSuit(),
							deck.trump.get(i - 1).getSerial(),
							deck.trump.get(i - 1).getColor());
				}
			}
			// Y軸回転用の変数を取得する
			centerX = trumpBackView[0].getWidth() / 2;
			centerY = trumpBackView[0].getLayoutParams().height;

			//スクロールViewの設定をする
			setScrollView();
			// ボーナスにテキストを設定する
			if (coin.getWager() == 0) {
				setTxtBounus();
			} else {
				setTxtBounus(coin.getWager());
			}
			// コインの初期値を設定する
			redrawCoin();

			// トランプ画像を非表示にする
			for (int i = 0; i <= 5; i++) {
				trumpView[i].setVisibility(View.INVISIBLE);
				trumpBackView[i].setVisibility(View.INVISIBLE);
			}

			// 最前面の黒画像をフェードアウトする
			new Thread((new Runnable() {

				@Override
				public void run() {
					handler.post(new Runnable() {

						@Override
						public void run() {

							AlphaAnimation alpha = new AlphaAnimation(
									1.0f, // 開始時の透明度（1は全く透過しない）
									0.0f); // 終了時の透明度（0は完全に透過）

							alpha.setDuration(1000);

							// アニメーション終了時の表示状態を維持する
							alpha.setFillEnabled(true);
							alpha.setFillAfter(true);

							// アニメーションを開始
							findViewById(R.id.black).startAnimation(alpha);
						}
					});

				}
			})).start();

			//カウンター処理
			txtSwitchOn();

			// makeView()内でフォントサイズを変更すると
			// TextSwitchewと同じlinearlayoutに配置されている子が
			// 文字の高さ分の空白が生まれてしまう(layout_Gravityを動的に変更しても改善せず)
			// 代価案としてマージンを詰める
			counterMsg.getLayoutParams();
			// counterMsgからマージンを取得
			MarginLayoutParams counterMsgParams = (MarginLayoutParams) counterMsg.getLayoutParams();
			// 移動させたい距離に変更
			counterMsgParams.topMargin -= txtSwitchFontSize;

			// counterMsgへ反映
			counterMsg.setLayoutParams(counterMsgParams);

			// コインの増加処理中に画面の回転が発生した場合
			// 払い戻しの演出をスキップしてCreditに反映させておく
			if (coin.getWin() != 0) {

				coin.setCredit(coin.getCredit() + coin.getWin());

				coin.setWager(0);
				coin.setWin(0);
				coin.setPaid(0);

				redrawCoin();
			}

			if (game) {

				findViewById(R.id.btnLayout).setVisibility(View.INVISIBLE);
				// トランプ画像を非表示にする
				for (int i = 0; i <= 5; i++) {
					trumpView[i].setVisibility(View.VISIBLE);
				}
				selectAble();
			}

			// 起動時のみ必要な処理が終了したのでフラグを変更する
			replaceFlag = false;

		}
	}

	// ////////////////////////////////////////////////
	// GameFlag
	// ////////////////////////////////////////////////

	// GameOver関数…ゲームフラグの管理
	private void GameOver() {
		for (int i = 1; i < 6; i++) {

			if (trumpView[0].getSuit().equals(trumpView[i].getSuit()) ||
					trumpView[0].getNumber() == trumpView[i].getNumber()) {
				// スート(図柄)と数字の両方が一致した場合
				return;
			}

		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(msgSleepTime);

					handler.post(new Runnable() {

						@Override
						public void run() {

							coin.setBeforeWager(coin.getWager());

							for (int i = 1; i < 6; i++) {
								trumpView[i].setVisibility(View.INVISIBLE);
							}

							msg = (TextView) findViewById(R.id.msgView1);

							if (coin.getWager() < calculateCoin()) {

								if (ringerMode && !isPlugged) {
									soundPool.play(se_winner, 0.5F, 0.5F, 0, 0, 1.0F);
								} else if (isPlugged) {
									soundPool.play(se_winner, 0.1F, 0.1F, 0, 0, 1.0F);
								}

								msg.setText("WINNER!");
								msg.setTextColor(Color.RED);
							}
							else if (coin.getWager() == calculateCoin()) {

								if (ringerMode && !isPlugged) {
									soundPool.play(se_even, 0.5F, 0.5F, 0, 0, 1.0F);
								} else if (isPlugged) {
									soundPool.play(se_even, 0.1F, 0.1F, 0, 0, 1.0F);
								}

								msg.setText("DRAW!");
								msg.setTextColor(Color.GREEN);
							}
							else {
								if (ringerMode && !isPlugged) {
									soundPool.play(se_loser, 0.5F, 0.5F, 0, 0, 1.0F);
								} else if (isPlugged) {
									soundPool.play(se_loser, 0.1F, 0.1F, 0, 0, 1.0F);
								}

								msg.setText("LOSER!");
								msg.setTextColor(Color.BLUE);

								present();
							}

							stop = System.currentTimeMillis();

							clearTime = stop - start;

							long diff = stop - start;
							Log.v(TAG, String.valueOf(diff));

							// 手札を非表示にして、メッセージ画面手札を表示する
							findViewById(R.id.msgLayout).setVisibility(View.VISIBLE);
							refundCoin();

							//Toast.makeText(WhatifActivity.this, "ＧＡＭＥ ＯＶＥＲ", Toast.LENGTH_SHORT).show();

						}

					});

				} catch (InterruptedException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}

			}

		}).start();

	}// GameOver_**********

	private void present() {
		if (coin.getCredit() == 0) {
			Toast.makeText(this.getApplicationContext(), "Present for you!", Toast.LENGTH_SHORT).show();
			countUp(100);
		}
	}

	private void GameClear() {

		if (counter == 52) {
			if (ringerMode && !isPlugged) {
				soundPool.play(se_clear, 0.5F, 0.5F, 0, 0, 1.0F);
			} else if (isPlugged) {
				soundPool.play(se_clear, 0.1F, 0.1F, 0, 0, 1.0F);
			}
			for (int i = 1; i < 6; i++) {
				trumpView[i].setVisibility(View.INVISIBLE);
			}

			coin.setBeforeWager(coin.getWager());
			msg = (TextView) findViewById(R.id.msgView1);
			msg.setText("CONGRATULATION!");
			msg.setTextColor(Color.YELLOW);

			stop = System.currentTimeMillis();
			long diff = stop - start;
			Log.v(TAG, String.valueOf(diff));

			// 手札を非表示にして、メッセージ画面手札を表示する
			findViewById(R.id.msgLayout).setVisibility(View.VISIBLE);

			refundCoin();
			//Toast.makeText(WhatifActivity.this, "ＧＡＭＥ ＣＬＥＡＲ", Toast.LENGTH_SHORT).show();
		}

	}

	// ////////////////////////////////////////////////
	// ViewFactory
	// ////////////////////////////////////////////////
	@Override
	public View makeView() {
		txt = new TextView(this);

		txt.setHeight(counterHeight);

		if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			txtSwitchFontSize = (int) ((counterHeight / scale) * 0.7);
		}
		else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			txtSwitchFontSize = (int) ((counterHeight / scale) * 0.5);
		}

		txt.setGravity(Gravity.CENTER);
		txt.setTextColor(0xFFFFFFFF);
		txt.setTextSize(txtSwitchFontSize);
		return txt;
	}

	// ////////////////////////////////////////////////
	// 端末画面が回転した時の処理
	// ////////////////////////////////////////////////
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("WAGER", coin.getWager());
		outState.putInt("WIN", coin.getWin());
		outState.putInt("PAID", coin.getPaid());
		outState.putInt("CREDIT", coin.getCredit());

		if (findViewById(R.id.msgLayout).getVisibility() == View.INVISIBLE &&
				findViewById(R.id.btnLayout).getVisibility() == View.INVISIBLE) {

			outState.putBoolean("GAME", true);// 画面回転時にゲーム中であることを記録する

			outState.putInt("COUNTER", counter);

			outState.putInt("RF", RFcount);
			outState.putInt("SF", SFcount);
			outState.putInt("FK", FKcount);
			outState.putInt("FH", FHcount);
			outState.putInt("FL", FLcount);

			outState.putInt("LAYOUT", trumpView[0].getSerial());

			outState.putInt("HAND1", trumpView[1].getSerial());
			outState.putInt("HAND2", trumpView[2].getSerial());
			outState.putInt("HAND3", trumpView[3].getSerial());
			outState.putInt("HAND4", trumpView[4].getSerial());
			outState.putInt("HAND5", trumpView[5].getSerial());

			outState.putSerializable("DECK", deck);
			outState.putSerializable("RECORD", record);

			outState.putLong("START_TIME", start);

		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		coin.setWager(savedInstanceState.getInt("WAGER"));
		coin.setWin(savedInstanceState.getInt("WIN"));
		coin.setPaid(savedInstanceState.getInt("PAID"));
		coin.setCredit(savedInstanceState.getInt("CREDIT"));

		if (savedInstanceState.getBoolean("GAME")) {// 画面回転前がゲーム中なら処理をする箇所
			game = true;
			counter = savedInstanceState.getInt("COUNTER");

			RFcount = savedInstanceState.getInt("RF");
			SFcount = savedInstanceState.getInt("SF");
			FKcount = savedInstanceState.getInt("FK");
			FHcount = savedInstanceState.getInt("FH");
			FLcount = savedInstanceState.getInt("FL");

			deck = (Deck) savedInstanceState.getSerializable("DECK");
			record = (Deck) savedInstanceState.getSerializable("RECORD");

			start = savedInstanceState.getLong("START_TIME");

			trumpView[0].setTrump(
					standard.trump.get(savedInstanceState.getInt("LAYOUT")).getNumber(),
					standard.trump.get(savedInstanceState.getInt("LAYOUT")).getSuit(),
					standard.trump.get(savedInstanceState.getInt("LAYOUT")).getSerial(),
					standard.trump.get(savedInstanceState.getInt("LAYOUT")).getColor()
					);

			trumpView[1].setTrump(
					standard.trump.get(savedInstanceState.getInt("HAND1")).getNumber(),
					standard.trump.get(savedInstanceState.getInt("HAND1")).getSuit(),
					standard.trump.get(savedInstanceState.getInt("HAND1")).getSerial(),
					standard.trump.get(savedInstanceState.getInt("HAND1")).getColor()
					);

			trumpView[2].setTrump(
					standard.trump.get(savedInstanceState.getInt("HAND2")).getNumber(),
					standard.trump.get(savedInstanceState.getInt("HAND2")).getSuit(),
					standard.trump.get(savedInstanceState.getInt("HAND2")).getSerial(),
					standard.trump.get(savedInstanceState.getInt("HAND2")).getColor()
					);

			trumpView[3].setTrump(
					standard.trump.get(savedInstanceState.getInt("HAND3")).getNumber(),
					standard.trump.get(savedInstanceState.getInt("HAND3")).getSuit(),
					standard.trump.get(savedInstanceState.getInt("HAND3")).getSerial(),
					standard.trump.get(savedInstanceState.getInt("HAND3")).getColor()
					);

			trumpView[4].setTrump(
					standard.trump.get(savedInstanceState.getInt("HAND4")).getNumber(),
					standard.trump.get(savedInstanceState.getInt("HAND4")).getSuit(),
					standard.trump.get(savedInstanceState.getInt("HAND4")).getSerial(),
					standard.trump.get(savedInstanceState.getInt("HAND4")).getColor()
					);

			trumpView[5].setTrump(
					standard.trump.get(savedInstanceState.getInt("HAND5")).getNumber(),
					standard.trump.get(savedInstanceState.getInt("HAND5")).getSuit(),
					standard.trump.get(savedInstanceState.getInt("HAND5")).getSerial(),
					standard.trump.get(savedInstanceState.getInt("HAND5")).getColor()
					);
		}
	}

	// ////////////////////////////////////////////////
	// ボタンクリック時の処理
	// ////////////////////////////////////////////////

	// レイアウトViewをクリックした時の処理
	OnClickListener layoutListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

		}
	};

	// 手札1Viewをクリックした時の処理
	OnClickListener hand1Listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (dealAnimFlag && flipAnimFlag && moveAnimFlag && agreeTrump(1)) {
				if (ringerMode && !isPlugged) {
					soundPool.play(se_trump_select, 0.5F, 0.5F, 0, 0, 1.0F);
				} else if (isPlugged) {
					soundPool.play(se_trump_select, 0.1F, 0.1F, 0, 0, 1.0F);
				}
				moveTrump(1);
			}

		}
	};

	// 手札2Viewをクリックした時の処理
	OnClickListener hand2Listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (dealAnimFlag && flipAnimFlag && moveAnimFlag && agreeTrump(2)) {
				if (ringerMode && !isPlugged) {
					soundPool.play(se_trump_select, 0.5F, 0.5F, 0, 0, 1.0F);
				} else if (isPlugged) {
					soundPool.play(se_trump_select, 0.1F, 0.1F, 0, 0, 1.0F);
				}
				moveTrump(2);
			}

		}
	};
	// 手札3Viewをクリックした時の処理
	OnClickListener hand3Listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (dealAnimFlag && flipAnimFlag && moveAnimFlag && agreeTrump(3)) {
				if (ringerMode && !isPlugged) {
					soundPool.play(se_trump_select, 0.5F, 0.5F, 0, 0, 1.0F);
				} else if (isPlugged) {
					soundPool.play(se_trump_select, 0.1F, 0.1F, 0, 0, 1.0F);
				}
				moveTrump(3);
			}
		}

	};
	// 手札4Viewをクリックした時の処理
	OnClickListener hand4Listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (dealAnimFlag && flipAnimFlag && moveAnimFlag && agreeTrump(4)) {
				if (ringerMode && !isPlugged) {
					soundPool.play(se_trump_select, 0.5F, 0.5F, 0, 0, 1.0F);
				} else if (isPlugged) {
					soundPool.play(se_trump_select, 0.1F, 0.1F, 0, 0, 1.0F);
				}
				moveTrump(4);
			}

		}
	};
	// 手札5Viewをクリックした時の処理
	OnClickListener hand5Listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (dealAnimFlag && flipAnimFlag && moveAnimFlag && agreeTrump(5)) {
				if (ringerMode && !isPlugged) {
					soundPool.play(se_trump_select, 0.5F, 0.5F, 0, 0, 1.0F);
				} else if (isPlugged) {
					soundPool.play(se_trump_select, 0.1F, 0.1F, 0, 0, 1.0F);
				}
				moveTrump(5);
			}
		}

	};

	// コレクトボタンをクリックした時の処理
	OnClickListener collectBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (ringerMode && !isPlugged) {
				soundPool.play(se_cancel, 0.5F, 0.5F, 0, 0, 1.0F);
			} else if (isPlugged) {
				soundPool.play(se_cancel, 0.1F, 0.1F, 0, 0, 1.0F);
			}

			if (coinFlag) {
				skipFlag = true;
			}

			if (0 < coin.getWager()) {
				coin.cancelBet();
				wagerView.setText(String.valueOf(coin.getWager()));
				creditView.setText(String.valueOf(coin.getCredit()));
			}

		}
	};

	//	// ハーフダブルボタンをクリックした時の処理
	//	OnClickListener hbBtnListener = new OnClickListener() {
	//
	//		// 当たり枚数の半分を賭けて行うダブルダウンのこと。負けても半分は返ってくる。
	//		@Override
	//		public void onClick(View v) {
	//
	//			if (ringerMode && !isPlugged) {
	//				soundPool.play(se_beep, 0.5F, 0.5F, 0, 0, 1.5F);
	//			} else if (isPlugged) {
	//				soundPool.play(se_beep, 0.1F, 0.1F, 0, 0, 1.5F);
	//			}
	//
	//		}
	//	};
	// ベットボタンをクリックした時の処理
	OnClickListener betBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (!coinFlag) {

				if (ringerMode && !isPlugged) {
					soundPool.play(se_beep, 0.5F, 0.5F, 0, 0, 1.0F);
				} else if (isPlugged) {
					soundPool.play(se_beep, 0.1F, 0.1F, 0, 0, 1.0F);
				}

				coin.minBet();

				wagerView.setText(String.valueOf(coin.getWager()));
				creditView.setText(String.valueOf(coin.getCredit()));
			}
		}
	};
	// リピートボタンをクリックした時の処理
	OnClickListener repeatBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!coinFlag) {

				if (ringerMode && !isPlugged) {
					soundPool.play(se_beep, 0.5F, 0.5F, 0, 0, 0.5F);

				} else if (isPlugged) {
					soundPool.play(se_beep, 0.1F, 0.1F, 0, 0, 0.5F);

				}

				coin.repBet();
				wagerView.setText(String.valueOf(coin.getWager()));
				creditView.setText(String.valueOf(coin.getCredit()));
			}
		}
	};

	// ディールボタンをクリックした時の処理
	OnClickListener dealBtnListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (!coinFlag) {
				// 最小BET数を満たしていたらゲーム開始
				if (coin.getWager() >= coin.getMinbet() && counter == 0) {
					if (ringerMode && !isPlugged) {
						soundPool.play(se_enter, 0.5F, 0.5F, 0, 0, 1.0F);
					} else if (isPlugged) {
						soundPool.play(se_enter, 0.1F, 0.1F, 0, 0, 1.0F);
					}
					setTxtBounus(coin.getWager());

					RFcount = 0;
					SFcount = 0;
					FKcount = 0;
					FHcount = 0;
					FLcount = 0;

					// コイン操作画面を非表示にして、手札を表示する				
					findViewById(R.id.btnLayout).setVisibility(View.INVISIBLE);

					dealFlipTrump(1);

					start = System.currentTimeMillis();

				} else if (coin.getWager() >= coin.getMinbet() && counter > 0) {
					if (ringerMode && !isPlugged) {
						soundPool.play(se_enter, 0.5F, 0.5F, 0, 0, 1.0F);
					} else if (isPlugged) {
						soundPool.play(se_enter, 0.1F, 0.1F, 0, 0, 1.0F);
					}
					counter = 0;
					count.setText(String.valueOf(0));

					trumpView[0].setTrump(0, "", 0, Color.WHITE);

					// トランプ(52枚)を管理するDeckクラスのインスタンスの取得
					deck = new Deck(getApplicationContext());
					// トランプ(52枚)の生成とシャッフル
					deck.shuffle(getApplicationContext());
					// 場札に置いた順番を記録するインスタンスを取得する
					record = new Deck(getApplicationContext());

					setScrollView();

					setTxtBounus(coin.getWager());

					RFcount = 0;
					SFcount = 0;
					FKcount = 0;
					FHcount = 0;
					FLcount = 0;

					redrawGuide();

					for (int i = 1; i < 6; i++) {
						// 手札のビュー(trumpView[index])に新しい札の情報を移す
						trumpView[i].setTrump(deck.trump.get(i - 1).getNumber(),
								deck.trump.get(i - 1).getSuit(),
								deck.trump.get(i - 1).getSerial(),
								deck.trump.get(i - 1).getColor());

					}

					// コイン操作画面を非表示にして、手札を表示する				
					findViewById(R.id.btnLayout).setVisibility(View.INVISIBLE);
					trumpView[0].setVisibility(View.GONE);
					dealFlipTrump(1);
				}

			}
		}
	};

	// メッセージレイアウトをクリックした時の処理
	OnClickListener msgListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (coinFlag) {
				skipFlag = true;
			}

			findViewById(R.id.msgLayout).setVisibility(View.INVISIBLE);
			findViewById(R.id.btnLayout).setVisibility(View.VISIBLE);

			if (ringerMode && !isPlugged) {
				soundPool.play(se_peep, 0.5F, 0.5F, 0, 0, 1.0F);
			} else if (isPlugged) {
				soundPool.play(se_peep, 0.1F, 0.1F, 0, 0, 1.0F);
			}

		}
	};

	// ////////////////////////////////////////////////
	// /google-play-services_lib
	// ////////////////////////////////////////////////
	@Override
	public void onSignInFailed() {
		findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
		findViewById(R.id.sign_out_button).setVisibility(View.GONE);
		//		Log.v(TAG, "onSignInFailed()");
	}

	@Override
	public void onSignInSucceeded() {
		findViewById(R.id.sign_in_button).setVisibility(View.GONE);
		findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
		//		Log.v(TAG, "onSignInSucceeded()");
	}

	private OnClickListener SignInListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			beginUserInitiatedSignIn();

		}
	};

	private OnClickListener SignOutListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			signOut();
			findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
			findViewById(R.id.sign_out_button).setVisibility(View.GONE);
		}
	};

	private GamesClient mGamesClient;

	// ////////////////////////////////////////////////
	// /menu
	// ////////////////////////////////////////////////	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int movement = 0;
		int id = item.getItemId();

		if (id == R.id.rotate) {
			// Portrait(縦長)
			if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			// Landscape(横長)
			else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}

		}

		//		else if (id == R.id.RF) {
		//			pokerPosition = 1;
		//
		//			if (pokerPosition < pokerPrevPosition) {// 現在位置が移動先よりも上にある場合
		//				movement = pokerPosition - pokerPrevPosition;
		//				if (movement != 0) {// 移動量が0でなければスクロール
		//					bonusScroll2.smoothScrollBy(0, scrollHeight * movement);
		//					paysScroll2.smoothScrollBy(0, scrollHeight * movement);
		//					hitsScroll1.smoothScrollBy(0, scrollHeight * movement);
		//				}
		//			} else {// 現在位置が移動先よりも下にある場合
		//				movement = pokerPrevPosition - pokerPosition;
		//				if (movement != 0) {// 移動量が0でなければスクロール
		//					bonusScroll2.smoothScrollBy(0, -scrollHeight * movement);
		//					paysScroll2.smoothScrollBy(0, -scrollHeight * movement);
		//					hitsScroll1.smoothScrollBy(0, -scrollHeight * movement);
		//				}
		//			}
		//
		//			RFcount++;
		//			((TextView) findViewById(R.id.hitsFlag1)).setText(String.valueOf(RFcount));
		//			((TextView) findViewById(R.id.handBonus1)).setText(String.valueOf(ratePoker[1] * coin.getWager()));
		//
		//			if (ringerMode && !isPlugged) {
		//				soundPool.play(se_coin, 0.5F, 0.5F, 0, 0, 1.0F);
		//			} else if (isPlugged) {
		//				soundPool.play(se_coin, 0.1F, 0.1F, 0, 0, 1.0F);
		//			}
		//			pokerPrevPosition = pokerPosition;
		//
		//		} else if (id == R.id.SF) {
		//			pokerPosition = 2;
		//
		//			if (pokerPosition < pokerPrevPosition) {// 現在位置が移動先よりも上にある場合
		//				movement = pokerPosition - pokerPrevPosition;
		//				if (movement != 0) {// 移動量が0でなければスクロール
		//					bonusScroll2.smoothScrollBy(0, scrollHeight * movement);
		//					paysScroll2.smoothScrollBy(0, scrollHeight * movement);
		//					hitsScroll1.smoothScrollBy(0, scrollHeight * movement);
		//				}
		//			} else {// 現在位置が移動先よりも下にある場合
		//				movement = pokerPrevPosition - pokerPosition;
		//				if (movement != 0) {// 移動量が0でなければスクロール
		//					bonusScroll2.smoothScrollBy(0, -scrollHeight * movement);
		//					paysScroll2.smoothScrollBy(0, -scrollHeight * movement);
		//					hitsScroll1.smoothScrollBy(0, -scrollHeight * movement);
		//				}
		//			}
		//
		//			SFcount++;
		//			((TextView) findViewById(R.id.hitsFlag2)).setText(String.valueOf(SFcount));
		//			((TextView) findViewById(R.id.handBonus2)).setText(String.valueOf(ratePoker[2] * coin.getWager()));
		//
		//			if (ringerMode && !isPlugged) {
		//				soundPool.play(se_coin, 0.5F, 0.5F, 0, 0, 1.0F);
		//			} else if (isPlugged) {
		//				soundPool.play(se_coin, 0.1F, 0.1F, 0, 0, 1.0F);
		//			}
		//			pokerPrevPosition = pokerPosition;
		//
		//		} else if (id == R.id.FK) {
		//			pokerPosition = 3;
		//
		//			if (pokerPosition < pokerPrevPosition) {// 現在位置が移動先よりも上にある場合
		//				movement = pokerPosition - pokerPrevPosition;
		//				if (movement != 0) {// 移動量が0でなければスクロール
		//					bonusScroll2.smoothScrollBy(0, scrollHeight * movement);
		//					paysScroll2.smoothScrollBy(0, scrollHeight * movement);
		//					hitsScroll1.smoothScrollBy(0, scrollHeight * movement);
		//				}
		//			} else {// 現在位置が移動先よりも下にある場合
		//				movement = pokerPrevPosition - pokerPosition;
		//				if (movement != 0) {// 移動量が0でなければスクロール
		//					bonusScroll2.smoothScrollBy(0, -scrollHeight * movement);
		//					paysScroll2.smoothScrollBy(0, -scrollHeight * movement);
		//					hitsScroll1.smoothScrollBy(0, -scrollHeight * movement);
		//				}
		//			}
		//			FKcount++;
		//			((TextView) findViewById(R.id.hitsFlag3)).setText(String.valueOf(FKcount));
		//			((TextView) findViewById(R.id.handBonus3)).setText(String.valueOf(ratePoker[3] * coin.getWager()));
		//
		//			if (ringerMode && !isPlugged) {
		//				soundPool.play(se_coin, 0.5F, 0.5F, 0, 0, 1.0F);
		//			} else if (isPlugged) {
		//				soundPool.play(se_coin, 0.1F, 0.1F, 0, 0, 1.0F);
		//			}
		//			pokerPrevPosition = pokerPosition;
		//
		//		} else if (id == R.id.FH) {
		//			pokerPosition = 4;
		//
		//			if (pokerPosition < pokerPrevPosition) {// 現在位置が移動先よりも上にある場合
		//				movement = pokerPosition - pokerPrevPosition;
		//				if (movement != 0) {// 移動量が0でなければスクロール
		//					bonusScroll2.smoothScrollBy(0, scrollHeight * movement);
		//					paysScroll2.smoothScrollBy(0, scrollHeight * movement);
		//					hitsScroll1.smoothScrollBy(0, scrollHeight * movement);
		//				}
		//			} else {// 現在位置が移動先よりも下にある場合
		//				movement = pokerPrevPosition - pokerPosition;
		//				if (movement != 0) {// 移動量が0でなければスクロール
		//					bonusScroll2.smoothScrollBy(0, -scrollHeight * movement);
		//					paysScroll2.smoothScrollBy(0, -scrollHeight * movement);
		//					hitsScroll1.smoothScrollBy(0, -scrollHeight * movement);
		//				}
		//			}
		//			FHcount++;
		//			((TextView) findViewById(R.id.hitsFlag4)).setText(String.valueOf(FHcount));
		//			((TextView) findViewById(R.id.handBonus4)).setText(String.valueOf(ratePoker[4] * coin.getWager()));
		//
		//			if (ringerMode && !isPlugged) {
		//				soundPool.play(se_coin, 0.5F, 0.5F, 0, 0, 1.0F);
		//			} else if (isPlugged) {
		//				soundPool.play(se_coin, 0.1F, 0.1F, 0, 0, 1.0F);
		//			}
		//			pokerPrevPosition = pokerPosition;
		//
		//		} else if (id == R.id.FL) {
		//			pokerPosition = 5;
		//
		//			if (pokerPosition < pokerPrevPosition) {// 現在位置が移動先よりも上にある場合
		//				movement = pokerPosition - pokerPrevPosition;
		//				if (movement != 0) {// 移動量が0でなければスクロール
		//					bonusScroll2.smoothScrollBy(0, scrollHeight * movement);
		//					paysScroll2.smoothScrollBy(0, scrollHeight * movement);
		//					hitsScroll1.smoothScrollBy(0, scrollHeight * movement);
		//				}
		//			} else {// 現在位置が移動先よりも下にある場合
		//				movement = pokerPrevPosition - pokerPosition;
		//				if (movement != 0) {// 移動量が0でなければスクロール
		//					bonusScroll2.smoothScrollBy(0, -scrollHeight * movement);
		//					paysScroll2.smoothScrollBy(0, -scrollHeight * movement);
		//					hitsScroll1.smoothScrollBy(0, -scrollHeight * movement);
		//				}
		//			}
		//			FLcount++;
		//			((TextView) findViewById(R.id.hitsFlag5)).setText(String.valueOf(FLcount));
		//			((TextView) findViewById(R.id.handBonus5)).setText(String.valueOf(ratePoker[5] * coin.getWager()));
		//
		//			if (ringerMode && !isPlugged) {
		//				soundPool.play(se_coin, 0.5F, 0.5F, 0, 0, 1.0F);
		//			} else if (isPlugged) {
		//				soundPool.play(se_coin, 0.1F, 0.1F, 0, 0, 1.0F);
		//			}
		//			pokerPrevPosition = pokerPosition;
		//
		//		} 

		else if (id == R.id.ranking) {

			if (isSignedIn()) {

				startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), getString(R.string.lb_id)), 4649);

			} else {
				showAlert("Please sign in to view leaderboards.");
			}

		} else if (id == R.id.send) {

			if (isSignedIn()) {
				int score = 99999999;

				//				Games.Leaderboards.submitScore(getApiClient(), getString(R.string.lb_id), score);

				ResultCallback<SubmitScoreResult> rc = new ResultCallback<Leaderboards.SubmitScoreResult>() {

					@Override
					public void onResult(SubmitScoreResult arg0) {
						Log.v(TAG, "TonResult" + arg0.getStatus());
						Log.v(TAG, "TonResult" + arg0.getStatus().getStatusCode());

						if (arg0.getStatus().getStatusCode() == GamesStatusCodes.STATUS_OK) {
							Log.v(TAG, "STATUS_OK");//正常に送信された場合

							Toast.makeText(WhatifActivity.this, "ランキングへの送信しました", Toast.LENGTH_SHORT).show();
						} else if (arg0.getStatus().getStatusCode() == GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED) {
							Log.v(TAG, "STATUS_NETWORK_ERROR_OPERATION_DEFERRED");//端末がオフラインだった場合
						} else if (arg0.getStatus().getStatusCode() == GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED) {
							Log.v(TAG, "STATUS_CLIENT_RECONNECT_REQUIRED");//スコア送信前に再接続が必要な場合
						} else if (arg0.getStatus().getStatusCode() == GamesStatusCodes.STATUS_LICENSE_CHECK_FAILED) {
							Log.v(TAG, "STATUS_LICENSE_CHECK_FAILED");//ユーザーに許可されなかった場合
						} else if (arg0.getStatus().getStatusCode() == GamesStatusCodes.STATUS_INTERNAL_ERROR) {
							Log.v(TAG, "STATUS_INTERNAL_ERROR");//予期しないエラーが発生した場合
						}

					}
				};

				Games.Leaderboards.submitScoreImmediate(getApiClient(), getString(R.string.lb_id), score).setResultCallback(rc);

				//				Toast.makeText(this, "TEST:成功", Toast.LENGTH_SHORT).show();

			}

		} else if (id == R.id.trophy) {
			if (isSignedIn()) {
				startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 4649);
			}
		} else if (id == R.id.sendtrophy) {
			user.clear_count++;
			Toast.makeText(this, "user.clear_count:" + user.clear_count, Toast.LENGTH_SHORT).show();
		} else if (id == R.id.dialog) {

			callDialog();

		}

		return super.onOptionsItemSelected(item);
	}

	////////////////////////////////////////////////
	// Leaderboards.SubmitScoreResult
	////////////////////////////////////////////////

	@Override
	public void release() {
		Log.v(TAG, "release()");
	}

	@Override
	public Status getStatus() {
		Log.v(TAG, "getStatus()");
		return null;
	}

	@Override
	public ScoreSubmissionData getScoreData() {
		Log.v(TAG, "getScoreData()");
		return null;
	}

}
