package org.iottree.ext.vo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

public class VoRecoChker
{
	private static final String LOG_SEG = "Ignoring word missing in vocabulary:";

	public static void checkErrorWords(String model_path, List<String> words) throws Exception
	{
		if (words == null || words.size() <= 0)
			return;
		File f = new File(model_path) ;
		String fp = f.getCanonicalPath()+"/" ;
		fp = fp.replaceAll("\\\\", "/") ;
		System.out.println("model path="+fp) ;
		long st = System.currentTimeMillis() ;
		LibVosk.setLogLevel(LogLevel.WARNINGS); // 设置日志级别
		Model model = new Model(fp);
		
		long et1 = System.currentTimeMillis() ;
		System.out.println("load model cost="+(et1-st)) ;
		Recognizer reco = null;
		try
		{
			reco = new Recognizer(model, VoReco_NM.SAMPLE_RATE);
			//reco.
			JSONArray jarr = new JSONArray(words);
			System.out.println(jarr.toString()) ;
			reco.setGrammar(jarr.toString());
		}
		finally
		{
			if (reco != null)
				reco.close();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		if(args.length<=0)
			return ;
		String modelpath = args[0] ;
		File wordf = new File("./words.txt") ;
		if(!wordf.exists())
			return ;
		String txt = Convert.readFileTxt(wordf) ;
		List<String> words = Convert.splitStrWith(txt, ", ") ;
		if(words==null||words.size()<=0)
			return ;
		checkErrorWords(modelpath, words) ;
	}
}
