package com.martin.beans;

import com.martin.utils.PinYinUtil;

public class FriendBean implements Comparable<FriendBean> {

	public FriendBean(String nickName) {
		this.nickName = nickName;
		this.nickPinYin = PinYinUtil.getPinYin(nickName);
	}

	public int id;
	public String nickName;
	public String nickPinYin;

	/**
	 * 正序排列，如果想降序排列，在返回值前加负号
	 */
	@Override
	public int compareTo(FriendBean another) {
		// if (nickName.charAt(0) <= 127) {
		// return 1;
		// }
		return nickPinYin.compareTo(another.nickPinYin);
	}
}
