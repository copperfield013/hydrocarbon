package cn.sowell.datacenter.common.jstl.render;

import cn.sowell.datacenter.common.choose.HTMLTag;
import cn.sowell.datacenter.common.jstl.CriteriaInputRenderer;
import cn.sowell.dataserver.model.modules.service.view.ViewListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;

public class UnknowInputRenderer<CRI extends AbstractListCriteria> implements CriteriaInputRenderer<CRI> {

	@Override
	public boolean match(CRI tCriteria) {
		return true;
	}

	@Override
	public HTMLTag render(CRI tCriteria, ViewListCriteria<CRI> viewCriteia) {
		HTMLTag $input = new HTMLTag("input");
		$input
			.attribute("type", "text")
			.attribute("disabled", "disabled")
			.attribute("placeholder", "没有配置对应的控件" + tCriteria.getInputType());
		return $input;
	}

}
