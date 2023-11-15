HMI Editing instructions
==



When we right-click on an HMI node and select "Edit", we can add an HMI(UI) editing tab in the main content area. As shown in the following figure:


<img src="../img/hmi/h001.png" />



The editing UI appears to be quite complex, with only three parts on the left, middle, and right. The vertical bar on the left is the UI Components library (including nodes in the tree, components library, background elements, etc.), with a ruler in the middle being the editing area (including basic draw item toolbox), and the right is the properties/events area. Among them, the properties area on the right can be hidden and used as much as possible for the editing area. If you feel that the entire editing area is not enough, you can also click on the maximize icon in the upper right corner of the tab to make it fill the entire window.


<img src="../img/hmi/h002.png" />

## 1 Basic operations of the editing UI

### 1.1 Stepless zoom



The editing area uses infinite scaling, and can be quickly viewed as a whole and as a part with the mouse. There are three buttons arranged vertically in the upper left corner of the editing area, namely "Adaptive Display", "Zoom In", and "Zoom Out".


<img src="../img/hmi/h003.png" />



Click on the "Adaptive Display" button to fill the entire area with all the content in the editing area. This center point and zoom are the ideal state for the overall display. Clicking on "Zoom In" and "Zoom Out" will cause the image to be zoomed in and out centered around the center point of the editing area.

Move the mouse to a certain position in the editing area, and then rotate the scroll wheel. You will find that the image will be zoomed in and out according to the different directions of your scroll wheel, and the zooming in and out action will be centered around the current mouse position.


### 1.2 Right mouse button(Overall roaming)

Right click the picture without releasing it. When moving, you can roam the picture as a whole.

### 1.3 Left mouse button (select and move)



The left mouse button function mainly involves selecting and moving elements. IOT-Tree stipulates that the movement of elements must be based on the selected elements - that is, if one or more elements are to be moved, they must be selected first.


#### 1.3.1 Select DrawItem



_Single selection of Draw Item_

Move the mouse over a certain element, click and release the left button without any movement, and after releasing, you will find that the element is selected.

If multiple elements overlap at a certain location, do not move the mouse. Press the left mouse button again and release, and you will find that the selected element will switch between the overlapping elements.

_Multiple Selection Draw Items_

If you want to select multiple elements within a area, simply press the left mouse button without releasing it, and then move the mouse. At this point, you will notice a rectangular selection area composed of dashed lines, which continuously changes in size with your mouse position. You just need to cover the dashed selection box with multiple corresponding elements, and then release the left button to select multiple draw items at the same time.

>Note: All selected elements will have a red rectangular border appearing

If you want to cancel the selected items, just left click on the empty area.


#### 1.3.2 Move Selected Items



Unselected draw items are not movable, so after single or multiple selection, you can move the selected items. At this point, the mouse must be above the selected items, press the left button without releasing it, and then move the mouse. You will find that the selected items will follow the mouse movement. After the items reaches the desired position, release the left mouse button to complete the move operation.


#### 1.3.3 Modify the size of the selected item



Some draw items support resizing with the mouse, provided that they are individually selected - the selected item will display a rectangular bounding box. At this point, if the item can be resized, move the mouse over the boundary line, and you will notice that the mouse marker will become adjustable up, down, or left, right arrows. If the mouse is moved over the corner of the edit box, it will become an arrow to move this point (including both directions).


<img src="../img/hmi/h009.png" />



At this point, you can change the size of the draw item by pressing the left mouse button without releasing and moving it.


#### 1.3.4 Rotate Selected Items



Some draw items support rotation, provided that they are individually selected - the selected item will display a rectangular bounding box and an anchor point that supports rotation will appear above the border. Moving the mouse to this position will change to a cross shaped shape. At this point, you can adjust the rotation angle of the item by pressing the left mouse button without releasing it.


<img src="../img/hmi/h010.png" />


### 1.4 Keyboard operation instructions

#### 1.4.1 Copy paste




When selecting an drawitem, use the keyboard combination Ctrl + C to copy the currently selected element.

Then, use the keyboard combination Ctrl + V to paste the copied item at the mouse position in the drawing area. When you move the mouse, the copied element moves with the mouse. You can move the mouse to the appropriate position and click the left mouse button to place it.

If you want to discard the paste, right-click.

IOT tree server supports copy and paste across drawing areas.


#### 1.4.2 Delete operation

Select an drawitem and press del to delete the drawitem


#### 1.4.3 rollback

TODO

## 2 Edit property panel

On the right side of the drawing area is the properties editing area. When the mouse selects an element, the property area displays all the properties of the element. As shown below

<img src="../img/hmi_prop1.png">


### 2.1 Basic properties



All items have basic properties, as shown in the above figure. The basic properties include ID, name, title, coordinate position x, y, stack height Z-index. Locked or not.

The ID is automatically generated by the system, and the name must be unique in the UI editing area and meet the name naming qualification. The coordinate position is the coordinate of the upper left corner of this entity in the drawing area (Cartesian coordinate system).

As the stacking height of items, Z-index is similar to Z-index in CSS. The higher the value, the later the painting order. When an item overlaps with other items, the larger the display effect Z-index is, the more it is displayed on the following items.

If the locked property is set to yes, the item is locked in the drawing area. The mouse cannot drag this. And when the mouse selects multiple items in a rectangular area, the locked items will not be selected. However, you can make a single selection of the locked element by clicking the mouse. At this time, you can also modify or unlock the properties.


