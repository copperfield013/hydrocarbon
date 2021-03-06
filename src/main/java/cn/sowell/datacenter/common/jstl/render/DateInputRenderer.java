package cn.sowell.datacenter.common.jstl.render;

import cn.sowell.datacenter.common.choose.HTMLTag;
import cn.sowell.dataserver.model.modules.service.view.ViewListCriteria;
import cn.sowell.dataserver.model.tmpl.pojo.AbstractListCriteria;

public class DateInputRenderer<CRI extends AbstractListCriteria> extends AbstractMatchTypeInputFormControlRenderer<CRI>{

	
	private static final String DATE = "date";
	private static final String DATETIME = "datetime";
	private static final String TIME = "time";
	private static final String YEARMONTH = "yearmonth";
	private static final String YMRANGE = "ymrange";

	@Override
	protected String[] getMatchInputTypes() {
		return new String[] {DATE, DATETIME, TIME, YEARMONTH, YMRANGE};
	}

	@Override
	protected HTMLTag createInputTag(CRI tCriteria, ViewListCriteria<CRI> viewCriteia) {
		HTMLTag $input = new HTMLTag("input");
		$input
			.addClass(tCriteria.getInputType() + "picker")
			.attribute("type", "text")
			.attribute("autocomplete", "off")
			.attribute("value", viewCriteia.getValue())
		;
		return $input;
	}

}
