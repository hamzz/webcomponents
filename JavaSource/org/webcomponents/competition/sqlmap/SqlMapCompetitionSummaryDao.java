package org.webcomponents.competition.sqlmap;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.webcomponents.competition.CompetitionSummaryDao;
import org.webcomponents.summary.sqlmap.SqlMapClusterDAO;

public class SqlMapCompetitionSummaryDao extends SqlMapClusterDAO implements CompetitionSummaryDao {

	private String dailyParticipantsStatement = "getDailyParticipants";
	private String monthlyParticipantsStatement = "getMonthlyParticipants";
	private String weeklyParticipantsStatement = "getWeeklyParticipants";

	@SuppressWarnings("unchecked")
	public TreeMap<Date, Number> getDailyParticipantsCount(Date begin, Date end) {
		Map<String, Date> model = Collections.emptyMap();
		if(begin != null) {
			model = new HashMap<String, Date>(2);
			model.put("begin", begin);
		}
		if(end != null)  {
			if (model.equals(Collections.emptyMap())) {
				model = new HashMap<String, Date>(1);
			}
			model.put("end", end);
		}
		TreeMap<Date, Number> rv = new TreeMap<Date, Number>(getSqlMapClientTemplate().queryForMap(dailyParticipantsStatement, model, "DAY", "TOTAL"));
		return rv;
	}

	public TreeMap<Date, Number> getDailyParticipationsCount(Date begin, Date end) {
		return getMonthlyCount(begin, end);
	}

	@SuppressWarnings("unchecked")
	public TreeMap<Date, Number> getMonthlyParticipantsCount(Date begin, Date end) {
		Map<String, Date> model = Collections.emptyMap();
		if(begin != null) {
			model = new HashMap<String, Date>(2);
			model.put("begin", begin);
		}
		if(end != null)  {
			if (model.equals(Collections.emptyMap())) {
				model = new HashMap<String, Date>(1);
			}
			model.put("end", end);
		}
		TreeMap<Date, Number> rv = new TreeMap<Date, Number>(getSqlMapClientTemplate().queryForMap(monthlyParticipantsStatement, model, "MONTH", "TOTAL"));
		return rv;
	}

	public TreeMap<Date, Number> getMonthlyParticipationsCount(Date begin, Date end) {
		return getMonthlyCount(begin, end);
	}

	@SuppressWarnings("unchecked")
	public TreeMap<Date, Number> getWeeklyParticipantsCount(Date begin, Date end) {
		Map<String, Date> model = Collections.emptyMap();
		if(begin != null) {
			model = new HashMap<String, Date>(2);
			model.put("begin", begin);
		}
		if(end != null)  {
			if (model.equals(Collections.emptyMap())) {
				model = new HashMap<String, Date>(1);
			}
			model.put("end", end);
		}
		TreeMap<Date, Number> rv = new TreeMap<Date, Number>(getSqlMapClientTemplate().queryForMap(weeklyParticipantsStatement, model, "WEEK", "TOTAL"));
		return rv;
	}

	public TreeMap<Date, Number> getWeeklyParticipationsCount(Date begin, Date end) {
		return getWeeklyCount(begin, end);
	}

	public void setDailyParticipantsStatement(String dailyParticipantsStatement) {
		this.dailyParticipantsStatement = dailyParticipantsStatement;
	}

	public void setMonthlyParticipantsStatement(String monthlyParticipantsStatement) {
		this.monthlyParticipantsStatement = monthlyParticipantsStatement;
	}

	public void setWeeklyParticipantsStatement(String weeklyParticipantsStatement) {
		this.weeklyParticipantsStatement = weeklyParticipantsStatement;
	}

}
