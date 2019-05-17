package cn.sowell.datacenter.model.api2.service.impl;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import cn.sowell.datacenter.model.api2.service.MetaJsonService;
import cn.sowell.datacenter.model.config.pojo.SideMenuLevel2Menu;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;

@Service
public class MetaJsonServiceImpl implements MetaJsonService{

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

	
}
