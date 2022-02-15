















<img src="../img/case_auto1.png"/>
























<img src="../img/case_auto_simimp.png"/>









<img src="../img/case_auto2.png">





```
watertank
Water tank and Medicament dosing
```









<img src="../img/case_auto3.png">









```
Name = ch1
Title = channel1
Driver = Modbus RTU
```

<img src="../img/case_auto4.png">





```
Name = dio
```
<img src="../img/case_auto5.png">





<img src="../img/case_auto6.png">









<img src="../img/case_auto6.1.png">





```
Name = pstart
Title = pump start do0
Data type = bool
R/W = Read/Write
Address = 000001
```



<img src="../img/case_auto7.png">



```
Name = pstop
Title = pump stop do1
Data type = bool
R/W = Read/Write
Address = 000002
```
```
Name = p_running
Title = pump running state di0
Data type = bool
R/W = Read Only
Address = 100001
```


<img src="../img/case_auto8.png">





```
Name = valve_da0
Title = valve_da0
Data type = int16
R/W = Read/Write
Address = 400001
```
```
Name = wl_ain0
Title = wl_ain0
Data type = int16
R/W = Read Only
Address = 300001
```



<img src="../img/case_auto9.png">



<img src="../img/case_auto10.png">




<img src="../img/case_auto11.png">




```
Name = flow_h
Title = speed high
Data type = int16
R/W = Read
Address = 404113
```
```
Name = flow_l
Title = speed low
Data type = int16
R/W = Read Only
Address = 404113
```



<img src="../img/case_auto12.png">




<img src="../img/case_auto13.png">




```
Name = LEVEL_H
Title = hight water level
Data type = float
R/W = Read/Write
Local=true
DefaultVal=4.5
Auto Save=True
```


<img src="../img/case_auto13.1.png">



```
Name = LEVEL_L
Title = low water level
Data type = float
R/W = Read/Write
Local=true
DefaultVal=1.0
Auto Save=True
```






<img src="../img/case_auto14.png">




<img src="../img/case_auto15.png">




<img src="../img/case_auto16.png">






<img src="../img/case_auto17.png">




<img src="../img/case_auto18.png">




<img src="../img/case_auto19.png">





<img src="../img/case_auto20.png">



<img src="../img/case_auto21.png">



<img src="../img/case_auto22.png">




<img src="../img/case_auto23.png">




<img src="../img/case_auto23.1.png">




```
Level Total Height=5
Liquid Color=#219fb8
```


<img src="../img/case_auto24.png">




<img src="../img/case_auto25.png">




<img src="../img/case_auto26.png">







<img src="../img/case_auto27.png">



<img src="../img/case_auto28.png">







<img src="../img/case_auto29.png">



<img src="../img/case_auto30.png">









<img src="../img/case_auto31.png">




Client JS
```
$event.fire_to_server();
$util.msg("start cmd issued") ;
```

Server JS
```
ch1.dio.pstart._pv=1;
```

<img src="../img/case_auto32.png">




Client JS
```
$event.fire_to_server();
$util.msg("stop cmd issued") ;
```

Server JS
```
ch1.dio.pstop._pv=1; 
```






<img src="../img/case_auto33.png">



<img src="../img/case_auto34.png">



Client JS
```
 var v = valve1.open_v; //$this.open_v;
 $util.dlg_input_num({tp:'slide',min:0,max:100,val:v},(val)=>{
      $event.fire_to_server(val);
      $util.msg("valve cmd issued") ;
 }) ;
```

Server JS
```
var val = parseInt($input);
if(val<0||val>100)
   return "invalid input value";
ch1.aio.valve_val._pv=val;
```



<img src="../img/case_auto35.png">






<img src="../img/case_auto36.png">



Client JS
```
 var v = $this.txt;
 $util.dlg_input_num({is_float:true,val:v},(val)=>{
      $event.fire_to_server(val);
      $util.msg("chang high level issued") ;
 }) ;
```

Server JS
```
var v = parseFloat($input);
if(isNaN(v))
  return ;
LEVEL_H._pv=v;
```



Client JS
```
 var v = $this.txt;
 $util.dlg_input_num({is_float:true,val:v},(val)=>{
      $event.fire_to_server(val);
      $util.msg("chang low level issued") ;
 }) ;
```

Server JS
```
var v = parseFloat($input);
if(isNaN(v))
  return ;
LEVEL_L._pv=v;
```



<img src="../img/case_auto37.png">



<img src="../img/case_auto38.png">







<img src="../img/case_auto39.png">




<img src="../img/case_auto40.png">



<img src="../img/case_auto40.1.png">





```
//def support func or var
var ob_pump_running = ch1.dio.p_running ;
var ob_water_level = ch1.aio.wl_val;
var ob_flow_speed = ch1.flow.flow_val;
var ob_valve_open = ch1.aio.valve_val;

function pump_start()
{
    ch1.dio.pstart._pv=1;
}
function pump_stop()
{
    ch1.dio.pstop._pv=1;
}
function cal_speed2valve(spd)
{
    if(spd<=0)
      return 0 ;
    var r = spd*5 ;
    if(r>=100)
       return 100 ;
    return r;
}

//pump ctrl by water level
function pump_ctrl()
{
   var wl = ob_water_level._pv;
   if(ob_pump_running._pv)
   {//pump is running
        if(wl>=LEVEL_H._pv)
           pump_stop();
   }
   else
   {
        if(wl<=LEVEL_L._pv)
           pump_start();
   }
}

//valve ctrl by flow speed
function valve_ctrl()
{
    var spd = ob_flow_speed._pv;
    var v_open = cal_speed2valve(spd);
    ob_valve_open._pv=v_open;
}
```




```
pump_ctrl();
valve_ctrl();
```




<img src="../img/case_auto41.png">



