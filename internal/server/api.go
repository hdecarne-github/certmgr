// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package server

import (
	"crypto"
	"crypto/x509"
	"crypto/x509/pkix"
	"encoding/asn1"
	"math/big"
	"net"
	"net/url"
	"strconv"
	"time"

	"github.com/hdecarne-github/go-certstore"
	"github.com/hdecarne-github/go-certstore/certs"
	"github.com/hdecarne-github/go-certstore/keys"
)

// <- /api/about
type AboutInfo struct {
	Version   string `json:"version"`
	Timestamp string `json:"timestamp"`
}

// <- /api/entries
type EntriesFilter struct {
	Start int `form:"start"`
	Limit int `form:"limit"`
}

type Entries struct {
	Entries []Entry `json:"entries"`
	Start   int     `json:"start"`
	Total   int     `json:"total"`
}

type Entry struct {
	Name      string   `json:"name"`
	DN        string   `json:"dn"`
	Serial    *big.Int `json:"serial"`
	KeyType   string   `json:"keyType"`
	Key       bool     `json:"key"`
	CRT       bool     `json:"crt"`
	CSR       bool     `json:"csr"`
	CRL       bool     `json:"crl"`
	CA        bool     `json:"ca"`
	ValidFrom string   `json:"validFrom"`
	ValidTo   string   `json:"validTo"`
}

func newEntry(entry *certstore.RegistryEntry) *Entry {
	return populateEntry(&Entry{}, entry)
}

// <- /api/details/:name
type EntryDetails struct {
	Name   string              `json:"name"`
	Groups []EntryDetailsGroup `json:"groups"`
}

func newDetails(entry *certstore.RegistryEntry) *EntryDetails {
	return populateEntryDetails(&EntryDetails{}, entry)
}

type EntryDetailsGroup struct {
	Title      string                  `json:"title"`
	Attributes []EntryDetailsAttribute `json:"attributes"`
}

func (group *EntryDetailsGroup) Empty() bool {
	return len(group.Attributes) == 0
}

type EntryDetailsAttribute struct {
	Key   string `json:"key"`
	Value string `json:"value"`
}

// <- /api/cas
type CAs struct {
	CAs []CA `json:"cas"`
}

type CA struct {
	Name string `json:"name"`
}

// <- /api/issuers
type IssuersFilter struct {
	KeyUsage x509.KeyUsage `form:"keyUsage"`
}

// <- /api/generate/local
type Generate struct {
	Name string `json:"name"`
	CA   string `json:"ca"`
}

func (generate *Generate) validate() error {
	return nil
}

type GenerateLocal struct {
	Generate
	DN               string               `json:"dn"`
	KeyType          string               `json:"keyType"`
	Issuer           string               `json:"issuer"`
	ValidFrom        time.Time            `json:"validFrom"`
	ValidTo          time.Time            `json:"validTo"`
	KeyUsage         KeyUsageSpec         `json:"keyUsage"`
	ExtKeyUsage      ExtKeyUsageSpec      `json:"extKeyUsage"`
	BasicConstraints BasicConstraintsSpec `json:"basicConstraints"`
}

func (generate *GenerateLocal) validate() error {
	err := generate.Generate.validate()
	if err != nil {
		return err
	}
	return nil
}

func (generate *GenerateLocal) toTemplate() (*x509.Certificate, error) {
	dn, err := certs.ParseDN(generate.DN)
	if err != nil {
		return nil, err
	}
	template := &x509.Certificate{
		Subject:   *dn,
		NotBefore: generate.ValidFrom,
		NotAfter:  generate.ValidTo,
	}
	generate.KeyUsage.applyToCertificate(template)
	generate.ExtKeyUsage.applyToCertificate(template)
	generate.BasicConstraints.applyToCertificate(template)
	return template, nil
}

type ExtensionSpec struct {
	Enabled bool `json:"enabled"`
}

type KeyUsageSpec struct {
	ExtensionSpec
	KeyUsage x509.KeyUsage `json:"keyUsage"`
}

func (spec *KeyUsageSpec) applyToCertificate(certificate *x509.Certificate) {
	if !spec.Enabled {
		return
	}
	certificate.KeyUsage = spec.KeyUsage
}

type ExtKeyUsageSpec struct {
	ExtensionSpec
	Any                            bool `json:"any"`
	ServerAuth                     bool `json:"serverAuth"`
	ClientAuth                     bool `json:"clientAuth"`
	CodeSigning                    bool `json:"codeSigning"`
	EmailProtection                bool `json:"emailProtection"`
	IPSECEndSystem                 bool `json:"ipsecEndSystem"`
	IPSECTunnel                    bool `json:"ipsecTunnel"`
	IPSECUser                      bool `json:"ipsecUser"`
	TimeStamping                   bool `json:"timeStamping"`
	OCSPSigning                    bool `json:"ocspSigning"`
	MicrosoftServerGatedCrypto     bool `json:"microsoftServerGatedCrypto"`
	NetscapeServerGatedCrypto      bool `json:"netscapeServerGatedCrypto"`
	MicrosoftCommercialCodeSigning bool `json:"microsoftCommercialCodeSigning"`
	MicrosoftKernelCodeSigning     bool `json:"microsoftKernelCodeSigning"`
}

