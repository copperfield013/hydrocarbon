package cn.sowell.datacenter.admin.controller.relationtreeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.abc.hc.FusionContext;
import com.abc.hc.HCFusionContext;
import com.abc.mapping.entity.Entity;
import com.abc.mapping.entity.EntityRelationParser;
import com.abc.mapping.entity.RecordEntity;
import com.abc.mapping.entity.RelationEntity;
import com.abc.mapping.node.ABCNode;
import com.abc.mapping.node.AttributeNode;
import com.abc.panel.Discoverer;
import com.abc.rrc.query.criteria.CommonSymbol;
import com.abc.rrc.query.criteria.EntityCriteriaFactory;
import com.abc.rrc.query.entity.SortedPagedQuery;
import com.abc.rrc.query.queryrecord.criteria.Criteria;
import com.abc.service.RelationTreeServiceFactory;
import com.abc.vo.RelationVO;
import com.alibaba.fastjson.JSONObject;

import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.datacenter.model.relationtreeview.service.RelationTreeViewService;
import cn.sowell.dataserver.model.modules.service.ViewDataService;


@Controller
@RequestMapping("/admin/relationtreeview")
public class AdminRelationTreeViewController {
	@Resource
	ViewDataService vService;
	
	@Resource
	RelationTreeViewService relationTreeViewService;
	
	@RequestMapping("/treeView")
	public String treeView(@RequestParam String id, @RequestParam String mappingName, @RequestParam(defaultValue="") Integer nodeAttrCount, PageInfo pageInfo, Model model) {
		//ABCNode node = relationTreeViewService.getABCNode(mappingName);
		//model.addAttribute("node", node);
		List<AttributeNode> abcNodeAttrList = getAbcNodeList(nodeAttrCount, mappingName);
		if(abcNodeAttrList != null && abcNodeAttrList.size() > 0) {
			model.addAttribute("abcNodeAttrSize", abcNodeAttrList.size());
		}else {
			model.addAttribute("abcNodeAttrSize", 0);
		}
		
		List<String> nameList = new ArrayList<String>();
		Map<String, String> mappingNameMap = new HashMap<>();
		Map<String, List<String>> labelSetMap = new HashMap<>();
		Map<String, String> subAbcNodeNameMap = new HashMap<>();
		getNodeInfo(mappingName, nameList, mappingNameMap, labelSetMap, subAbcNodeNameMap);
		
		model.addAttribute("nameList", nameList);
		model.addAttribute("mappingNameMap", JSONObject.toJSON(mappingNameMap));
		model.addAttribute("labelSetMap", JSONObject.toJSON(labelSetMap));
		model.addAttribute("subAbcNodeNameMap", JSONObject.toJSON(subAbcNodeNameMap));
		
		model.addAttribute("id", id);
		model.addAttribute("mappingName", mappingName);
		return "/admin/relationtreeview/tree_view.jsp";
	}
	
	@RequestMapping("/tree_view_new")
	public String treeViewNew(@RequestParam String id, @RequestParam String mappingName, Integer nodeAttrCount, PageInfo pageInfo, Model model) {
		//ABCNode node = relationTreeViewService.getABCNode(mappingName);
		//model.addAttribute("node", node);
		List<AttributeNode> abcNodeAttrList = getAbcNodeList(null, mappingName);
		int abcNodeAttrSize = 0;
		if(abcNodeAttrList != null && abcNodeAttrList.size() > 0) {
			abcNodeAttrSize = abcNodeAttrList.size() > 3? 3:abcNodeAttrList.size();
			model.addAttribute("abcNodeAttrSize", abcNodeAttrSize);
		}
		
		nodeAttrCount = nodeAttrCount == null? abcNodeAttrSize : nodeAttrCount;
		
		model.addAttribute("abcNodeAttrSize", abcNodeAttrSize);
		model.addAttribute("abcNodeAttrCount", nodeAttrCount);
		
		List<String> nameList = new ArrayList<String>();
		Map<String, String> mappingNameMap = new HashMap<>();
		Map<String, List<String>> labelSetMap = new HashMap<>();
		Map<String, String> subAbcNodeNameMap = new HashMap<>();
		getNodeInfo(mappingName, nameList, mappingNameMap, labelSetMap, subAbcNodeNameMap);
		
		List<Object> result = getDataNew(id, mappingName, nodeAttrCount);
		
		model.addAttribute("nameList", nameList);
		model.addAttribute("mappingNameMap", JSONObject.toJSON(mappingNameMap));
		model.addAttribute("labelSetMap", JSONObject.toJSON(labelSetMap));
		model.addAttribute("subAbcNodeNameMap", JSONObject.toJSON(subAbcNodeNameMap));
		
		model.addAttribute("id", id);
		model.addAttribute("mappingName", mappingName);
		model.addAttribute("resultList", result);
		return "/admin/relationtreeview/treeview.jsp";
	}
	
