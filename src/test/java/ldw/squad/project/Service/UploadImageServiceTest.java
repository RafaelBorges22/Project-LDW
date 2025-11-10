package ldw.squad.project.Service;

import ldw.squad.project.Config.FileConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadImageServiceTest {

    @Mock
    private FileConfig fileConfig;

    @Mock
    private MultipartFile multipartFile;

    private UploadImageService uploadImageService;

    // Mocks para os métodos estáticos que vamos interceptar
    private MockedStatic<Paths> pathsMockedStatic;
    private MockedStatic<Files> filesMockedStatic;
    private MockedStatic<UUID> uuidMockedStatic;

    @BeforeEach
    void setUp() throws IOException {
        // 1. Mockar a configuração para retornar um diretório de teste
        when(fileConfig.getUploadDir()).thenReturn("test-upload-dir");

        // 2. Mockar as chamadas estáticas para que não toquem no sistema de arquivos
        pathsMockedStatic = mockStatic(Paths.class);
        filesMockedStatic = mockStatic(Files.class);
        uuidMockedStatic = mockStatic(UUID.class);

        // Configurar o mock do Paths para funcionar com o mock do Files
        Path mockPath = mock(Path.class);
        when(Paths.get(anyString())).thenReturn(mockPath);
        when(mockPath.toAbsolutePath()).thenReturn(mockPath);
        when(mockPath.normalize()).thenReturn(mockPath);
        // Adicionamos lenient() aqui porque este mock não é usado em todos os testes
        lenient().when(mockPath.resolve(anyString())).thenReturn(mockPath);

        // 3. Inicializar o serviço com a configuração mockada
        // A chamada para Files.createDirectories() no construtor será interceptada pelo mock
        uploadImageService = new UploadImageService(fileConfig);
    }

    @AfterEach
    void tearDown() {
        // É crucial fechar os mocks estáticos depois de cada teste para não afetar outros testes
        pathsMockedStatic.close();
        filesMockedStatic.close();
        uuidMockedStatic.close();
    }

    @Test
    @DisplayName("Should save file successfully and return the new filename")
    void saveFile_Success() throws IOException {
        // Arrange
        String originalFilename = "test-image.jpg";
        String fixedUuidString = "a1b2c3d4-e5f6-7890-1234-567890abcdef";
        byte[] fileContent = "test content".getBytes();

        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getBytes()).thenReturn(fileContent);

        // --- NOVA ESTRATÉGIA DE MOCK ---
        // 1. Crie um mock da classe UUID
        UUID mockedUuid = mock(UUID.class);
        // 2. Diga ao método toString() desse mock para retornar nossa string fixa
        when(mockedUuid.toString()).thenReturn(fixedUuidString);
        // 3. Diga ao mock estático para retornar nosso UUID mockado
        uuidMockedStatic.when(UUID::randomUUID).thenReturn(mockedUuid);

        String expectedFilename = fixedUuidString + "_" + originalFilename;

        // Act
        String resultFilename = uploadImageService.saveFile(multipartFile);

        // Assert
        // Verifica se o nome do arquivo retornado é o que esperamos
        assertEquals(expectedFilename, resultFilename);

        // Verifica se o método Files.write foi chamado uma vez.
        filesMockedStatic.verify(() -> Files.write(any(Path.class), eq(fileContent)), times(1));
    }

    @Test
    @DisplayName("Should throw IOException when file is empty")
    void saveFile_ThrowsIOException_WhenFileIsEmpty() {
        // Arrange
        when(multipartFile.isEmpty()).thenReturn(true);

        // Act & Assert
        // Verifica se uma IOException é lançada quando o método é chamado
        IOException exception = assertThrows(IOException.class, () -> {
            uploadImageService.saveFile(multipartFile);
        });

        assertEquals("O arquivo está vazio", exception.getMessage());

        // Verifica se o método Files.write NUNCA foi chamado
        filesMockedStatic.verify(() -> Files.write(any(Path.class), any(byte[].class)), never());
    }
}
