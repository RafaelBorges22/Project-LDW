package ldw.squad.project.Controller;

import ldw.squad.project.Config.FileConfig;
import ldw.squad.project.Service.UploadImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UploadImageController.class)
class UploadImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UploadImageService uploadImageService;

    @MockBean
    private FileConfig fileConfig; // Precisamos mockar isso também, pois é uma dependência do controller

    @BeforeEach
    void setUp() {
        // CORREÇÃO: Diga ao mock FileConfig como se comportar para evitar o NullPointerException no construtor do Controller.
        when(fileConfig.getUploadDir()).thenReturn("test-dir");
    }

    @Test
    @DisplayName("Should upload file successfully and return the download URI")
    void uploadFile_Success() throws Exception {
        // Arrange
        String newFileName = "uuid_test-image.jpg";
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", // O nome do parâmetro @RequestParam
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // Diga ao serviço mockado para retornar o nome do arquivo quando for chamado
        when(uploadImageService.saveFile(any(MockMultipartFile.class))).thenReturn(newFileName);

        // Act & Assert
        mockMvc.perform(multipart("/upload").file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().string("Upload bem-sucedido! URL da imagem: http://localhost/upload/download/" + newFileName));
    }
}
