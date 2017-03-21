package ch.fhnw.cssr.domain;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "presentation")
public class Presentation implements Serializable {
	private static final long serialVersionUID = 10013000L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int presentationId;
	
	private java.sql.Date dateTime;


	private String location;
	
	private long speakerId;
	
	private String title;
	
	@Column (name = "abstract")
	private String _abstract;
	
	public java.sql.Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(java.sql.Date dateTime) {
		this.dateTime = dateTime;
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


	public int getPresentationId() {
		return presentationId;
	}
}