package cn.sowell.datacenter.model.config.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abc.auth.pojo.AuthorityVO;
import com.abc.auth.service.ServiceFactory;
import com.google.common.collect.Lists;

import cn.sowell.copframe.common.UserIdentifier;
import cn.sowell.copframe.dao.utils.NormalOperateDao;
import cn.sowell.copframe.dao.utils.UserUtils;
import cn.sowell.copframe.utils.CollectionUtils;
import cn.sowell.copframe.utils.TextUtils;
import cn.sowell.datacenter.model.admin.pojo.ABCUser;
import cn.sowell.datacenter.model.config.dao.SideMenuDao;
import cn.sowell.datacenter.model.config.pojo.SideMenuLevel1Menu;
import cn.sowell.datacenter.model.config.pojo.SideMenuLevel2Menu;
import cn.sowell.datacenter.model.config.service.AuthorityService;
import cn.sowell.datacenter.model.config.service.SideMenuService;
import cn.sowell.dataserver.model.modules.pojo.ModuleMeta;
import cn.sowell.dataserver.model.modules.service.ModulesService;
import cn.sowell.dataserver.model.statview.service.StatViewService;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateGroup;
import cn.sowell.dataserver.model.tmpl.pojo.TemplateStatView;
import cn.sowell.dataserver.model.tmpl.service.TemplateGroupService;

@Service
public class SideMenuServiceImpl implements SideMenuService, InitializingBean{

	@Resource
	SideMenuDao sDao;
	
	@Resource
	NormalOperateDao nDao;
	
	@Resource
	ModulesService mService;
	
	@Resource
	AuthorityService authService;
	
	@Resource
	TemplateGroupService tmplGroupService;
	
	@Resource
	StatViewService statViewService;
	
	Map<Long, SideMenuLevel1Menu> l1MenuMap;
	
	Map<Long, SideMenuLevel2Menu> l2MenuMap;
	
	@Override
	public synchronized void reloadMenuMap() {
		l1MenuMap = null;
		l2MenuMap = null;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		tmplGroupService.bindTemplateGroupReloadEvent(tmplGroup->{
			l2MenuMap.values().stream()
				.filter(l2->tmplGroup.getId().equals(l2.getTemplateGroupId()))
				.forEach(l2->{
					l2.setTemplateGroupTitle(tmplGroup.getTitle());
					l2.setTemplateGroupKey(tmplGroup.getKey());
				});
		});
	}
	
	synchronized Map<Long, SideMenuLevel1Menu> getL1MenuMap(){
		if(l1MenuMap == null) {
			List<SideMenuLevel1Menu> level1s = sDao.getSideMenus();
			
			Map<Long, List<SideMenuLevel2Menu>> groupsMap 
						= sDao.querySideMenuLevel2Map();
			level1s.forEach(level1->{
				//转换权限字符串
				String l1Authorities = level1.getAuthorities();
				if(l1Authorities != null) {
					String[] split = l1Authorities.split(";");
					for (String auth : split) {
						if(TextUtils.hasText(auth))
						level1.getAuthoritySet().add(auth);
					}
				}
				List<SideMenuLevel2Menu> level2s = groupsMap.get(level1.getId());
				if(level2s != null) {
					level1.setLevel2s(level2s);
				}
				if(level2s != null) {
					Iterator<SideMenuLevel2Menu> itr = level2s.iterator();
					while(itr.hasNext()) {
						SideMenuLevel2Menu l2 = itr.next();
						String moduleName = null;
						if(l2.getTemplateGroupId() != null) {
							TemplateGroup tmplGroup = tmplGroupService.getTemplate(l2.getTemplateGroupId());
							if(tmplGroup != null) {
								l2.setTemplateGroupTitle(tmplGroup.getTitle());
								l2.setTemplateGroupKey(tmplGroup.getKey());
								moduleName = tmplGroup.getModule();
							}
						}else if(l2.getStatViewId() != null) {
							TemplateStatView statView = statViewService.getTemplate(l2.getStatViewId());
							if(statView != null) {
								l2.setStatViewTitle(statView.getTitle());
								moduleName = statView.getModule();
							}
						}
						if(moduleName != null) {
							ModuleMeta module = mService.getModule(moduleName);
							if(module != null) {
								l2.setTemplateModuleTitle(module.getTitle());
								l2.setTemplateModule(module.getName());
								l2.setLevel1Menu(level1);
								
								//转换权限字符串
								String l2Authorities = l2.getAuthorities();
								if(l2Authorities != null) {
									String[] split = l2Authorities.split(";");
									for (String auth : split) {
										if(TextUtils.hasText(auth))
											l2.getAuthoritySet().add(auth);
									}
								}
								continue ;
							}
						}
						itr.remove();
					}
				}
			});
			
			l1MenuMap = CollectionUtils.toMap(level1s, SideMenuLevel1Menu::getId);
			
		}
		return l1MenuMap;
	}
	
	
	Map<Long, SideMenuLevel2Menu> getL2MenuMap(){
		synchronized (this) {
			if(l2MenuMap == null) {
				Map<Long, SideMenuLevel2Menu> map = new HashMap<>();
				getL1MenuMap().values().stream().forEach(l1->{
					List<SideMenuLevel2Menu> l2Menus = l1.getLevel2s();
					if(l2Menus != null) {
						l2Menus.forEach(l2->map.put(l2.getId(), l2)); 
					}
				});
				l2MenuMap = map;
			}
			return l2MenuMap;
		}
	}
	
	
	@Override
	@Transactional(propagation=Propagation.SUPPORTS)
	public List<SideMenuLevel1Menu> getSideMenuLevelMenus(UserIdentifier user) {
		return new ArrayList<>(getL1MenuMap().values());
	}
	
