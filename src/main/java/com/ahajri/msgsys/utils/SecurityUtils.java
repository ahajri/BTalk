package com.ahajri.msgsys.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public final class SecurityUtils {
	
	
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(SecurityUtils.class);

	public static String md5(String value) {
		try {
			MessageDigest md;

			md = MessageDigest.getInstance("MD5");

			md.update(value.getBytes());
			byte byteData[] = md.digest();

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error(e);
			return value;
		}
	}

}
