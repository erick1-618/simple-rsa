package br.com.erick.rsa;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

public class Key implements Serializable{
	
	private static final long serialVersionUID = 1L;
	public static enum usage {PUBLIC, PRIVATE}
	
	public String getName() {
		return name;
	}

	private String name;
	private BigInteger x;
	private BigInteger y;
	private usage usage;
	private LocalDateTime expirationDate;
	
	public BigInteger getX() {
		return x;
	}

	public BigInteger getY() {
		return y;
	}
	
	public boolean isExpired() {
		if(expirationDate != null) {
			return expirationDate.isBefore(LocalDateTime.now());			
		}
		return false;
	}

	public Key(BigInteger x, BigInteger y, usage u, String name, LocalDateTime expirationDate) {
		this.x = x;
		this.y = y;
		this.usage = u;
		this.name = name.replaceAll("#", this.usage.toString());
		this.expirationDate = expirationDate;
	}

	public usage getUsage() {
		return usage;
	}
	
	public String getOwner() {
		return name.substring(0, name.indexOf("_"));
	}
	
	public String getExpirationDate() {
		if(expirationDate != null) {
			return expirationDate.toString();			
		}
		return "indefined";
	}
}