	@ResponseBody
	@RequestMapping("/data")
	public String getData(@RequestParam(defaultValue="") String code, @RequestParam String configName, @RequestParam(defaultValue="1") Integer nodeAttrCount) {
		String dataJsonStr = relationTreeViewService.getData(configName, code);
		JSONObject dataJsonObj = JSONObject.parseObject(dataJsonStr);
		List<AttributeNode> abcAttrNodeList = getAbcNodeList(nodeAttrCount, configName);
		List<Object> resultList = new ArrayList<Object>();
		resultList.add(dataJsonObj.get("唯一编码"));
		abcAttrNodeList.forEach(key -> {
			resultList.add(dataJsonObj.get(key.getAbcattr()));
		});
		return JSONObject.toJSONString(resultList);
		//return resultList;
	}
	
	private List<Object> getDataNew(String code, String configName, Integer nodeAttrCount) {
		String dataJsonStr = relationTreeViewService.getData(configName, code);
		JSONObject dataJsonObj = JSONObject.parseObject(dataJsonStr);
		List<AttributeNode> abcAttrNodeList = getAbcNodeList(nodeAttrCount, configName);
		List<Object> resultList = new ArrayList<>();
		resultList.add(dataJsonObj.get("唯一编码"));
		abcAttrNodeList.forEach(key -> {
			resultList.add(dataJsonObj.get(key.getAbcattr()));
		});
		return resultList;
	}
	
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping("/getNodeSelf")
	public String getNodeSelf(@RequestParam String id, @RequestParam String mappingName, @RequestParam(defaultValue="") Integer nodeAttrCount) {
		List result = getDataNew(id, mappingName, nodeAttrCount);
		return JSONObject.toJSONString(result);
	}
	
	@ResponseBody
	@RequestMapping("/getChildrenNode")
	public String getChildrenNode(@RequestParam(defaultValue="") String code, @RequestParam String mappingName, @RequestParam(defaultValue="") Integer nodeAttrCount) {
		Entity dataEntity = relationTreeViewService.getEntity(mappingName, code);
		
		
		Map<String, Collection<String>> labelSetValue = new HashMap<String, Collection<String>>();
		
		EntityRelationParser parser = new EntityRelationParser(dataEntity);
		Collection<String> relationNames2 = parser.getRelationNames();
		
		for (String relationName : relationNames2) {
			Collection<String> relatedCodes = parser.getRelatedCodes(relationName);
			
			for (String relatedCode : relatedCodes) {
				Collection<String> relatedLabels = parser.getRelatedLabels(relatedCode);
				labelSetValue.put(relationName, relatedLabels);
			}
		}
		
		Set<String> relationNames = dataEntity.getRelationNames();
		Map<String, List<Object>> relationsMap = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		
		//获取当前节点下的节点信息
		List<String> nameList = new ArrayList<String>();
		Map<String, String> mappingNameMap = new HashMap<>();
		Map<String, List<String>> labelSetMap = new HashMap<>();
		Map<String, String> subAbcNodeNameMap = new HashMap<>();
		getNodeInfo(mappingName, nameList, mappingNameMap, labelSetMap, subAbcNodeNameMap);
		resultMap.put("mappingNameMap", mappingNameMap);
		resultMap.put("labelSetMap", labelSetMap);
		
		resultMap.put("labelSetValue", labelSetValue);
		
		List<AttributeNode> abcAttrNodeList = getAbcNodeList(nodeAttrCount, mappingName);
		Map<String, List<AttributeNode>> relationListMap = new HashMap<>(); 
		for(String relationName : relationNames) {
			List<RelationEntity> relationList = dataEntity.getRelations(relationName);
			List<AttributeNode> abcAttrNodeList1 = getAbcNodeList(nodeAttrCount, mappingNameMap.get(relationName));
			relationListMap.put(relationName, abcAttrNodeList1);
			if(relationList != null && relationList.size() > 0) {
				for(RelationEntity relationEntity : relationList) {
					JSONObject nodeJsonObj = JSONObject.parseObject(relationEntity.getEntity().toJson());
					List<Object> result = new ArrayList<Object>();
					result.add(relationName);
					for(AttributeNode attributeNode : abcAttrNodeList1) {
						result.add(nodeJsonObj.get(attributeNode.getAbcattr()));
					}
					//relationListMap.put(relationName, result);
					relationsMap.put(nodeJsonObj.getString("唯一编码"), result);
				}
			}
		}
		
		resultMap.put("relations", relationsMap);
		resultMap.put("abcAttrNodeList", abcAttrNodeList);
		resultMap.put("relationNames", relationNames);
		resultMap.put("relationNameAttrMap", relationListMap);
		System.out.println("\n\n------------------------------------------");
		System.out.println(resultMap);
		return JSONObject.toJSONString(resultMap);
	}
	
