package ch.fhnw.cssr.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

@Entity
public class PresentationFileMeta implements Serializable {
	private static final long serialVersionUID = 10013002L;

	@Id
	private final long presentationFileId;

	private final int presentationId;

	private final String type;

	private final String contentLink;

	public PresentationFileMeta(long presentationFileId, int presentationId, String type, String contentLink) {
		this.presentationFileId = presentationFileId;
		this.presentationId = presentationId;
		this.type = type;
		this.contentLink = contentLink;
	}

	public int getPresentationId() {
		return presentationId;
	}

	@ApiModelProperty(value = "The type of the file. f for Presentation, r for Ressource", allowableValues = "f,r")
	public String getType() {
		return type;
	}

	public String getContentLink() {
		return contentLink;
	}

	public long getPresentationFileId() {
		return presentationFileId;
	}
}
