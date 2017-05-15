# psw-robotics
Physical Semantic Web (PSW) framework applied to robotics systems

## Development

The proposed prototypical testbed implementing semantic-based robots consists of three basic components:

* Eddystone BLE Beacons: PSW beacons can be exposed by off-the-shelf devices or using the [psw-node-eddystone-beacon](<https://github.com/sisinflab-swot/psw-node-eddystone-beacon>) project;
* Robot Platform: robots were implemented using [ROS](<http://www.ros.org/>) (Robot Operating System) Indigo running on a [UDOO Quad](<http://www.udoo.org/>) board equipped with [UDOObuntu 2.0 Minimal Edition](<http://www.udoo.org/udoobuntu-2-minimal-edition/>) OS. Robot software includes: 
  * [psw-node-eddystone-beacon-scanner](<https://github.com/sisinflab-swot/psw-node-eddystone-beacon-scanner>): used to discover Eddystone PSW beacons;   
  * [psw-ble-libs](./psw-ble-libs): a Java wrapper mapping the PSW BLE beacons retrieved through Node.js to the data sctructures provided by the [Physical Semantic Web Collection library](https://github.com/sisinflab-swot/physical-semantic-web/tree/phy-sem-web/java/libs);
  * [psw-robotics](./psw-robotics): a Java-based runtime to manage all robot tasks;
* Simulation Environment: robot movements and the reference environment were simulated exploiting [Gazebo](<http://gazebosim.org/>).

## License

_psw-robotics_ and _psw-ble-libs_ modules are distributed under the [Apache License, Version 2.0](./LICENSE).


References
-------------

If you want to refer to the _PSW framework applied to robotics systems_ in a publication, please cite the following paper:

```
@InProceedings{psw-robotics,
  author       = {Michele Ruta and Floriano Scioscia and Saverio Ieva and Giuseppe Loseto and Filippo Gramegna and Agnese Pinto and Eugenio {Di Sciascio}},
  title        = {Knowledge discovery and sharing in the IoT: the Physical Semantic Web vision},
  booktitle    = {32nd ACM SIGAPP Symposium On Applied Computing},
  month        = {apr},
  year         = {2017},
  note         = {to appear}
}
```
