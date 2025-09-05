package ldw.squad.project.Service;

import ldw.squad.project.Config.FileConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadImageService {

    private final Path uploadPath;

    public UploadImageService(FileConfig fileConfig) throws IOException {
        this.uploadPath = Paths.get(fileConfig.getUploadDir()).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("O arquivo está vazio");
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = uploadPath.resolve(filename);

        Files.write(path, file.getBytes());

        return filename;
    }
}

