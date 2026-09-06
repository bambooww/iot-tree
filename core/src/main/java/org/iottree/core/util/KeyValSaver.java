package org.iottree.core.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.iottree.core.Config;
import org.iottree.core.station.StationLocSaver.Item;

/**
 * 通用的可靠存储器——用来支持一些目录下的信息可靠存储（代替文件系统的写入中断不可靠）
 * 如：在UAPrj中，记录local tag，整体最新标签数据快照等内容，都需要有个可靠高效的能力
 * 
 * @author jason.zhu
 *
 */
public abstract class KeyValSaver
{
	public abstract boolean delByKey(String key) throws Exception;
	/**
	 * 
	 * @param key
	 * @param value null表示删除
	 */
	public abstract void setKeyVal(String key,String value) throws Exception;
	
	public abstract String getValByKey(String key) throws Exception;
}
