package com.ayaseya.whatifcool104;

import java.io.Serializable;

public class User implements Serializable {
	// プレイ回数
	public int play_count = 0;
	// クリア回数
	public int clear_count = 0;

	public int rotate = 0;

	public int doubledown_count = 0;

	// 実績の管理
	public boolean[] achievement_52 = new boolean[52];

	public boolean RF_achievement = false;// ロイヤル・ストレート・フラッシュ
	public boolean SF_achievement = false;// ストレート・フラッシュ
	public boolean FK_achievement = false;// フォー・オブ・ア・カインド
	public boolean FH_achievement = false;// フルハウス
	public boolean FL_achievement = false;// フラッシュ
	public boolean complete_10_achievement = false;
	public boolean doubledown_5_achievement = false;
	public boolean doubledown_10_achievement = false;
	public boolean doubledown_15_achievement = false;

	// ゲーム中であった場合以下の情報を保存しておく
	public boolean game = false;

	public int wager = 0;
	public int win = 0;
	public int paid = 0;
	public int credit = 100;

	public int counter = 0;

	public int RFcount = 0;
	public int SFcount = 0;
	public int FKcount = 0;
	public int FHcount = 0;
	public int FLcount = 0;

	public int trump0_serial = 0;

	public int trump1_serial = 0;
	public int trump2_serial = 0;
	public int trump3_serial = 0;
	public int trump4_serial = 0;
	public int trump5_serial = 0;

	public Deck deck = null;
	public Deck record = null;

	public long start = 0;

}
