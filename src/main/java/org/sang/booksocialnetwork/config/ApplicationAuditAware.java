package org.sang.booksocialnetwork.config;

import java.util.Optional;
import org.sang.booksocialnetwork.user.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

//cấu hình phuong thuc này khi dùng @CreatedBy and @LastmodifiedBy để ctr bik duoc ai là người tạo
public class ApplicationAuditAware implements AuditorAware<Integer> {

	@Override
	public Optional<Integer> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null
				|| !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken
		) {

			return Optional.empty();
		}
		User userPrincipal=(User) authentication.getPrincipal();
		return Optional.ofNullable(userPrincipal.getId());
	}
}
