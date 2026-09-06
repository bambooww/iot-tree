package org.iottree.core.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.json.JSONObject;

public class KeyValSaverFile extends KeyValSaver
{
	JSONObject kvJO = null;
	File saveF = null;

	public KeyValSaverFile(File f) throws IOException
	{
		this.saveF = f;
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();

		String txt = Convert.readFileTxt(f);
		if (Convert.isNotNullEmpty(txt))
			kvJO = new JSONObject(txt);
		else
			kvJO = new JSONObject();
	}

	private static void atomicFileWrite(File f, String data) throws IOException
	{
		Path tarp = Path.of(f.getCanonicalPath()) ;
		Path tempPath = tarp.resolveSibling(tarp.getFileName() + ".tmp");
		try
		{
			Files.write(tempPath, data.getBytes("utf-8"));
			// automic file replace
			Files.move(tempPath, tarp, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
		}
		catch ( IOException e)
		{
			Files.deleteIfExists(tempPath);
			throw e;
		}
	}
	
	private void save() throws IOException
	{
		atomicFileWrite(this.saveF, kvJO.toString()) ;
	}

	@Override
	public boolean delByKey(String key) throws Exception
	{
		if (kvJO.remove(key) == null)
			return false;
		//Convert.writeFileJO(this.saveF, kvJO);
		save() ;
		return true;
	}

	@Override
	public void setKeyVal(String key, String val) throws Exception
	{
		kvJO.putOpt(key, val);
		//Convert.writeFileJO(this.saveF, kvJO);
		save() ;
	}

	@Override
	public String getValByKey(String key)// throws Exception
	{
		return kvJO.optString(key);
	}

}
