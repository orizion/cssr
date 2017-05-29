package ch.fhnw.cssr.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
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

    @Column(name="\"to\"")
    private String to;

    private String bcc;

    private String cc;

    private String subject;

    private String body;

    private LocalDateTime sentDate;

    private int tryCount = 0;

    private String error;

    private LocalDateTime insertedAt;

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

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDateTime sentDate) {
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

    public LocalDateTime getInsertedAt() {
        return insertedAt;
    }

    public long getEmailId() {
        return emailId;
    }

    private Email() {
        // 4 JPA
        this.insertedAt = LocalDateTime.now();
    }

    /**
     * Creates a new email out of a view class.
     * @param v The view containing the email info.
     */
    public Email(EmailView v) {
        this();
        this.to = v.getTo();
        this.bcc = v.getBcc();
        this.cc = v.getCc();
        this.subject = v.getSubject();
        this.body = v.getBody();
        if (to == null || to.equals("")) {
            throw new IllegalArgumentException("to");
        }
        this.primitiveEmailAddressCheck(bcc);
        this.primitiveEmailAddressCheck(cc);
    }

    /**
     * Checks if all email addresses in a list do have an @ in the address You can pass an empty
     * address.
     * 
     * @param address
     *            One or more (;-separated) addresses
     */
    private final void primitiveEmailAddressCheck(String address) {
        if (address == null || address.equals("")) {
            return; // it's fine, no address is a correct address :)
        }
        if (address.contains(";")) {
            String[] adresses = address.split(";");
            for (String addr : adresses) {
                this.primitiveEmailAddressCheck(addr);
            }
        }
        if (!address.contains("@")) {
            throw new IllegalArgumentException("address");
        }
    }
}
