package cn.sowell.datacenter.api2.controller.meta;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.sowell.copframe.dto.ajax.JSONObjectResponse;
import cn.sowell.copframe.dto.ajax.ResponseJSON;
import cn.sowell.datacenter.api2.controller.Api2Constants;
import cn.sowell.datacenter.common.ApiUser;
import cn.sowell.datacenter.model.api2.service.MetaJsonService;
import cn.sowell.datacenter.model.config.pojo.MenuBlock;
import cn.sowell.datacenter.model.config.pojo.SideMenuBlock;
import cn.sowell.datacenter.model.config.pojo.SideMenuLevel1Menu;
import cn.sowell.datacenter.model.config.pojo.SideMenuLevel2Menu;
import cn.sowell.datacenter.model.config.pojo.SystemConfig;
import cn.sowell.datacenter.model.config.service.AuthorityService;
import cn.sowell.datacenter.model.config.service.ConfigureService;
import cn.sowell.datacenter.model.config.service.NonAuthorityException;
import cn.sowell.datacenter.model.config.service.SideMenuService;

@RestController
@RequestMapping(Api2Constants.URI_META + "/menu")
public class Api2MenuController {
	
	@Resource
	ConfigureService configService;
	
	@Resource
	SideMenuService menuService;
	
	@Resource
	AuthorityService authService;
	
	@Resource
	MetaJsonService mJsonService;
	
	
	@RequestMapping("/get_blocks")
	public ResponseJSON getBlocks(Long blockId, Long menuId, ApiUser user) {
		JSONObjectResponse jRes = new JSONObjectResponse();
		SystemConfig sysConfig = configService.getSystemConfig();
		List<SideMenuBlock> blocks = menuService.getAllBlocks();
		List<MenuBlock> jBlocks = mJsonService.convertBlocksJson(blocks, user);
		SideMenuBlock currentBlock = null;
		if(blockId != null) {
			currentBlock = menuService.getBlock(blockId);
		}
		if(menuId != null) {
			SideMenuLevel2Menu l2Menu = authService.validateUserL2MenuAccessable(user, menuId);
			currentBlock = menuService.getBlock(l2Menu.getLevel1Menu().getBlockId());
			jRes.put("menuId", l2Menu.getId());
		}
		if(currentBlock == null) {
			currentBlock = menuService.getBlock(sysConfig.getDefaultBlockId());
		}
		if(currentBlock == null && blocks.size() > 0) {
			currentBlock = blocks.get(0);
		}
		jRes.put("currentBlockId", currentBlock.getId());
		
		jRes.put("blocks", jBlocks);
		jRes.put("sysConfig", sysConfig);
		jRes.setStatus("suc");
		return jRes;
	}
	
	@RequestMapping("/get_menu")
	public ResponseJSON all(ApiUser user) {
		JSONObjectResponse res = new JSONObjectResponse();
		List<SideMenuLevel1Menu> menus = menuService.getSideMenuLevelMenus(user);
		menus = menus.stream().filter(menu->{
			try {
				authService.validateUserL1MenuAccessable(user, menu.getId());
			} catch (NonAuthorityException e) {
				return false;
			}
			return true;
		}).collect(Collectors.toList());
		res.put("menus", JSON.toJSON(menus));
		return res;
	}
}
