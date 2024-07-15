package ke.co.myfuture.Myfuture.NonJdbc.MyScp.FileUpload;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.utils.EntityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("files")
public class FileController2 {
    @Autowired
    private final FileService fileService;
    @Autowired
    protected DownloadFromOnline downloadFromOnline;

    @GetMapping("/download")
    public ResponseEntity downloadFileFromLocal(@RequestParam String filePath) {
        Path path = Paths.get(filePath);
        UrlResource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/move")
    public ResponseEntity moveBatch() throws JSONException, IOException {
        downloadFromOnline.downloadRecussively();
        return null;
    }

    @GetMapping("/download/base2")
    public ResponseEntity downloadFileFromLocalBase2(@RequestParam("filePath") String filePath) {
        Path path = Paths.get(fileService.generateFullFilePath(filePath, true));
        File file = new File(String.valueOf(path));
        if (!file.exists()){
            System.out.println("file does not exist");
            EntityResponse<String> entityResponse = new EntityResponse<>();
            entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            entityResponse.setMessage("file does not exist");

            return ResponseEntity.ok(entityResponse);
        }
        UrlResource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    public FileController2(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @GetMapping("/listfiles")
    public ResponseEntity readFilesFromLocal(@RequestParam("filePath") String filePath) {
        System.out.println("listfiles");
        EntityResponse<List<String>> entityResponse = new EntityResponse<>();
        entityResponse.setStatusCode(HttpStatus.OK.value());
        Path path = Paths.get(fileService.generateFullFilePath(filePath, true));
        File file = new File(String.valueOf(path));
        if (!file.exists()) {
            System.out.println("file does not exist");
            entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            entityResponse.setMessage("file does not exist");

            return ResponseEntity.ok(entityResponse);
        }
        entityResponse.setEntity(listFiles(String.valueOf(path), true));

        return ResponseEntity.ok(entityResponse);
    }

    @GetMapping("/listfolders")
    public ResponseEntity readFoldersFromLocal(@RequestParam("filePath") String filePath) {
        System.out.println("listfolders");
        EntityResponse<List<String>> entityResponse = new EntityResponse<>();
        entityResponse.setStatusCode(HttpStatus.OK.value());
        Path path = Paths.get(fileService.generateFullFilePath(filePath, true));
        File file = new File(String.valueOf(path));
        if (!file.exists()) {
            System.out.println("file does not exist");
            entityResponse.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
            entityResponse.setMessage("file does not exist");

            return ResponseEntity.ok(entityResponse);
        }
        entityResponse.setEntity(listFiles(String.valueOf(path), false));

        return ResponseEntity.ok(entityResponse);
    }

    public List<String> listFiles(String directoryPath, Boolean isFile) {
        List<String> fileList = new ArrayList<>();
        File directory = new File(directoryPath);

        // Check if the directory exists and is a directory
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            // Iterate over the files
            if (files != null) {
                for (File file : files) {
                    // Add files to the list (excluding directories)
//                    System.out.println("is file "+file.isFile());
//                    System.out.println("need file "+isFile);
                    if (file.isFile() == isFile) {
                        fileList.add(file.getAbsolutePath());
                    } else {
                        System.out.println("ignored "+file.getName());
                    }
                }
            }
        } else {
            System.out.println("Invalid directory path.");
        }
        return fileList;
    }
}
