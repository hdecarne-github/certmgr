<script lang="ts">
	import { ExtKeyUsage } from '$lib/certs';
	import { Checkbox } from 'flowbite-svelte';

	export let extension: ExtKeyUsage;
</script>

<div class="mb-2">
	<Checkbox bind:checked={extension.any}>Any (any)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.serverAuth}>Server authentication (serverAuth)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.clientAuth}>Client authentication (clientAuth)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.codeSigning}>Code signing (codeSigning)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.emailProtection}>Email protection (emailProtection)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.ipsecEndSystem}>IPSEC end system (ipsecEndSystem)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.ipsecTunnel}>IPSEC tunnel (ipsecTunnel)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.ipsecUser}>IPSEC User (ipsecUser)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.timeStamping}>Time stamping (timeStamping)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.ocspSigning}>OCSP signing (OCSPSigning)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.microsoftServerGatedCrypto}
		>MS server gated crypto (microsoftServerGatedCrypto)</Checkbox
	>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.netscapeServerGatedCrypto}
		>Netscape server gated crypto (netscapeServerGatedCrypto)</Checkbox
	>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.microsoftCommercialCodeSigning}
		>MS commercial code signing (microsoftCommercialCodeSigning)</Checkbox
	>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.microsoftKernelCodeSigning}
		>MS kernel code signing (microsoftKernelCodeSigning)</Checkbox
	>
</div>
