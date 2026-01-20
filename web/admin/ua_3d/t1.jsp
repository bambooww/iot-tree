<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.core.*,
				org.iottree.core.res.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
				java.net.*"%><%@ taglib uri="wb_tag" prefix="wbt"%><%
	String bkcolor="#eeeeee" ;
%>
<!-- Licensed under a BSD license. See license.html for license -->
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=yes">
    <title>Three.js - Fundamentals</title>
    <style type="text/css">
    html, body {
   margin: 0;
   height: 100%;
}

#c {
   width: 100%;
   height: 100%;
   display: block;
}
    </style>
  </head>
  <body>
    <canvas id="c"></canvas>
  </body>
<script type="importmap">
{
  "imports": {
    "three": "/_js/three/three.module.js",
"three/addons/": "../../examples/jsm/"
  }
}
</script>

<script type="module">
import * as THREE from 'three';




function main()
{
	const canvas = document.querySelector( '#c' );
	const renderer = new THREE.WebGLRenderer( { antialias: true, canvas } );

	const fov = 75;
	const aspect = 2; // the canvas default
	const near = 0.1;
	const far = 5;
	const camera = new THREE.PerspectiveCamera( fov, aspect, near, far );
	camera.position.z = 2;

	const scene = new THREE.Scene();

	const boxWidth = 1;
	const boxHeight = 1;
	const boxDepth = 1;
	const geometry = new THREE.BoxGeometry( boxWidth, boxHeight, boxDepth );

	//const material = new THREE.MeshBasicMaterial( { color: 0x44aa88 } ); // greenish blue
	const material = new THREE.MeshPhongMaterial({color: 0x44aa88});
	const cube = new THREE.Mesh( geometry, material );

	function makeInstance(geometry, color, x) {
	  const material = new THREE.MeshPhongMaterial({color});
	  const cube = new THREE.Mesh(geometry, material);
	  scene.add(cube);
	  cube.position.x = x;
	  return cube;
	}

	const cubes = [
	  makeInstance(geometry, 0x44aa88,  0),
	  makeInstance(geometry, 0x8844aa, -2),
	  makeInstance(geometry, 0xaa8844,  2),
	];

	//scene.add( cube );

	{
		const color = 0xFFFFFF;
		const intensity = 3;
		const light = new THREE.DirectionalLight( color, intensity );
		light.position.set( - 1, 2, 4 );
		scene.add( light );

	}
/*
	function render0( time ) {
		time *= 0.001; // convert time to seconds
		cube.rotation.x = time;
		cube.rotation.y = time;
		renderer.render( scene, camera );
		requestAnimationFrame( render );
	}
*/

function resizeRendererToDisplaySize(renderer) {
  const canvas = renderer.domElement;
  const width = canvas.clientWidth;
  const height = canvas.clientHeight;
  const needResize = canvas.width !== width || canvas.height !== height;
  if (needResize) {
    renderer.setSize(width, height, false);
  }
  return needResize;
}


	function render(time) {
		  time *= 0.001;  // 将时间单位变为秒
		if(resizeRendererToDisplaySize(renderer))
		{
			const canvas = renderer.domElement;
  			camera.aspect = canvas.clientWidth / canvas.clientHeight;
  			camera.updateProjectionMatrix();
		}
		  cubes.forEach((cube, ndx) => {
		    const speed = 1 + ndx * .1;
		    const rot = time * speed;
		    cube.rotation.x = rot;
		    cube.rotation.y = rot;
		  });

		renderer.render( scene, camera );
		requestAnimationFrame(render );
	}
	
	
	requestAnimationFrame(render );
}

main();
</script>
</html>
