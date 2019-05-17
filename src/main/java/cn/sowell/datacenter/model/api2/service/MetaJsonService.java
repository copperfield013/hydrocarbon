package cn.sowell.datacenter.model.api2.service;

import com.alibaba.fastjson.JSONObject;

import cn.sowell.datacenter.model.config.pojo.SideMenuLevel2Menu;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;

public interface MetaJsonService {

	JSONObject toMenuJson(SideMenuLevel2Menu menu);

	JSONObject toModuleJson(ModuleMeta module);

	JSONObject toButtonStatus(TemplateGroup tmplGroup);

}
