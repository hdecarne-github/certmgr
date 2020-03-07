## Changelog:
This is a maintenance release of the CertMgr application.

Main changes are:
* Removal of ExtendedKeyUsage attribute from RootCA template to create RFC 5280 compliant certificates chains (Preventing: openssl-1.1.0+ validation error "error (26): unsupported certificate purpose")
* BouncyCastle version bump 1.61

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

![Install4j](http://certmgr.carne.de/install4j_small.png) The provided installer/launcher packages have been created using the multi-platform installer builder [Install4J](https://www.ej-technologies.com/products/install4j/overview.html).

### v1.1.3 (2020-03-07)
* Handle password protected key stores properly
* BouncyCastle version bump 1.64

### v1.1.2 (2019-02-23)
* Removal of ExtendedKeyUsage attribute from RootCA template to create RFC 5280 compliant certificates chains by default (Preventing: openssl-1.1.0+ validation error "error (26): unsupported certificate purpose")
* BouncyCastle version bump 1.61

### v1.1.1 (2018-09-30)
* Support for EKU "IP Security IKE Intermediate" (OID 1.3.6.1.5.5.8.2.2) 
* BouncyCastle version bump 1.60
* Minor technical updates (new update URL, ...)

### v1.1.0 (2018-04-06)
* Make DER file reader more robust in case of non-DER-input.
* Perform uninstall during I4J based update to discard no longer used files.
* Fix logging and preferences handling when launched via I4J.
* Update dependencies (and make application Java 9 compatible).

### v1.0.1 (2018-04-01)
* Fix export order if exporting multiple objects to a single file.

### v1.0.0 (2017-12-27)
* Fix automatic CN update in certificate options dialog in case of an empty name.

### v1.0.0-beta3 (2017-11-14)
* Add tooltip support to main window

### v1.0.0-beta1 (2017-09-14)
* Initial beta release of new completely re-build version.

### v0.2.58 (2016-05-29)
* Various bug fixes and code cleanups.
* Last release of 0.2.x branch
