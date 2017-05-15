package ch.fhnw.cssr.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table(name = "presentationfile")
public class PresentationFile implements Serializable {
	private static final long serialVersionUID = 10013001L;

	public static final String TYPE_PRESENTATION = "f";
	public static final String TYPE_RESSOURCEN = "r";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long presentationFileId;

	private int presentationId;

	private String type;

	private byte[] content;

	private String contentLink;
	
	private String displayName;
	
	private String contentType;

	private PresentationFile() {
		// Not used currently
	}

	public PresentationFile(int presentationId, String type, String contentLink,
	        String displayName) {
		this.presentationId = presentationId;
		this.type = type;
		this.contentLink = contentLink;
		this.displayName = displayName;
	}
	
	public PresentationFile(int presentationId, String type, byte[] content,
	        String displayName,
	        String contentType) {
		this.presentationId = presentationId;
		this.type = type;
		this.content = content;
		this.contentType = contentType;
		this.displayName = displayName;
	}

	public int getPresentationId() {
		return presentationId;
	}

	public void setPresentationId(int presentationId) {
		this.presentationId = presentationId;
	}
	

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
	@ApiModelProperty(value = "The type of the file. f for Presentation, r for Ressource", allowableValues = "f,r")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
		if (content != null) {
			this.contentLink = null;
		}
	}

	public String getContentLink() {
		return contentLink;
	}

	public void setContentLink(String contentLink) {
		this.contentLink = contentLink;
		if (contentLink != null) {
			this.content = null;
		}
	}

	public long getPresentationFileId() {
		return presentationFileId;
	}
	
	public PresentationFileMeta getAsMeta(){
		return new PresentationFileMeta(presentationFileId, presentationId, type, contentLink, displayName, contentType);
	}
}
