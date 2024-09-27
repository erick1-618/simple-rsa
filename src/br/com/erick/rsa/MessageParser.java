package br.com.erick.rsa;

import java.math.BigInteger;

public class MessageParser {

	public static BigInteger StringToBigInteger(String message) {
		String binMessage = "";
		for(int i = 0; i < message.length(); i++) {
			int n = (int) message.charAt(i);
			String chr = Integer.toBinaryString(n);
			while(chr.length() < 8) {
				chr = "0" + chr;
			}
			binMessage += chr;
		}
		return new BigInteger(binMessage, 2);
	}
	
	public static String BigIntegerToString(BigInteger message) {
		String binMessage = message.toString(2);
		int length = binMessage.length();
		for(int i = 0; i < 8 - (length % 8); i++) {
			binMessage = "0" + binMessage;
		}
		String textMessage = "";
		for(int i = 0; i < binMessage.length(); i += 8) {
			String binWord = binMessage.substring(i, i + 8);
			char chr = (char) Integer.parseInt(binWord, 2);
			textMessage += chr;
		}
		return textMessage;
	}
}
