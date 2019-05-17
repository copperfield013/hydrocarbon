package cn.sowell.datacenter.model.config.service.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.abc.mapping.conf.MappingContainer;
import com.abc.mapping.node.ABCNode;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.FusionContextConfigFactory;
import cn.sowell.datacenter.entityResolver.config.ModuleConfigureMediator;
import cn.sowell.datacenter.entityResolver.config.abst.Module;
import cn.sowell.datacenter.entityResolver.config.param.QueryModuleCriteria;
import cn.sowell.datacenter.model.config.dao.ConfigureDao;
import cn.sowell.datacenter.model.config.service.ConfigureService;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.service.TemplateGroupService;

@Service
public class ConfigureServiceImpl implements ConfigureService{

	@Resource
	ConfigureDao cDao;
	
	@Resource
	NormalOperateDao nDao;
	
	@Resource
	ModuleConfigureMediator moduleConfigMediator;
	
	@Resource
	TemplateGroupService tmplGroupService;
	
	@Resource
	FusionContextConfigFactory fFactory;
	
	@Override
	public List<Module> getEnabledModules(){
		QueryModuleCriteria criteria = new QueryModuleCriteria();
		criteria.setFilterDisabled(true);
		List<Module> modules = moduleConfigMediator.queryModules(criteria);
		return modules;
	}
	
	@Override
	public List<Module> getSiblingModules(String moduleName) {
		Module sourceModule = fFactory.getModule(moduleName);
		ABCNode node = MappingContainer.getABCNode(BigInteger.valueOf(sourceModule.getMappingId()));
		String abcattr = node.getAbcattr();
		return getEnabledModules().stream().filter(module->{
			if(module.getMappingId() != null) {
				ABCNode abcNode = MappingContainer.getABCNode(BigInteger.valueOf(module.getMappingId()));
				return abcattr.equals(abcNode.getAbcattr());
			}
			return false;
		}).collect(Collectors.toList());
	}
	
	@Override
	public JSONArray getSiblingModulesJson(String moduleName) {
		JSONArray modulesJson = new JSONArray();
		getSiblingModules(moduleName).forEach(module->{
			JSONObject jModule = new JSONObject();
			jModule.put("title", module.getTitle());
			jModule.put("moduleName", module.getName());
			modulesJson.add(jModule);
		});
		return modulesJson;
	}
	
	
	@Override
	public JSONObject getModuleConfigJson() {
		JSONObject jConfig = new JSONObject();
		JSONObject jModules = new JSONObject();
		QueryModuleCriteria criteria = new QueryModuleCriteria();
		criteria.setFilterDisabled(true);
		List<Module> modules = moduleConfigMediator.queryModules(criteria);
		Map<String, List<TemplateGroup>> moduleGroupsMap = tmplGroupService.queryModuleGroups(CollectionUtils.toSet(modules, module->module.getName()));
		Map<String, FusionContextConfig> configMap = CollectionUtils.toMap(fFactory.getAllConfigs(), FusionContextConfig::getModule);
		modules.forEach(module->{
			JSONObject jModule = new JSONObject();
			jModule.put("name", module.getName());
			jModule.put("title", module.getTitle());
			FusionContextConfig config = configMap.get(module.getName());
			if(config != null && config.isStatistic()) {
				jModule.put("isStat", true);
			}
			JSONArray jGroups = new JSONArray();
			List<TemplateGroup> groups = moduleGroupsMap.get(module.getName());
			if(groups != null) {
				for (TemplateGroup group : groups) {
					JSONObject jGroup = new JSONObject();
					jGroup.put("title", group.getTitle());
					jGroup.put("id", group.getId());
					jGroups.add(jGroup);
				}
			}
			jModule.put("groups", jGroups);
			jModules.put(module.getName(), jModule);
		});
		jConfig.put("modules", jModules);
		return jConfig;
	}
	
}
