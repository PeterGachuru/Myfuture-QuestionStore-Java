package ke.co.myfuture.Myfuture.ImageStore.FileManagement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Optional;

//@Service
@Slf4j
@Service
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
        return save(fileUploaded, "", "");
    }

    public ImageFile save(MultipartFile fileUploaded, String description, String tags) {
        String fileName = fileUploaded.getOriginalFilename();
        ImageFile imageFile = new ImageFile();
        try {
            imageFile.setImageContent(fileUploaded.getBytes());
            DimensionsDTO dimensionsDTO = getDimensions(fileUploaded.getBytes());
            imageFile.setWidth(dimensionsDTO.getWidth());
            imageFile.setLength(dimensionsDTO.getLength());
            imageFile.setCode(generateCode(fileName));
            imageFile.setDescription(description);
            imageFile.setTags(tags);
            imageFile.setFileExtension(getFileExtension(fileUploaded));
            imageFile.setFileName(fileUploaded.getOriginalFilename());
            imageFile.setContentType(fileUploaded.getContentType());
            imageFile.setFileSize(fileUploaded.getSize());

            return repository.save(imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DimensionsDTO getDimensions(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            BufferedImage image = ImageIO.read(bais);
            DimensionsDTO dimensionsDTO = new DimensionsDTO(image.getWidth(), image.getHeight());
            return dimensionsDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String generateCode(String fileName) {
        String code = fileName.trim().toLowerCase().replaceAll("[^A-Za-z0-9]", "-");
        while (code.contains("--"))
            code = code.replaceAll("--", "-");
        Optional<ImageFile> imageFile = repository.findByCode(code);
        if (imageFile.isPresent()) {
            return code+"-"+(repository.count()+1);
        }
        return code;
    }
}