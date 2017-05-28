package ch.fhnw.cssr.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.fhnw.cssr.domain.PresentationFile;
import ch.fhnw.cssr.domain.PresentationFileMeta;

@Repository
public interface PresentationFileRepository extends CrudRepository<PresentationFile, Long> {
    
    @Query("SELECT NEW ch.fhnw.cssr.domain.PresentationFileMeta(" + 
            "pf.presentationFileId, pf.presentationId, " +  
            "pf.type, pf.contentLink, pf.displayName, pf.contentType) " + 
            "FROM PresentationFile AS pf WHERE pf.presentationId=?1")
    public List<PresentationFileMeta> findByPresentationId(int presentationId);

    @Query("SELECT NEW ch.fhnw.cssr.domain.PresentationFileMeta(" +
            "pf.presentationFileId, pf.presentationId, " + 
            "pf.type, pf.contentLink, pf.displayName, pf.contentType) " + 
            " FROM PresentationFile AS pf WHERE pf.presentationFileId=?1")
    public PresentationFileMeta findOneMeta(long fileId);

    
}