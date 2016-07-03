package com.martin.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.text.TextUtils;

/**
 * 只能转换单个汉字
 * 
 * @author hbm
 * 
 */
public class PinYinUtil {

	public static String getPinYin(String hanzi) {
		String pinyin = "";
		if (TextUtils.isEmpty(hanzi)) {
			return pinyin;
		}
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);// 大小写
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不要拼音的音标
		char[] array = hanzi.toCharArray();
		for (char c : array) {
			//如果是空格则去掉这个字符
			if (Character.isWhitespace(c))
				continue;
			// 汉字有两个字节，如果小于127则肯定不是汉子，直接拼接，如果大于127则有可能是汉字
			if (c > 127) {
				try {
					//有可能是多音字，所以是返回一个数组
					String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
					//如果是全角字符也是两个字节，但是肯定是不能正常转换成拼音，直接拼接就行了
					if (pinyinArray==null) {
						pinyin+=c;
					}else {
						pinyin+=pinyinArray[0];
					}
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				// 不可能是汉字
				pinyin += c;
			}
		}
		return pinyin;
	}
}
