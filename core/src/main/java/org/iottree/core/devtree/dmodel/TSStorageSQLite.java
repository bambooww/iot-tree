package org.iottree.core.devtree.dmodel;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;

public class TSStorageSQLite implements Closeable
{
	private String name ;
	
	private Connection conn = null;

	/**
	 
	 * @param name
	 */
	public TSStorageSQLite(String name) throws SQLException,IOException
	{
		StringBuilder failedr = new StringBuilder() ;
		if(!Convert.checkVarName(name, failedr))
			throw new IllegalArgumentException(failedr.toString()) ;
		this.name = name ;
		
		File dbf = new File(Config.getDataDirBase()+"/devtree/parts/"+name+".db") ;
		if(!dbf.getParentFile().exists())
			dbf.getParentFile().mkdirs() ;
		
		try
		{
			Class.forName("org.sqlite.JDBC");
		}
		catch ( ClassNotFoundException e)
		{
			throw new SQLException("SQLite JDBC Driver not found", e);
		}
		
		conn = DriverManager.getConnection("jdbc:sqlite:" + dbf.getCanonicalPath());
		// 3. enable WAL mode (Optional, improve concurrency performance)
		try (Statement stmt = conn.createStatement())
		{
			stmt.execute("PRAGMA journal_mode=WAL");
		}
		
		initTables();
	}

	/**
	 *
	 */
	private void initTables() throws SQLException
	{
		String createMain = "CREATE TABLE IF NOT EXISTS t_main (" + "  ts INTEGER PRIMARY KEY, " + // ts ms
				"  text TEXT NOT NULL" + ") WITHOUT ROWID"; // 

		String createIdxUser = "CREATE TABLE IF NOT EXISTS t_idx_user (" + "  ts INTEGER PRIMARY KEY, "
				+ "  user_id TEXT NOT NULL" + ") WITHOUT ROWID";

		String createIdxUserIndex = "CREATE INDEX IF NOT EXISTS idx_user_id ON t_idx_user(user_id)";

		try (Statement stmt = conn.createStatement())
		{
			stmt.execute(createMain);
			stmt.execute(createIdxUser);
			stmt.execute(createIdxUserIndex);
		}
	}

	/**
	 * 插入一条完整数据（基础文本 + 索引字段） 使用事务保证原子性
	 * 
	 * @param ts
	 *            时间戳（毫秒，必须唯一）
	 * @param text
	 *            大文本内容
	 * @param userId
	 *            从文本中提取的用户ID（若业务无此维度可传 null，但建议建表时设为 NOT NULL）
	 */
	public void insertData(long ts, String text, String userId) throws SQLException
	{
		String sqlMain = "INSERT INTO t_main (ts, text) VALUES (?, ?)";
		String sqlIdxUser = "INSERT INTO t_idx_user (ts, user_id) VALUES (?, ?)";

		// 开启事务
		conn.setAutoCommit(false);
		try (PreparedStatement pstmtMain = conn.prepareStatement(sqlMain);
				PreparedStatement pstmtUser = conn.prepareStatement(sqlIdxUser))
		{

			// 插入基础表
			pstmtMain.setLong(1, ts);
			pstmtMain.setString(2, text);
			pstmtMain.executeUpdate();

			// 插入索引表（若 userId 不为空）
			if (userId != null && !userId.isEmpty())
			{
				pstmtUser.setLong(1, ts);
				pstmtUser.setString(2, userId);
				pstmtUser.executeUpdate();
			}

			conn.commit(); // 提交事务
		}
		catch ( SQLException e)
		{
			conn.rollback(); // 出错回滚
			throw e;
		}
		finally
		{
			conn.setAutoCommit(true); // 恢复自动提交模式
		}
	}

	/**
	 * 根据用户 ID 查询所有匹配的文本（按时间升序）
	 * 
	 * @param userId
	 *            用户ID
	 * @return 文本列表
	 */
	public List<String> queryTextByUserId(String userId) throws SQLException
	{
		String sql = "SELECT m.text FROM t_main m " + "INNER JOIN t_idx_user u ON m.ts = u.ts " + "WHERE u.user_id = ? "
				+ "ORDER BY m.ts ASC";
		List<String> results = new ArrayList<>();
		try (PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, userId);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					results.add(rs.getString("text"));
				}
			}
		}
		return results;
	}

	/**
	 * 删除早于指定时间戳的所有数据（基础表和索引表级联删除）
	 * 
	 * @param beforeTs
	 *            时间戳界限（不含）
	 */
	public void deleteOldData(long beforeTs) throws SQLException
	{
		// 由于基础表和索引表通过 ts 关联，只需删除基础表，利用外键级联删除索引表？
		// 但 SQLite 默认不开启外键，且我们没定义外键，所以需要手动删除两张表。
		// 这里使用事务保证原子性
		String sqlMain = "DELETE FROM t_main WHERE ts < ?";
		String sqlIdxUser = "DELETE FROM t_idx_user WHERE ts < ?";

		conn.setAutoCommit(false);
		try (PreparedStatement pstmtMain = conn.prepareStatement(sqlMain);
				PreparedStatement pstmtUser = conn.prepareStatement(sqlIdxUser))
		{

			pstmtUser.setLong(1, beforeTs);
			pstmtUser.executeUpdate();

			pstmtMain.setLong(1, beforeTs);
			pstmtMain.executeUpdate();

			conn.commit();
		}
		catch ( SQLException e)
		{
			conn.rollback();
			throw e;
		}
		finally
		{
			conn.setAutoCommit(true);
		}
	}

	/**
	 * 关闭数据库连接
	 */
	public void close()
	{
		try
		{
			if (conn != null && !conn.isClosed())
			{
				conn.close();
			}
		}
		catch ( SQLException e)
		{
			e.printStackTrace();
		}
	}

	// ========== main 演示 ==========
//	public static void main(String[] args)
//	{
//		// 数据库文件路径
//		String dbPath = "test.db";
//		try (TSStorageSQLite store = new TSStorageSQLite(dbPath))
//		{ // 实现 AutoCloseable 需要手动添加 close 方法
//			// 插入几条测试数据
//			long now = System.currentTimeMillis();
//			store.insertData(now, "用户A的日志内容：登录成功", "userA");
//			store.insertData(now + 1000, "用户B的日志内容：操作失败", "userB");
//			store.insertData(now + 2000, "用户A的日志内容：退出系统", "userA");
//
//			// 查询用户A的所有文本
//			List<String> texts = store.queryTextByUserId("userA");
//			System.out.println("用户A的文本记录：");
//			for (String txt : texts)
//			{
//				System.out.println("  " + txt);
//			}
//
//			// 删除 5 秒前的数据（模拟定时清理）
//			long fiveSecondsAgo = System.currentTimeMillis() - 5000;
//			store.deleteOldData(fiveSecondsAgo);
//			System.out.println("已删除 " + fiveSecondsAgo + " 之前的数据");
//
//			// 再次查询用户A（可能已被删除）
//			texts = store.queryTextByUserId("userA");
//			System.out.println("剩余用户A文本数：" + texts.size());
//
//		}
//		catch ( SQLException e)
//		{
//			e.printStackTrace();
//		}
//	}

}
