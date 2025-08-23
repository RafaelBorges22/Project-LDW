package ldw.squad.project.Controller;

import jakarta.servlet.http.HttpServletRequest;
import ldw.squad.project.Config.FileConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/upload")
public class UploadImageController {

    private final Path fileLocation;

    public UploadImageController(FileConfig fileConfig) throws IOException {
        this.fileLocation = Paths.get(fileConfig.getUploadDir())
                .toAbsolutePath().normalize();
        Files.createDirectories(this.fileLocation);
    }

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            Path targetLocation = fileLocation.resolve(fileName);
            file.transferTo(targetLocation);

            String fileDownload = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/upload/download/")
                    .path(fileName)
                    .toUriString();

            return ResponseEntity.ok("Upload FUNFADO! URLA da imagem: " + fileDownload);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao fazer upload: " + e.getMessage());
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName,
                                                 HttpServletRequest request) {
        try {
            Path filePath = fileLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

