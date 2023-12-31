// Copyright (C) 2015-2024 Holger de Carne and contributors
//
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.

package server

import (
	"crypto/x509"
	"math/big"
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
	Name      string    `json:"name"`
	DN        string    `json:"dn"`
	Serial    *big.Int  `json:"serial"`
	KeyType   string    `json:"keyType"`
	Key       bool      `json:"key"`
	CRT       bool      `json:"crt"`
	CSR       bool      `json:"csr"`
	CRL       bool      `json:"crl"`
	CA        bool      `json:"ca"`
	ValidFrom time.Time `json:"validFrom"`
	ValidTo   time.Time `json:"validTo"`
}

func newEntry(entry *certstore.RegistryEntry) *Entry {
	dn := ""
	serial := big.NewInt(0)
	keyType := ""
	ca := false
	validFrom := time.Time{}
	validTo := time.Time{}
	if entry.HasCertificate() {
		certificate := entry.Certificate()
		dn = certificate.Subject.String()
		serial = certificate.SerialNumber
		keyAlg, _ := keys.AlgorithmFromKey(certificate.PublicKey)
		keyType = keyAlg.String()
		ca = certificate.BasicConstraintsValid && certificate.IsCA
		validFrom = certificate.NotBefore
		validTo = certificate.NotAfter
	} else if entry.HasCertificateRequest() {
		certificateRequest := entry.CertificateRequest()
		dn = certificateRequest.Subject.String()
		keyAlg, _ := keys.AlgorithmFromKey(certificateRequest.PublicKey)
		keyType = keyAlg.String()
	}
	return &Entry{
		Name:      entry.Name(),
		DN:        dn,
		Serial:    serial,
		KeyType:   keyType,
		Key:       entry.HasKey(),
		CRT:       entry.HasCertificate(),
		CSR:       entry.HasCertificateRequest(),
		CRL:       entry.HasRevocationList(),
		CA:        ca,
		ValidFrom: validFrom,
		ValidTo:   validTo,
	}
}

// <- /api/details/:name
type EntryDetails struct {
	Entry
	CRT CRTDetails `json:"crt"`
}

type CRTDetails struct {
	Version    int         `json:"version"`
	Serial     string      `json:"serial"`
	KeyType    string      `json:"key_type"`
	Issuer     string      `json:"issuer"`
	SigAlg     string      `json:"sig_alg"`
	Extensions [][2]string `json:"extensions"`
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
		Version:   3,
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

// <- /api/remote/generate
type StoreGenerateRemoteRequest struct {
	Generate
	DN      string `json:"dn"`
	KeyType string `json:"key_type"`
}

// <- /api/acme/generate
type StoreGenerateACMERequest struct {
	Generate
	Domains []string `json:"domains"`
	KeyType string   `json:"key_type"`
}

// <- /api/*
type ServerErrorResponse struct {
	Message string `json:"message"`
}
