package cn.sowell.datacenter.test.ks;

import java.util.HashMap;
import java.util.Map;

import cho.carbon.auth.pojo.UserInfo;
import cn.sowell.datacenter.common.ApiUser;
import cn.sowell.datacenter.common.UserWithToken;
import cn.sowell.datacenter.model.ks.service.KaruiServExecService;
import cn.sowell.dataserver.model.karuiserv.match.KaruiServMatcher;

/*@ContextConfiguration(locations = "classpath*:spring-config/spring-junit.xml")
@RunWith(SpringJUnit4ClassRunner.class)*/
public class TestKsExecService {
	KaruiServExecService ksExecService;
	
	//@Test
	public void test() {
		String path = "/get_people/123456138009021234";
		Map<String, String> parameters = new HashMap<>();
		KaruiServMatcher matcher = ksExecService.match(path, parameters);
		UserInfo u = new UserInfo();
		u.setCode("codecode");
		u.setUserName("admin");
		ApiUser user = new UserWithToken("123456", u);
		ksExecService.executeKaruiServ(matcher, user);
		System.out.println(matcher);
	}
	
	
}
