package cn.sowell.datacenter.model.treeview.service;

import com.abc.mapping.entity.Entity;
import com.abc.mapping.node.ABCNode;

public interface TreeViewService {
	
	String saveTree(String paramJson);
	
	String getData(String mappingName, String code);
	
	Entity getEntity(String mappingName, String code);
	
	boolean deleteTree(String code);
	
	ABCNode getABCNode(String mappingName);
	
}
