package cn.sowell.datacenter.admin.controller.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.sowell.copframe.dao.utils.UserUtils;
import cn.sowell.copframe.dto.ajax.AjaxPageResponse;
import cn.sowell.copframe.dto.ajax.JSONObjectResponse;
import cn.sowell.copframe.dto.ajax.NoticeType;
import cn.sowell.copframe.dto.ajax.ResponseJSON;
import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.copframe.utils.FormatUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.admin.controller.AdminConstants;
import cn.sowell.datacenter.admin.controller.modules.AdminModulesController;
import cn.sowell.datacenter.common.RequestParameterMapComposite;
import cn.sowell.datacenter.entityResolver.FieldDescCacheMap;
import cn.sowell.datacenter.entityResolver.FusionContextConfig;
import cn.sowell.datacenter.entityResolver.ModuleEntityPropertyParser;
import cn.sowell.datacenter.entityResolver.impl.RelSelectionEntityPropertyParser;
import cn.sowell.datacenter.model.admin.pojo.ABCUser;
import cn.sowell.datacenter.model.config.service.ConfigUserService;
import cn.sowell.datacenter.model.modules.service.ExportService;
import cn.sowell.dataserver.model.abc.service.EntitiesQueryParameter;
import cn.sowell.dataserver.model.abc.service.EntityQueryParameter;
import cn.sowell.dataserver.model.abc.service.ModuleEntityService;
import cn.sowell.dataserver.model.modules.pojo.EntityVersionItem;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.modules.service.ViewDataService;
import cn.sowell.dataserver.model.modules.service.view.SelectionTemplateEntityView;
import cn.sowell.dataserver.model.modules.service.view.SelectionTemplateEntityViewCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailTemplate;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateSelectionTemplate;
import cn.sowell.dataserver.model.tmpl.service.ArrayItemFilterService;
import cn.sowell.dataserver.model.tmpl.service.DetailTemplateService;
import cn.sowell.dataserver.model.tmpl.service.SelectionTemplateService;

@Controller
@RequestMapping(AdminConstants.URI_CONFIG + "/user")
public class AdminConfigUserController {
	
	@Resource
	ConfigUserService userService;
	
	@Resource
	DetailTemplateService dtmplService;
	
	@Resource
	SelectionTemplateService stmplService;
	
	@Resource
	ViewDataService vService;
	
	@Resource
	ModulesService mService;
	
	@Resource
	ExportService eService;
	
	@Resource
	ModuleEntityService entityService;
	
	@Resource
	ArrayItemFilterService arrayItemFilterService;
	
	
	static Logger logger = Logger.getLogger(AdminConfigUserController.class);
	
	
	@RequestMapping({"/detail", "/detail/"})
	public String detail(Long dtmplId, 
			String versionCode, Model model) {
		ABCUser user = UserUtils.getCurrentUser(ABCUser.class);
		if(user != null) {
			TemplateDetailTemplate dtmpl = userService.getUserDetailTemplate(dtmplId);
			if(dtmpl != null){
				
				String moduleName = userService.getUserModuleName();
				String code = user.getCode();
				
				
				ModuleEntityPropertyParser entity = null;
				EntityQueryParameter param = new EntityQueryParameter(moduleName, code, user);
				param.setArrayItemCriterias(arrayItemFilterService.getArrayItemFilterCriterias(dtmpl.getId(), user));
				EntityVersionItem lastHistory = entityService.getLastHistoryItem(param);
				//EntityHistoryItem lastHistory = mService.getLastHistoryItem(moduleName, code, user);
				if(versionCode != null) {
					if(versionCode != null && !versionCode.equals(lastHistory.getCode())) {
						entity = entityService.getHistoryEntityParser(param, versionCode, null);
					}
		        }
		        if(entity == null) {
		        	entity = entityService.getEntityParser(param);
		        	//entity = mService.getEntity(moduleName, code, null, user);
		        }
				
		        if(lastHistory != null) {
		        	model.addAttribute("hasHistory", true);
		        }
		        model.addAttribute("versionCode", versionCode);
				model.addAttribute("dtmpl", dtmpl);
				model.addAttribute("user", user);
				model.addAttribute("entity", entity);
				
				List<TemplateDetailTemplate> dtmpls = dtmplService.queryAll(dtmpl.getModule());
				model.addAttribute("dtmpls", dtmpls);
				
			}
		}
		return AdminConstants.JSP_CONFIG_USER + "/user_detail.jsp";
	}
	
