import axios, { type AxiosResponse } from 'axios';

const responseBody = <T>(response: AxiosResponse<T>) => response.data;

const request = {
	get: <T>(url: string, params?: object) => axios.get<T>(url, { params: params }).then(responseBody),
	put: <T>(url: string, body: object) => axios.put<T>(url, body).then(responseBody),
	delete:  <T>(url: string, params?: object) => axios.delete<T>(url, { params: params }).then(responseBody)
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
	validFrom: string = "";
	validTo: string = "";
}

const entries = {
	get: (base: string, params: EntriesFilter) => request.get<Entries>(`${base}/api/entries`, params)
};

// /details/${name}
export class EntryDetails {
	name: string = "";
	groups: EntryDetailsGroup[] = [];
}

export class EntryDetailsGroup {
	title: string = "";
	attributes: EntryDetailsAttribute[] = [];
}

export class EntryDetailsAttribute {
	key: string = "";
	value: string = "";
}

const details = {
	get: (base: string, name: string) => request.get<EntryDetails>(`${base}/api/details/${name}`)
};

// /delete/${name}
const del = {
	delete: (base: string, name: string) => request.delete<EntryDetails>(`${base}/api/delete/${name}`)
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
	validFrom: string = new Date(0).toJSON();
	validTo: string = new Date(0).toJSON();
	keyUsage: KeyUsageSpec = new KeyUsageSpec();
	extKeyUsage: ExtKeyUsageSpec = new ExtKeyUsageSpec();
	basicConstraints: BasicConstraintsSpec = new BasicConstraintsSpec();
}

const generateLocal = {
	put: (base: string, body: GenerateLocal) => request.put<Entry>(`${base}/api/generate/local`, body)
};

export class GenerateRemote extends Generate {
	dn: string = '';
	keyType: string = '';
}

const generateRemote = {
	put: (base: string, body: GenerateRemote) => request.put<void>(`${base}/api/generate/remote`, body)
};

export class GenerateAcme extends Generate {
	domains: string[] = [];
	keyType: string = '';
}

const generateAcme = {
	put: (base: string, body: GenerateAcme) => request.put<void>(`${base}/api/generate/acme`, body)
};

const api = {
	about,
	entries,
	details,
	del,
	cas,
	issuers,
	generateLocal,
	generateRemote,
	generateAcme,
};

export default api;
