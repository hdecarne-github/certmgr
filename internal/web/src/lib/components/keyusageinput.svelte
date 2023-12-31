<script lang="ts">
	import type { KeyUsage } from '$lib/certs';
	import { Checkbox } from 'flowbite-svelte';

	export let extension: KeyUsage;
</script>

<div class="mb-2">
	<Checkbox bind:checked={extension.digitalSignature}
		>Digital signature verification (digitalSignature)</Checkbox
	>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.contentCommitment}
		>Content commitment (nonRepudiation/contentCommitment)</Checkbox
	>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.keyEncipherment}>Key encipherment (keyEncipherment)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.dataEncipherment}>Data encipherment (dataEncipherment)</Checkbox
	>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.keyAgreement}>Key agreement (keyAgreement)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.keyCertSign}
		>Certificate signature verification (keyCertSign)</Checkbox
	>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.cRLSign}>CRL signature verification (cRLSign)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.encipherOnly}>Encipher only (encipherOnly)</Checkbox>
	<div class="mb-2"></div>
	<Checkbox bind:checked={extension.decipherOnly}>Decipher only (decipherOnly)</Checkbox>
</div>
