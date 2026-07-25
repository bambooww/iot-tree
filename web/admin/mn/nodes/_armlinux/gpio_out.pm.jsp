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
	org.iottree.ext.armlinux.*,
	org.iottree.core.msgnet.util.*,
	org.iottree.ext.msg_net.*
	"%><%@ taglib uri="wb_tag" prefix="w"%><%
	
%>
<div class="layui-form-item">
    <label class="layui-form-label">GPIO:</label>
    <div class="layui-input-inline" style="width: 200px;">
      <select id="gpio_num">
      	<option value="" > --- </option>
<%
	boolean bfirst = true;
LinkedHashMap<Integer,ArmLinuxConf.GPIOPin> num2pin = ArmLinuxConf.getNum2Pin();
for (ArmLinuxConf.GPIOPin pin : num2pin.values())
{
%><option value="<%=pin.getNum() %>" ><%=pin.getTitle() %> gpio[<%=pin.getNum() %>]</option><%
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
	let gpio_num = $('#gpio_num').val()||"";
	
	let js_ob={} ;
	js_ob.gpio_num = gpio_num ;
	return js_ob ;
}

function set_pm_jo(jo)
{
	$('#gpio_num').val(jo.gpio_num);
}

function get_pm_size()
{
	return {w:600,h:350} ;
}

//on_init_pm_ok() ;
</script>