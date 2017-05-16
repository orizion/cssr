package ch.fhnw.cssr.domain;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "presentation")
public class Presentation implements Serializable {
    private static final long serialVersionUID = 10013000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer presentationId;

    private LocalDateTime dateTime;

    private LocalDateTime deadLine;

    private String location;

    private long speakerId;

    private String title;

    @Column(name = "abstract")
    private String _abstract;

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalDateTime getDeadline() {
        if(deadLine == null) {
            if(dateTime.getDayOfWeek() == DayOfWeek.MONDAY) {
                deadLine = dateTime.plusDays(-3);
            }
            else if(dateTime.getDayOfWeek() == DayOfWeek.TUESDAY) {
                deadLine = dateTime.plusDays(-4);
            }
            else {
                deadLine = dateTime.plusDays(-2);
            }
        }
        return deadLine;
    }

    public void setDeadline(LocalDateTime dateTime) {
        
        this.deadLine = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(long speakerId) {
        this.speakerId = speakerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbstract() {
        return _abstract;
    }

    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }

    public Integer getPresentationId() {
        return presentationId;
    }

    public Presentation() {

    }
}
