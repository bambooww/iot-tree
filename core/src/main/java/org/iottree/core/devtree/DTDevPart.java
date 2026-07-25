package org.iottree.core.devtree;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;

/**
 * device spare part
 * 
 * @author jason.zhu
 *
 */
public class DTDevPart
{
	/**
	 * using partId
	 */
	String partId = null;

	String partCode = null;

	String parttpUID = null;

	String modelNo = null;

	String barCode = null;

	String title = null;

	String supplier;

	String factory;

	public String getPartId()
	{
		return this.partId;
	}

	/**
	 * may be code for other system
	 * 
	 * @return
	 */
	public String getPartCode()
	{
		return this.partCode;
	}

	public String getPartTpUID()
	{
		return this.parttpUID;
	}

	public DTDevPartTP getPartTp()
	{
		if (Convert.isNullOrEmpty(this.parttpUID))
			return null;
		return DTDevPartManager.getInstance().getPartTPByUID(this.parttpUID);
	}

	public String getModelNo()
	{
		return this.modelNo;
	}

	public String getBarCode()
	{
		return this.barCode;
	}

	public String getTitle()
	{
		return title;
	}

	public String getSupplier()
	{
		return this.supplier;
	}

	public String getFactory()
	{
		return this.factory;
	}

	private static String dbUrl = "jdbc:sqlite:devpart.db";

	/**
	 * 设置数据库连接 URL（例如 "jdbc:sqlite:/path/to/db"）
	 */
	public static void setDbUrl(String url)
	{
		dbUrl = url;
	}

	/**
	 * 获取数据库连接
	 */
	private static Connection getConnection() throws SQLException
	{
		// 确保驱动已加载（大部分环境会自动加载，显式调用确保）
		try
		{
			Class.forName("org.sqlite.JDBC");
		}
		catch ( ClassNotFoundException e)
		{
			throw new RuntimeException("SQLite JDBC driver not found", e);
		}
		return DriverManager.getConnection(dbUrl);
	}

	/**
	 * 自动建表（如果表不存在）
	 */
	public static void createTable()
	{
		String sql = "CREATE TABLE IF NOT EXISTS dt_dev_part (" + "partId TEXT PRIMARY KEY," + "partCode TEXT,"
				+ "parttpUID TEXT," + "modelNo TEXT," + "barCode TEXT," + "title TEXT," + "supplier TEXT,"
				+ "factory TEXT" + ")";
		try (Connection conn = getConnection(); Statement stmt = conn.createStatement())
		{
			stmt.execute(sql);
		}
		catch ( SQLException e)
		{
			throw new RuntimeException("Failed to create table", e);
		}
	}

