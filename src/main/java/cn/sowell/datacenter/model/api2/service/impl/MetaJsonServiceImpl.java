package cn.sowell.datacenter.model.api2.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.sowell.datacenter.model.api2.service.MetaJsonService;
import cn.sowell.datacenter.model.config.pojo.SideMenuBlock;
import cn.sowell.datacenter.model.config.pojo.SideMenuLevel1Menu;
import cn.sowell.datacenter.model.config.pojo.SideMenuLevel2Menu;
import cn.sowell.datacenter.model.config.service.AuthorityService;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;

@Service
public class MetaJsonServiceImpl implements MetaJsonService{

	@Resource
	AuthorityService authService;
	
	@Override
	public JSONObject toMenuJson(SideMenuLevel2Menu menu) {
		if(menu != null) {
			JSONObject jMenu = new JSONObject();
			jMenu.put("id", menu.getId());
			jMenu.put("title", menu.getTitle());
			return jMenu;
		}
		return null;
	}

	@Override
	public JSONObject toModuleJson(ModuleMeta module) {
		if(module != null) {
			JSONObject jModule = new JSONObject();
			jModule.put("name", module.getName());
			jModule.put("title", module.getTitle());
			return jModule;
		}
		return null;
	}

	@Override
	public JSONObject toButtonStatus(TemplateGroup tmplGroup) {
		if(tmplGroup != null) {
			JSONObject jStatus = new JSONObject();
			jStatus.put("saveButton", !isTrue(tmplGroup.getHideSaveButton()));
			jStatus.put("createButton", !isTrue(tmplGroup.getHideCreateButton()));
			jStatus.put("deleteButton", !isTrue(tmplGroup.getHideDeleteButton()));
			jStatus.put("exportButton", !isTrue(tmplGroup.getHideExportButton()));
			jStatus.put("importButton", !isTrue(tmplGroup.getHideImportButton()));
			jStatus.put("queryButton", !isTrue(tmplGroup.getHideQueryButton()));
			jStatus.put("treeToggleButton", !isTrue(tmplGroup.getHideTreeToggleButton()));
			return jStatus;
		}
		return null;
	}

	private boolean isTrue(Integer hideSaveButton) {
		return Integer.valueOf(1).equals(hideSaveButton);
	}

	@Override
	public JSONArray convertBlocksJson(List<SideMenuBlock> blocks, UserDetails user) {
		JSONArray jBlocks = new JSONArray();
		for (SideMenuBlock block : blocks) {
			try {
				authService.validateUserBlockAccessable(user, block.getId());
				JSONObject jBlock = new JSONObject();
				jBlocks.add(jBlock);
				jBlock.put("id", block.getId());
				jBlock.put("title", block.getTitle());
				jBlock.put("authorities", block.getAuthorities());
				if(block.getL1Menus() != null) {
					JSONArray jL1Menus = new JSONArray();
					jBlock.put("l1Menus", jL1Menus);
					for (SideMenuLevel1Menu l1Menu : block.getL1Menus()) {
						try {
							authService.validateUserL1MenuAccessable(user, l1Menu.getId());
							JSONObject jL1Menu = new JSONObject();
							jL1Menu.put("id", l1Menu.getId());
							jL1Menu.put("title", l1Menu.getTitle());
							jL1Menu.put("authorities", l1Menu.getAuthorities());
							jL1Menu.put("order", l1Menu.getOrder());
							jL1Menus.add(jL1Menu);
							if(l1Menu.getLevel2s() != null) {
								JSONArray jL2Menus = new JSONArray();
								jL1Menu.put("l2Menus", jL2Menus);
								for (SideMenuLevel2Menu l2Menu : l1Menu.getLevel2s()) {
									try {
										authService.validateUserL2MenuAccessable(user, l2Menu.getId());
										jL2Menus.add(l2Menu);
									} catch (Exception e) {}
								}
							}
						} catch (Exception e1) {}
					}
				}
				
			} catch (Exception e) {}
		}
		return jBlocks;
	}

	
}
