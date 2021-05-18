# 通过http json数据接口-脚本转换例子
## 1 例子相关内容
### 1.1 http url 和返回json格式
### 1.2 转换成device-tag树形结构
<img src="./img/p1.png" >

## 2 转换脚本

```javascript
//根据group ，pt输入，输出对应设备和内部tag
var dev_prefix={
   "qc_p1_":{prefix:"qc_p1_",fullid:"qc.p1",title:"1号泵"},
   "qc_p2_":{prefix:"qc_p2_",fullid:"qc.p2",title:"2号泵"},
   "qc_qxc":{prefix:"qc_qxc",fullid:"qc.qxc",title:"清水出水位"},
   "qc_ph_":{prefix:"qc_",fullid:"qc.sz",title:"出厂水质"},
   "qc_zd_":{prefix:"qc_",fullid:"qc.sz",title:"出厂水质"},
   "qc_yl_":{prefix:"qc_",fullid:"qc.sz",title:"出厂水质"},
   "gw_":{prefix:"",fullid:"gw.p",title:"管网压力"},
}
function get_map_dev(n)
{
  for(var prefix in dev_prefix)
  {
    if(n.indexOf(prefix)==0)
    {
        return dev_prefix[prefix] ;
    }
  }
  return null ;
}

_output.setNode("qc","水厂");
_output.setNode("gw","管网");
var len=_input.length;
for(var i =0 ;i<len ; i ++)
{
  var ob = _input[i];
  var n = ob.n ;
  var dev = get_map_dev(n) ;
  if(dev==null)
     continue ;
//_debug.println(dev.fullid+"    "+dev.title);
  _output.setNode(dev.fullid,dev.title) ;

  var tagid = n.substring(dev.prefix.length) ;

//_debug.println(dev.fullid+"."+tagid+"    "+ob.pt);
  _output.setNode(dev.fullid+"."+tagid,ob.pt) ;

  var g = ob.g ;
  var p = ob.p ;

  var bvalid=('good'==ob.q);
  var dt = new Date(ob.dt).getTime();
  
  _output.setTagVal(dev.fullid+"."+tagid,ob.v,bvalid,dt) ;
}
```


## 3 运行结果

