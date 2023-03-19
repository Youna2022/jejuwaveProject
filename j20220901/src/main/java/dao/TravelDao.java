package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class TravelDao {
	private static TravelDao instance;
	private TravelDao() {}
	public static TravelDao getInstance() {
		if(instance == null) {
			instance = new TravelDao();
		}
		return instance;
	}
	private Connection getConnection() {
		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/OracleDB");
			conn = ds.getConnection();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}
	
	public int getTotalCnt(Travel tvl) throws SQLException {
// 		System.out.println("=====================TravelDao getTotalCnt Start=====================");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String addSql = "";
		
		/* 날짜조회 쿼리 추가 */
		if(tvl.getT_start() != null && tvl.getT_start() != "") {
			addSql += " and t_start >= '" + tvl.getT_start() + "'";
		}
		if(tvl.getT_end() != null && tvl.getT_end() != "") {
			addSql += " and t_end <= '" + tvl.getT_end() + "'";
		}
		
		/* 모집 진행중 쿼리 추가 */
		if(tvl.getT_dealstatus() != null && tvl.getT_dealstatus() != "") {
			addSql += " and t_dealstatus = '" + tvl.getT_dealstatus() + "'";
		}
		
		/* 구분 쿼리 추가 */
		//,'숙박','레저'
		// and t_gubun in ('숙박','레저') >> in 또는
		if(tvl.getT_gubun() != null && tvl.getT_gubun() != "") {
			addSql += " and t_gubun in (" + tvl.getT_gubun().substring(1) + ")";
		}
		
		/* 메인 검색 쿼리 추가 */
		if(tvl.getT_title() != null && tvl.getT_title() != "") {
			addSql += " and t_title like  '%" + tvl.getT_title() + "%'";
		}
		
		String sql = "select count(*) from travel_board where t_relevel=0" + addSql;
		
		int tot = 0;
		
// 		System.out.println("TravelDao  getTotalCnt sql-->" + sql);
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				tot = rs.getInt(1);
			}
// 			System.out.println("TravelDao  getTotalCnt tot-->" + tot);
// 			System.out.println("=====================TravelDao getTotalCnt End=====================");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn != null)conn.close();
			if(stmt != null)stmt.close();
			if(rs != null)rs.close();
		}
		return tot;
		
	}
	
	public int getTotalCnt() throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		
		String sql = "select count(*) from travel_board where t_relevel=0";
		int tot = 0;
		
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				tot = rs.getInt(1);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn != null)conn.close();
			if(stmt != null)stmt.close();
			if(rs != null)rs.close();
		}
		return tot;
		
	}
	public List<Travel> traveList(int startRow, int endRow) throws SQLException {
// 		System.out.println("=====================TravelDao traveList Start=====================");
		List<Travel> list = new ArrayList<Travel>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql =   "select *"
						+ "  from (select rownum r, a.*, reply_cnt(a.t_ref) reply_cnt, fn_user_img(a.user_id) as user_img"
						+ "          from (select * "
						+ "                  from travel_board where t_Relevel = 0  order by t_ref desc) a)"
						+ " where r between ? and ?";
		
		
// 		System.out.println("TravelDao  traveList startRow-->"+startRow);
// 		System.out.println("TravelDao  traveList endRow-->"+endRow);
// 		System.out.println("TravelDao  traveList sql-->"+sql);

		
		try {
			conn  = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			rs = pstmt.executeQuery();
			while(rs.next()){
				Travel travel = new Travel();
				travel.setT_num			(rs.getInt		("t_num"));
				travel.setUser_id		(rs.getString	("user_id"));
				travel.setT_img			(rs.getString	("t_img"));
				travel.setT_title		(rs.getString	("t_title"));
				travel.setT_content		(rs.getString	("t_content").replace("<br>","\r\n"));
				travel.setT_gubun		(rs.getString	("t_gubun"));
				travel.setT_date		(rs.getString	("t_date"));
				travel.setT_person		(rs.getInt		("t_person"));
				travel.setT_start		(rs.getString	("t_start").substring(5));
				travel.setT_end			(rs.getString	("t_end").substring(5));
				travel.setT_dealstatus	(rs.getString	("t_dealstatus"));
				travel.setT_ref			(rs.getInt		("t_ref"));
				travel.setT_relevel		(rs.getInt		("t_relevel"));
				travel.setT_restep		(rs.getInt		("t_restep"));
				travel.setReply_cnt		(rs.getInt		("reply_cnt"));
				travel.setUser_img		(rs.getString	("user_img"));
				
				list.add(travel);
			}
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn  != null) conn.close();
			if(pstmt != null) pstmt.close();
			if(rs 	 != null) rs.close();
		}
		return list;
	}
	
	
	
	public Travel select (int t_num) throws SQLException {
// 		System.out.println("=====================TravelDao select Start=====================");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
// 		System.out.println("=====> t_num ==" + t_num);
		String sql 	 = "select t_num, user_id, t_img, t_title, t_content, t_gubun,"
				     + " TO_CHAR(TO_DATE(t_date) , 'YYYY.MM.DD') AS t_date, t_person, t_start,"
				     + " t_end, t_dealstatus, t_ref, t_relevel, t_restep, fn_user_img(user_id) as user_img"
				     + " from travel_board where t_num=?";
		

		Travel travel = new Travel();
		
		try {
			conn 	= getConnection();
			pstmt	= conn.prepareStatement(sql);
			pstmt.setInt(1, t_num);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				travel.setT_num			(rs.getInt		("t_num"));
				travel.setUser_id		(rs.getString	("user_id"));
				travel.setT_img			(rs.getString	("t_img"));
				travel.setT_title		(rs.getString	("t_title"));
				travel.setT_content		(rs.getString	("t_content"));
				travel.setT_gubun		(rs.getString	("t_gubun"));
				travel.setT_date		(rs.getString	("t_date"));
				travel.setT_person		(rs.getInt		("t_person"));
				travel.setT_start		(rs.getString	("t_start"));
				travel.setT_end			(rs.getString	("t_end"));
				travel.setT_dealstatus	(rs.getString	("t_dealstatus"));
				travel.setT_ref			(rs.getInt		("t_ref"));
				travel.setT_relevel		(rs.getInt		("t_relevel"));
				travel.setT_restep		(rs.getInt		("t_restep"));
				travel.setUser_img		(rs.getString	("user_img"));

				System.out.println("TravelDao  select user_img-->"+rs.getString	("user_img"));
				System.out.println("TravelDao  select t_title-->"+rs.getString	("t_title"));
				System.out.println("TravelDao  select t_content-->"+rs.getString	("t_content"));
			}
			
// 			System.out.println("=====================TravelDao select End=====================");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn != null) conn.close();
			if(pstmt != null) pstmt.close();
			if(rs != null) rs.close();
		}
		return travel;
	}
	public List<Travel> replyList (int t_ref) throws SQLException {
		List<Travel> list = new ArrayList<Travel>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "select t_num, user_id, t_content, t_date, t_ref, t_relevel, t_restep, fn_user_img(user_id) as user_img"
				  + " from travel_board where t_ref=? and t_relevel != 0 order by t_restep, t_relevel, t_date";
		
// 		System.out.println();
		
		try {
			conn  = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, t_ref);
			rs = pstmt.executeQuery();
			while(rs.next()){
				Travel travel = new Travel();
				travel.setT_num			(rs.getInt		("t_num"));
				travel.setUser_id		(rs.getString	("user_id"));
				travel.setT_content		(rs.getString	("t_content"));
				travel.setT_date		(rs.getString	("t_date"));
				travel.setT_ref			(rs.getInt		("t_ref"));
				travel.setT_relevel		(rs.getInt		("t_relevel"));
				travel.setT_restep		(rs.getInt		("t_restep"));
				travel.setUser_img		(rs.getString	("user_img"));
				
				list.add(travel);
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn  != null) conn.close();
			if(pstmt != null) pstmt.close();
			if(rs 	 != null) rs.close();
		}
		return list;
	}
	
	
	
	public int update(Travel travel) throws SQLException{
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = "update travel_board set t_img=?, t_title=?, t_content=?, t_gubun=?, t_person=?, t_start=?, t_end=? where t_num=?";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString (1, travel.getT_img());
			pstmt.setString (2, travel.getT_title());
			pstmt.setString (3, travel.getT_content());
			pstmt.setString (4, travel.getT_gubun());
			pstmt.setInt	(5, travel.getT_person());
			pstmt.setString (6, travel.getT_start());
			pstmt.setString (7, travel.getT_end());
			pstmt.setInt    (8, travel.getT_num());
			
			result  = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn != null)  conn.close();
			if(pstmt != null) pstmt.close();
		}
		return result; 
	}
	
	public Travel insert(Travel travel) throws SQLException {
		Connection conn = null;
// 		System.out.println("나는 insert 시작");
		PreparedStatement pstmt = null;
		int result = 0;
		ResultSet rs = null;
		String sql 	= "insert into travel_board values(?, ?, ?, ?, ?, ?, sysdate, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			System.out.println("최대값 === > " + travel.getT_num());
			conn 	= getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt	(1,  travel.getT_num());
			pstmt.setString	(2,  travel.getUser_id());
			pstmt.setString	(3,  travel.getT_img());
			pstmt.setString	(4,  travel.getT_title());
			pstmt.setString	(5,  travel.getT_content());
			pstmt.setString	(6,  travel.getT_gubun());

			pstmt.setInt	(7,  travel.getT_person());
			pstmt.setString	(8,	 travel.getT_start());
			pstmt.setString	(9,  travel.getT_end());
			pstmt.setString	(10, travel.getT_dealstatus());
			pstmt.setInt	(11, travel.getT_ref());
			pstmt.setInt	(12, travel.getT_relevel());
			pstmt.setInt	(13, travel.getT_restep());
			
			result = pstmt.executeUpdate();
			if(result > 0) {
				System.out.println("INSERT 완료");
			} else {
				System.out.println("INSERT 실패");
			}
			
			travel.setResult(result);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		}
		return travel;
	}
	
	public int getMaxT_num() throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql 	 = "select nvl(max(t_num), 0) from travel_board";
		
		int result = 0;
		
		try {
			conn 	= getConnection();
			pstmt	= conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1) + 1;
			}