	@Override
	public void updateSideMenuModules(UserIdentifier user, List<SideMenuLevel1Menu> l1Menus) {
		List<SideMenuLevel1Menu> originModules = getSideMenuLevelMenus(user);
		CollectionUpdateStrategy<SideMenuLevel1Menu> updateLevel1Menu = 
				new CollectionUpdateStrategy<>(SideMenuLevel1Menu.class, nDao,
						module->module.getId());
		CollectionUpdateStrategy<SideMenuLevel2Menu> updateLevel2Menu = 
				new CollectionUpdateStrategy<>(SideMenuLevel2Menu.class, nDao,
						group->group.getId());
		updateLevel1Menu.setBeforeUpdate((origin, module)->{
					origin.setTitle(module.getTitle());
					origin.setAuthorities(module.getAuthorities());
					origin.setOrder(module.getOrder());
				});
		updateLevel1Menu.setAfterUpdate((origin, module)->{
			module.getLevel2s().forEach(group->group.setSideMenuLevel1Id(origin.getId()));
			updateLevel2Menu.doUpdate(origin.getLevel2s(), module.getLevel2s());
		});
		updateLevel1Menu.setAfterCreate(module->{
			module.getLevel2s().forEach(group->group.setSideMenuLevel1Id(module.getId()));
			updateLevel2Menu.doUpdate(null, module.getLevel2s());
		});
		updateLevel2Menu.setBeforeUpdate((o, g)->{
			o.setOrder(g.getOrder());
			o.setTitle(g.getTitle());
			o.setAuthorities(g.getAuthorities());
		});
		updateLevel1Menu.doUpdate(originModules, l1Menus);
		reloadMenuMap();
	}
	
	@Override
	public SideMenuLevel2Menu getLevel2Menu(Long menuId) {
		return getL2MenuMap().get(menuId);
	}
	
	@Override
	public SideMenuLevel1Menu getLevel1Menu(Long menuId) {
		return getL1MenuMap().get(menuId);
	}

	public Map<Long, String[]> getMenuAuthNameMap(Set<Long> menuIds, Function<Long, Set<String>> authGetter) {
		Map<Long, String[]> map = new HashMap<>();
		if(menuIds != null) {
			Collection<? extends AuthorityVO> auths =  ServiceFactory.getRoleAuthorityService().getFunctionAuth(((ABCUser) UserUtils.getCurrentUser()).getUserInfo());
			menuIds.forEach(menuId->{
				Set<String> authorities = authGetter.apply(menuId);
				if(authorities != null) {
					List<String> authNameList = Lists.newArrayList();
					authorities.forEach(authCode->{
						AuthorityVO auth = auths.stream().filter(au->authCode.equals(au.getCode())).findFirst().orElse(null);
						if(auth != null) {
							authNameList.add(auth.getName());
						}
					});
					map.put(menuId, authNameList.toArray(new String[authNameList.size()]));
				}
			});
		}
		return map;
	}
	
	@Override
	public Map<Long, String[]> getMenu1AuthNameMap(Set<Long> level1MenuId) {
		return getMenuAuthNameMap(level1MenuId, l1MenuId->{
			SideMenuLevel1Menu menu = getLevel1Menu(l1MenuId);
			if(menu != null) {
				return menu.getAuthoritySet();
			}
			return null;
		});
	}
	
	@Override
	public Map<Long, String[]> getMenu2AuthNameMap(Set<Long> level2MenuId) {
		return getMenuAuthNameMap(level2MenuId, l1MenuId->{
			SideMenuLevel2Menu menu = getLevel2Menu(l1MenuId);
			if(menu != null) {
				return menu.getAuthoritySet();
			}
			return null;
		});
	}
	

}
