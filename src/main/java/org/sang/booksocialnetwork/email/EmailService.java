package org.sang.booksocialnetwork.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;
	private final SpringTemplateEngine templateEngine;


	@Async//tạo một thread riêng biệt để gửi email tránh làm gián đoạn các chức năng khác
	public void sendEmail(
			String to,
			String username,
			EmailTemplateName emailTemplate,
			String confirmationUrl,
			String activationCode,
			String subject
	) throws MessagingException {
		String templateName;
		if(emailTemplate == null){
			templateName="confirm-email";
		}else{
			templateName = emailTemplate.name();
		}

		MimeMessage mimeMessage= mailSender.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(
				mimeMessage,
				MimeMessageHelper.MULTIPART_MODE_MIXED,
				StandardCharsets.UTF_8.name()
		);
		Map<String,Object>properties= new HashMap<String,Object>();
		properties.put("username",username);
		properties.put("confirmationUrl",confirmationUrl);
		properties.put("activation_code",activationCode);

		Context context=new Context();
		context.setVariables(properties);

		helper.setFrom("contact@jae.com");
		helper.setTo(to);
		helper.setSubject(subject);

		String template=templateEngine.process(templateName,context);
		helper.setText(template,true);

		mailSender.send(mimeMessage);
	}
}