// 			System.out.println("getMaxNum t_num====>" + result);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn != null) conn.close();
			if(pstmt != null) pstmt.close();
			if(rs != null) rs.close();
		}
		return result;
	}
	
	public int delete(int t_num) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = "delete from travel_board where t_num=?";
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, t_num);
			
			result  = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn != null)  conn.close();
			if(pstmt != null) pstmt.close();
		}
		return result; 
	}
	public int statusUpdate(int t_num) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = "update travel_board set t_dealstatus=1 where t_num=?";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, t_num);
			
			result  = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn != null)  conn.close();
			if(pstmt != null) pstmt.close();
		}
		return result; 
	}
	
	public int deleteReply(int t_num) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		String sql = "update travel_board set t_content='삭제된 댓글입니다.' where t_num=?";
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setInt(1, t_num);
			
			result  = pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn != null)  conn.close();
			if(pstmt != null) pstmt.close();
		}
		return result; 
	}

	
	public int getMaxT_restep(int t_ref) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql 	 = "select nvl(max(t_restep), 0) from travel_board where t_ref=? and t_relevel=1";
		
		int result = 0;
		
		try {
			conn 	= getConnection();
			pstmt	= conn.prepareStatement(sql);
			pstmt.setInt(1, t_ref);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				result = rs.getInt(1) + 1;
			}
