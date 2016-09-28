# psw-robotics
Physical Semantic Web (PSW) framework applied to robotics systems

## Development

The proposed prototypical testbed implementing semantic-based robots consists of three basic components:

* Eddystone BLE Beacons: PSW beacons can be exposed by off-the-shelf devices or using the [psw-node-eddystone-beacon](<https://github.com/sisinflab-swot/psw-node-eddystone-beacon>) project;
* Robot Platform: robots were implemented using [ROS](<http://www.ros.org/>) (Robot Operating System) Indigo running on a [UDOO Quad](<http://www.udoo.org/>) board equipped with [UDOObuntu 2.0 Minimal Edition](<http://www.udoo.org/udoobuntu-2-minimal-edition/>) OS. Robot software includes: 
  * [psw-robotics](./psw-robotics): a Java-based runtime to manage all robot tasks;
  * [psw-ble-libs](./psw-ble-libs): Java library implementing low-level data structure for PSW BLE beacons;
  * [psw-node-eddystone-beacon-scanner](<https://github.com/sisinflab-swot/psw-node-eddystone-beacon-scanner>): used to scan Eddystone PSW beacons;
* Simulation Environment: robots and the reference environment were simulated exploiting [Gazebo](<http://gazebosim.org/>).

## License

_psw-robotics_ and _psw-ble-libs_ modules are distributed under the [Apache License, Version 2.0](./LICENSE).