func (spec *ExtKeyUsageSpec) applyToCertificate(certificate *x509.Certificate) {
	if !spec.Enabled {
		return
	}
	extKeyUsage := make([]x509.ExtKeyUsage, 0)
	if spec.Any {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageAny)
	}
	if spec.ServerAuth {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageServerAuth)
	}
	if spec.ClientAuth {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageClientAuth)
	}
	if spec.CodeSigning {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageCodeSigning)
	}
	if spec.EmailProtection {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageEmailProtection)
	}
	if spec.TimeStamping {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageTimeStamping)
	}
	if spec.OCSPSigning {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageOCSPSigning)
	}
	if spec.IPSECEndSystem {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageIPSECEndSystem)
	}
	if spec.IPSECTunnel {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageIPSECTunnel)
	}
	if spec.IPSECUser {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageIPSECUser)
	}
	if spec.MicrosoftServerGatedCrypto {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageMicrosoftServerGatedCrypto)
	}
	if spec.NetscapeServerGatedCrypto {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageNetscapeServerGatedCrypto)
	}
	if spec.MicrosoftCommercialCodeSigning {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageMicrosoftCommercialCodeSigning)
	}
	if spec.MicrosoftKernelCodeSigning {
		extKeyUsage = append(extKeyUsage, x509.ExtKeyUsageMicrosoftKernelCodeSigning)
	}
	certificate.ExtKeyUsage = extKeyUsage
}

type BasicConstraintsSpec struct {
	ExtensionSpec
	CA                bool `json:"ca"`
	PathLenConstraint int  `json:"pathLenConstraint"`
}

func (spec *BasicConstraintsSpec) applyToCertificate(certificate *x509.Certificate) {
	if !spec.Enabled {
		return
	}
	certificate.BasicConstraintsValid = true
	certificate.IsCA = spec.CA
	if spec.CA && spec.PathLenConstraint >= 0 {
		certificate.MaxPathLen = spec.PathLenConstraint
		certificate.MaxPathLenZero = true
	} else {
		certificate.MaxPathLen = -1
		certificate.MaxPathLenZero = false
	}
}

// <- /api/generate/remote
type GenerateRemote struct {
	Generate
	DN      string `json:"dn"`
	KeyType string `json:"keyType"`
}

func (generate *GenerateRemote) validate() error {
	err := generate.Generate.validate()
	if err != nil {
		return err
	}
	return nil
}

func (generate *GenerateRemote) toTemplate() (*x509.CertificateRequest, error) {
	dn, err := certs.ParseDN(generate.DN)
	if err != nil {
		return nil, err
	}
	template := &x509.CertificateRequest{
		Subject: *dn,
	}
	return template, nil
}

// <- /api/generate/acme
type GenerateACME struct {
	Generate
	Domains []string `json:"domains"`
	KeyType string   `json:"keyType"`
}

func (generate *GenerateACME) validate() error {
	err := generate.Generate.validate()
	if err != nil {
		return err
	}
	return nil
}

// <- /api/*
type ErrorResponse struct {
	Message string `json:"message"`
}

// helper
func populateEntry(apiEntry *Entry, registryEntry *certstore.RegistryEntry) *Entry {
	apiEntry.Name = registryEntry.Name()
	apiEntry.Key = registryEntry.HasKey()
	apiEntry.CRT = registryEntry.HasCertificate()
	apiEntry.CSR = registryEntry.HasCertificateRequest()
	apiEntry.CRL = registryEntry.HasRevocationList()
	if apiEntry.CRT {
		certificate := registryEntry.Certificate()
		apiEntry.DN = certificate.Subject.String()
		apiEntry.Serial = certificate.SerialNumber
		keyAlg, _ := keys.AlgorithmFromKey(certificate.PublicKey)
		apiEntry.KeyType = keyAlg.String()
		apiEntry.CA = certificate.BasicConstraintsValid && certificate.IsCA
		apiEntry.ValidFrom = certificate.NotBefore.Format(time.RFC3339)
		apiEntry.ValidTo = certificate.NotAfter.Format(time.RFC3339)
	} else if apiEntry.CSR {
		certificateRequest := registryEntry.CertificateRequest()
		apiEntry.DN = certificateRequest.Subject.String()
		apiEntry.Serial = big.NewInt(0)
		keyAlg, _ := keys.AlgorithmFromKey(certificateRequest.PublicKey)
		apiEntry.KeyType = keyAlg.String()
	}
	return apiEntry
}

