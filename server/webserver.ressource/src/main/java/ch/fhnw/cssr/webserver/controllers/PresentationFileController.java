package ch.fhnw.cssr.webserver.controllers;

import java.util.List;

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
import ch.fhnw.cssr.domain.PresentationFileRepository;

@RestController
@RequestMapping("/presentation/{presentationId}/file")
public class PresentationFileController {

	@Autowired
	private PresentationFileRepository fileRepo;

	@RequestMapping(method = RequestMethod.GET)
	public List<PresentationFileMeta> getFiles(
			@PathVariable(name = "presentationId", required = true) int presentationId) {
		return fileRepo.findByPresentationId(presentationId);
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "{fileId}")
	public ResponseEntity<PresentationFileMeta> deleteFile(
			@PathVariable(name = "presentationId", required = true) int presentationId,
			@PathVariable(name = "fileId", required = true) long fileId) {
		PresentationFileMeta file = fileRepo.findOneMeta(fileId);
		if (file == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		if (file.getPresentationId() != presentationId) {
			return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
		}
		fileRepo.delete(file.getPresentationFileId());
		return new ResponseEntity<PresentationFileMeta>(file, HttpStatus.OK);
	}


	@RequestMapping(method = RequestMethod.POST, path = "binary")
	public ResponseEntity<PresentationFileMeta> addFileBinary(
			@PathVariable(name = "presentationId", required = true) int presentationId,
			@RequestBody byte[] content,
			@RequestParam(name= "type", required=true) String type) {
		PresentationFile f = new PresentationFile(presentationId, type, content); 
		fileRepo.save(f);		
		return new ResponseEntity<PresentationFileMeta>(f.getAsMeta(), HttpStatus.CREATED);
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "link")
	public ResponseEntity<PresentationFileMeta> addFileLink(
			@PathVariable(name = "presentationId", required = true) int presentationId,
			@RequestBody PresentationFileMeta file,
			@RequestParam(name= "contentLink", required=true) String type) {
		if (file.getPresentationFileId() != 0) {
			return new ResponseEntity<PresentationFileMeta>(HttpStatus.PRECONDITION_FAILED);
		}
		if (file.getPresentationId() != presentationId) {
			return new ResponseEntity<PresentationFileMeta>(HttpStatus.PRECONDITION_FAILED);
		}
		PresentationFile rfile = new PresentationFile(file.getPresentationId(), file.getType(), file.getContentLink());
		fileRepo.save(rfile);
		return new ResponseEntity<PresentationFileMeta>(file, HttpStatus.CREATED);
	}
	
}
