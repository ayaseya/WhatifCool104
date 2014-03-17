package com.ayaseya.whatifcool104;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TrumpView extends LinearLayout {

	private int number; // 1～13までのトランプの数字を格納する変数

	private String suit; // トランプの図柄の文字列を格納する変数

	private int serial;

	private int color;

	// LogCat用のタグを定数で定義する
	public static final String TAG = "Test";

	public TrumpView(Context context) {
		super(context);
	}

	public TrumpView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View layout = LayoutInflater.from(context).inflate(R.layout.trump_view, this);
		// styleable から TypedArray の取得
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TrumpView);

		// レイアウト時に設定された属性値を取得する
		number = typedArray.getInt(R.styleable.TrumpView_number, 0);// 属性値が設定されていない時は第2引数が格納される
		suit = typedArray.getString(R.styleable.TrumpView_suit); // 属性値が設定されていない時はnullが格納される

		// 属性値が初期値でない場合、TextViewに変数の値を表示する
		if (number > 0) {
			TextView tv = (TextView) findViewById(R.id.TrumpNum);
			tv.setText(String.valueOf(number));
		}
		if (!"null".equals(suit)) {// !suit.equals(null)だと、suitがnullの場合に例外が発生する
			TextView tv1 = (TextView) findViewById(R.id.TrumpSuit1);
			TextView tv2 = (TextView) findViewById(R.id.TrumpSuit2);
			tv1.setText(suit);
			tv2.setText(suit);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	public int getNumber() {
		return number;
	}

	public String getSuit() {
		return suit;
	}

	public int getSerial() {
		return serial;
	}

	public int getColor() {
		return color;
	}

	public void setTrump(int number, String suit, int serial, int color) {
		this.number = number;
		this.suit = suit;
		this.serial = serial;
		this.color = color;

		TextView tv = (TextView) findViewById(R.id.TrumpNum);
		tv.setText(String.valueOf(number));
		TextView tv1 = (TextView) findViewById(R.id.TrumpSuit1);
		TextView tv2 = (TextView) findViewById(R.id.TrumpSuit2);
		tv1.setText(suit);
		tv2.setText(suit);

		tv.setTextColor(color);
		tv1.setTextColor(color);
		tv2.setTextColor(color);

	}

	public void setNumber(int number) {
		this.number = number;

		TextView tv = (TextView) findViewById(R.id.TrumpNum);
		tv.setText(String.valueOf(number));
	}

	public void setSuit(String suit) {
		this.suit = suit;

		TextView tv1 = (TextView) findViewById(R.id.TrumpSuit1);
		TextView tv2 = (TextView) findViewById(R.id.TrumpSuit2);
		tv1.setText(suit);
		tv2.setText(suit);
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public void setColor(int color) {
		this.color = color;

		TextView tv = (TextView) findViewById(R.id.TrumpNum);
		TextView tv1 = (TextView) findViewById(R.id.TrumpSuit1);
		TextView tv2 = (TextView) findViewById(R.id.TrumpSuit2);
		tv.setTextColor(color);
		tv1.setTextColor(color);
		tv2.setTextColor(color);
	}

	public void setFontSize(int fontSize1, int fontSize2) {
		int size1 = fontSize1;
		int size2 = fontSize2;

		TextView tv = (TextView) findViewById(R.id.TrumpNum);
		TextView tv1 = (TextView) findViewById(R.id.TrumpSuit1);
		TextView tv2 = (TextView) findViewById(R.id.TrumpSuit2);
		tv.setTextSize(size1);
		tv1.setTextSize(size1);
		tv2.setTextSize(size2);
	}
}
