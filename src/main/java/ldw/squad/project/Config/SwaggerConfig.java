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
                        .description("""
                                **Documentação interativa dos endpoints da aplicação.**

                                **Integrantes da equipe:**
                                - Rafael Mascarenhas Borges  
                                - Kevyn Cavalcanti  
                                - Lucas Retanero  
                                - Vitor Tavares  

                                **Repositório do projeto:**  
                                [GitHub - Kazu Tattoo](https://github.com/RafaelBorges22/Project-LDW)
                                """)
                        .version("1.0.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org"))
                );
    }
}
