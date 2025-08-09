<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,
	java.io.*,org.json.*,
	org.iottree.core.*,
	org.iottree.core.basic.*,
	org.iottree.core.task.*,
	org.iottree.core.util.*,
	org.iottree.core.dict.*,
	org.iottree.core.comp.*,
	org.iottree.core.store.*,
	org.iottree.core.msgnet.*,
	org.iottree.ext.vo.*, javax.sound.sampled.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<div class="layui-form-item">
    <label class="layui-form-label">Input Line:</label>
    <div class="layui-input-inline" style="width: 400px;">
      <select id="mixer_n">
      	<option value="" > --- </option>
<%
	boolean bfirst = true;
Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
for (Mixer.Info info : mixerInfos)
{
	String mix_n = info.getName();
	
	String vd = info.getVendor();
		String chked = bfirst?"selected":"" ;
		bfirst=false;
		
		Mixer mixer = AudioSystem.getMixer(info);
        // 检查是否支持目标数据线（TargetDataLine）即输入设备（麦克风）
        Line.Info targetLineInfo = new Line.Info(TargetDataLine.class);
        boolean isInput = mixer.isLineSupported(targetLineInfo);
        if(!isInput)
        	continue ;
        
        try {
        	TargetDataLine tdl = (TargetDataLine)AudioSystem.getLine(targetLineInfo) ;
        	/*
            Line line = mixer.getLine(targetLineInfo);
            if (line instanceof DataLine) {
                AudioFormat[] formats = ((DataLine.Info) line.getLineInfo()).getFormats();
                System.out.println("ff: " + formats.length + " ");
            }
            */
        } catch (Exception e) {
            System.out.println(e.getMessage());
            continue ;
        }
        
%><option value="<%=mix_n %>" <%=chked %>><%=mix_n %></option><%
	}
%>
      </select>
    </div>
  </div>

<script>


function on_after_pm_show(form)
{
	//update_ui();
}


function get_pm_jo()
{
	let mixer_n = $('#mixer_n').val()||"";
	
	let js_ob={} ;
	js_ob.mixer_n = mixer_n ;
	return js_ob ;
}

function set_pm_jo(jo)
{
	$('#mixer_n').val(jo.mixer_n);
}

function get_pm_size()
{
	return {w:600,h:350} ;
}

//on_init_pm_ok() ;
</script>