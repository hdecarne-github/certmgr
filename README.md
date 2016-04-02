# CertMgr
The Certificate Manager application (CertMgr) supports creation and management of X.509 certificates and corresponding objects.

## Installation & usage:
A Java SE 8 Runtime Environment (JRE) is required to run CertMgr.

Simply extract the downloaded archive to a folder of your choice.
The archive contains a single executable Jar as well as a folder with the licence information.
Invoke the application by typing java -jar <certmgr.jar> < command line> in a terminal or
use the corresponding menu of your desktop environment.

The application command line is quite simple:

certmgr.jar [--verbose|--debug] [store home]

--verbose
	Enable verbose logging.
--debug
	Enable debug logging.
	
store home
	The store home path to open.

## Changelog:
* 2016-04-02
 * Now hosted on GitHub
* 2016-01-08
 * Help files updated.
* 2015-12-29
 * Support added for controlling the extensions critical flags.
 * Made (Extended)KeyUsage option editing more consistent.
* 2015-11-22
 * Help added (HowTo page still in progress)
* 2015-10-13
 * Password changing function added.
 * CSR detail view corrected.
*2015-10-12
 * Support for re-signing added.
 * Handling of CSR extension data fixed.
*2015-07-06
 * Initial version ready for testing.
