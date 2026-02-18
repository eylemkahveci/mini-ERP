package com.minierp.mini_erp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI (Swagger) konfigürasyonu.
 * Swagger UI: /swagger-ui.html veya /swagger-ui/index.html
 * JWT kullanmak için Authorize butonuna tıklayıp "Bearer &lt;token&gt;" girin (login'den alınan token).
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mini-ERP API")
                        .version("1.0")
                        .description("Akıllı Stok ve Tedarik Yönetim Sistemi – Ürün, Kategori, Stok hareketleri, raporlama ve kullanıcı yönetimi."))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Önce POST /api/auth/login ile giriş yapın, dönen token'ı buraya yapıştırın (Bearer öneki otomatik eklenir).")));
    }
}
