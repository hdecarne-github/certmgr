import axios, { type AxiosResponse } from 'axios';

const responseBody = <T>(response: AxiosResponse<T>) => response.data;

const request = {
	get: <T>(url: string, params?: object) => axios.get<T>(url, { params: params }).then(responseBody),
	put: <T>(url: string, body: object) => axios.put<T>(url, body).then(responseBody)
};

// /api/about
export class AboutInfo {
	version: string = '';
	timestamp: string = '';
}

const about = {
	get: (base: string) => request.get<AboutInfo>(`${base}/api/about`)
};

// /api/entries
export class EntriesFilter {
	start: number = 0;
	limit: number = 0;
}

export class Entries {
	entries: Entry[] = [];
	start: number = 0;
	total: number = 0;
}

export class Entry {
	name: string = "";
	dn: string = "";
	serial: number = 0;
	keyType: string = "";
	key: boolean = false;
	crt: boolean = false;
	csr: boolean = false;
	crl: boolean = false;
	ca: boolean = false;
	validFrom: Date = new Date(0);
	validTo: Date = new Date(0);
}

const entries = {
	get: (base: string, params: EntriesFilter) => request.get<Entries>(`${base}/api/entries`, params)
};

// /details/${name}

export class EntryDetails extends Entry {
	crt_details: CRTDetails = new CRTDetails();
}

export class CRTDetails {
	version: number = -1;
	serial: string = '';
	keyType: string = '';
	issuer: string = '';
	sigAlg: string = '';
	extensions: string[2][] = [];
}

const entryDetails = {
	get: (base: string, name: string) => request.get<EntryDetails>(`${base}/api/details/${name}`)
};

// /api/cas

export class CAs {
	cas: CA[] = [];
}

export class CA {
	name: string = '';
}

const cas = {
	get: (base: string) => request.get<CAs>(`${base}/api/cas`)
};

// /api/issuers
export class IssuersFilter {
	keyUsage: number = 0;
}

const issuers = {
	get: (base: string, params: IssuersFilter) => request.get<Entries>(`${base}/api/issuers`, params)
};

// /generate

export class ExtensionSpec {
	enabled: boolean = false;
}

export class KeyUsageSpec extends ExtensionSpec {
	keyUsage: number = 0;
}

export class ExtKeyUsageSpec extends ExtensionSpec {
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
}

export class BasicConstraintsSpec extends ExtensionSpec {
	ca: boolean = false;
	pathLenConstraint: number = -1;
}

export class Generate {
	name: string = '';
	ca: string = '';
}

export class GenerateLocal extends Generate {
	dn: string = '';
	keyType: string = '';
	issuer: string = '';
	validFrom: Date = new Date(0);
	validTo: Date = new Date(0);
	keyUsage: KeyUsageSpec = new KeyUsageSpec();
	extKeyUsage: ExtKeyUsageSpec = new ExtKeyUsageSpec();
	basicConstraints: BasicConstraintsSpec = new BasicConstraintsSpec();
}

const generateLocal = {
	put: (base: string, body: GenerateLocal) => request.put<Entry>(`${base}/api/generate/local`, body)
};

export class StoreRemoteGenerate extends Generate {
	dn: string = '';
	key_type: string = '';
}

const storeRemoteGenerate = {
	put: (base: string, body: StoreRemoteGenerate) => request.put<void>(`${base}/api/store/remote/generate`, body)
};

export class StoreACMEGenerate extends Generate {
	domains: string[] = [];
	key_type: string = '';
}

const storeACMEGenerate = {
	put: (base: string, body: StoreRemoteGenerate) => request.put<void>(`${base}/api/store/acme/generate`, body)
};

const api = {
	about,
	entries,
	entryDetails,
	cas,
	issuers,
	generateLocal,
	storeRemoteGenerate,
	storeACMEGenerate
};

export default api;
