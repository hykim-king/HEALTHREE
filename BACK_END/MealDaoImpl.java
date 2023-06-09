package com.pcwk.ehr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class MealDaoImpl implements MealDao {

	private final Logger LOG = LogManager.getLogger(getClass());

	private DataSource dataSource;

	// Spring이 제공하는 jdbcTemplate
	private JdbcTemplate jdbcTemplate;

	// **** default 생성
	public MealDaoImpl() {
	}

	// *** applicationContext.xml에서 주입
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		// 수동으로 DI
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	// 자주 먹은 식사 조회
	public List<MealVO> getFrequentMeal(MealVO meal, FoodVO food) throws ClassNotFoundException, SQLException {

		List<MealVO> outList = new ArrayList<>();

		StringBuilder sb = new StringBuilder(300);
		sb.append(" SELECT    						  	\n ");
		sb.append(" f_name   						  	\n ");
		sb.append(" from    						  	\n ");
		sb.append("  	meal t1   					  	\n ");
		sb.append("  JOIN food t2                     	\n ");
		sb.append("  	ON t1.f_code = t2.f_code      	\n ");
		sb.append("  WHERE                            	\n ");
		sb.append("  	t1.m_id = ?			      	    \n ");
		sb.append("  GROUP BY t1.f_code, t2.f_name    	\n ");
		sb.append("  ORDER BY COUNT(*) DESC, f_name   	\n ");
		sb.append("  FETCH FIRST 10 ROWS ONLY		  	\n ");

		LOG.debug("1. sql = \n" + sb.toString());
		LOG.debug("2. param = " + meal);

		// param
		Object[] args = { meal.getId() };

		outList = this.jdbcTemplate.query(sb.toString(), new RowMapper<MealVO>() {
			@Override
			public MealVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				MealVO out = new MealVO();

				out.setDiv(rs.getString("f_name"));
				return out;
			}
		}, args);
		LOG.debug("3.outVO:" + outList);

		return outList;

	} // getFrequentMeal()

	// 최근 먹은 식사 조회
	@Override
	public List<MealVO> getRecentMeal(MealVO meal) throws ClassNotFoundException, SQLException {
		List<MealVO> outList = new ArrayList<>();

		StringBuilder sb = new StringBuilder(200);
		sb.append(" SELECT f_name                  \n ");
		sb.append(" FROM (                         \n ");
		sb.append("   SELECT *                     \n ");
		sb.append("   FROM meal t1, food t2        \n ");
		sb.append("   WHERE t1.f_code = t2.f_code  \n ");
		sb.append("   AND m_id = ?                 \n ");
		sb.append("   ORDER BY m_date DESC         \n ");
		sb.append(" )                              \n ");
		sb.append(" WHERE ROWNUM <= 10             \n ");

		LOG.debug("1. sql=\n" + sb.toString());
		LOG.debug("2. param=" + meal);

		// param
		Object[] args = { meal.getId() };

		outList = this.jdbcTemplate.query(sb.toString(), new RowMapper<MealVO>() {
			@Override
			public MealVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				MealVO out = new MealVO();

				out.setDiv(rs.getString("f_name"));
				return out;
			}
		}, args);
		LOG.debug("3.outVO:" + outList);

		return outList;

	}

	// 회원ID, 날짜까지 선택 후 식단 조회
	@Override
	public List<MealVO> getDivSeqFoodCode(MealVO meal) throws ClassNotFoundException, SQLException {
		List<MealVO> outList = new ArrayList<>();

		StringBuilder sb = new StringBuilder(200);
		sb.append(" SELECT             \n");
		sb.append("     m_id,          \n");
		sb.append("     m_date,        \n");
		sb.append("     m_div,         \n");
		sb.append("     m_seq,         \n");
		sb.append("     f_code         \n");
		sb.append(" FROM               \n");
		sb.append("     meal           \n");
		sb.append(" WHERE              \n");
		sb.append("         m_id =   ? \n");
		sb.append("     AND m_date = ? \n");

		LOG.debug("1. sql=\n" + sb.toString());
		LOG.debug("2. param=" + meal);

		// param
		Object[] args = { meal.getId(), meal.getDate() };
		outList = this.jdbcTemplate.query(sb.toString(), new RowMapper<MealVO>() {

			@Override
			public MealVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				MealVO out = new MealVO();

				out.setId(rs.getString("m_id"));
				out.setDate(rs.getString("m_date"));
				out.setDiv(rs.getString("m_div"));
				out.setSeq(rs.getInt("m_seq"));
				out.setCode(rs.getString("f_code"));

				return out;
			}

		}, args);

		LOG.debug("3.outVO:" + outList);

		return outList;
	}

	// 회원ID, 날짜, 식사구분까지 선택 후 식단 조회
	@Override
	public List<MealVO> getSeqFoodCode(MealVO meal) throws SQLException {
		List<MealVO> outList = new ArrayList<>();

		StringBuilder sb = new StringBuilder(200);
		sb.append(" SELECT             \n");
		sb.append("     m_id,          \n");
		sb.append("     m_date,        \n");
		sb.append("     m_div,         \n");
		sb.append("     m_seq,         \n");
		sb.append("     f_code         \n");
		sb.append(" FROM               \n");
		sb.append("     meal           \n");
		sb.append(" WHERE              \n");
		sb.append("         m_id =   ? \n");
		sb.append("     AND m_date = ? \n");
		sb.append("     AND m_div =  ? \n");

		LOG.debug("1. sql=\n" + sb.toString());
		LOG.debug("2. param=" + meal);

		// param
		Object[] args = { meal.getId(), meal.getDate(), meal.getDiv() };
		outList = this.jdbcTemplate.query(sb.toString(), new RowMapper<MealVO>() {

			@Override
			public MealVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				MealVO out = new MealVO();

				out.setId(rs.getString("m_id"));
				out.setDate(rs.getString("m_date"));
				out.setDiv(rs.getString("m_div"));
				out.setSeq(rs.getInt("m_seq"));
				out.setCode(rs.getString("f_code"));

				return out;
			}

		}, args);

		return outList;
	}

	// 회원ID, 날짜, 식사구분, SEQ까지 선택 후 식단 조회 (단 건 조회)
	@Override
	public MealVO getFoodCode(MealVO meal) throws ClassNotFoundException, SQLException {
		MealVO outVO = null;

		StringBuilder sb = new StringBuilder(200);
		sb.append(" SELECT             \n");
		sb.append("     m_id,          \n");
		sb.append("     m_date,        \n");
		sb.append("     m_div,         \n");
		sb.append("     m_seq,         \n");
		sb.append("     f_code         \n");
		sb.append(" FROM               \n");
		sb.append("     meal           \n");
		sb.append(" WHERE              \n");
		sb.append("         m_id =   ? \n");
		sb.append("     AND m_date = ? \n");
		sb.append("     AND m_div =  ? \n");
		sb.append("     AND m_seq =  ? \n");

		LOG.debug("1. sql=\n" + sb.toString());
		LOG.debug("2. param=" + meal);

		// param
		Object[] args = { meal.getId(), meal.getDate(), meal.getDiv(), meal.getSeq() };
		outVO = this.jdbcTemplate.queryForObject(sb.toString(), new RowMapper<MealVO>() {

			@Override
			public MealVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				MealVO out = new MealVO();

				out.setId(rs.getString("m_id"));
				out.setDate(rs.getString("m_date"));
				out.setDiv(rs.getString("m_div"));
				out.setSeq(rs.getInt("m_seq"));
				out.setCode(rs.getString("f_code"));

				return out;
			}

		}, args);

		LOG.debug("3.outVO:" + outVO);

		return outVO;
	}

	// 삭제
	@Override
	public int deleteOne(final MealVO meal) throws SQLException {
		int flag = 0;

		StringBuilder sb = new StringBuilder(50);
		sb.append(" DELETE FROM meal   \n");
		sb.append(" WHERE              \n");
		sb.append("         m_id   = ? \n");
		sb.append("     AND m_date = ? \n");
		sb.append("     AND m_div  = ? \n");
		sb.append("     AND m_seq  = ? \n");

		LOG.debug("1. sql =  \n" + sb.toString());
		LOG.debug("2. param =\n" + meal.toString());

		// param
		Object[] args = { meal.getId(), meal.getDate(), meal.getDiv(), meal.getSeq() };

		// jdbcTemplate.update DML에 사용
		flag = jdbcTemplate.update(sb.toString(), args);
		LOG.debug("3. flag = " + flag);

		return flag;
	}

	// 추가
	@Override
	public int add(final MealVO meal) throws ClassNotFoundException, SQLException {
		int flag = 0;// 등록 건수

		StringBuilder sb = new StringBuilder(200);
		sb.append(" INSERT INTO meal ( \n");
		sb.append("     m_id,          \n");
		sb.append("     m_date,        \n");
		sb.append("     m_div,         \n");
		sb.append("     m_seq,         \n");
		sb.append("     f_code         \n");
		sb.append(" ) VALUES (         \n");
		sb.append("     ?,             \n");
		sb.append("     ?,             \n");
		sb.append("     ?,             \n");
		sb.append("     ?,             \n");
		sb.append("     ?              \n");
		sb.append(" )                  \n");

		LOG.debug("1. sql=\n" + sb.toString());
		LOG.debug("2. param=\n" + meal.toString());

		// param
		Object[] args = { meal.getId(), meal.getDate(), meal.getDiv(), meal.getSeq(), meal.getCode() };

		// jdbcTemplate.update DML에 사용
		flag = jdbcTemplate.update(sb.toString(), args);
		LOG.debug("3. flag=" + flag);

		return flag;
	}

}
