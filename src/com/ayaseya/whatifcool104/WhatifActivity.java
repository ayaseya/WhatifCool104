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

import net.nend.android.NendAdIconLoader;
import net.nend.android.NendAdListener;
import net.nend.android.NendAdView;
import net.nend.android.NendAdView.NendError;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.net.Uri;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.achievement.Achievements.UpdateAchievementResult;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.leaderboard.Leaderboards.SubmitScoreResult;
import com.google.example.games.basegameutils.BaseGameActivity;

public class WhatifActivity extends BaseGameActivity
		implements ViewFactory, NendAdListener {

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

	private enum GAME {
		WIN, LOSE, DRAW
	}

	private GAME result;

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

	private boolean gameFlag = false;//ゲーム中であるか否かの判断

	private boolean doubleDownGameFlag = false;//ゲーム中であるか否かの判断

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
	private Dialog alert_doubleDown1;
	private Dialog alert_doubleDown2;
	private Dialog alert_clear;

	private long clearTime;

	private ProgressDialog loading;

	private ResultCallback<SubmitScoreResult> callback;

	private int count_DoubleDown = 0;

	private NendAdIconLoader nend;

	private AlertDialog basic_alert;

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
			//			Log.v(TAG, "> SaveData.datが存在しました");
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
				//				Log.v(TAG, "Error");
			}
		} else {
			//			Log.v(TAG, "SaveData.datが存在しませんでした");
		}
		setContentView(R.layout.whatif_layout);

		findViewById(R.id.black).bringToFront();

		//端末の画面の向きを取得するためリソースを取得する
		config = getResources().getConfiguration();

		// トランプ(52枚)を管理するDeckクラスのインスタンスの取得
		deck = new Deck(this.getApplicationContext());
		// トランプ(52枚)の生成とシャッフル
		deck.shuffle(this.getApplicationContext());
		//		deck.standard(this.getApplicationContext());

		//		StringBuffer sequence = new StringBuffer(">Deck="); 
		//		for(int i=0;i<deck.trump.size();i++){
		//			
		//			sequence.append(deck.trump.get(i).getSuit());
		//			sequence.append(String.valueOf(deck.trump.get(i).getNumber()));
		//			sequence.append(",");
		//		}
		//		Log.v(TAG, sequence.toString());

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

		trumpBackView[2].setOnClickListener(back2Listener);
		trumpBackView[3].setOnClickListener(back3Listener);
		trumpBackView[4].setOnClickListener(back4Listener);
		trumpBackView[5].setOnClickListener(back5Listener);

		findViewById(R.id.cancelBtn).setOnClickListener(collectBtnListener);
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
		msg = (TextView) findViewById(R.id.msgView1);

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

		// ダイアログの設定
		loading = new ProgressDialog(this);
		loading.setTitle(getString(R.string.transmitting));
		loading.setMessage(getString(R.string.wait));
		loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		loading.setCancelable(false);

		nend = new NendAdIconLoader(getApplicationContext(), 156841, "ebaef1347f6ca1a4cd696a987d0d9fbba4dda8fa");

		//		Log.v(TAG, "onCreate()");

	}// onCreate()

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (user.game) {
			gameFlag = user.game;

			coin.setWager(user.wager);
			coin.setWin(user.win);
			coin.setPaid(user.paid);
			coin.setCredit(user.credit);

			counter = user.counter;

			RFcount = user.RFcount;
			SFcount = user.SFcount;
			FKcount = user.FKcount;
			FHcount = user.FHcount;
			FLcount = user.FLcount;

			deck = user.deck;
			record = user.record;

			start = user.start;

			trumpView[0].setTrump(
					standard.trump.get(user.trump0_serial).getNumber(),
					standard.trump.get(user.trump0_serial).getSuit(),
					standard.trump.get(user.trump0_serial).getSerial(),
					standard.trump.get(user.trump0_serial).getColor()
					);

			trumpView[1].setTrump(
					standard.trump.get(user.trump1_serial).getNumber(),
					standard.trump.get(user.trump1_serial).getSuit(),
					standard.trump.get(user.trump1_serial).getSerial(),
					standard.trump.get(user.trump1_serial).getColor()
					);

			trumpView[2].setTrump(
					standard.trump.get(user.trump2_serial).getNumber(),
					standard.trump.get(user.trump2_serial).getSuit(),
					standard.trump.get(user.trump2_serial).getSerial(),
					standard.trump.get(user.trump2_serial).getColor()
					);

			trumpView[3].setTrump(
					standard.trump.get(user.trump3_serial).getNumber(),
					standard.trump.get(user.trump3_serial).getSuit(),
					standard.trump.get(user.trump3_serial).getSerial(),
					standard.trump.get(user.trump3_serial).getColor()
					);

			trumpView[4].setTrump(
					standard.trump.get(user.trump4_serial).getNumber(),
					standard.trump.get(user.trump4_serial).getSuit(),
					standard.trump.get(user.trump4_serial).getSerial(),
					standard.trump.get(user.trump4_serial).getColor()
					);

			trumpView[5].setTrump(
					standard.trump.get(user.trump5_serial).getNumber(),
					standard.trump.get(user.trump5_serial).getSuit(),
					standard.trump.get(user.trump5_serial).getSerial(),
					standard.trump.get(user.trump5_serial).getColor()
					);
			//			Log.v(TAG, "読み込み");
			user.game = false;
		}

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

		// ゲーム中なら状態を保存しておく
		if (findViewById(R.id.msgLayout).getVisibility() == View.INVISIBLE &&
				findViewById(R.id.btnLayout).getVisibility() == View.INVISIBLE) {

			user.wager = coin.getWager();
			user.win = coin.getWin();
			user.paid = coin.getPaid();
			user.credit = coin.getCredit();

			user.game = true;
			user.counter = counter;

			user.RFcount = RFcount;
			user.SFcount = SFcount;
			user.FKcount = FKcount;
			user.FHcount = FHcount;
			user.FLcount = FLcount;

			user.trump0_serial = trumpView[0].getSerial();

			user.trump1_serial = trumpView[1].getSerial();
			user.trump2_serial = trumpView[2].getSerial();
			user.trump3_serial = trumpView[3].getSerial();
			user.trump4_serial = trumpView[4].getSerial();
			user.trump5_serial = trumpView[5].getSerial();

			user.deck = deck;
			user.record = record;

			user.start = start;

			//			Log.v(TAG, "保存");
		}

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
			//			Log.d(TAG, "Error");
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

	// トランプ1枚をアニメーションの処理(アニメーション開始の遅れを引数で指定する)
	private void FlipTrump(final int index, final long time) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(time);

					handler.post(new Runnable() {

						@Override
						public void run() {

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

											secondDoubleDownDialog();
										}
									});
								}
							});

						}

					});

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}).start();

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

		pokerPrevPosition = 0;

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
				checkCounterAchievement();// 実績解除

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

				if (isSignedIn()) {
					if (user.RF_achievement == true) {
						return;
					} else {
						Games.Achievements.unlockImmediate(getApiClient(), getString(R.string.achievement_royal_flush))
								.setResultCallback(new ResultCallback<UpdateAchievementResult>() {

									@Override
									public void onResult(UpdateAchievementResult result) {
										user.RF_achievement = true;
									}
								});
					}
				}

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

				if (isSignedIn()) {
					if (user.SF_achievement == true) {
						return;
					} else {
						Games.Achievements.unlockImmediate(getApiClient(), getString(R.string.achievement_straight_flush))
								.setResultCallback(new ResultCallback<UpdateAchievementResult>() {

									@Override
									public void onResult(UpdateAchievementResult result) {
										user.SF_achievement = true;
									}
								});
					}
				}

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

				if (isSignedIn()) {
					if (user.FK_achievement == true) {
						return;
					} else {
						Games.Achievements.unlockImmediate(getApiClient(), getString(R.string.achievement_four_of_a_kind))
								.setResultCallback(new ResultCallback<UpdateAchievementResult>() {

									@Override
									public void onResult(UpdateAchievementResult result) {
										user.FK_achievement = true;
									}
								});
					}
				}
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

				if (isSignedIn()) {
					if (user.FH_achievement == true) {
						return;
					} else {
						Games.Achievements.unlockImmediate(getApiClient(), getString(R.string.achievement_full_house))
								.setResultCallback(new ResultCallback<UpdateAchievementResult>() {

									@Override
									public void onResult(UpdateAchievementResult result) {
										user.FH_achievement = true;
									}
								});
					}
				}

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

				if (isSignedIn()) {
					if (user.FL_achievement == true) {
						return;
					} else {
						Games.Achievements.unlockImmediate(getApiClient(), getString(R.string.achievement_flush))
								.setResultCallback(new ResultCallback<UpdateAchievementResult>() {

									@Override
									public void onResult(UpdateAchievementResult result) {
										user.FL_achievement = true;
									}
								});
					}
				}
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

						} catch (InterruptedException e) {
							e.printStackTrace();
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
		//		paidView.setText(String.valueOf(coin.getPaid()));
		paidView.setText("0");
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
								coin.setWin(0);
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
								coin.setWin(0);
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
								coin.setWin(0);
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
								coin.setWin(0);
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
			coin.setWin(0);
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

	// ダイアログを表示する
	private void callDialog() {
		LayoutInflater inflater = LayoutInflater.from(WhatifActivity.this);
		View dialog = inflater.inflate(R.layout.clear_dialog,
				(ViewGroup) findViewById(R.id.clear_dialog));

		Button closeBtn = (Button) dialog.findViewById(R.id.closeBtn);
		closeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				alert_clear.dismiss();
			}
		});

		Button speedrunBtn = (Button) dialog.findViewById(R.id.SpeedrunBtn);
		speedrunBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				sendRanking();
			}
		});

		Button twitterBtn = (Button) dialog.findViewById(R.id.Twitter);
		twitterBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				// ミリ秒で計測した時間を時分秒形式に変換

				if (clearTime >= 3600000) {

					int hours = (int) ((clearTime / 1000) / 3600);
					int minutes = (int) (((clearTime / 1000) % 3600) / 60);
					int seconds = (int) ((clearTime / 1000) % 60);

					String tweet = getString(R.string.app_name) + getString(R.string.twitter_msg1)
							+ hours + getString(R.string.hours)
							+ minutes + getString(R.string.minutes)
							+ seconds + getString(R.string.seconds)
							+ getString(R.string.twitter_msg2);

					String url = "http://twitter.com/share?text=" + tweet;
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);
				} else if (clearTime < 3600000) {
					int minutes = (int) ((clearTime / 1000) / 60);
					int seconds = (int) ((clearTime / 1000) % 60);

					String tweet = getString(R.string.app_name) + getString(R.string.twitter_msg1)
							+ minutes + getString(R.string.minutes)
							+ seconds + getString(R.string.seconds)
							+ getString(R.string.twitter_msg2);

					String url = "http://twitter.com/share?text=" + tweet;
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(intent);

				}
			}
		});

		alert_clear = new AlertDialog.Builder(WhatifActivity.this)
				.setTitle(getString(R.string.game_clear))
				.setView(dialog)
				.setCancelable(false)
				.show();

	}

	// ランキングへハイスコアを送信する
	private void sendHighScore() {

		Games.Leaderboards.submitScoreImmediate(getApiClient(), getString(R.string.leaderboard_what_ifhigh_score), coin.getCredit())
				.setResultCallback(new ResultCallback<Leaderboards.SubmitScoreResult>() {

					@Override
					public void onResult(SubmitScoreResult result) {
						Log.v(TAG, "TonResult" + result.getStatus());
						Log.v(TAG, "TonResult" + result.getStatus().getStatusCode());

						if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_OK) {
							//						Log.v(TAG, "STATUS_OK");//正常に送信された場合
							Toast.makeText(WhatifActivity.this, getString(R.string.send_data_msg), Toast.LENGTH_SHORT).show();

						} else if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED) {
							//						Log.v(TAG, "STATUS_NETWORK_ERROR_OPERATION_DEFERRED");//端末がオフラインだった場合
							Toast.makeText(WhatifActivity.this, getString(R.string.send_error_msg), Toast.LENGTH_SHORT).show();
						} else if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED) {
							//						Log.v(TAG, "STATUS_CLIENT_RECONNECT_REQUIRED");//スコア送信前に再接続が必要な場合
							Toast.makeText(WhatifActivity.this, getString(R.string.send_error_msg), Toast.LENGTH_SHORT).show();
						} else if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_LICENSE_CHECK_FAILED) {
							//						Log.v(TAG, "STATUS_LICENSE_CHECK_FAILED");//ユーザーに許可されなかった場合
							Toast.makeText(WhatifActivity.this, getString(R.string.send_error_msg), Toast.LENGTH_SHORT).show();
						} else if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_INTERNAL_ERROR) {
							//						Log.v(TAG, "STATUS_INTERNAL_ERROR");//予期しないエラーが発生した場合
							Toast.makeText(WhatifActivity.this, getString(R.string.send_error_msg), Toast.LENGTH_SHORT).show();
						}

						loading.dismiss();

					}
				});

	}

	// ランキングへクリアタイムを送信する
	private void sendRanking() {

		if (isSignedIn() && clearTime != 0) {
			//			int score = 99999999;
			callback = new ResultCallback<Leaderboards.SubmitScoreResult>() {

				@Override
				public void onResult(SubmitScoreResult result) {
					Log.v(TAG, "TonResult" + result.getStatus());
					Log.v(TAG, "TonResult" + result.getStatus().getStatusCode());

					if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_OK) {
						//						Log.v(TAG, "STATUS_OK");//正常に送信された場合

						Toast.makeText(WhatifActivity.this, getString(R.string.send_data_msg), Toast.LENGTH_SHORT).show();

					} else if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED) {
						//						Log.v(TAG, "STATUS_NETWORK_ERROR_OPERATION_DEFERRED");//端末がオフラインだった場合
						Toast.makeText(WhatifActivity.this, getString(R.string.send_error_msg), Toast.LENGTH_SHORT).show();
					} else if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED) {
						//						Log.v(TAG, "STATUS_CLIENT_RECONNECT_REQUIRED");//スコア送信前に再接続が必要な場合
						Toast.makeText(WhatifActivity.this, getString(R.string.send_error_msg), Toast.LENGTH_SHORT).show();
					} else if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_LICENSE_CHECK_FAILED) {
						//						Log.v(TAG, "STATUS_LICENSE_CHECK_FAILED");//ユーザーに許可されなかった場合
						Toast.makeText(WhatifActivity.this, getString(R.string.send_error_msg), Toast.LENGTH_SHORT).show();
					} else if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_INTERNAL_ERROR) {
						//						Log.v(TAG, "STATUS_INTERNAL_ERROR");//予期しないエラーが発生した場合
						Toast.makeText(WhatifActivity.this, getString(R.string.send_error_msg), Toast.LENGTH_SHORT).show();
					}

					loading.dismiss();

				}
			};

			Games.Leaderboards.submitScoreImmediate(getApiClient(), getString(R.string.leaderboard_what_ifspeedrun), clearTime).setResultCallback(
					callback);
			loading.show();
		}
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
			if (!gameFlag) {
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
			new Thread(new Runnable() {

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
			}).start();

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

			if (gameFlag) {

				findViewById(R.id.btnLayout).setVisibility(View.INVISIBLE);
				// トランプ画像を表示にする
				for (int i = 0; i <= 5; i++) {
					trumpView[i].setVisibility(View.VISIBLE);
				}

				if (counter == 0) {
					trumpView[0].setVisibility(View.INVISIBLE);
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
			if (!gameFlag) {

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
			new Thread(new Runnable() {

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
			}).start();

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

			if (gameFlag) {

				findViewById(R.id.btnLayout).setVisibility(View.INVISIBLE);
				// トランプ画像を非表示にする
				for (int i = 0; i <= 5; i++) {
					trumpView[i].setVisibility(View.VISIBLE);
				}

				if (counter == 0) {
					trumpView[0].setVisibility(View.INVISIBLE);
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
							// リピートボタン用に掛け金を保存しておく
							coin.setBeforeWager(coin.getWager());

							for (int i = 1; i < 6; i++) {
								trumpView[i].setVisibility(View.INVISIBLE);
							}

							if (coin.getWager() < calculateCoin()) {

								if (ringerMode && !isPlugged) {
									soundPool.play(se_winner, 0.5F, 0.5F, 0, 0, 1.0F);
								} else if (isPlugged) {
									soundPool.play(se_winner, 0.1F, 0.1F, 0, 0, 1.0F);
								}

								if (counter >= 14 && counter < 52) {
									firstDoubleDownDialog();
									coin.setWin(calculateCoin());
									redrawCoin();
								} else if (counter < 14) {// ポーカーの役だけで払戻金が発生した場合はDoubleDownゲームは発生しない

									msg.setText(getString(R.string.winner));
									msg.setTextColor(Color.RED);
									// 手札を非表示にして、メッセージ画面手札を表示する

									findViewById(R.id.msgLayout).setVisibility(View.VISIBLE);
									refundCoin();

								} else if (counter == 52) {
									stop = System.currentTimeMillis();
									clearTime = stop - start;

									user.clear_count++;

									if (isSignedIn()) {
										if (user.clear_count >= 10 && user.complete_10_achievement == false) {

											Games.Achievements.unlockImmediate(getApiClient(), getString(R.string.achievement_complete_10))
													.setResultCallback(new ResultCallback<UpdateAchievementResult>() {

														@Override
														public void onResult(UpdateAchievementResult result) {
															user.complete_10_achievement = true;
														}
													});
										}
									}

									firstDoubleDownDialog();
									coin.setWin(calculateCoin());
									redrawCoin();

								}

							}
							else if (coin.getWager() == calculateCoin()) {

								if (ringerMode && !isPlugged) {
									soundPool.play(se_even, 0.5F, 0.5F, 0, 0, 1.0F);
								} else if (isPlugged) {
									soundPool.play(se_even, 0.1F, 0.1F, 0, 0, 1.0F);
								}

								msg.setText(getString(R.string.draw));
								msg.setTextColor(Color.GREEN);

								// 手札を非表示にして、メッセージ画面手札を表示する
								findViewById(R.id.msgLayout).setVisibility(View.VISIBLE);
								refundCoin();
							}
							else {
								if (ringerMode && !isPlugged) {
									soundPool.play(se_loser, 0.5F, 0.5F, 0, 0, 1.0F);
								} else if (isPlugged) {
									soundPool.play(se_loser, 0.1F, 0.1F, 0, 0, 1.0F);
								}

								msg.setText(getString(R.string.loser));
								msg.setTextColor(Color.BLUE);

								present();

								// 手札を非表示にして、メッセージ画面手札を表示する
								findViewById(R.id.msgLayout).setVisibility(View.VISIBLE);
								refundCoin();
							}

							//							stop = System.currentTimeMillis();
							//
							//							clearTime = stop - start;

							// callDialog();

						}

					});

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}).start();

	}// GameOver_**********

	private void present() {
		if (coin.getCredit() == 0) {
			Toast.makeText(this.getApplicationContext(), getString(R.string.present), Toast.LENGTH_SHORT).show();
			countUp(100);
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
			gameFlag = true;// onCreat()で初期化(false)するのでActivity中ではfalseにしない
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

			if (dealAnimFlag && flipAnimFlag && moveAnimFlag && agreeTrump(1) && !doubleDownGameFlag) {
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

			if (dealAnimFlag && flipAnimFlag && moveAnimFlag && agreeTrump(2) && !doubleDownGameFlag) {
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

			if (dealAnimFlag && flipAnimFlag && moveAnimFlag && agreeTrump(3) && !doubleDownGameFlag) {
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

			if (dealAnimFlag && flipAnimFlag && moveAnimFlag && agreeTrump(4) && !doubleDownGameFlag) {
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

			if (dealAnimFlag && flipAnimFlag && moveAnimFlag && agreeTrump(5) && !doubleDownGameFlag) {
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
					//					deck.standard(getApplicationContext());

					//					StringBuffer sequence = new StringBuffer(">Deck="); 
					//					for(int i=0;i<deck.trump.size();i++){
					//						
					//						sequence.append(deck.trump.get(i).getSuit());
					//						sequence.append(String.valueOf(deck.trump.get(i).getNumber()));
					//						sequence.append(",");
					//					}
					//					Log.v(TAG, sequence.toString());

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

			redrawCoin();

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

	// ////////////////////////////////////////////////
	// /menu
	// ////////////////////////////////////////////////	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//		int movement = 0;
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

		} else if (id == R.id.ranking) {

			if (isSignedIn()) {
				startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), getString(R.string.leaderboard_what_ifspeedrun)), 4649);
			} else {
				Toast.makeText(this, getString(R.string.please), Toast.LENGTH_SHORT).show();
			}

		} else if (id == R.id.trophy) {
			if (isSignedIn()) {
				startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 4649);
			} else {
				Toast.makeText(this, getString(R.string.please), Toast.LENGTH_SHORT).show();
			}
		} else if (id == R.id.lucky) {
			if (isSignedIn()) {
				startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), getString(R.string.leaderboard_what_ifdoubledown)),
						4649);
			} else {
				Toast.makeText(this, getString(R.string.please), Toast.LENGTH_SHORT).show();
			}
		} else if (id == R.id.high_score) {
			if (isSignedIn()) {
				startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), getString(R.string.leaderboard_what_ifhigh_score)),
						4649);
			} else {
				Toast.makeText(this, getString(R.string.please), Toast.LENGTH_SHORT).show();
			}
		} else if (id == R.id.high_score_send) {
			if (isSignedIn()) {
				
				
				
				LayoutInflater inflater = LayoutInflater.from(WhatifActivity.this);
				View dialog = inflater.inflate(R.layout.basic_dialog,
						(ViewGroup) findViewById(R.id.basic_dialog));

				((Button) dialog.findViewById(R.id.basic_canselBtn))
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View view) {

								basic_alert.dismiss();

							}
						});

				((Button) dialog.findViewById(R.id.basic_okBtn))
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View view) {
								
								basic_alert.dismiss();
								sendHighScore();

							}
						});

				basic_alert = new AlertDialog.Builder(WhatifActivity.this)
						.setView(dialog)
						.setCancelable(false)
						.show();
				
				
				
			} else {
				Toast.makeText(this, getString(R.string.please), Toast.LENGTH_SHORT).show();
			}
		}

		return super.onOptionsItemSelected(item);
	}

	// ////////////////////////////////////////////////
	// /DoubleDown
	// ////////////////////////////////////////////////	

	private void doubleDown() {

		if (counter >= 14) {
			doubleDownGameFlag = true;
			Deck dealer = new Deck(this);
			dealer.shuffle(this);

			// 掛け金にメインゲーム、もしくはDoubleDownゲームの払戻金を表示する
			// 全額を掛け金にするので払戻金を0にする

			if (count_DoubleDown == 0) {// 初回のDoubleDownゲーム
				coin.setWager(coin.getWin());
				coin.setWin(0);
				redrawCoin();
			} else {// 2回目以降のDoubleDownゲーム
				coin.setWager(coin.getWager() * 2);
				redrawCoin();

			}
			// 手札を非表示にする
			//			for (int i = 0; i < 6; i++) {
			//				trumpView[i].setVisibility(View.INVISIBLE);
			//			}

			// カードを裏面の状態で配る
			for (int i = 1; i < 6; i++) {
				trumpBackView[i].setVisibility(View.VISIBLE);
			}

			// DEALERの選択した手札の下に"DEALER"TextViewを表示する
			((TextView) findViewById(R.id.dealer)).setVisibility(View.VISIBLE);

			// 山札から5枚トランプの情報を読み込む

			for (int i = 1; i <= 5; i++) {
				trumpView[i].setTrump(dealer.trump.get(i - 1).getNumber(),
						dealer.trump.get(i - 1).getSuit(),
						dealer.trump.get(i - 1).getSerial(),
						dealer.trump.get(i - 1).getColor());
			}

			// DEALERの選択した手札を裏返す

			FlipTrump(1);

			count_DoubleDown++;

		}

	}

	private void halfDown() {

		if (counter >= 14) {
			doubleDownGameFlag = true;
			Deck dealer = new Deck(this);
			dealer.shuffle(this);

			// 掛け金にメインゲーム、もしくはDoubleDownゲームの払戻金の半額を表示する
			if (count_DoubleDown == 0) {// 初回のDoubleDownゲーム
				int wager = coin.getWin() / 2;

				coin.setWager(wager);
				coin.setWin(coin.getWin() - wager);
				redrawCoin();
			} else {// 2回目以降のDoubleDownゲーム

				coin.setWager(coin.getWager());
				coin.setWin(coin.getWin() + coin.getWager());
				redrawCoin();

			}

			// 手札を非表示にする
			//			for (int i = 0; i < 6; i++) {
			//				trumpView[i].setVisibility(View.INVISIBLE);
			//			}

			// カードを裏面の状態で配る
			for (int i = 1; i < 6; i++) {
				trumpBackView[i].setVisibility(View.VISIBLE);
			}

			// DEALERの選択した手札の下に"DEALER"TextViewを表示する
			((TextView) findViewById(R.id.dealer)).setVisibility(View.VISIBLE);

			// 山札から5枚トランプの情報を読み込む

			for (int i = 1; i <= 5; i++) {
				trumpView[i].setTrump(dealer.trump.get(i - 1).getNumber(),
						dealer.trump.get(i - 1).getSuit(),
						dealer.trump.get(i - 1).getSerial(),
						dealer.trump.get(i - 1).getColor());
			}

			// DEALERの選択した手札を裏返す

			FlipTrump(1);

			count_DoubleDown++;

		}

	}

	// DoubleDownダイアログを呼び出す処理
	private void firstDoubleDownDialog() {

		trumpView[0].setVisibility(View.INVISIBLE);

		LayoutInflater inflater = LayoutInflater.from(WhatifActivity.this);
		View dialog = inflater.inflate(R.layout.double_down_dialog,
				(ViewGroup) findViewById(R.id.double_down_dialog));

		((TextView) dialog.findViewById(R.id.coinView)).setText(String.valueOf(calculateCoin()));

		((Button) dialog.findViewById(R.id.collectBtn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {

						if (counter == 52) {
							alert_doubleDown1.dismiss();

							coin.setBeforeWager(coin.getWager());
							msg.setText(getString(R.string.congratulation));
							msg.setTextColor(Color.YELLOW);

							// 手札を非表示にして、メッセージ画面手札を表示する
							findViewById(R.id.msgLayout).setVisibility(View.VISIBLE);

							refundCoin();

							callDialog();

						} else {

							msg.setText(getString(R.string.winner));
							msg.setTextColor(Color.RED);
							// メッセージ画面手札を表示する

							findViewById(R.id.msgLayout).setVisibility(View.VISIBLE);
							refundCoin();
							alert_doubleDown1.dismiss();
						}

					}
				});

		((Button) dialog.findViewById(R.id.doubleBtn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {

						doubleDown();

						alert_doubleDown1.dismiss();

					}
				});

		((Button) dialog.findViewById(R.id.halfBtn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						// 手札を非表示にする
						for (int i = 1; i < 6; i++) {
							trumpView[i].setVisibility(View.INVISIBLE);
						}

						halfDown();

						alert_doubleDown1.dismiss();
					}
				});

		alert_doubleDown1 = new AlertDialog.Builder(WhatifActivity.this)
				.setTitle(getString(R.string.luck))
				.setView(dialog)
				.setCancelable(false)
				.show();

	}

	// 手札5枚がすべて表示状態になっているかをチェック
	private void secondDoubleDownDialog() {

		if (trumpView[2].getVisibility() == View.INVISIBLE) {
			return;
		} else if (trumpView[3].getVisibility() == View.INVISIBLE) {
			return;
		} else if (trumpView[4].getVisibility() == View.INVISIBLE) {
			return;
		} else if (trumpView[5].getVisibility() == View.INVISIBLE) {
			return;
		}

		// DoubleDownゲームを抜けてダイアログに以降するためfalseに変更
		doubleDownGameFlag = false;

		int dealer = trumpView[1].getNumber();
		int player = 0;

		// DEALERが最強の数字Aであるかを確認しAだった場合、14に置き換える

		if (dealer == 1) {
			dealer = 14;
		}
		if (((TextView) findViewById(R.id.player2)).getVisibility() == View.VISIBLE) {

			player = trumpView[2].getNumber();

			if (player == 1) {
				player = 14;
			}

			if (dealer > player) {
				result = GAME.LOSE;
			} else if (dealer < player) {
				result = GAME.WIN;
			} else if (dealer == player) {
				result = GAME.DRAW;
			}

		} else if (((TextView) findViewById(R.id.player3)).getVisibility() == View.VISIBLE) {
			player = trumpView[3].getNumber();

			if (player == 1) {
				player = 14;
			}

			if (dealer > player) {
				result = GAME.LOSE;
			} else if (dealer < player) {
				result = GAME.WIN;
			} else if (dealer == player) {
				result = GAME.DRAW;
			}

		} else if (((TextView) findViewById(R.id.player4)).getVisibility() == View.VISIBLE) {

			player = trumpView[4].getNumber();

			if (player == 1) {
				player = 14;
			}

			if (dealer > player) {
				result = GAME.LOSE;
			} else if (dealer < player) {
				result = GAME.WIN;
			} else if (dealer == player) {
				result = GAME.DRAW;
			}

		} else if (((TextView) findViewById(R.id.player5)).getVisibility() == View.VISIBLE) {

			player = trumpView[5].getNumber();

			if (player == 1) {
				player = 14;
			}

			if (dealer > player) {
				result = GAME.LOSE;
			} else if (dealer < player) {
				result = GAME.WIN;
			} else if (dealer == player) {
				result = GAME.DRAW;
			}

		}

		if (result == GAME.DRAW) {
			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						Thread.sleep(500);

						handler.post(new Runnable() {

							@Override
							public void run() {

								if (ringerMode && !isPlugged) {
									soundPool.play(se_even, 0.5F, 0.5F, 0, 0, 1.0F);
								} else if (isPlugged) {
									soundPool.play(se_even, 0.1F, 0.1F, 0, 0, 1.0F);
								}

								msg.setText(getString(R.string.draw));
								msg.setTextColor(Color.GREEN);

								// 手札を非表示にする
								for (int i = 1; i < 6; i++) {
									trumpView[i].setVisibility(View.INVISIBLE);
								}

								((TextView) findViewById(R.id.dealer)).setVisibility(View.INVISIBLE);
								((TextView) findViewById(R.id.player2)).setVisibility(View.INVISIBLE);
								((TextView) findViewById(R.id.player3)).setVisibility(View.INVISIBLE);
								((TextView) findViewById(R.id.player4)).setVisibility(View.INVISIBLE);
								((TextView) findViewById(R.id.player5)).setVisibility(View.INVISIBLE);

								// 手札を非表示にして、メッセージ画面手札を表示する
								findViewById(R.id.msgLayout).setVisibility(View.VISIBLE);

								if (coin.getWin() != 0) {
									countUp(coin.getWin());
								}

								coin.setWager(0);
								redrawCoin();

								count_DoubleDown = 0;

							}

						});

					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				}

			}).start();

			return;

		}
		else if (result == GAME.LOSE) {

			new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						Thread.sleep(500);

						handler.post(new Runnable() {

							@Override
							public void run() {

								if (ringerMode && !isPlugged) {
									soundPool.play(se_loser, 0.5F, 0.5F, 0, 0, 1.0F);
								} else if (isPlugged) {
									soundPool.play(se_loser, 0.1F, 0.1F, 0, 0, 1.0F);
								}

								msg.setText(getString(R.string.loser));
								msg.setTextColor(Color.BLUE);

								// 手札を非表示にする
								for (int i = 1; i < 6; i++) {
									trumpView[i].setVisibility(View.INVISIBLE);
								}

								((TextView) findViewById(R.id.dealer)).setVisibility(View.INVISIBLE);
								((TextView) findViewById(R.id.player2)).setVisibility(View.INVISIBLE);
								((TextView) findViewById(R.id.player3)).setVisibility(View.INVISIBLE);
								((TextView) findViewById(R.id.player4)).setVisibility(View.INVISIBLE);
								((TextView) findViewById(R.id.player5)).setVisibility(View.INVISIBLE);

								present();

								// 手札を非表示にして、メッセージ画面手札を表示する
								findViewById(R.id.msgLayout).setVisibility(View.VISIBLE);

								if (coin.getWin() != 0) {
									countUp(coin.getWin());
								}
								coin.setWager(0);
								redrawCoin();

								count_DoubleDown = 0;

							}

						});

					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				}

			}).start();

			return;
		}

		// result==GAME.WINの処理

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(500);

					handler.post(new Runnable() {

						@Override
						public void run() {

							LayoutInflater inflater = LayoutInflater.from(WhatifActivity.this);
							View dialog = inflater.inflate(R.layout.double_down_dialog,
									(ViewGroup) findViewById(R.id.double_down_dialog));

							((TextView) dialog.findViewById(R.id.coinView)).setText(String.valueOf(coin.getWager() * 2));

							((Button) dialog.findViewById(R.id.collectBtn))
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View view) {

											// 手札を非表示にする
											for (int i = 1; i < 6; i++) {
												trumpView[i].setVisibility(View.INVISIBLE);
											}

											((TextView) findViewById(R.id.dealer)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player2)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player3)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player4)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player5)).setVisibility(View.INVISIBLE);

											msg.setText(getString(R.string.winner));
											msg.setTextColor(Color.RED);
											// メッセージ画面手札を表示する

											findViewById(R.id.msgLayout).setVisibility(View.VISIBLE);
											countUp((coin.getWager() * 2) + coin.getWin());

											if (count_DoubleDown > user.doubledown_count) {
												user.doubledown_count = count_DoubleDown;

												if (isSignedIn()) {

													Games.Leaderboards.submitScoreImmediate(getApiClient(),
															getString(R.string.leaderboard_what_ifdoubledown), user.doubledown_count);

												}

												if (isSignedIn()) {
													if (user.doubledown_count >= 5 && user.doubledown_5_achievement == false) {

														Games.Achievements.unlockImmediate(getApiClient(),
																getString(R.string.achievement_doubledown_5))
																.setResultCallback(new ResultCallback<UpdateAchievementResult>() {

																	@Override
																	public void onResult(UpdateAchievementResult result) {
																		user.doubledown_5_achievement = true;
																	}
																});
													} else if (user.doubledown_count >= 10 && user.doubledown_10_achievement == false) {

														Games.Achievements.unlockImmediate(getApiClient(),
																getString(R.string.achievement_doubledown_10))
																.setResultCallback(new ResultCallback<UpdateAchievementResult>() {

																	@Override
																	public void onResult(UpdateAchievementResult result) {
																		user.doubledown_10_achievement = true;
																	}
																});
													} else if (user.doubledown_count >= 15 && user.doubledown_15_achievement == false) {

														Games.Achievements.unlockImmediate(getApiClient(),
																getString(R.string.achievement_doubledown_15))
																.setResultCallback(new ResultCallback<UpdateAchievementResult>() {

																	@Override
																	public void onResult(UpdateAchievementResult result) {
																		user.doubledown_15_achievement = true;
																	}
																});
													}

												}
											}
											count_DoubleDown = 0;

											alert_doubleDown2.dismiss();
										}
									});

							((Button) dialog.findViewById(R.id.doubleBtn))
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View view) {
											// 手札を非表示にする
											for (int i = 1; i < 6; i++) {
												trumpView[i].setVisibility(View.INVISIBLE);
											}

											((TextView) findViewById(R.id.dealer)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player2)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player3)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player4)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player5)).setVisibility(View.INVISIBLE);

											doubleDown();

											alert_doubleDown2.dismiss();
										}
									});

							((Button) dialog.findViewById(R.id.halfBtn))
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View view) {
											// 手札を非表示にする
											for (int i = 1; i < 6; i++) {
												trumpView[i].setVisibility(View.INVISIBLE);
											}

											((TextView) findViewById(R.id.dealer)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player2)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player3)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player4)).setVisibility(View.INVISIBLE);
											((TextView) findViewById(R.id.player5)).setVisibility(View.INVISIBLE);

											halfDown();

											alert_doubleDown2.dismiss();
										}
									});

							if (ringerMode && !isPlugged) {
								soundPool.play(se_winner, 0.5F, 0.5F, 0, 0, 1.0F);
							} else if (isPlugged) {
								soundPool.play(se_winner, 0.1F, 0.1F, 0, 0, 1.0F);
							}

							alert_doubleDown2 = new AlertDialog.Builder(WhatifActivity.this)
									.setTitle(getString(R.string.luck) + " +" + String.valueOf(count_DoubleDown))
									.setView(dialog)
									.setCancelable(false)
									.show();

						}

					});

				} catch (InterruptedException e) {

					e.printStackTrace();
				}

			}

		}).start();

	}

	// 裏面2Viewをクリックした時の処理
	OnClickListener back2Listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (trumpView[3].getVisibility() == View.INVISIBLE &&
					trumpView[4].getVisibility() == View.INVISIBLE &&
					trumpView[5].getVisibility() == View.INVISIBLE) {
				if (dealAnimFlag && flipAnimFlag && moveAnimFlag) {
					if (ringerMode && !isPlugged) {
						soundPool.play(se_trump_select, 0.5F, 0.5F, 0, 0, 1.0F);
					} else if (isPlugged) {
						soundPool.play(se_trump_select, 0.1F, 0.1F, 0, 0, 1.0F);
					}

					((TextView) findViewById(R.id.player2)).setVisibility(View.VISIBLE);

					FlipTrump(2);

					FlipTrump(3, 750);
					FlipTrump(4, 1000);
					FlipTrump(5, 1250);

				}
			}

		}

	};

	// 裏面3Viewをクリックした時の処理
	OnClickListener back3Listener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (trumpView[2].getVisibility() == View.INVISIBLE &&
					trumpView[4].getVisibility() == View.INVISIBLE &&
					trumpView[5].getVisibility() == View.INVISIBLE) {

				if (dealAnimFlag && flipAnimFlag && moveAnimFlag) {
					if (ringerMode && !isPlugged) {
						soundPool.play(se_trump_select, 0.5F, 0.5F, 0, 0, 1.0F);
					} else if (isPlugged) {
						soundPool.play(se_trump_select, 0.1F, 0.1F, 0, 0, 1.0F);
					}

					((TextView) findViewById(R.id.player3)).setVisibility(View.VISIBLE);

					FlipTrump(3);

					FlipTrump(2, 750);
					FlipTrump(4, 1000);
					FlipTrump(5, 1250);

				}
			}
		}

	};

	// 裏面4Viewをクリックした時の処理
	OnClickListener back4Listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (trumpView[2].getVisibility() == View.INVISIBLE &&
					trumpView[3].getVisibility() == View.INVISIBLE &&
					trumpView[5].getVisibility() == View.INVISIBLE) {
				if (dealAnimFlag && flipAnimFlag && moveAnimFlag) {
					if (ringerMode && !isPlugged) {
						soundPool.play(se_trump_select, 0.5F, 0.5F, 0, 0, 1.0F);
					} else if (isPlugged) {
						soundPool.play(se_trump_select, 0.1F, 0.1F, 0, 0, 1.0F);
					}

					((TextView) findViewById(R.id.player4)).setVisibility(View.VISIBLE);

					FlipTrump(4);

					FlipTrump(2, 750);
					FlipTrump(3, 1000);
					FlipTrump(5, 1250);

				}
			}
		}
	};

	// 裏面5Viewをクリックした時の処理
	OnClickListener back5Listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (trumpView[2].getVisibility() == View.INVISIBLE &&
					trumpView[3].getVisibility() == View.INVISIBLE &&
					trumpView[4].getVisibility() == View.INVISIBLE
			) {
				if (dealAnimFlag && flipAnimFlag && moveAnimFlag) {
					if (ringerMode && !isPlugged) {
						soundPool.play(se_trump_select, 0.5F, 0.5F, 0, 0, 1.0F);
					} else if (isPlugged) {
						soundPool.play(se_trump_select, 0.1F, 0.1F, 0, 0, 1.0F);
					}

					((TextView) findViewById(R.id.player5)).setVisibility(View.VISIBLE);

					FlipTrump(5);

					FlipTrump(2, 750);
					FlipTrump(3, 1000);
					FlipTrump(4, 1250);

				}
			}
		}

	};

	private AlertDialog alert_finish;

	// achievementの管理
	public void checkCounterAchievement() {

		if (isSignedIn()) {
			if (user.achievement_52[counter - 1] == true) {
				return;
			} else if (user.achievement_52[counter - 1] == false) {

				int id = res.getIdentifier("achievement_reached_" + counter, "string", getPackageName());
				Games.Achievements.unlockImmediate(getApiClient(), getString(id)).setResultCallback(
						new ResultCallback<UpdateAchievementResult>() {

							@Override
							public void onResult(UpdateAchievementResult result) {

								if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_OK) {
									//					Log.v(TAG, "STATUS_OK");//正常に送信された場合
									user.achievement_52[counter - 1] = true;
								} else {
									//					Log.v(TAG, "STATUS_ERROR");//予期しないエラーが発生した場合

								}

							}

						});
			}
		}
	}

	@Override
	public void onBackPressed() {
		//		Log.v(TAG, "onBackPressed()");
		finishDialog();
		//	    super.onBackPressed();
	}

	private void finishDialog() {

		LayoutInflater inflater = LayoutInflater.from(WhatifActivity.this);
		View dialog = inflater.inflate(R.layout.finish_dialog,
				(ViewGroup) findViewById(R.id.finish_dialog));

		NendAdView nendAdView = (NendAdView) dialog.findViewById(R.id.nend);
		nendAdView.setListener(this);
		// AdView をリソースとしてルックアップしてリクエストを読み込む
		//		AdView adView1 = (AdView) dialog.findViewById(R.id.adView_finish);
		//		AdRequest adRequest1 = new AdRequest.Builder().build();

		((Button) dialog.findViewById(R.id.finish_canselBtn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {

						alert_finish.dismiss();

					}
				});

		((Button) dialog.findViewById(R.id.finish_okBtn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {

						alert_finish.dismiss();
						finish();

					}
				});

		alert_finish = new AlertDialog.Builder(WhatifActivity.this)
				.setView(dialog)
				.setCancelable(false)
				.create();

		//		adView1.loadAd(adRequest1);
		nendAdView.loadAd();
		alert_finish.show();

	}

	/** 受信エラー通知 */
	@Override
	public void onFailedToReceiveAd(NendAdView nendAdView) {
		//		Toast.makeText(getApplicationContext(), "onFailedToReceiveAd", Toast.LENGTH_LONG).show();

		NendError nendError = nendAdView.getNendError();
		Log.v(TAG, "NendError=" + nendError.getMessage());

	}

	/** 受信成功通知 */
	@Override
	public void onReceiveAd(NendAdView nendAdView) {
		//		Toast.makeText(getApplicationContext(), "onReceiveAd", Toast.LENGTH_LONG).show();
	}

	/** クリック通知 */
	@Override
	public void onClick(NendAdView nendAdView) {
		//		Toast.makeText(getApplicationContext(), "onClick", Toast.LENGTH_LONG).show();
	}

	/** 復帰通知 */
	@Override
	public void onDismissScreen(NendAdView nendAdView) {
		//		Toast.makeText(getApplicationContext(), "onDismissScreen", Toast.LENGTH_LONG).show();
	}

}
