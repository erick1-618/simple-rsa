package br.com.erick.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import br.com.erick.rsa.Key.usage;

public class RSACrypto {
	
	public static Key[] getKeyPair(String name, LocalDateTime expirationDate) {
		BigInteger p = BigInteger.probablePrime(2048, new SecureRandom());
		BigInteger q = BigInteger.probablePrime(2048, new SecureRandom());
		BigInteger n = p.multiply(q);
		BigInteger pMinus1 = p.subtract(new BigInteger("1"));
		BigInteger qMinus1 = q.subtract(new BigInteger("1"));
		BigInteger e = new BigInteger("65537");
		BigInteger phi = pMinus1.multiply(qMinus1);
		BigInteger d = e.modInverse(phi);
		Key publicKey = new Key(e, n, usage.PUBLIC, name, expirationDate);
		Key privateKey = new Key(d, n, usage.PRIVATE, name, expirationDate);
		Key[] keys = new Key[2];
		keys[0] = publicKey;
		keys[1] = privateKey;
		return keys;
	}
	
	public static BigInteger crypt(BigInteger val, Key publicKey) {
		return val.modPow(publicKey.getX(), publicKey.getY());
	}
	
	public static BigInteger decrypt(BigInteger val, Key privateKey) {
		return val.modPow(privateKey.getX(), privateKey.getY());
	}
}