func populateEntryDetails(details *EntryDetails, registryEntry *certstore.RegistryEntry) *EntryDetails {
	details.Name = registryEntry.Name()
	details.Groups = append(details.Groups, *populateEntryDetailsKey(&EntryDetailsGroup{Title: "Key"}, registryEntry))
	if registryEntry.HasCertificate() {
		certificate := registryEntry.Certificate()
		details.Groups = append(details.Groups, *populateEntryDetailsCertificate(&EntryDetailsGroup{Title: "Certificate"}, certificate))
		extensionGroup := populateEntryDetailsCertificateExtensions(&EntryDetailsGroup{Title: "Certificate extensions"}, certificate)
		if !extensionGroup.Empty() {
			details.Groups = append(details.Groups, *extensionGroup)
		}
	}
	if registryEntry.HasCertificateRequest() {
		certificateRequest := registryEntry.CertificateRequest()
		details.Groups = append(details.Groups, *populateEntryDetailsCertificateRequest(&EntryDetailsGroup{Title: "Certificate request"}, certificateRequest))
		extensionGroup := populateEntryDetailsCertificateRequestExtensions(&EntryDetailsGroup{Title: "Certificate request extensions"}, certificateRequest)
		if !extensionGroup.Empty() {
			details.Groups = append(details.Groups, *extensionGroup)
		}
	}
	return details
}

func populateEntryDetailsKey(group *EntryDetailsGroup, registryEntry *certstore.RegistryEntry) *EntryDetailsGroup {
	keyType := keys.UnknownAlgorithm.String()
	if registryEntry.HasCertificate() {
		certificate := registryEntry.Certificate()
		keyType = keyTypeString(certificate.PublicKey, certificate.PublicKeyAlgorithm)
	} else if registryEntry.HasCertificateRequest() {
		certificateRequest := registryEntry.CertificateRequest()
		keyType = keyTypeString(certificateRequest.PublicKey, certificateRequest.PublicKeyAlgorithm)
	}
	privateKey := "no"
	if registryEntry.HasKey() {
		privateKey = "yes"
	}
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Key type", Value: keyType})
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Private key", Value: privateKey})
	return group
}

func keyTypeString(publicKey crypto.PublicKey, defaultType x509.PublicKeyAlgorithm) string {
	keyAlg, _ := keys.AlgorithmFromKey(publicKey)
	// for imported certificate objects we may encounter an unknown algorithm
	if keyAlg == keys.UnknownAlgorithm {
		return defaultType.String()
	}
	return keyAlg.String()
}

func populateEntryDetailsCertificate(group *EntryDetailsGroup, certificate *x509.Certificate) *EntryDetailsGroup {
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Version", Value: strconv.Itoa(certificate.Version)})
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "DN", Value: certificate.Subject.String()})
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Serial", Value: certificate.SerialNumber.String()})
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Issuer DN", Value: certificate.Issuer.String()})
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Signature type", Value: certificate.SignatureAlgorithm.String()})
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Valid from", Value: certificate.NotBefore.Format(time.RFC3339)})
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Valid to", Value: certificate.NotAfter.Format(time.RFC3339)})
	return group
}

func populateEntryDetailsCertificateExtensions(group *EntryDetailsGroup, certificate *x509.Certificate) *EntryDetailsGroup {
	populateEntryDetailsKeyUsage(group, certificate.KeyUsage)
	populateEntryDetailsExtKeyUsage(group, certificate.ExtKeyUsage, certificate.UnknownExtKeyUsage)
	if certificate.BasicConstraintsValid {
		populateEntryDetailsBasicConstraints(group, certificate.IsCA, certificate.MaxPathLen, certificate.MaxPathLenZero)
	}
	populateEntryDetailsSubjectKeyId(group, certificate.SubjectKeyId)
	populateEntryDetailsAuthorityKeyId(group, certificate.AuthorityKeyId)
	populateEntryDetailsSubjectAlternativeNames(group, certificate.DNSNames, certificate.EmailAddresses, certificate.IPAddresses, certificate.URIs)
	populateEntryDetailsExtensions(group, certificate.Extensions, certificateInlinExtensions)
	populateEntryDetailsExtensions(group, certificate.ExtraExtensions, certificateInlinExtensions)
	return group
}

func populateEntryDetailsCertificateRequest(group *EntryDetailsGroup, certificateRequest *x509.CertificateRequest) *EntryDetailsGroup {
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Version", Value: strconv.Itoa(certificateRequest.Version)})
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "DN", Value: certificateRequest.Subject.String()})
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Signature type", Value: certificateRequest.SignatureAlgorithm.String()})
	return group
}