	private void getNodeInfo(String mappingName,List<String> nameList, Map<String, String> mappingNameMap, Map<String, List<String>> labelSetMap, Map<String, String> subAbcNodeNameMap) {
		Collection<RelationVO> relationVoList = RelationTreeServiceFactory.getRelationTreeService().getRelationVO(mappingName);
		relationVoList.forEach(relationVo -> {
			
			nameList.add(relationVo.getName());
			
			mappingNameMap.put(relationVo.getName(), relationVo.getMappingName());
			
			labelSetMap.put(relationVo.getName(), new ArrayList<String>(relationVo.getLabelSet()));
			
			subAbcNodeNameMap.put(relationVo.getName(), relationVo.getSubAbcNodeName());
		});
	}
	
	@ResponseBody
	@RequestMapping("/getNodeAttr")
	public String getAbcNode(Integer nodeAttrCount,String mappingName) {
		List<AttributeNode> attributesNameList = getAbcNodeList(nodeAttrCount, mappingName);
		JSONObject result = new JSONObject();
		result.put("AttributesName", attributesNameList);
		return result.toString();
	}
	
	/**
	 * get某个mappingName对应的基本属性列表
	 * @param mappingName
	 * @return
	 */
	private List<AttributeNode> getAbcNodeList(Integer nodeAttrCount, String mappingName) {
		ABCNode abcNode = relationTreeViewService.getABCNode(mappingName);
		List<AttributeNode> attributesNameCollection = abcNode.getOrderAttributes();
		
		/*System.out.println("nodeAttrCount: " + nodeAttrCount + "mappingName: " + mappingName);
		for (int i = 0; i < attributesNameCollection.size(); i++) {
			AttributeNode attrNode = attributesNameCollection.get(i);
			
			System.out.println("序号："+ i +",order："+attrNode.getOrder()+",name:"+attrNode.getTitle()+",fullTitle:"+attrNode.getFullTitle());
		}*/
		
		List<AttributeNode> attributesNameList = new ArrayList<>();
		if(nodeAttrCount != null && nodeAttrCount>0) {
			for(Integer i=0; i<nodeAttrCount; i++) {
				if(attributesNameCollection.get(i).getOrder() > 0) {
					attributesNameList.add(attributesNameCollection.get(i));
				}
			}
		}else {
			for(AttributeNode attributeNode : attributesNameCollection) {
				if(attributeNode.getOrder() > 0) {
					attributesNameList.add(attributeNode);
				}
			}
		}
		return attributesNameList;
	}
	
	@RequestMapping("/add")
	public String add(String type, String mappingName, String id, String rootId, Model model) {
		if(type.equals("relation")) {
			List<String> nameList = new ArrayList<String>();
			Map<String, String> mappingNameMap = new HashMap<>();
			Map<String, List<String>> labelSetMap = new HashMap<>();
			Map<String, String> subAbcNodeNameMap = new HashMap<>();
			getNodeInfo(mappingName, nameList, mappingNameMap, labelSetMap, subAbcNodeNameMap);
			
			model.addAttribute("nameList", nameList);
			model.addAttribute("mappingNameMap", JSONObject.toJSON(mappingNameMap));
			model.addAttribute("labelSetMap", JSONObject.toJSON(labelSetMap));
			model.addAttribute("subAbcNodeNameMap", JSONObject.toJSON(subAbcNodeNameMap));
			
			model.addAttribute("parentNodeId", id);
			model.addAttribute("mappingName", mappingName);
			model.addAttribute("rootId", rootId);
			return "/admin/relationtreeview/tree_relation_add.jsp";
		}
		return "";
	}
	
