package cn.sowell.datacenter.model.config.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.sowell.datacenter.entityResolver.config.abst.Module;

public interface ConfigureService {

	JSONObject getModuleConfigJson();

	List<Module> getEnabledModules();

	List<Module> getSiblingModules(String moduleName);

	JSONArray getSiblingModulesJson(String moduleName);

}
