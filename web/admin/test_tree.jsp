<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>界面风格</title>
</head>

<body >
<div id="metadata-box">
  <div id="left">左边的div</div>
  <div id="middle"></div>
  <div id="right">右边的div</div>
</div>
<script>
function DragDiv
{
	this.leftBoxWidth = 360
	
    this.drag_div=function()
    {
      // debugger
      var resizeBtn = document.getElementById('resize-btn')
      var left = document.getElementById('left')
      var middle = document.getElementById('middle')
      var right = document.getElementById('right')
      var box = document.getElementById('metadata-box')

      left.style.width = this.leftBoxWidth * 1 + 'px'
      right.style.width = (box.clientWidth - this.leftBoxWidth - 24) + 'px'

      resizeBtn.onmousedown = (resizeEvent) => {
        var startX = resizeEvent.clientX
        middle.left = middle.offsetLeft
        document.onmousemove = (documentEvent) => {
          var endX = documentEvent.clientX
          let moveLen = middle.left + (endX - startX)
          var maxT = box.clientWidth - middle.offsetWidth
          if (moveLen < 200) moveLen = 200
          if (moveLen > maxT - 500) moveLen = maxT - 500
          left.style.width = moveLen + 'px'
          right.style.width = (box.clientWidth - moveLen - 24) + 'px'
        }
        document.onmouseup = () => {
          document.onmousemove = null
          document.onmouseup = null
          setItem('metaDataLeftBoxWidth', left.clientWidth)
          if (resizeBtn.releaseCapture) resizeBtn.releaseCapture()
        }
        if (resizeBtn.setCapture) resizeBtn.setCapture()
        return false
      }
    } //end of drag_div
  
}

var dd = new DragDiv() ;
dd.drag_div() ;
 </script>
</body>
</html>