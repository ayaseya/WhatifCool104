package com.ayaseya.whatifcool104;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.Color;

public class Deck implements Serializable {
	ArrayList<Trump> trump;

	public Deck(Context context) {
		trump = new ArrayList<Trump>();
	}

	public void standard(Context context) {

		for (int i = 1; i <= 13; i++) {
			trump.add(new Trump(i, context.getResources().getString(R.string.spade), i - 1, Color.BLACK));
		}
		for (int i = 1; i <= 13; i++) {
			trump.add(new Trump(i, context.getResources().getString(R.string.heart), i + 12, Color.RED));
		}
		for (int i = 1; i <= 13; i++) {
			trump.add(new Trump(i, context.getResources().getString(R.string.club), i + 25, Color.BLACK));
		}
		for (int i = 1; i <= 13; i++) {
			trump.add(new Trump(i, context.getResources().getString(R.string.diamond), i + 38, Color.RED));
		}
	}

	public void shuffle(Context context) {

		for (int i = 1; i <= 13; i++) {
			trump.add(new Trump(i, context.getResources().getString(R.string.spade), i - 1, Color.BLACK));
		}
		for (int i = 1; i <= 13; i++) {
			trump.add(new Trump(i, context.getResources().getString(R.string.heart), i + 12, Color.RED));
		}
		for (int i = 1; i <= 13; i++) {
			trump.add(new Trump(i, context.getResources().getString(R.string.club), i + 25, Color.BLACK));
		}
		for (int i = 1; i <= 13; i++) {
			trump.add(new Trump(i, context.getResources().getString(R.string.diamond), i + 38, Color.RED));
		}

		// リスト内の要素をシャッフルする
		Collections.shuffle(trump);
	}

	//	public void standard(Context context) {
	//	for (int i = 1; i <= 13; i++) {
	//		trump.add(new Trump(i, context.getResources().getString(R.string.spade), i - 1, Color.BLACK));
	//	}
	//	for (int i = 1; i <= 13; i++) {
	//		trump.add(new Trump(i, context.getResources().getString(R.string.heart), i + 12, Color.RED));
	//	}
	//	for (int i = 1; i <= 13; i++) {
	//		trump.add(new Trump(i, context.getResources().getString(R.string.club), i + 25, Color.BLACK));
	//	}
	//	for (int i = 1; i <= 13; i++) {
	//		trump.add(new Trump(i, context.getResources().getString(R.string.diamond), i + 38, Color.RED));
	//	}
	//}

	//	@Override
	//	public int describeContents() {
	//		return 0;
	//	}
	//
	//	@Override
	//	public void writeToParcel(Parcel out, int flags) {
	//		out.writeTypedList(trump);
	//	}
	//
	//	public static final Parcelable.Creator<Deck> CREATOR = new Parcelable.Creator<Deck>() {
	//		public Deck createFromParcel(Parcel in) {
	//			return new Deck(in);
	//		}
	//
	//		public Deck[] newArray(int size) {
	//			return new Deck[size];
	//		}
	//	};
	//
	//	private Deck(Parcel in) {
	//		trump = in.createTypedArrayList(Trump.CREATOR);
	//	}

}