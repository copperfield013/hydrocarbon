package cn.sowell.datacenter.api2.controller.meta;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.sowell.copframe.dto.ajax.JSONObjectResponse;
import cn.sowell.copframe.dto.ajax.ResponseJSON;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.api2.controller.Api2Constants;
import cn.sowell.datacenter.common.ApiUser;
import cn.sowell.datacenter.model.config.service.AuthorityService;
import cn.sowell.dataserver.model.dict.pojo.DictionaryOption;
import cn.sowell.dataserver.model.dict.pojo.OptionItem;
import cn.sowell.dataserver.model.dict.service.DictionaryService;

@RestController
@RequestMapping(Api2Constants.URI_META + "/dict")
public class Api2DictionaryController {
	@Resource
	DictionaryService dictService;
	
	@Resource
	AuthorityService authService;
	
	@RequestMapping("/field_options")
	public ResponseJSON getOptions(@RequestParam String fieldIds, ApiUser user) {
		JSONObjectResponse res = new JSONObjectResponse();
		Set<Integer> fieldIdSet = TextUtils.splitToIntegerSet(fieldIds, ",");
		Map<Integer, List<OptionItem>> optionsMap = dictService.getOptionsMap(fieldIdSet);
		JSONObject map = new JSONObject();
		optionsMap.forEach((fieldId, options)->{
			map.put(String.valueOf(fieldId), options);
		});
		res.put("optionsMap", map);
		return res;
	}
	
	@RequestMapping("/cas_ops/{optGroupId}")
	public ResponseJSON casOptions(@PathVariable Integer optGroupId, ApiUser user) {
		JSONObjectResponse jRes = new JSONObjectResponse();
		List<DictionaryOption> options = dictService.queryOptions(optGroupId);
		jRes.put("options", options);
		jRes.setStatus("suc");
		return jRes;
	}
}