func populateEntryDetailsCertificateRequestExtensions(group *EntryDetailsGroup, certificateRequest *x509.CertificateRequest) *EntryDetailsGroup {
	populateEntryDetailsSubjectAlternativeNames(group, certificateRequest.DNSNames, certificateRequest.EmailAddresses, certificateRequest.IPAddresses, certificateRequest.URIs)
	populateEntryDetailsExtensions(group, certificateRequest.Extensions, certificateRequestInlinExtensions)
	populateEntryDetailsExtensions(group, certificateRequest.ExtraExtensions, certificateRequestInlinExtensions)
	return group
}

func populateEntryDetailsKeyUsage(group *EntryDetailsGroup, keyUsage x509.KeyUsage) *EntryDetailsGroup {
	if keyUsage != 0 {
		group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Key usage", Value: certs.KeyUsageString(keyUsage)})
	}
	return group
}

func populateEntryDetailsExtKeyUsage(group *EntryDetailsGroup, extKeyUsage []x509.ExtKeyUsage, unknownExtKeyUsage []asn1.ObjectIdentifier) *EntryDetailsGroup {
	if len(extKeyUsage) > 0 || len(unknownExtKeyUsage) > 0 {
		group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Extended key usage", Value: certs.ExtKeyUsageString(extKeyUsage, unknownExtKeyUsage)})
	}
	return group
}

func populateEntryDetailsBasicConstraints(group *EntryDetailsGroup, isCA bool, maxPathLen int, maxPathLenZero bool) *EntryDetailsGroup {
	group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Basic constraints", Value: certs.BasicConstraintsString(isCA, maxPathLen, maxPathLenZero)})
	return group
}

func populateEntryDetailsSubjectKeyId(group *EntryDetailsGroup, keyId []byte) *EntryDetailsGroup {
	if len(keyId) > 0 {
		group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Subject key id", Value: certs.KeyIdentifierString(keyId)})
	}
	return group
}

func populateEntryDetailsAuthorityKeyId(group *EntryDetailsGroup, keyId []byte) *EntryDetailsGroup {
	if len(keyId) > 0 {
		group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: "Authority key id", Value: certs.KeyIdentifierString(keyId)})
	}
	return group
}

func populateEntryDetailsSubjectAlternativeNames(group *EntryDetailsGroup, dnsNames []string, emailAddresses []string, ipAddresses []net.IP, uris []*url.URL) *EntryDetailsGroup {
	nextKey := "Subject alternative name"
	for _, dnsName := range dnsNames {
		group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: nextKey, Value: "DNS:" + dnsName})
		nextKey = ""
	}
	for _, emailAddress := range emailAddresses {
		group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: nextKey, Value: "EMAIL:" + emailAddress})
		nextKey = ""
	}
	for _, ipAddress := range ipAddresses {
		group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: nextKey, Value: "IP:" + ipAddress.String()})
		nextKey = ""
	}
	for _, uri := range uris {
		group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: nextKey, Value: "URI:" + uri.String()})
		nextKey = ""
	}
	return group
}

var extensionNames map[string]string = map[string]string{
	certs.KeyUsageExtensionOID:               certs.KeyUsageExtensionName,
	certs.ExtKeyUsageExtensionOID:            certs.ExtKeyUsageExtensionName,
	certs.BasicConstraintsExtensionOID:       certs.BasicConstraintsExtensionName,
	certs.SubjectKeyIdentifierExtensionOID:   certs.SubjectKeyIdentifierExtensionName,
	certs.AuthorityKeyIdentifierExtensionOID: certs.AuthorityKeyIdentifierExtensionName,
	"2.5.29.17":                              "SubjectAlternativeName",
}

var certificateInlinExtensions map[string]string = map[string]string{
	certs.KeyUsageExtensionOID:               certs.KeyUsageExtensionName,
	certs.ExtKeyUsageExtensionOID:            certs.ExtKeyUsageExtensionName,
	certs.BasicConstraintsExtensionOID:       certs.BasicConstraintsExtensionName,
	certs.SubjectKeyIdentifierExtensionOID:   certs.SubjectKeyIdentifierExtensionName,
	certs.AuthorityKeyIdentifierExtensionOID: certs.AuthorityKeyIdentifierExtensionName,
	"2.5.29.17":                              "SubjectAlternativeName",
}

var certificateRequestInlinExtensions map[string]string = map[string]string{
	"2.5.29.17": "SubjectAlternativeName",
}

func populateEntryDetailsExtensions(group *EntryDetailsGroup, extensions []pkix.Extension, ignore map[string]string) *EntryDetailsGroup {
	for _, extension := range extensions {
		extensionOID := extension.Id.String()
		if ignore[extensionOID] == "" {
			extensionName := extensionNames[extensionOID]
			if extensionName == "" {
				extensionName = extensionOID
			}
			group.Attributes = append(group.Attributes, EntryDetailsAttribute{Key: extensionName, Value: "xxx"})
		}
	}
	return group
}