	@RequestMapping({"/update/{tmplId}", "/update", "/update/"})
	public String update(@PathVariable(required=false) Long tmplId, Model model) {
		ABCUser user = UserUtils.getCurrentUser(ABCUser.class);
		if(user != null) {
			TemplateDetailTemplate dtmpl = userService.getUserDetailTemplate(tmplId);
			if(dtmpl != null){
				EntityQueryParameter queryParam = new EntityQueryParameter(dtmpl.getModule(), user.getCode(), user);
				ModuleEntityPropertyParser entity = entityService.getEntityParser(queryParam);
				//ModuleEntityPropertyParser entity = mService.getEntity(dtmpl.getModule(), user.getCode(), null, user);
				FusionContextConfig config = userService.getUserModuleConfig();
				model.addAttribute("module", mService.getModule(config.getModule()));
				model.addAttribute("config", config);
				model.addAttribute("dtmpl", dtmpl);
				model.addAttribute("user", user);
				model.addAttribute("entity", entity);
				model.addAttribute("fieldDescMap", new FieldDescCacheMap(config.getConfigResolver()));
				
				
				List<TemplateDetailTemplate> dtmpls = dtmplService.queryAll(dtmpl.getModule());
				model.addAttribute("dtmpls", dtmpls);
				
				return AdminConstants.JSP_CONFIG_USER + "/user_update.jsp";
			}
		}
		return null;
	}
	
	@ResponseBody
    @RequestMapping({"/save"})
    public AjaxPageResponse save(
    		RequestParameterMapComposite composite){
    	 try {
    		 userService.mergeUserEntity(composite.getMap(), UserUtils.getCurrentUser(ABCUser.class));
    		 AjaxPageResponse res = new AjaxPageResponse();
    		 res.setNoticeType(NoticeType.SUC);
    		 res.setNotice("保存成功");
    		 res.setLocalPageRedirectURL("admin/config/user/detail");
    		 return res;
         } catch (Exception e) {
             logger.error("保存时发生错误", e);
             return AjaxPageResponse.FAILD("保存失败");
         }
    }
	
	@RequestMapping("/open_selection/{stmplId}")
	public String openSelection(
			@PathVariable Long stmplId, 
			String exists, 
			PageInfo pageInfo,
			HttpServletRequest request, 
			Model model) {
		TemplateSelectionTemplate stmpl = stmplService.getTemplate(stmplId);
		userService.validateUserAuthentication(stmpl.getModule());
		//创建条件对象
		Map<Long, String> criteriaMap = exractTemplateCriteriaMap(request);
		SelectionTemplateEntityViewCriteria criteria = new SelectionTemplateEntityViewCriteria(stmpl, criteriaMap);
		//设置条件
		criteria.setExistCodes(TextUtils.split(exists, ",", HashSet<String>::new, e->e));
		criteria.setPageInfo(pageInfo);
		criteria.setUser(UserUtils.getCurrentUser());
		//执行查询
		SelectionTemplateEntityView view = (SelectionTemplateEntityView) vService.query(criteria);
		model.addAttribute("view", view);
		
		//隐藏条件拼接成文件用于提示
		List<TemplateSelectionCriteria> tCriterias = view.getListTemplate().getCriterias();
		StringBuffer hidenCriteriaDesc = new StringBuffer();
		if(tCriterias != null){
			for (TemplateSelectionCriteria tCriteria : tCriterias) {
				if(tCriteria.getQueryShow() == null && TextUtils.hasText(tCriteria.getDefaultValue()) && tCriteria.getFieldAvailable()) {
					hidenCriteriaDesc.append(tCriteria.getTitle() + ":" + tCriteria.getDefaultValue() + "&#10;");
				}
			}
		}
		
		model.addAttribute("stmpl", stmpl);
		model.addAttribute("criteria", criteria);
		return AdminConstants.JSP_CONFIG_USER + "/user_relation_selection.jsp";
	}
	
