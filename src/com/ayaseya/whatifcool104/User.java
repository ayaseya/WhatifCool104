package com.ayaseya.whatifcool104;

import java.io.Serializable;

public class User implements Serializable {
	// プレイ回数
	public int play_count = 0;
	// クリア回数
	public int clear_count = 0;

	public int rotate = 0;

	// 実績の管理
	public boolean first_play_achievement = false;// はじめの一歩
	public boolean first_clear_achievement = false;// 52枚達成
	public boolean first_gameover_achievement = false;// ゲームオーバー
	public boolean RS_achievement = false;// ロイヤル・ストレート・フラッシュ
	public boolean SF_achievement = false;// ストレート・フラッシュ
	public boolean FK_achievement = false;// フォー・オブ・ア・カインド
	public boolean FH_achievement = false;// フルハウス
	public boolean FL_achievement = false;// フラッシュ	

}
