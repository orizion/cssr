package ch.fhnw.cssr.webserver.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.fhnw.cssr.domain.PresentationFile;
import ch.fhnw.cssr.domain.PresentationFileMeta;
import ch.fhnw.cssr.domain.repository.PresentationFileRepository;

@RestController
@RequestMapping("/presentation/{presentationId}/file")
public class PresentationFileController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PresentationFileRepository fileRepo;

    @RequestMapping(method = RequestMethod.GET)
    public List<PresentationFileMeta> getFiles(
            @PathVariable(name = "presentationId", required = true) int presentationId) {
        logger.debug("Getting presentation {}", presentationId);
        return fileRepo.findByPresentationId(presentationId);
    }

    /**
     * Deletes the given file of the given presentation.
     * 
     * @param presentationId
     *            The presentationId.
     * @param fileId
     *            The file Id.
     * @return The deleted data.
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "{fileId}")
    public ResponseEntity<PresentationFileMeta> deleteFile(
            @PathVariable(name = "presentationId", required = true) int presentationId,
            @PathVariable(name = "fileId", required = true) long fileId) {
        PresentationFileMeta file = fileRepo.findOneMeta(fileId);
        if (file == null) {
            logger.warn("Could not find file with id {}", fileId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (file.getPresentationId() != presentationId) {
            logger.warn("PresentationIds do not match: {} vs {}", file.getPresentationId(),
                    presentationId);
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        fileRepo.delete(file.getPresentationFileId());
        return new ResponseEntity<PresentationFileMeta>(file, HttpStatus.OK);
    }

    /**
     * Adds a file in a binary way. The body must be the raw file.
     * 
     * @param presentationId
     *            The presentationId (in url path)
     * @param content
     *            The body content in the request body.
     * @param type
     *            The type of the Presentation file.
     * @return The new data of the File.
     */
    @RequestMapping(method = RequestMethod.POST, path = "binary")
    public ResponseEntity<PresentationFileMeta> addFileBinary(
            @PathVariable(name = "presentationId", required = true) int presentationId,
            @RequestBody byte[] content,
            @RequestParam(name = "type", required = true) String type) {

        if (content.length == 0) {
            logger.warn("Could not get content");
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
        }
        logger.debug("Adding file binary");
        PresentationFile f = new PresentationFile(presentationId, type, content);
        fileRepo.save(f);
        return new ResponseEntity<PresentationFileMeta>(f.getAsMeta(), HttpStatus.CREATED);
    }

    /**
     * Adds a file that is just a link.
     * 
     * @param presentationId
     *            The presentationId
     * @param file
     *            The file
     * @return The new file
     */
    @RequestMapping(method = RequestMethod.POST, path = "link")
    public ResponseEntity<PresentationFileMeta> addFileLink(
            @PathVariable(name = "presentationId", required = true) int presentationId,
            @RequestBody PresentationFileMeta file) {
        if (file.getPresentationFileId() != 0) {
            logger.warn("PresentationFileId is not zero: {}", file.getPresentationFileId());
            return new ResponseEntity<PresentationFileMeta>(HttpStatus.PRECONDITION_FAILED);
        }
        if (file.getPresentationId() != presentationId) {
            logger.warn("PresentationFileId do not match: {} vs {}", file.getPresentationId(), 
                    presentationId);
            return new ResponseEntity<PresentationFileMeta>(HttpStatus.PRECONDITION_FAILED);
        }
        logger.debug("Adding file by using link {}", file.getContentLink());
        PresentationFile rfile = new PresentationFile(file.getPresentationId(), file.getType(),
                file.getContentLink());
        fileRepo.save(rfile);
        PresentationFileMeta nfile = new PresentationFileMeta(rfile.getPresentationFileId(),
                rfile.getPresentationId(), 
                rfile.getType(), 
                rfile.getContentLink());
        return new ResponseEntity<PresentationFileMeta>(nfile, HttpStatus.CREATED);
    }

}