These basic properties are part of the commons, please refer to for more details:[Common properties][hmi_props]

[hmi_props]:./hmi_props.md

### 2.2 Base drawitems and properties



Basic primitives include polyline, polygon, circle, ellipse, arc, text, picture, pipe (not implemented), Bezier curve, etc. they all have their own properties and characteristics.

In the upper left corner of the drawing area, there is a basic item toolbar. Click one of the items to select. When the mouse cursor moves to the back area, it will become a cross. Click and move the left mouse button to start drawing the corresponding basic item. When you release the left key, the currently created item is selected by default. You can then make adjustments and attribute modifications

<img src="../img/hmi_basic1.png">

#### 2.2.1 Polyline editing and properties



Click the toolbar <img src="../img/hmi_basic_zln.png">, then left click in the drawing area and move the drawing polyline. When the polyline drawing is completed, click the right mouse button to finish the drawing operation.

When the polyline is selected, the intersection of each line segment can be dragged with the mouse to support the adjustment and modification of the polyline.

In the attribute area, you can modify the line color and line width of the line. As shown below:

<img src="../img/hmi_basic_zln1.png">

#### 2.2.2 Polygon editing and properties



Click the toolbar <img src="../img/hmi_basic_py.png">, then left click in the drawing area and move the drawing polygon. When the polygon drawing is completed, right-click to finish the drawing operation.

When a polygon is selected, the intersection of each line segment can be dragged with the mouse to support the adjustment and modification of the polygon.

In the property area, you can modify the line color and line width of the line.

At the same time, the polygon belongs to the surface graph, and the fill style can be set, as shown in the following figure:

<img src="../img/hmi_basic_py1.png">


#### 2.2.3 Text editing and properties




Click the toolbar <img src= "../img/hmi_basic_txt.png">, and then click place text box in the drawing area.

The text box itself is a rectangular box. When selected, you can use the mouse to adjust the size and other basic operations.

Text attributes include rectangle size, rotation size, text attribute, font and font color. As shown below:


<img src="../img/hmi_basic_txt1.png">

#### 2.2.4 Picture added and properties


Click the toolbar <img src="../img/hmi_basic_img.png">, and then click in the drawing area to draw a rectangular area, which is also the picture display area.

The picture area itself is a rectangular box. When selected, you can use the mouse to adjust the size and other basic operations.

Text attributes include rectangle size, rotation size, and picture path or picture resources. As shown below:

<img src="../img/hmi_basic_img1.png">




For project resources, please refer to [quick understanding of associated resources in IOT tree][qn_res]



## 3 Sub-HMI,UI Components



In the IOT-Tree project tree, the top-level HMI(UI) can refer to the underlying Sub-HMI UI, as well as "HMI(UI) Components" in the "HMI library".


For HMI(UI) Components(Controllers), please refer to[HMI(UI) Components(Controllers)][hmi_comp]

### 3.1 Sub-HMI



In order to illustrate the reference of Sub-HMI in high-level UI nodes, we open the built-in demonstration project [Water tank and Medical Dosing][case_auto] in IOT-Tree. On the device node "ch1/flow:, right-click to add a UI node (right-click to select "New HMI" and fill in the name "fui"). Then, right-click on this node, select "Edit UI", and simply edit the internal content, as shown in the following figure:


<img src="../img/hmi/h004.png">



Now, the "u1" node below the root node can reference this Sub-HMI item. Right click on the "u1" node and select "Edit UI" to open the UI editing tab. When clicking on the button "Context Sub HMI" in the upper left corner, you will find that a selection panel will slide out, which includes the sub item "/ch1/flow/fui".


<img src="../img/hmi/h005.png">


Click on this item with the mouse, drag it to the middle editing area, release the mouse, and you can see that the Sub-HMI item have been added to the UI.

It can be seen that if the device definition and some UI of the device itself are referenced in the project, it can greatly facilitate the editing and configuration of the top-level HMI(UI) of the project.


### 3.2 Using UI Components



Similarly, we will continue with the previous demonstration project u1 editing, click on the "Components" button on the left, and a selection panel will slide out, which contains the content of the "HMI (UI) Components" belong to "HMI library" of our system:


<img src="../img/hmi/h006.png">



On the left side of this panel is a secondary tree - Library/Category. Select a Category under a library, and all components (controls) will be listed on the right. Select the desired item with the mouse, drag it to the editing area, and release it. You will see the UI Component item appear in the editing area.


### 3.3 Using background



Continuing with the previous demonstration project u1 editing, clicking on the "Background" button on the left will bring up a selection panel that includes some background images of the screen:


<img src="../img/hmi/h007.png">



Click to select a background, drag it to the editing area, and release it. You will see the background appear in the editing area. This type of background image is generally used to set a certain scale of the image scene to meet the needs of display devices with different resolutions.


><font color=red>Attention: Please save your editing work in a timely manner. When saving, click the save button above the attribute area on the right</font>
><img src="../img/hmi/h008.png">

The above is the basic UI node editing operation.


If you want to understand how UI draw items are dynamically displayed using contextual data, please continue to review [Properties binding and event handling][bind_evt]

[bind_evt]:./hmi_bind_evt.md
[case_auto]:../case/case_auto.md
[hmi_comp]: ./hmi_comp.md
[qn_res]: ./quick_know_res.md
