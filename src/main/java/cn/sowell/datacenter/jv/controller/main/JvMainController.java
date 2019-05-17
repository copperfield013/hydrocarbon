package cn.sowell.datacenter.jv.controller.main;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.sowell.datacenter.SessionKey;
import cn.sowell.datacenter.common.ApiUser;
import cn.sowell.datacenter.jv.JvConstants;
import cn.sowell.datacenter.jv.controller.entity.JvBaseController;

@Controller
@RequestMapping(JvConstants.URI_BASE + "/main")
public class JvMainController extends JvBaseController{
	
	@RequestMapping("/login")
	public String login() {
		return JvConstants.JSP_MAIN + "/login.jsp";
	}

	@RequestMapping("/redirect/{token}")
	public String redirect(@PathVariable String token, HttpSession session) {
		session.setAttribute(SessionKey.API_USER_TOKEN, token);
		return "redirect:/jv/main/index";
	}
	
	@RequestMapping("/index")
	public String home(ApiUser user) {
		return JvConstants.JSP_MAIN + "/index.jsp";
	}
}