	/**
	 * 插入一条记录
	 * 
	 * @return 影响行数（1表示成功，0表示失败）
	 */
	public static int insert(DTDevPart part)
	{
		String sql = "INSERT INTO dt_dev_part (partId, partCode, parttpUID, modelNo, barCode, title, supplier, factory) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, part.getPartId());
			pstmt.setString(2, part.getPartCode());
			pstmt.setString(3, part.getPartTpUID());
			pstmt.setString(4, part.getModelNo());
			pstmt.setString(5, part.getBarCode());
			pstmt.setString(6, part.getTitle());
			pstmt.setString(7, part.getSupplier());
			pstmt.setString(8, part.getFactory());
			return pstmt.executeUpdate();
		}
		catch ( SQLException e)
		{
			throw new RuntimeException("Insert failed", e);
		}
	}

	/**
	 * 根据 partId 删除记录
	 * 
	 * @return 影响行数
	 */
	public static int delete(String partId)
	{
		String sql = "DELETE FROM dt_dev_part WHERE partId = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, partId);
			return pstmt.executeUpdate();
		}
		catch ( SQLException e)
		{
			throw new RuntimeException("Delete failed", e);
		}
	}

	/**
	 * 更新记录（根据 partId 更新其他字段）
	 * 
	 * @return 影响行数
	 */
	public static int update(DTDevPart part)
	{
		String sql = "UPDATE dt_dev_part SET partCode=?, parttpUID=?, modelNo=?, barCode=?, title=?, supplier=?, factory=? "
				+ "WHERE partId=?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, part.getPartCode());
			pstmt.setString(2, part.getPartTpUID());
			pstmt.setString(3, part.getModelNo());
			pstmt.setString(4, part.getBarCode());
			pstmt.setString(5, part.getTitle());
			pstmt.setString(6, part.getSupplier());
			pstmt.setString(7, part.getFactory());
			pstmt.setString(8, part.getPartId());
			return pstmt.executeUpdate();
		}
		catch ( SQLException e)
		{
			throw new RuntimeException("Update failed", e);
		}
	}

	/**
	 * 根据 partId 查询单个对象
	 * 
	 * @return 对象，若不存在返回 null
	 */
	public static DTDevPart queryById(String partId)
	{
		String sql = "SELECT * FROM dt_dev_part WHERE partId = ?";
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, partId);
			try (ResultSet rs = pstmt.executeQuery())
			{
				if (rs.next())
				{
					return mapResultSet(rs);
				}
				return null;
			}
		}
		catch ( SQLException e)
		{
			throw new RuntimeException("Query by id failed", e);
		}
	}

	/**
	 * 查询所有记录
	 */
	public static List<DTDevPart> queryAll()
	{
		String sql = "SELECT * FROM dt_dev_part";
		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{
			List<DTDevPart> list = new ArrayList<>();
			while (rs.next())
			{
				list.add(mapResultSet(rs));
			}
			return list;
		}
		catch ( SQLException e)
		{
			throw new RuntimeException("Query all failed", e);
		}
	}

	/**
	 * 分页查询（页码从 1 开始）
	 * 
	 * @param pageNum
	 *            当前页码（≥1）
	 * @param pageSize
	 *            每页大小（>0）
	 * @return 分页结果对象，包含总记录数和当前页数据列表
	 */
	public static PageResult<DTDevPart> queryPage(int pageNum, int pageSize)
	{
		if (pageNum < 1)
			pageNum = 1;
		if (pageSize < 1)
			pageSize = 10;

		// 1. 查询总记录数
		String countSql = "SELECT COUNT(*) FROM dt_dev_part";
		int total = 0;
		try (Connection conn = getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(countSql))
		{
			if (rs.next())
			{
				total = rs.getInt(1);
			}
		}
		catch ( SQLException e)
		{
			throw new RuntimeException("Count failed", e);
		}

		// 2. 查询分页数据
		String dataSql = "SELECT * FROM dt_dev_part LIMIT ? OFFSET ?";
		int offset = (pageNum - 1) * pageSize;
		List<DTDevPart> data = new ArrayList<>();
		try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(dataSql))
		{
			pstmt.setInt(1, pageSize);
			pstmt.setInt(2, offset);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					data.add(mapResultSet(rs));
				}
			}
		}
		catch ( SQLException e)
		{
			throw new RuntimeException("Query page failed", e);
		}

		return new PageResult<>(total, data);
	}

	/**
	 * 将 ResultSet 当前行映射为 DTDevPart 对象
	 */
	private static DTDevPart mapResultSet(ResultSet rs) throws SQLException
	{
		DTDevPart r = new DTDevPart();
		r.partId = rs.getString("partId");
		r.partCode = rs.getString("partCode");
		r.parttpUID = rs.getString("parttpUID");
		r.modelNo = rs.getString("modelNo");
		r.barCode = rs.getString("barCode");
		r.title = rs.getString("title");
		r.supplier = rs.getString("supplier");
		r.factory = rs.getString("factory");
		return r;
	}

	// ==================== 分页结果内部类 ====================
	public static class PageResult<T>
	{
		private final int total;
		private final List<T> list;

		public PageResult(int total, List<T> list)
		{
			this.total = total;
			this.list = list;
		}

		public int getTotal()
		{
			return total;
		}

		public List<T> getList()
		{
			return list;
		}
	}
}
