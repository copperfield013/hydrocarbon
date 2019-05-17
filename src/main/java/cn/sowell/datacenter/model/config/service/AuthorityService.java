package cn.sowell.datacenter.model.config.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import com.abc.auth.pojo.AuthorityVO;

import cn.sowell.datacenter.common.ApiUser;
import cn.sowell.datacenter.model.config.bean.ValidateDetailParamter;
import cn.sowell.datacenter.model.config.bean.ValidateDetailResult;
import cn.sowell.datacenter.model.config.pojo.SideMenuLevel1Menu;
import cn.sowell.datacenter.model.config.pojo.SideMenuLevel2Menu;
import cn.sowell.datacenter.model.config.pojo.criteria.AuthorityCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateDetailFieldGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroupAction;

public interface AuthorityService {
	SideMenuLevel2Menu validateL2MenuAccessable(Long level2MenuId) throws NonAuthorityException;

	SideMenuLevel1Menu validateL1MenuAccessable(Long level1MenuId) throws NonAuthorityException;
	
	List<AuthorityVO> queryAuthorities(AuthorityCriteria criteria);

	AuthorityVO getAuthority(String authCode);

	SideMenuLevel1Menu validateUserL1MenuAccessable(UserDetails user, Long level1MenuId) throws NonAuthorityException;

	SideMenuLevel2Menu validateUserL2MenuAccessable(UserDetails user, Long level2MenuId) throws NonAuthorityException;

	void validateGroupAction(TemplateGroupAction groupAction, TemplateGroup tmplGroup, String codes);

	TemplateDetailFieldGroup validateSelectionAuth(String validateSign, Long groupId, ApiUser user);

	ValidateDetailResult validateDetailAuth(ValidateDetailParamter param);

	void validateUserAccessable(UserDetails user, String validateSign);

}
