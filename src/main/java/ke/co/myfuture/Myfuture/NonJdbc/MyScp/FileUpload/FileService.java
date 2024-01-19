package ke.co.myfuture.Myfuture.NonJdbc.MyScp.FileUpload;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class FileService {

    public String uploadFile(MultipartFile file) {
        String fileName = FileStorageUtil.storeFile(file);
        return  fileName + "uploaded";
    }
}
