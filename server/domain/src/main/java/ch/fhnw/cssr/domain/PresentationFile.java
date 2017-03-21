package ch.fhnw.cssr.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "presentationfile")
public class PresentationFile {
	private static final long serialVersionUID = 10013001L;

	public static final char TYPE_PRESENTATION = 'f';
	public static final char TYPE_RESSOURCEN = 'r';
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long presentationFileId;

	private int presentationId;
	
	private char type;
	
	private byte[] content;
	
	private String contentLink;

	public int getPresentationId() {
		return presentationId;
	}

	public void setPresentationId(int presentationId) {
		this.presentationId = presentationId;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
		if(content != null) {
			this.contentLink = null;
		}
	}

	public String getContentLink() {
		return contentLink;
	}

	public void setContentLink(String contentLink) {
		this.contentLink = contentLink;
		if(contentLink != null) {
			this.content = null;
		}
	}

	public long getPresentationFileId() {
		return presentationFileId;
	}
}
