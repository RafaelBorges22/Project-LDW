package ldw.squad.project.Config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConfigurationProperties(prefix = "file")
@Getter
@Setter
public class FileConfig implements WebMvcConfigurer {
    private String uploadDir;
}