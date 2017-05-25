package ch.fhnw.cssr.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import io.swagger.annotations.ApiModelProperty;

@Entity
public class PresentationFileMeta implements Serializable {
    private static final long serialVersionUID = 10013002L;

    @Id
    private long presentationFileId;

    private int presentationId;

    private String type;

    private String contentLink;

    private String contentType;

    private String displayName;

    public PresentationFileMeta() {

    }

    public PresentationFileMeta(long presentationFileId, int presentationId, String type,
            String contentLink, String displayName, String contentType) {
        this.presentationFileId = presentationFileId;
        this.presentationId = presentationId;
        this.type = type;
        this.contentLink = contentLink;
        this.displayName = displayName;
        this.contentType = contentType;
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

    public String getDisplayName() {
        return displayName;
    }

    public String getContentType() {
        return contentType;
    }

    public long getPresentationFileId() {
        return presentationFileId;
    }
}
