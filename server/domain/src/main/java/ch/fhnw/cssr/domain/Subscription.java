package ch.fhnw.cssr.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "subscription")
public class Subscription implements Serializable {
	private static final long serialVersionUID = 10013005L;

	public static final char TYPE_SANDWICH_VEGI = 'v';
	public static final char TYPE_SANDWICH_MEAT = 'f';
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long subscriptionId;
	
	private int presentationId;
	
	private int userId;

	private Character sandwichType;
	
	private byte drink; // Currently zero for no drink, one for drink

	public int getPresentationId() {
		return presentationId;
	}

	public void setPresentationId(int presentationId) {
		this.presentationId = presentationId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Character getSandwichType() {
		return sandwichType;
	}

	public void setSandwichType(Character sandwichType) {
		this.sandwichType = sandwichType;
	}

	public byte getDrink() {
		return drink;
	}

	public void setDrink(byte drink) {
		this.drink = drink;
	}

	public Long getSubscriptionId() {
		return subscriptionId;
	}
}
