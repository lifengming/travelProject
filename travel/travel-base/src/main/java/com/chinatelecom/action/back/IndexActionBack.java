package com.chinatelecom.action.back;

import com.chinatelecom.util.action.AbstractAction;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexActionBack extends AbstractAction {
	@RequiresUser
	@RequestMapping("/pages/back/index")
	public ModelAndView index() {
		ModelAndView mav = new ModelAndView(super.getUrl("index.page"));
		return mav;
	}
	@RequiresAuthentication
	@RequestMapping("/unauthen")
	public ModelAndView unauthen() {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		return mav;
	}
	@RequiresAuthentication
	@RequestMapping("/unauthor")
	public ModelAndView unauthor() {
		ModelAndView mav = new ModelAndView(super.getUrl("back.forward.page"));
		return mav;
	}
}
