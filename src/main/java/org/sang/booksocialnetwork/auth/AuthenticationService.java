package org.sang.booksocialnetwork.auth;

import jakarta.mail.MessagingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.booksocialnetwork.email.EmailService;
import org.sang.booksocialnetwork.email.EmailTemplateName;
import org.sang.booksocialnetwork.role.RoleRepository;
import org.sang.booksocialnetwork.security.JwtService;
import org.sang.booksocialnetwork.user.Token;
import org.sang.booksocialnetwork.user.TokenRepository;
import org.sang.booksocialnetwork.user.User;
import org.sang.booksocialnetwork.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final RoleRepository roleRepository;

	private final PasswordEncoder passwordEncoder;

	private final UserRepository userRepository;

	private final TokenRepository tokenRepository;

	private final EmailService emailService;

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	@Value("${application.mailing.frontend.activation-url}")
	private String activationUrl;


	public void register(RegistrationRequest request) throws MessagingException {
		var UserRole=roleRepository.findByName("USER")
				//todo -better exception handling
				.orElseThrow(()->new IllegalStateException("ROLE USER was not found"));
		var user=User.builder()
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.accountLocked(false)
				.enabled(false)
				.roles(List.of(UserRole))
				.build();
		userRepository.save(user);
		sendValidationEmail(user);

	}

	private void sendValidationEmail(User user) throws MessagingException {
		var newToken=generateAndSaveActivationToken(user);
		//send email
		emailService.sendEmail(
				user.getEmail(),
				user.getFullName(),
				EmailTemplateName.ACTIVATE_ACCOUNT,
				activationUrl,
				newToken,
				"Account Activation"
		);

	}

	private String generateAndSaveActivationToken(User user) {
		//generateToken
		String generateToken=generateActivationCode(6);
		var token= Token.builder()
				.token(generateToken)
				.createdAt(LocalDateTime.now())
				.expiresAt(LocalDateTime.now().plusMinutes(15))
				.user(user)
				.build();
		tokenRepository.save(token);
		return generateToken;
	}

	private String generateActivationCode(int length) {
		String characters="0123456789";
		StringBuilder codeBuilder=new StringBuilder();
		SecureRandom secureRandom=new SecureRandom();
		for (int i = 0; i < length ; i++) {
			int randomIndex=secureRandom.nextInt(characters.length());
			codeBuilder.append(characters.charAt(randomIndex));

		}
		return codeBuilder.toString();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		var auth=authenticationManager.authenticate(
				 new UsernamePasswordAuthenticationToken(
						 request.getEmail(),
						 request.getPassword()
				 )
		);
		var claims=new HashMap<String,Object>();
		var user=((User)auth.getPrincipal());
		claims.put("fullName",user.getFullName());
		var jwtToken=jwtService.generateToken(claims,user);
		return AuthenticationResponse.builder()
				.token(jwtToken).build();
	}

	//@Transactional//quản lý một giao dịch dảm bảo nó hoạt dong thành công hoặcc co loi thi ko co hoat dong nao dc
	// thuc hien
	public void activateAccount(String token) throws MessagingException {
		Token savedToken=tokenRepository.findByToken(token).orElseThrow(()->new RuntimeException("Invalid token"));
		if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
			sendValidationEmail(savedToken.getUser());
			throw new RuntimeException("Activate token has expired. A new token has been sent to the same email");
		}
		var user=
				userRepository.findById(savedToken.getUser().getId()).orElseThrow(()-> new UsernameNotFoundException(
						"User not found"));
		user.setEnabled(true);
		userRepository.save(user);
		savedToken.setValidatedAt(LocalDateTime.now());
		tokenRepository.save(savedToken);

	}
}
