package cn.sowell.datacenter.model.config.dao;

import java.util.Map;
import java.util.Set;

import cn.sowell.datacenter.model.config.pojo.AuthencationConfig;
import cn.sowell.datacenter.model.config.pojo.ConfigModule;

public interface ConfigureDao {

	Map<String, ConfigModule> getConfigModule(Set<String> moduleNames);

	AuthencationConfig getAdminDefaultAuthen(Long configId);

}