// 			System.out.println("getMaxNum t_restep====>" + result);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn != null) conn.close();
			if(pstmt != null) pstmt.close();
			if(rs != null) rs.close();
		}
		return result;
	}
	

	public List<Travel> travelIng(int startRow, int endRow) throws SQLException {
		List<Travel> list = new ArrayList<Travel>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql =   "select *"
						+ "  from (select rownum r, a.*, reply_cnt(a.t_ref) reply_cnt, fn_user_img(a.user_id) as user_img"
						+ "          from (select * "
						+ "                  from travel_board where t_Relevel = 0 and t_dealstatus = 0 order by t_ref desc) a)"
						+ " where r between ? and ?";
		
		
		System.out.println("TravelDao  traveList startRow-->"+startRow);
		System.out.println("TravelDao  traveList endRow-->"+endRow);
		System.out.println("TravelDao  traveList sql-->"+sql);

		
		try {
			conn  = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			rs = pstmt.executeQuery();
			while(rs.next()){
				Travel travel = new Travel();
				travel.setT_num			(rs.getInt		("t_num"));
				travel.setUser_id		(rs.getString	("user_id"));
				travel.setT_img			(rs.getString	("t_img"));
				travel.setT_title		(rs.getString	("t_title"));
				travel.setT_content		(rs.getString	("t_content"));
				travel.setT_gubun		(rs.getString	("t_gubun"));
				travel.setT_date		(rs.getString	("t_date"));
				travel.setT_person		(rs.getInt		("t_person"));
				travel.setT_start		(rs.getString	("t_start"));
				travel.setT_end			(rs.getString	("t_end"));
				travel.setT_dealstatus	(rs.getString	("t_dealstatus"));
				travel.setT_ref			(rs.getInt		("t_ref"));
				travel.setT_relevel		(rs.getInt		("t_relevel"));
				travel.setT_restep		(rs.getInt		("t_restep"));
				travel.setReply_cnt		(rs.getInt		("reply_cnt"));
				travel.setUser_img		(rs.getString	("user_img"));
				
				System.out.println("TravelDao  traveList t_title-->"+rs.getString	("t_title"));
				
				
				list.add(travel);
			}
			
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn  != null) conn.close();
			if(pstmt != null) pstmt.close();
			if(rs 	 != null) rs.close();
		}
		return list;
	}

																/* tvl.get */
	public List<Travel> traveListSearch(int startRow, int endRow, Travel tvl) throws SQLException {
// 		System.out.println("=====================TravelDao traveListSearch Start=====================");
		List<Travel> list = new ArrayList<Travel>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String addSql = "";
		
		/* 날짜조회 쿼리 추가 */
		if(tvl.getT_start() != null && tvl.getT_start() != "") {
			addSql += " and t_start >= '" + tvl.getT_start() + "'";
		}
		if(tvl.getT_end() != null && tvl.getT_end() != "") {
			addSql += " and t_end <= '" + tvl.getT_end() + "'";
		}
		
		/* 모집 진행중 쿼리 추가 */
		if(tvl.getT_dealstatus() != null && tvl.getT_dealstatus() != "") {
			addSql += " and t_dealstatus ='" + tvl.getT_dealstatus() + "'";
		}
		
		/* 구분 쿼리 추가 */
		//,'숙박','레저'
		// and t_gubun in ('숙박','레저') >> in 또는
		if(tvl.getT_gubun() != null && tvl.getT_gubun() != "") {
			addSql += " and t_gubun in (" + tvl.getT_gubun().substring(1) + ")";
		}
		
		/* 메인 검색 쿼리 추가 */
		if(tvl.getT_title() != null && tvl.getT_title() != "") {
			addSql += " and t_title like  '%" + tvl.getT_title() + "%'";
		}
		
		
		/* 기본 리스트 조회 */
		String sql =   "select *"
						+ "  from (select rownum r, a.*, reply_cnt(a.t_ref) as reply_cnt, fn_user_img(a.user_id) as user_img"
						+ "          from (select * "
						+ "                  from travel_board"
						+ "				    where t_Relevel = 0 "
						+ addSql
						
						+ " order by t_ref desc) a)"
						+ " where r between ? and ?";
		
// 		System.out.println("TravelDao  traveList startRow-->"+startRow);
// 		System.out.println("TravelDao  traveList endRow-->"+endRow);
// 		System.out.println("TravelDao  traveList sql-->"+sql);

		
		try {
			conn  = getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			rs = pstmt.executeQuery();
			while(rs.next()){
				Travel travel = new Travel();
				travel.setT_num			(rs.getInt		("t_num"));
				travel.setUser_id		(rs.getString	("user_id"));
				travel.setT_img			(rs.getString	("t_img"));
				travel.setT_title		(rs.getString	("t_title"));
				travel.setT_content		(rs.getString	("t_content").replace("<br>","\r\n"));
				travel.setT_gubun		(rs.getString	("t_gubun"));
				travel.setT_date		(rs.getString	("t_date"));
				travel.setT_person		(rs.getInt		("t_person"));
				//날짜 포맷 변경
				travel.setT_start		(rs.getString	("t_start").substring(5));
				travel.setT_end			(rs.getString	("t_end").substring(5));
				
				travel.setT_dealstatus	(rs.getString	("t_dealstatus"));
				travel.setT_ref			(rs.getInt		("t_ref"));
				travel.setT_relevel		(rs.getInt		("t_relevel"));
				travel.setT_restep		(rs.getInt		("t_restep"));
				travel.setReply_cnt		(rs.getInt		("reply_cnt"));
				travel.setUser_img		(rs.getString	("user_img"));
				
				list.add(travel);
			}
			
// 			System.out.println("=====================TravelDao traveListSearch End=====================");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if(conn  != null) conn.close();
			if(pstmt != null) pstmt.close();
			if(rs 	 != null) rs.close();
		}
		return list;
	}
}
