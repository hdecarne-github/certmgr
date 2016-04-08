## CertMgr
Create and manage X.509 certificates.

![store view](certmgr.png)

### About CertMgr
The Certificate Manager application (CertMgr) supports the creation and management of X.509 certificates and their corresponding objects.

Certificates are organized in so called Certificate store. Such a store is actually a simple directory structure containing the individual certificate files.

The application supports the following certificate operations:
 * Creation and management of **private certificates** (signed by your own Certificate Authority)
 * Creation and management of **public certificates** (signed by an external Certificate Authority)
 * Creation and management of **Certificate Revocation Lists** (CRL)
 * **Import and export** of certificates (in PKCS#12 as well as PEM format)

### Installation & usage:
A Java SE 8 Runtime Environment (JRE) is required to run CertMgr.

Download the latest version from the project's [releases page](https://github.com/hdecarne/certmgr/releases/latest).

Simply extract the downloaded archive to a folder of your choice.
The archive contains a single executable Jar as well as a folder with the license information. Invoke the application by either double clicking the jar or invoke the command

```
java -jar <certmgr.jar> [command line arguments]
```

in a terminal. The application command line supports the following options:

```
certmgr.jar [--verbose|--debug] [store home]

--verbose
	Enable verbose logging.
--debug
	Enable debug logging.

store home
	The store home path to open.
```

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
* 2015-10-12
 * Support for re-signing added.
 * Handling of CSR extension data fixed.
* 2015-07-06
 * Initial version ready for testing.
