package ch.fhnw.cssr.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "subscription",
    uniqueConstraints=@UniqueConstraint(columnNames={"presentationId", "userId"}, name="UK_user_subs"))
public class Subscription implements Serializable {
	private static final long serialVersionUID = 10013005L;

	public static final String TYPE_SANDWICH_VEGI = "v";
	public static final String TYPE_SANDWICH_MEAT = "f";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long subscriptionId;

	private int presentationId;

	private int userId;

	private String sandwichType;

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

	@ApiModelProperty(value = "The type of the sandwich. v for Vegi, f for Meat", allowableValues = "v,f", dataType = "String")
	public String getSandwichType() {
		return sandwichType.toString();
	}

	public void setSandwichType(String sandwichType) {
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
