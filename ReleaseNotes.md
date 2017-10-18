# v0.2 (10/20/2017)
# Release Notes

## Notable Changes
The Barcelona Release (v 0.2) of the Support Scheduler micro service includes the following:
* Application of Google Style Guidelines to the code base
* Increase in unit/intergration tests from 27 tests to 115 tests
* POM changes for appropriate repository information for distribution/repos management, checkstyle plugins, etc.
* Removed all references to unfinished DeviceManager work as part of Dell Fuse
* Added Dockerfile for creation of micro service targeted for ARM64 
* Added interfaces for all Controller classes

## Bug Fixes
* Removed OS specific file path for logging file 
* Provide option to include stack trace in log outputs

## Pull Request/Commit Details

 - [#13](https://github.com/edgexfoundry/support-scheduler/pull/13) - Remove staging plugin contributed by Jeremy Phelps ([JPWKU](https://github.com/JPWKU))
 - [#12](https://github.com/edgexfoundry/support-scheduler/pull/12) - Fixes Maven artifact dependency path contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#11](https://github.com/edgexfoundry/support-scheduler/pull/11) - added staging and snapshots repos to pom along with nexus staging mav… contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#10](https://github.com/edgexfoundry/support-scheduler/pull/10) - Removed device manager url refs in properties files contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#9](https://github.com/edgexfoundry/support-scheduler/pull/9) - Google styles applied, checkstyles and nexus repos added to pom.xml, … contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#8](https://github.com/edgexfoundry/support-scheduler/pull/8) - Add aarch64 docker file contributed by ([feclare](https://github.com/feclare))
 - [#7](https://github.com/edgexfoundry/support-scheduler/pull/7) - Adds Docker build capability contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#6](https://github.com/edgexfoundry/support-scheduler/pull/6) - Fix compilation issue and enable tests. contributed by ([feclare](https://github.com/feclare))
 - [#5](https://github.com/edgexfoundry/support-scheduler/pull/5) - Fixes Log File Path contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#4](https://github.com/edgexfoundry/support-scheduler/issues/4) - Log File Path not Platform agnostic
 - [#3](https://github.com/edgexfoundry/support-scheduler/pull/3) - Add distributionManagement for artifact storage contributed by Andrew Grimberg ([tykeal](https://github.com/tykeal))
 - [#2](https://github.com/edgexfoundry/support-scheduler/pull/2) - Fixes default schedule provisioning for docker contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#1](https://github.com/edgexfoundry/support-scheduler/pull/1) - Contributed Project Fuse source code contributed by Tyler Cox ([trcox](https://github.com/trcox))
