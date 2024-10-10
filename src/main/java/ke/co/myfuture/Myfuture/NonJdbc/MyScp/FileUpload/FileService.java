package ke.co.myfuture.Myfuture.NonJdbc.MyScp.FileUpload;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;


@Service
public class FileService {

    public String uploadFile(MultipartFile file) {
        String fileName = FileStorageUtil.storeFile(file);
        return  fileName + " uploaded";
    }


    public String generateFullFilePath(String base64Path, boolean isBase64) {
        System.out.println("base64Path: "+base64Path);
        String wildcardPath = isBase64? decodeBase64ToString(base64Path): base64Path;
        return wildcardPath;

    }

    public String decodeBase64ToString(String base64String) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        return new String(decodedBytes);
    }
}
