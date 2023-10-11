package ke.co.myfuture.Myfuture.ImageStore.FileManagement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

//@Service
@Slf4j
public class ImageFileService {
    @Autowired
    public ImageFileRepository repository;
    public String encodeFileToBase64(MultipartFile file) {
        try {
            byte[] fileContent = file.getBytes();
            return Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            throw new IllegalStateException("could not read file " + file, e);
        }
    }

    public String getFileExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1 || (lastIndexOf+1) == name.length()) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf+1);
    }

    public ImageFile findById(long fileUploaded) {
        return repository.findById(fileUploaded).orElse(null);
    }

    public void deleteById(long fileUploaded) {
        repository.deleteById(fileUploaded);
    }

    public byte[] toBytes(String fileContents) throws UnsupportedEncodingException {
        return Base64.getDecoder().decode(new String(fileContents).getBytes("UTF-8"));
    }

    public ImageFile save(MultipartFile fileUploaded) {
        ImageFile imageFile = new ImageFile();
        try {
            imageFile.imageContent = fileUploaded.getBytes();
            imageFile.fileExtension = getFileExtension(fileUploaded);
            imageFile.fileName = fileUploaded.getOriginalFilename();
            imageFile.contentType = fileUploaded.getContentType();
            imageFile.fileSize = fileUploaded.getSize();

            return repository.save(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}