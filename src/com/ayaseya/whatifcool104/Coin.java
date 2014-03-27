package com.ayaseya.whatifcool104;

public class Coin {

	private int wager = 0;// 掛け金
	private int win = 0;// 払戻金(このゲームで勝った総額)
	private int paid = 0;// 払戻金(プレイヤーに払い戻した金額)
	private int credit = 100;// プレイヤーのコイン枚数（初期値100）

	private int beforeCredit = 0;// プレイヤーのコイン枚数
	private int beforeWager = 0;// 掛け金

	private int minbet = 1;// 最小BET数
	private int maxbet = 10;// 最大BET数

	// コインを1枚ベットする処理
	public void minBet() {

		// 所持コインが0以上かつ掛け金が10以下の場合にベット可能
		if ((0 < credit) && (wager < maxbet)) {
			credit--;
			wager++;
		}
	}

	// コインを1枚ベットする処理
	public void repBet() {

		if (beforeWager <= credit && wager <= maxbet) {
			if (wager == 0) {

				credit -= beforeWager;
				wager += beforeWager;
				//				Log.v("Test", "1:credit="+credit+" wager="+wager);

			} else if (wager + beforeWager < maxbet) {

				credit -= beforeWager;
				wager += beforeWager;

				//				Log.v("Test", "2:credit="+credit+" wager="+wager);

			} else if (wager + beforeWager >= maxbet) {

				credit -= maxbet - wager;
				wager = maxbet;

				//				Log.v("Test", "3:credit="+credit+" wager="+wager);
			} else if (beforeWager == credit) {
				credit = 0;
				wager += beforeWager;
			}

		}

	}

	// コインを掛け金のMAX(10枚)まで一度にベットする処理
	public void maxBet() {

		// 掛け金が最大BET数に達しているか否かの判定
		// 所持コイン数が0枚以上であるか否かの判定
		if ((0 < credit) && (wager != maxbet)) {
			// Log.d("Test", "判定 wager" + wager + " credit" + credit);

			// 掛け金0の状態で所持コイン数がMAXBET数よりも多い場合
			if ((maxbet <= credit) && (wager == 0)) {
				credit -= maxbet;
				wager += maxbet;

			} else {
				// 掛け金が1枚以上の場合
				if (credit + wager == maxbet) {
					credit = 0;
					wager = maxbet;

				} else if (credit + wager < maxbet) {
					wager += credit;
					credit = 0;

				} else if (credit + wager > maxbet) {
					credit -= maxbet - wager;
					wager = maxbet;

				}
			}
		}
	}

	// 投入コインの枚数をキャンセルする処理
	public void cancelBet() {
		credit += wager;
		wager = 0;

	}

	// ////////////////////////////////////////////////
	// getter、setter群
	// ////////////////////////////////////////////////

	public int getWager() {
		return wager;
	}

	public void setWager(int wager) {
		this.wager = wager;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getPaid() {
		return paid;
	}

	public void setPaid(int paid) {
		this.paid = paid;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public int getMinbet() {
		return minbet;
	}

	public int getMaxbet() {
		return maxbet;
	}

	public int getBeforeCredit() {
		return beforeCredit;
	}

	public void setBeforeCredit(int beforeCredit) {
		this.beforeCredit = beforeCredit;
	}

	public int getBeforeWager() {
		return beforeWager;
	}

	public void setBeforeWager(int beforeWager) {
		this.beforeWager = beforeWager;
	}

}