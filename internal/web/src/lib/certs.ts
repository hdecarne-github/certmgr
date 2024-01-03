import { KeyUsageSpec, BasicConstraintsSpec, ExtKeyUsageSpec } from "./api";

function isLocalCa(ca: string): boolean {
    return ca == 'Local';
}

function isRemoteCa(ca: string): boolean {
    return ca == 'Remote';
}

function isAcmeCa(ca: string): boolean {
    return (ca ?? '').startsWith('ACME:');
}

const defaultKeyTypes: string[][] = [
    ['ECDSA P-224', 'ECDSA224'],
    ['ECDSA P-256', 'ECDSA256'],
    ['ECDSA P-384', 'ECDSA384'],
    ['ECDSA P-521', 'ECDSA521'],
    ['ED25519', 'ED25519'],
    ['RSA 2048', 'RSA2048'],
    ['RSA 3072', 'RSA3072'],
    ['RSA 4096', 'RSA4096'],
    ['RSA 8192', 'RSA8192'],
];

const acmeKeyTypes: string[][] = [
    ['ECDSA P-256', 'ECDSA256'],
    ['ECDSA P-384', 'ECDSA384'],
    ['RSA 2048', 'RSA2048'],
    ['RSA 3072', 'RSA3072'],
    ['RSA 4096', 'RSA4096'],
];

export enum KeyUsageFlag {
    DigitalSignature = 1,
    ContentCommitment = 2,
    KeyEncipherment = 4,
    DataEncipherment = 8,
    KeyAgreement = 16,
    KeyCertSign = 32,
    CRLSign = 64,
    EncipherOnly = 128,
    DecipherOnly = 256,
}

export class KeyUsage {
    digitalSignature: boolean = false;
    contentCommitment: boolean = false;
    keyEncipherment: boolean = false;
    dataEncipherment: boolean = false;
    keyAgreement: boolean = false;
    keyCertSign: boolean = false;
    crlSign: boolean = false;
    encipherOnly: boolean = false;
    decipherOnly: boolean = false;

    fromSpec(spec: KeyUsageSpec) {
        this.digitalSignature = (spec.keyUsage & KeyUsageFlag.DigitalSignature) != 0;
        this.contentCommitment = (spec.keyUsage & KeyUsageFlag.ContentCommitment) != 0;
        this.keyEncipherment = (spec.keyUsage & KeyUsageFlag.KeyEncipherment) != 0;
        this.dataEncipherment = (spec.keyUsage & KeyUsageFlag.DataEncipherment) != 0;
        this.keyAgreement = (spec.keyUsage & KeyUsageFlag.KeyAgreement) != 0;
        this.keyCertSign = (spec.keyUsage & KeyUsageFlag.KeyCertSign) != 0;
        this.crlSign = (spec.keyUsage & KeyUsageFlag.CRLSign) != 0;
        this.encipherOnly = (spec.keyUsage & KeyUsageFlag.EncipherOnly) != 0;
        this.decipherOnly = (spec.keyUsage & KeyUsageFlag.DecipherOnly) != 0;
    }

    toSpec(): KeyUsageSpec {
        let spec = new KeyUsageSpec();
        spec.enabled = true;
        if (this.digitalSignature) {
            spec.keyUsage |= KeyUsageFlag.DigitalSignature;
        }
        if (this.contentCommitment) {
            spec.keyUsage |= KeyUsageFlag.ContentCommitment;
        }
        if (this.keyEncipherment) {
            spec.keyUsage |= KeyUsageFlag.KeyEncipherment;
        }
        if (this.dataEncipherment) {
            spec.keyUsage |= KeyUsageFlag.DataEncipherment;
        }
        if (this.keyAgreement) {
            spec.keyUsage |= KeyUsageFlag.KeyAgreement;
        }
        if (this.keyCertSign) {
            spec.keyUsage |= KeyUsageFlag.KeyCertSign;
        }
        if (this.crlSign) {
            spec.keyUsage |= KeyUsageFlag.CRLSign;
        }
        if (this.encipherOnly) {
            spec.keyUsage |= KeyUsageFlag.EncipherOnly;
        }
        if (this.decipherOnly) {
            spec.keyUsage |= KeyUsageFlag.DecipherOnly;
        }
        return spec;
    }
}

export class ExtKeyUsage {
    any: boolean = false;
    serverAuth: boolean = false;
    clientAuth: boolean = false;
    codeSigning: boolean = false;
    emailProtection: boolean = false;
    ipsecEndSystem: boolean = false;
    ipsecTunnel: boolean = false;
    ipsecUser: boolean = false;
    timeStamping: boolean = false;
    ocspSigning: boolean = false;
    microsoftServerGatedCrypto: boolean = false;
    netscapeServerGatedCrypto: boolean = false;
    microsoftCommercialCodeSigning: boolean = false;
    microsoftKernelCodeSigning: boolean = false;

    fromSpec(spec: ExtKeyUsageSpec) {
        this.any = spec.any;
        this.serverAuth = spec.serverAuth;
        this.clientAuth = spec.clientAuth;
        this.codeSigning = spec.codeSigning;
        this.emailProtection = spec.emailProtection;
        this.ipsecEndSystem = spec.ipsecEndSystem;
        this.ipsecTunnel = spec.ipsecTunnel
        this.ipsecUser = spec.ipsecUser
        this.timeStamping = spec.timeStamping;
        this.ocspSigning = spec.ocspSigning;
        this.microsoftServerGatedCrypto = spec.microsoftServerGatedCrypto;
        this.netscapeServerGatedCrypto = spec.netscapeServerGatedCrypto;
        this.microsoftCommercialCodeSigning = spec.microsoftCommercialCodeSigning;
        this.microsoftKernelCodeSigning = spec.microsoftKernelCodeSigning;
    }

    toSpec(): ExtKeyUsageSpec {
        let spec = new ExtKeyUsageSpec();
        spec.enabled = true;
        spec.any = this.any;
        spec.serverAuth = this.serverAuth;
        spec.clientAuth = this.clientAuth;
        spec.codeSigning = this.codeSigning;
        spec.emailProtection = this.emailProtection;
        spec.ipsecEndSystem = this.ipsecEndSystem;
        spec.ipsecTunnel = this.ipsecTunnel;
        spec.ipsecUser = this.ipsecUser;
        spec.timeStamping = this.timeStamping;
        spec.ocspSigning = this.ocspSigning;
        spec.microsoftServerGatedCrypto = this.microsoftServerGatedCrypto;
        spec.netscapeServerGatedCrypto = this.netscapeServerGatedCrypto;
        spec.microsoftCommercialCodeSigning = this.microsoftCommercialCodeSigning;
        spec.microsoftKernelCodeSigning = this.microsoftKernelCodeSigning;
        return spec;
    }
}

export class BasicConstraints {
    ca: boolean = false;
    pathLenConstraint: number = -1;

    fromSpec(spec: BasicConstraintsSpec) {
        this.ca = spec.ca;
        this.pathLenConstraint = spec.pathLenConstraint;
    }

    toSpec(): BasicConstraintsSpec {
        let spec = new BasicConstraintsSpec();
        spec.enabled = true;
        spec.ca = this.ca;
        spec.pathLenConstraint = this.pathLenConstraint;
        return spec;
    }
}

const certs = {
    isLocalCa,
    isRemoteCa,
    isAcmeCa,
    defaultKeyTypes,
    acmeKeyTypes,
};

export default certs;