package ke.co.myfuture.Myfuture.ImageStore.FileManagement;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
        return save(fileUploaded, "", "").getEntity();
    }


    public UniversalResponse<ImageFile> save(MultipartFile fileUploaded, String description, String tags) {
        String fileName = fileUploaded.getOriginalFilename();
        ImageFile imageFile = new ImageFile();
        try {
            imageFile.setImageContent(fileUploaded.getBytes());
            DimensionsDTO dimensionsDTO = getDimensions(fileUploaded.getBytes());
            imageFile.setWidth(dimensionsDTO.getWidth());
            imageFile.setHeight(dimensionsDTO.getLength());
            imageFile.setCode(generateCode(fileName));
            imageFile.setDescription(description);
            imageFile.setTags(tags);
            imageFile.setFileExtension(getFileExtension(fileUploaded));
            imageFile.setFileName(fileUploaded.getOriginalFilename());
            imageFile.setContentType(fileUploaded.getContentType());
            imageFile.setFileSize(fileUploaded.getSize());

            ImageFile savedImageFile = repository.save(imageFile);
            UniversalResponse response = new UniversalResponse();
            response.setStatus("Success");
            response.setMessage("Saved successfully");
            response.setEntity(savedImageFile);
            response.setStatusCode(201);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Could not save");
        response.setEntity(null);
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return response;
    }

    public  byte[] imageToBytes(Image image, String formatName) throws IOException {
        BufferedImage bufferedImage = toBufferedImage(image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, formatName, baos);
        return baos.toByteArray();
    }

    public  BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // Create a buffered image with transparency
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        bufferedImage.getGraphics().drawImage(image, 0, 0, null);
        // Return the buffered image
        return bufferedImage;
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