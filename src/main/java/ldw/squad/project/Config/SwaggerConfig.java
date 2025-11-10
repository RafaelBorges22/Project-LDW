package ldw.squad.project.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Gestão de Clientes e Orçamentos (Kazu Tattoo)")
                        .description("Documentação interativa dos endpoints da aplicação." +
                                "Rafael Masca" +
                                "")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Rafael Mascarenhas Borges")
                                .name("Kevyn Cavalcante")
                                .name("Lucas Retanero")
                                .name("Vitor Tavares")
                                )
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")));
    }
}
