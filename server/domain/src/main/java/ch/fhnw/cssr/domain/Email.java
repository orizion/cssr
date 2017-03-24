package ch.fhnw.cssr.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "email")
public class Email implements Serializable {
	private static final long serialVersionUID = 10013000L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long emailId;

	private String to;
	
	private String bcc;
	
	private String cc;
	
	private String subject;
	
	private String body;
	
	private java.sql.Timestamp sentDate;
	
	private int tryCount=0;
	
	private String error;
	
	private java.sql.Timestamp insertedAt;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public java.sql.Timestamp getSentDate() {
		return sentDate;
	}

	public void setSentDate(java.sql.Timestamp sentDate) {
		this.sentDate = sentDate;
	}

	public int getTryCount() {
		return tryCount;
	}

	public void setTryCount(int tryCount) {
		this.tryCount = tryCount;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public java.sql.Timestamp getInsertedAt() {
		return insertedAt;
	}

	public void setInsertedAt(java.sql.Timestamp insertedAt) {
		this.insertedAt = insertedAt;
	}

	public long getEmailId() {
		return emailId;
	}
}
