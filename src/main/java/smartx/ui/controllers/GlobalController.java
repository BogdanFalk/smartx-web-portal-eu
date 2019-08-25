package smartx.ui.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import smartx.data.user.User;
import smartx.data.user.UserService;


@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GlobalController {

	@Autowired
	private UserService userService;

	private User loginUser;

	public User getLoginUser() {
		if (loginUser == null) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			loginUser = userService.getUserByUsername(auth.getName());
		}

		if(loginUser != null)
		{
			System.out.println("login UserName = " + loginUser.getUsername() + " ID = " + loginUser.getId());
		}

		return loginUser;
	}
}
