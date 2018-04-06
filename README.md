[![Build Status](https://travis-ci.org/hdecarne/certmgr.svg?branch=master)](https://travis-ci.org/hdecarne/certmgr)
[![codecov](https://codecov.io/gh/hdecarne/certmgr/branch/master/graph/badge.svg)](https://codecov.io/gh/hdecarne/certmgr)
[![Downloads](https://img.shields.io/github/downloads/hdecarne/certmgr/total.svg)](https://github.com/hdecarne/certmgr/releases)

## CertMgr
Create and manage X.509 certificates.

### About CertMgr
The Certificate Manager application supports the creation and management of X.509 certificates and their corresponding objects.
![store view](docs/certmgr1.png)
Certificates are organized in so called Certificate store. Such a store is actually a simple directory structure containing the individual certificate files.

The application supports the following certificate operations:
 * Creation and management of **private certificates** (signed by your own Certificate Authority)
 * Creation and management of **public certificates** (signed by an external Certificate Authority)
 * Creation and management of **Certificate Revocation Lists** (CRL)
 * **Import and export** of certificates (in PEM, DER, PKCS#12 as well as JKS format)

### Installation & usage:
A Java SE 8 Runtime Environment (JRE) is required to install/run CertMgr.

Download the latest version from the project's [releases page](https://github.com/hdecarne/certmgr/releases/latest).

![Install4j](docs/install4j_small.png)
The provided installer/launcher packages have been created using the multi-platform installer builder
[Install4J](https://www.ej-technologies.com/products/install4j/overview.html). Simply run the installer suitable for your platform to install the application and keep it up-to-date.

If you downloaded one of the generic archives, simply extract it to a folder of your choice.
The archive contains a single executable Jar as well as a folder with the license information. Invoke the application by either double clicking the jar or invoke the command

```
java -jar certmgr-boot-<version> [command line arguments]
```

in a terminal. The application command line supports the following options:

```
certmgr-boot-<version> [--verbose|--debug] [store home]

--verbose
	Enable verbose logging.
--debug
	Enable debug logging.

store home
	The store home path to open.
```

#### HowTos
 * [Create your own private CA](http://certmgr.carne.de/howtoLocalCA/)
 * [Create and manage certificates of an external CA](http://certmgr.carne.de/howtoExternalCA/)
 * [Import existing certificate objects](http://certmgr.carne.de/howtoImport/)
 * [Configure Apache to use your certificates](http://certmgr.carne.de/howtoApache/)
