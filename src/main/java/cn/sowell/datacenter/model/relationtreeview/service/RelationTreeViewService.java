package cn.sowell.datacenter.model.relationtreeview.service;

import com.abc.hc.HCFusionContext;
import com.abc.mapping.entity.Entity;
import com.abc.mapping.node.ABCNode;

public interface RelationTreeViewService {
	
	String saveTree(String paramJson);
	
	String getData(String mappingName, String code);
	
	Entity getEntity(String mappingName, String code);
	
	boolean deleteTree(String code);
	
	ABCNode getABCNode(String mappingName);

	/**
	 * 建立实体与实体之间的关系
	 * @param parentMappingName   父实体mappingname
	 * @param parentId            父实体id
	 * @param chileMappingName    孩子的mappingname
	 * @param chileId			  孩子的id
	 * @param relationName			关系名称
	 * @param labelsetmap		关系的所有标签
	 * @param labelsetvalue     关系的已选择的标签
	 * @return
	 */
	String addRelation(String parentMappingName, String parentId, String chileMappingName, String chileId,
			String relationName, String labelsetmap,
			String labelsetvalue);
	
	public HCFusionContext getHCFusionContext(String mappingName);
}
