package org.sang.booksocialnetwork.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
		info = @Info(
				contact = @Contact(
						name = "Nguyen Cong Sang",
						email = "contact@jae.com",
						url = "httpqq"
				),
				description = "Open API documentation for Bookstore Social",
				title = "Open API - JAE",
				version = "1.0",
				license = @License(
						name = "License name",
						url = "httpurl"
				),
				termsOfService = "Term of service"
		),
		servers = {
				@Server(
						description = "Local environment",
						url = "http://localhost:8080/api/v1"
				),
				@Server(
						description = "Product environment",
						url = ""
				)
		},
		security = @SecurityRequirement(
				name = "bearerAuth"

		)
)
@SecurityScheme(
		name = "bearerAuth",  // Tên của scheme bảo mật
		description = "JWT Auth description",  // Mô tả về JWT Auth
		scheme = "bearer",  // Loại bảo mật sử dụng là Bearer
		type = SecuritySchemeType.HTTP,  // Loại bảo mật HTTP
		bearerFormat = "JWT",  // Định dạng của Bearer Token là JWT
		in = SecuritySchemeIn.HEADER  // Token sẽ được gửi trong phần header của yêu cầu
)
public class OpenApiConfig {

}
