<%@ page contentType="text/html;charset=UTF-8"%><%@ page import="java.util.*,
				 java.io.*,
				 java.net.*,
				 org.iottree.core.util.*,
				 org.iottree.core.util.web.*,
				 org.iottree.core.*,
				 org.iottree.core.util.xmldata.*,
				 org.apache.commons.fileupload.*,
org.apache.commons.fileupload.servlet.*,
org.apache.commons.fileupload.disk.*"%><%!
		 private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
		 private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
		 private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
		 
		 public static HashMap<String,String> getReqParams(List<FileItem> fitems) throws Exception
		 {
		 	HashMap<String,String> ret = new HashMap<>();
		 	if(fitems==null||fitems.size()<=0)
		 		return ret;
		 	for(FileItem fi:fitems)
		 	{
		 		if (!fi.isFormField())
		 			continue;
		 		
		        	String fn = fi.getFieldName();
		        	String v = fi.getString("UTF-8") ;
		         ret.put(fn,v);
		 	}
		 	return ret;
		 }
%><%

if (!ServletFileUpload.isMultipartContent(request)) {
    // 如果不是则停止
    PrintWriter writer = response.getWriter();
    writer.println("Error: form must has enctype=multipart/form-data");
    writer.flush();
    return;
}

DiskFileItemFactory factory = new DiskFileItemFactory();

factory.setSizeThreshold(MEMORY_THRESHOLD);
// 

factory.setRepository(new File(Config.getDataTmpDir()));

ServletFileUpload upload = new ServletFileUpload(factory);
 

upload.setFileSizeMax(MAX_FILE_SIZE);
 

upload.setSizeMax(MAX_REQUEST_SIZE);


upload.setHeaderEncoding("UTF-8"); 

File dirb = null ;

    List<FileItem> formItems = upload.parseRequest(request);
    HashMap<String,String> pms = getReqParams(formItems);
    //String cxtid = pms.get("cxtid") ;
    //String name = pms.get("name") ;
    
    
    
    FileItem fi = null ;
    
    if (formItems != null && formItems.size() > 0)
    {
        for (FileItem item : formItems)
        {
            if (item.isFormField())
            	continue;

           	long flen = item.getSize();
           	if(flen<=0)
           		continue;
           	String tmpn = item.getName();
           	int k = tmpn.indexOf('.');
           	if(k<=0)
           		continue ;
           	fi = item ;
           	break;
        }
    }
    
    if(fi==null)
    {
    	out.print("no file upload");
    	return ;
    }
    String fn = UUID.randomUUID().toString() ;
    fi.write(new File(Config.getDataTmpDir(),fn)) ;
   
%>succ=<%=fn%>