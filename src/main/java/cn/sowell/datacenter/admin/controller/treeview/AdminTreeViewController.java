package cn.sowell.datacenter.admin.controller.treeview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.abc.mapping.entity.Entity;
import com.abc.mapping.entity.RelationEntity;
import com.abc.mapping.node.ABCNode;
import com.abc.mapping.node.AttributeNode;
import com.abc.service.RelationTreeServiceFactory;
import com.abc.vo.RelationVO;
import com.alibaba.fastjson.JSONObject;

import cn.sowell.copframe.dto.page.PageInfo;
import cn.sowell.datacenter.model.treeview.service.TreeViewService;


@Controller
@RequestMapping("/admin/treeview")
public class AdminTreeViewController {

	@Resource
	TreeViewService treeService;
	
	@RequestMapping("/treeView")
	public String treeView(@RequestParam String id, @RequestParam String mappingName, @RequestParam(defaultValue="") Integer nodeAttrCount, PageInfo pageInfo, Model model) {
		//ABCNode node = treeService.getABCNode(mappingName);
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
		return "/admin/treeview/tree_view.jsp";
	}
	
	@RequestMapping("/tree_view_new")
	public String treeViewNew(@RequestParam String id, @RequestParam String mappingName, Integer nodeAttrCount, PageInfo pageInfo, Model model) {
		//ABCNode node = treeService.getABCNode(mappingName);
		//model.addAttribute("node", node);
		List<AttributeNode> abcNodeAttrList = getAbcNodeList(null, mappingName);
		int abcNodeAttrSize = 0;
		if(abcNodeAttrList != null && abcNodeAttrList.size() > 0) {
			abcNodeAttrSize = abcNodeAttrList.size();
			model.addAttribute("abcNodeAttrSize", abcNodeAttrSize);
		}
		
		nodeAttrCount = nodeAttrCount == null ? abcNodeAttrSize : nodeAttrCount;
		
		model.addAttribute("abcNodeAttrSize", abcNodeAttrList.size());
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
		return "/admin/treeview/treeview.jsp";
	}
	
	@ResponseBody
	@RequestMapping("/data")
	public String getData(@RequestParam(defaultValue="") String code, @RequestParam String configName, @RequestParam(defaultValue="1") Integer nodeAttrCount) {
		String dataJsonStr = treeService.getData(configName, code);
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
		String dataJsonStr = treeService.getData(configName, code);
		JSONObject dataJsonObj = JSONObject.parseObject(dataJsonStr);
		List<AttributeNode> abcAttrNodeList = getAbcNodeList(nodeAttrCount, configName);
		List<Object> resultList = new ArrayList<Object>();
		resultList.add(dataJsonObj.get("唯一编码"));
		abcAttrNodeList.forEach(key -> {
			resultList.add(dataJsonObj.get(key.getAbcattr()));
		});
		return resultList;
	}
	
	@ResponseBody
	@RequestMapping("/getNodeSelf")
	public String getNodeSelf(@RequestParam String id, @RequestParam String mappingName, @RequestParam(defaultValue="") Integer nodeAttrCount) {
		List<Object> result = getDataNew(id, mappingName, nodeAttrCount);
		return JSONObject.toJSONString(result);
	}
	
	@ResponseBody
	@RequestMapping("/getChildrenNode")
	public String getChildrenNode(@RequestParam(defaultValue="") String code, @RequestParam String mappingName, @RequestParam(defaultValue="") Integer nodeAttrCount) {
		Entity dataEntity = treeService.getEntity(mappingName, code);
		Set<String> relationNames = dataEntity.getRelationNames();
		Map<String, List<Object>> relationsMap = new HashMap<String, List<Object>>();
		Map<String, Object> resultMap = new HashMap<>();
		
		//获取当前节点下的节点信息
		List<String> nameList = new ArrayList<String>();
		Map<String, String> mappingNameMap = new HashMap<>();
		Map<String, List<String>> labelSetMap = new HashMap<>();
		Map<String, String> subAbcNodeNameMap = new HashMap<>();
		getNodeInfo(mappingName, nameList, mappingNameMap, labelSetMap, subAbcNodeNameMap);
		resultMap.put("mappingNameMap", mappingNameMap);
		resultMap.put("labelSetMap", labelSetMap);
		
		List<AttributeNode> abcAttrNodeList = getAbcNodeList(nodeAttrCount, mappingName);
		Map<String, List<AttributeNode>> relationListMap = new HashMap<>(); 
		for(String relationName : relationNames) {
			List<RelationEntity> relationList = dataEntity.getRelations(relationName);
			List<AttributeNode> abcAttrNodeList1 = getAbcNodeList(nodeAttrCount, mappingNameMap.get(relationName));
			relationListMap.put(relationName, abcAttrNodeList1);
			if(relationList != null && relationList.size() > 0) {
				for(RelationEntity relationEntity : relationList) {
					JSONObject nodeJsonObj = JSONObject.parseObject(relationEntity.getEntity().toJson());
					List<Object> result = new ArrayList<>();
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
			
			labelSetMap.put(relationVo.getName(), new ArrayList<>(relationVo.getLabelSet()));
			
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
		ABCNode abcNode = treeService.getABCNode(mappingName);
		List<AttributeNode> attributesNameCollection = abcNode.getOrderAttributes();
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
			return "/admin/treeview/tree_relation_add.jsp";
		}
		return "";
	}
	
	@RequestMapping("/edit")
	public String edit(String mappingName, String id, String rootId, Model model) {
		String nodeStr = treeService.getData(mappingName, id);
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
		return "/admin/treeview/tree_node_edit.jsp";
	}
	
	@ResponseBody
	@RequestMapping("/delete")
	public String delete(String code) {
		boolean bool = treeService.deleteTree(code);
		if(bool) {
			return "true";
		}else {
			return "false";
		}
	}
	
	
	@ResponseBody
	@RequestMapping("/saveNode")
	public String saveNode(String paramJson) {
		//String code = treeService.saveRole();
		treeService.saveTree(paramJson);
		return "true";
	}
	
}