	@RequestMapping("/edit")
	public String edit(String mappingName, String id, String rootId, Model model) {
		String nodeStr = relationTreeViewService.getData(mappingName, id);
		List<AttributeNode> abcAttrNodeList = getAbcNodeList(null, mappingName);
		JSONObject nodeJsonObj = JSONObject.parseObject(nodeStr);
		Map<String, Object> nodeMap = new HashMap<>();
		for(Map.Entry<String, Object> entry : nodeJsonObj.entrySet()) {
			for(AttributeNode attributeNode : abcAttrNodeList) {
				if(entry.getKey().equals(attributeNode.getAbcattr())) {
					nodeMap.put(entry.getKey(), entry.getValue());
				}
			}
		}
		model.addAttribute("node", nodeMap);
		
		model.addAttribute("abcAttrNodeList", abcAttrNodeList);
		model.addAttribute("parentNodeId", id);
		model.addAttribute("mappingName", mappingName);
		model.addAttribute("rootId", rootId);
		return "/admin/relationtreeview/tree_node_edit.jsp";
	}
	
	@ResponseBody
	@RequestMapping("/delete")
	public String delete(String code) {
		boolean bool = relationTreeViewService.deleteTree(code);
		if(bool) {
			return "true";
		}else {
			return "false";
		}
	}
	
	
	@ResponseBody
	@RequestMapping("/saveNode")
	public String saveNode(String paramJson) {
		//String code = relationTreeViewService.saveRole();
		relationTreeViewService.saveTree(paramJson);
		return "true";
	}
	
	/**
	 * 选择实体列表页面
	 * @param mappingName
	 * @param relationName
	 * @param pageInfo
	 * @return
	 */
	@SuppressWarnings({ "unused", "null" })
	@ResponseBody
	@RequestMapping("/openSelection")
	public ModelAndView openSelection(String mappingName,String relationName, PageInfo pageInfo,@RequestParam(defaultValue="1") Integer attrCount, HttpServletRequest request) {
		String mapperName = mappingName+"."+relationName;
		HCFusionContext context = relationTreeViewService.getHCFusionContext(mapperName);
		context.setToEntityRange(FusionContext.ENTITY_CONTENT_RANGE_INTERSECTION);
		
		Map<String, Map<String, Object>> reulstMap = new HashMap<String, Map<String, Object>>();
		
		//属性列表
		List<AttributeNode> attrList = getAbcNodeList(attrCount, mapperName);
		Map<String, Object> attrMap = new TreeMap<String, Object>();
		
		//设置查询条件start
		//CriteriaFactory criteriaFactory = new CriteriaFactory(context);
		EntityCriteriaFactory criteriaFactory = new EntityCriteriaFactory(context);
		
		List<Criteria> criterias = null;
		for (AttributeNode attr : attrList) {
			String param = request.getParameter(attr.getTitle());
			attrMap.put(attr.getTitle(), param);
			if (param !=null && param.trim() !="") {
				//Criteria common = criteriaFactory.createLikeQueryCriteria(attr.getTitle(),param);
				criteriaFactory.addCriteria(attr.getTitle(), param, CommonSymbol.LIKE);
			}
		}
		criterias = criteriaFactory.getCriterias();
		
		//end
		
		Discoverer discoverer = null;//PanelFactory.getDiscoverer(context);
		//EntitySortedPagedQuery sortedPagedQuery = discoverer.discover(criterias, null);
		SortedPagedQuery<RecordEntity> sortedPagedQuery = null;// discoverer.discover(criterias, null);
		Integer pageNo = pageInfo.getPageNo();
		sortedPagedQuery.setPageSize(pageInfo.getPageSize());
		pageInfo.setCount(sortedPagedQuery.getAllCount());
		
		for (RecordEntity entity : sortedPagedQuery.visitEntity(pageNo)) {
			String json = entity.toJson();
			JSONObject parse = (JSONObject)JSONObject.parse(json);
			Map<String, Object> map = new TreeMap<String, Object>();
			for (int i=0; i<attrList.size(); i++) {
				AttributeNode attributeNode = attrList.get(i);
				map.put(attributeNode.getTitle(), parse.get(attributeNode.getTitle()));
				reulstMap.put((String) parse.get("唯一编码"), map);
			}
		}
		ModelAndView mv = new ModelAndView();
		
		mv.addObject("attrCount", attrCount);
		mv.addObject("reulstMap", reulstMap);
		mv.addObject("pageInfo", pageInfo);
		mv.addObject("attrNameList", attrList);
		mv.addObject("attrMap", attrMap);
		mv.addObject("mappingName", mappingName);
		mv.addObject("relationName", relationName);
		mv.setViewName("/admin/relationtreeview/entity_selection.jsp");
		return mv;
	}
	
