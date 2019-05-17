package cn.sowell.datacenter.admin.controller.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dao.utils.UserUtils;
import cn.sowell.datacenter.admin.controller.AdminConstants;
import cn.sowell.datacenter.model.config.pojo.SideMenuLevel1Menu;
import cn.sowell.datacenter.model.config.pojo.SideMenuLevel2Menu;
import cn.sowell.datacenter.model.config.service.AuthorityService;
import cn.sowell.datacenter.model.config.service.ConfigAuthencationService;
import cn.sowell.datacenter.model.config.service.NonAuthorityException;
import cn.sowell.datacenter.model.config.service.SideMenuService;

@Controller
@RequestMapping("/admin")
public class  AdminMainController {
	
	@Resource
	SideMenuService menuService;
	
	@Resource
	AuthorityService authService;

	@Resource
	ConfigAuthencationService confAuthenService;
	
	@RequestMapping("/login")
	public String login(@RequestParam(name="error",required=false) String error, Model model){
		model.addAttribute("error", error);
		model.addAttribute("errorMap", AdminConstants.ERROR_CODE_MAP);
		return "/admin/common/login.jsp";
	}
	
	@RequestMapping({"/", ""})
	public String index(Model model){
		UserIdentifier user = UserUtils.getCurrentUser();
		Map<Long, Boolean> l1disables = new HashMap<Long, Boolean>(),
					l2disables = new HashMap<Long, Boolean>();
		List<SideMenuLevel1Menu> menus = menuService.getSideMenuLevelMenus(user);
		menus.forEach(l1->{
			try {
				authService.validateL1MenuAccessable(l1.getId());
				for(SideMenuLevel2Menu l2 : l1.getLevel2s()) {
					try {
						authService.validateL2MenuAccessable(l2.getId());
					} catch (Exception e) {
						l2disables.put(l2.getId(), true);
					}
				}
			} catch (NonAuthorityException e) {
				l1disables.put(l1.getId(), true);
			}
		});
		model.addAttribute("user", user);
		model.addAttribute("menus", menus);
		model.addAttribute("l1disables", l1disables);
		model.addAttribute("l2disables", l2disables);
		model.addAttribute("configAuth", confAuthenService.getAdminConfigAuthen());
		return "/admin/index.jsp";
	}
	
}
