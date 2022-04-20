package org.iottree.driver.opc.opcua;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

import java.util.UUID;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.XmlElement;
import org.iottree.core.UAVal;

import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;

import kotlin.NotImplementedError;

public class UaHelper
{
	public static NodeId transValTp2UaTp(UAVal.ValTP vt)
	{
		switch(vt)
		{
		case vt_bool:
			return Identifiers.Boolean;
		case vt_byte:
			return Identifiers.SByte;
		case vt_char:
			return Identifiers.Int16;
		case vt_int16:
			return Identifiers.Int16;
		case vt_int32:
			return Identifiers.Int32;
		case vt_int64:
			return Identifiers.Int64;
		case vt_float:
			return Identifiers.Float;
		case vt_double:
			return Identifiers.Double;
		case vt_str:
			return Identifiers.String;
		case vt_date:
			return Identifiers.DateTime;
		case vt_uint16:
			return Identifiers.UInt16;
		case vt_uint32:
			return Identifiers.UInt32;
		case vt_uint64:
			return Identifiers.UInt64;
			
		default:
			return null ;
			//throw new NotImplementedError();
			
		}
		
	}
}
