package com.ayaseya.whatifcool104;

import java.io.Serializable;

public class Trump implements Serializable {

	//トランプの数字(1～13)
	private int number;
	//トランプの図柄(spade heart club diamond)
	private String suit;
	//トランプの連番(0～51)
	private int serial;

	private int color;

	public Trump(int number, String suit, int serial, int color) {
		this.number = number;
		this.suit = suit;
		this.serial = serial;
		this.color = color;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setSuit(String suit) {
		this.suit = suit;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public void setColor(int color) {
		this.color = color;
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

//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	@Override
//	public void writeToParcel(Parcel out, int flags) {
//
//		out.writeInt(number);
//		out.writeString(suit);
//		out.writeInt(serial);
//		out.writeInt(color);
//
//	}
//
//	public static final Parcelable.Creator<Trump> CREATOR = new Parcelable.Creator<Trump>() {
//		public Trump createFromParcel(Parcel in) {
//			return new Trump(in);
//		}
//
//		public Trump[] newArray(int size) {
//			return new Trump[size];
//		}
//	};
//
//	private Trump(Parcel in) {
//
//		number = in.readInt();
//		suit = in.readString();
//		serial = in.readInt();
//		color = in.readInt();
//	}

}
