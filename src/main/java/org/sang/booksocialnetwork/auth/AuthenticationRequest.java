package org.sang.booksocialnetwork.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {

	@Email(message = "Email not formatted")//avoid "nsang@gmail" not .com
	@NotEmpty(message = "Email is required")
	@NotBlank(message = "Email is required")
	private String email;
	@NotEmpty(message = "Password is required")
	@NotBlank(message = "Password is required")
	@Size(min = 8,message = "Password should contains 8 characters at least")
	private String password;
}
