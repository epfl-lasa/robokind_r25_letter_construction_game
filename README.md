# Robokind r25 Letter Construction Game

### Kinect Tracking
- Install OpenNI 
```
 sudo apt-get install git-core cmake freeglut3-dev pkg-config build-essential libxmu-dev libxi-dev libusb-1.0-0-dev doxygen graphviz mono-complete
``` 
- Execute these commands everytime you start the application:
```
roslaunch openni_launch openni.launch
``` 
```
rosrun rqt_reconfigure rqt_reconfigure
``` 
```
rosrun rviz rviz
``` 
```
 rosrun object_recognition_core detection -c ~/params.yaml
``` 

[Detailes of Tabletop Segmentation and Object Detection library](http://wg-perception.github.io/tabletop/) 