	/**
	 * 获取指定实体的所有关系
	 * @param mappingName
	 */
	@ResponseBody
	@RequestMapping("/getRelationAll")
	public String getRelationAll(String mappingName) {
		JSONObject json = new JSONObject();
		try {
			List<String> nameList = new ArrayList<String>();
			Map<String, List<String>> labelSetMap = new HashMap<>();
			Collection<RelationVO> relationVoList = RelationTreeServiceFactory.getRelationTreeService().getRelationVO(mappingName);
			relationVoList.forEach(relationVo -> {
				nameList.add(relationVo.getName());
				labelSetMap.put(relationVo.getName(), new ArrayList<>(relationVo.getLabelSet()));
			});
			
			json.put("code", 200);
			json.put("msg", "操作成功！");
			json.put("relationAll", nameList);
			json.put("labelSetMap", labelSetMap);
			return json.toJSONString();
		} catch (Exception e) {
			json.put("code", 400);
			json.put("msg", "操作失败！");
			e.printStackTrace();
		}
		return "";
	}
	
	
	@ResponseBody
	@RequestMapping("/getEntityData")
	public String getEntityData(@RequestParam(defaultValue="") String code, @RequestParam String mappingName, String relationName, @RequestParam(defaultValue="1") Integer nodeAttrCount) {
		JSONObject json = new JSONObject();
		String configName = mappingName+ "." + relationName;
		try {
			String dataJsonStr = relationTreeViewService.getData(configName, code);
			JSONObject dataJsonObj = JSONObject.parseObject(dataJsonStr);
			List<AttributeNode> abcAttrNodeList = getAbcNodeList(nodeAttrCount, configName);
			
			Map<String, Map<String, Object>> resulmap = new HashMap<String, Map<String, Object>>();
			Map<String, Object> map = new TreeMap<String, Object>();
			resulmap.put((String) dataJsonObj.get("唯一编码"), map);
			abcAttrNodeList.forEach(key -> {
				map.put(key.getAbcattr(), dataJsonObj.get(key.getAbcattr()));
			});
			
			Map<String, List<String>> labelSetMap = new HashMap<>();
			Map<String, Object> mappingNameMap = new HashMap<String, Object>();
			Collection<RelationVO> relationVoList = RelationTreeServiceFactory.getRelationTreeService().getRelationVO(mappingName);
			relationVoList.forEach(relationVo -> {
				labelSetMap.put(relationVo.getName(), new ArrayList<>(relationVo.getLabelSet()));
				mappingNameMap.put(relationVo.getName(), relationVo.getMappingName());
			});
			
			json.put("abcAttrNodeList", abcAttrNodeList);
			json.put("resulmap", resulmap);
			json.put("labelSetMap", labelSetMap);
			json.put("mappingNameMap", mappingNameMap);
			json.put("code", 200);
			json.put("msg", "操作成功！");
			return json.toJSONString();
		} catch (Exception e) {
			json.put("code", 400);
			json.put("msg", "操作失败！");
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 保存实体与实体之间的关系
	 * @param paramJson
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/addRelation")
	public String addRelation(String parentMappingName, 
			String parentId, 
			String chileMappingName,
			String chileId, 
			String relationName, 
			String labelsetmap,
			String labelsetvalue) {
		JSONObject json = new JSONObject();
		try {
			
			relationTreeViewService.addRelation(parentMappingName.trim(), 
					parentId.trim(), 
					chileMappingName.trim(),
					chileId.trim(), 
					relationName.trim(), 
					 labelsetmap.trim(),
					 labelsetvalue.trim());
			json.put("code", 200);
			json.put("msg", "操作成功！");
			return json.toJSONString();
		} catch (Exception e) {
			json.put("code", 400);
			json.put("msg", "操作失败！");
			e.printStackTrace();
		}
		
		return null;
		
	}

}
