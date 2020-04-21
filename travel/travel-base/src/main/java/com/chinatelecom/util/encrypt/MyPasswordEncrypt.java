package com.chinatelecom.util.encrypt;
import com.chinatelecom.util.MD5Code;

public class MyPasswordEncrypt {
	private static final String SALT = "d3d3LmNoaW5hdGVsZWNvbS5jb20=";
	/** 
	 * 提供有密码的加密处理操作
	 * @param password
	 * @return
	 */
	public static String encryptPassword(String password) {
		return new MD5Code().getMD5ofStr(password + "{{"+SALT+"}}") ;
	}
}
