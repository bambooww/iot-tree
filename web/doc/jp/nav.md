<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js?v="></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
            <link href="./inc/common.css" rel="stylesheet" type="text/css">
        <link href="./inc/index.css" rel="stylesheet" type="text/css">




























<script>
$("a").each(function(){
    var docp = $(this).attr("doc_path") ;
    if(docp)
    {
        $(this).click(function(){
            parent.nav_to($(this).attr("doc_path"));
        });
    }
});
</script>
