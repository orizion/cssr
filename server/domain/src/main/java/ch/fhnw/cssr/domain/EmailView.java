package ch.fhnw.cssr.domain;

/** 
 * A class for email that just represents the editable part of an email.
 *
 */
public class EmailView {

    String to;
    String bcc;
    String cc;
    String subject;
    String body;
    
    public EmailView() {
        
    }
    
    

    public String getTo() {
        return to;
    }

    public EmailView setTo(String to) {
        this.to = to;
        return this;
    }

    public String getBcc() {
        return bcc;
    }

    public EmailView setBcc(String bcc) {
        this.bcc = bcc;
        return this;
    }

    public String getCc() {
        return cc;
    }

    public EmailView setCc(String cc) {
        this.cc = cc;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public EmailView setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getBody() {
        return body;
    }

    public EmailView setBody(String body) {
        this.body = body;
        return this;
    }
}