	private Map<Long, String> exractTemplateCriteriaMap(HttpServletRequest request) {
		ServletRequestParameterPropertyValues pvs = new ServletRequestParameterPropertyValues(request, "criteria", "_");
		Map<Long, String> criteriaMap = new HashMap<Long, String>();
		pvs.getPropertyValueList().forEach(pv->{
			 Long criteriaId = FormatUtils.toLong(pv.getName());
			 if(criteriaId != null){
				 criteriaMap.put(criteriaId, FormatUtils.toString(pv.getValue()));
			 }
		 });
		return criteriaMap;
	}
	
	@ResponseBody
	@RequestMapping("/load_entities/{stmplId}")
	public ResponseJSON loadEntities(
			@PathVariable Long stmplId,
			@RequestParam String codes, 
			@RequestParam String fields) {
		JSONObjectResponse jRes = new JSONObjectResponse();
		TemplateSelectionTemplate stmpl = stmplService.getTemplate(stmplId);
		userService.validateUserAuthentication(stmpl.getModule());
		/*Map<String, CEntityPropertyParser> parsers = mService.getEntityParsers(
				stmpl.getModule(), 
				stmpl.getRelationName(), 
				TextUtils.split(codes, ",", HashSet<String>::new, c->c), UserUtils.getCurrentUser())
				;*/
		
		EntitiesQueryParameter param = new EntitiesQueryParameter(stmpl.getModule(), UserUtils.getCurrentUser());
		param.setEntityCodes(TextUtils.split(codes, ",", HashSet<String>::new, c->c));
		param.setRelationName(stmpl.getRelationName());
		Map<String, RelSelectionEntityPropertyParser> parsers = entityService.queryRelationEntityParsers(param);
		
		
		JSONObject entities = AdminModulesController.toEntitiesJson(parsers, TextUtils.split(fields, ",", HashSet<String>::new, f->f));
		jRes.put("entities", entities);
		jRes.setStatus("suc");
		return jRes;
	}
	
	@ResponseBody
    @RequestMapping("/paging_history")
    public JSONObjectResponse pagingHistory(
    		@RequestParam Integer pageNo, 
    		@RequestParam(defaultValue="100") Integer pageSize){
		ABCUser user = UserUtils.getCurrentUser(ABCUser.class);
    	JSONObjectResponse response = new JSONObjectResponse();
    	try {
    		EntityQueryParameter param = new EntityQueryParameter(userService.getUserModuleName(), user.getCode(), user);
    		List<EntityVersionItem> historyItems = entityService.queryHistory(param , pageNo, pageSize);
			//List<EntityHistoryItem> historyItems = mService.queryHistory(userService.getUserModuleName(), user.getCode(), pageNo, pageSize, user);
			response.put("history", JSON.toJSON(historyItems));
			response.setStatus("suc");
			if(historyItems.size() < pageSize){
				response.put("isLast", true);
			}
		} catch (Exception e) {
			logger.error("查询历史失败", e);
		}
    	
    	return response;
    }
	
	@ResponseBody
	@RequestMapping("/export_detail/{dtmplId}")
	public ResponseJSON exportDetail(
			@PathVariable Long dtmplId,
			String versionCode) {
		JSONObjectResponse jRes = new JSONObjectResponse();
		TemplateDetailTemplate dtmpl = userService.getUserDetailTemplate(dtmplId);
		ABCUser user = UserUtils.getCurrentUser(ABCUser.class);
		String moduleName = dtmpl.getModule();
		ModuleEntityPropertyParser entity = null;
		EntityQueryParameter param = new EntityQueryParameter(moduleName, user.getCode(), user);
		EntityVersionItem lastHistory = entityService.getLastHistoryItem(param);
		//EntityHistoryItem lastHistory = mService.getLastHistoryItem(moduleName, user.getCode(), user);
		if(versionCode != null) {
			if(lastHistory != null && !versionCode.equals(lastHistory.getCode())) {
				entity = entityService.getHistoryEntityParser(param, versionCode, null);
				//entity = mService.getHistoryEntityParser(moduleName, user.getCode(), historyId, user);
			}
        }
        if(entity == null) {
        	entity = entityService.getEntityParser(param);
        	//entity = mService.getEntity(moduleName, user.getCode(), null, user);
        }
		try {
			String uuid = eService.exportDetailExcel(entity, dtmpl);
			if(uuid != null) {
				jRes.put("uuid", uuid);
				jRes.setStatus("suc");
			}
		} catch (Exception e) {
			logger.error("导出时发生错误", e);
			jRes.setStatus("error");
		}
		return jRes;
	}
	
	
}
