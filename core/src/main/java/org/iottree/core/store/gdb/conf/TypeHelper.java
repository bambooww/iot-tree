package org.iottree.core.store.gdb.conf;

import java.sql.*;
import java.util.Date;

import org.iottree.core.store.gdb.GdbException;

public class TypeHelper
{
	public static ParamType DbTypeToParamType(int dbtype)
		throws GdbException
	{
		switch(dbtype)
		{
		case Types.ARRAY:
			throw new GdbException("not support!");
		case Types.BIGINT:
			return ParamType.Int64;
		case Types.BINARY:
			return ParamType.ByteArray;
		case Types.BIT:
			return ParamType.Boolean;
		case Types.BLOB:
			return ParamType.ByteArray;
		case Types.BOOLEAN:
			return ParamType.Boolean;
		case Types.CHAR:
			return ParamType.String;
		case Types.CLOB:
			return ParamType.String;
		case Types.DATALINK:
			throw new GdbException("not support!");
		case Types.DATE:
			return ParamType.DateTime;
		case Types.DECIMAL:
			return ParamType.Decimal;
		case Types.DISTINCT:
			throw new GdbException("not support!");
		case Types.DOUBLE:
			return ParamType.Double;
		case Types.FLOAT:
			return ParamType.Single;
		case Types.INTEGER:
			return ParamType.Int32;
		case Types.JAVA_OBJECT:
			throw new GdbException("not support!");
		case Types.LONGVARBINARY:
			return ParamType.ByteArray;
		case Types.LONGVARCHAR:
			return ParamType.String;
		case Types.NULL:
			return ParamType.Null;
		case Types.NUMERIC:
			return ParamType.Decimal;
		case Types.OTHER:
			throw new GdbException("not support!");
		case Types.REAL:
			return ParamType.Double;
		case Types.REF:
			throw new GdbException("not support!");
		case Types.SMALLINT:
			return ParamType.Int16;
		case Types.STRUCT:
			throw new GdbException("not support!");
		case Types.TIME:
			return ParamType.DateTime;
		case Types.TIMESTAMP:
			return ParamType.DateTime;
		case Types.TINYINT:
			return ParamType.Byte;
		case Types.VARBINARY:
			return ParamType.ByteArray;
		case Types.VARCHAR:
			return ParamType.String;
			default:
				throw new GdbException("没有这种类型的DbType（Not such DbType!)");
		}
	}


    public static ParamType TypeToParamType(Class t)
    	throws GdbException
    {
        if(t==String.class)
        {
            return ParamType.String;
        }
        else if(t==Boolean.class||t==boolean.class)
        {
            return ParamType.Boolean;
        }
        else if(t==Byte.class||t==byte.class)
        {
            return ParamType.Byte;
        }
        else if(t==Short.class||t==short.class)
        {
            return ParamType.Int16;
        }
        else if(t==Integer.class||t==int.class)
        {
            return ParamType.Int32;
        }
        else if(t==Long.class||t==long.class)
        {
            return ParamType.Int64;
        }
        
        else if(t==Float.class||t==float.class)
        {
            return ParamType.Single;
        }
        else if(t==Double.class||t==double.class)
        {
            return ParamType.Double;
        }
//        else if(t==typeof(Decimal))
//        {
//            return ParamType.Decimal;
//        }
//        else if(t==typeof(Guid))
//        {
//            return ParamType.Guid;
//        }
        else if(t==Date.class)
        {
            return ParamType.DateTime;
        }
        else if(t==byte[].class)
        {
            return ParamType.ByteArray;
        }
		
        throw new GdbException("不能转换类型:"+t.getCanonicalName()+" to ParamType") ;
    }

    /// <summary>
    /// 参数类型到SqlServer类型（字符串表示）的转换
    /// </summary>
    /// <param name="t"></param>
    /// <returns></returns>
//    public static String ParamTypeToSqlServerType(ParamType t,int len)
//    {
//        switch(t)
//        {
//            case ParamType.String:
//                if(len<0)
//                    return "ntext";
//
//                if(len==0)
//                    return "nvarchar(4000)" ;
//
//                return "nvarchar("+len+")" ;
//            case ParamType.Boolean:
//                return "bit" ;
//            case ParamType.Byte:
//                return "tinyint" ;
//            case ParamType.SByte://replace by int16
//                return "smallint" ;
//            case ParamType.Int16:
//                return "smallint" ;
//            case ParamType.Int32:
//                return "int" ;
//            case ParamType.Int64:
//                return "bigint" ;
//            case ParamType.UInt16://replace by int32
//                return "int" ;
//            case ParamType.UInt32://replace by int64
//                return "bigint" ;
//            case ParamType.UInt64:
//                return "numeric(21)" ;
//            case ParamType.Single:
//                return "real" ;
//            case ParamType.Double:
//                return "float" ;
//            case ParamType.Decimal:
//                return "numeric(21)" ;
//            case ParamType.Guid:
//                return "uniqueidentifier" ;
//            case ParamType.DateTime:
//                return "datetime" ;
//            case ParamType.ByteArray:
//                return "binary" ;
//        }
//		
//        throw new DataAccessException("不能转换类型:"+t.ToString()+" to SqlServer Type") ;
//    }
}
