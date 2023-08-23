package FileUploadApplication.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import FileUploadApplication.model.ImageFile;

@Repository
public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
     
}
