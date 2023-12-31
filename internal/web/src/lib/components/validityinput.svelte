<script lang="ts">
	import { Label, Input } from 'flowbite-svelte';

	export let validFromLabel: string = 'Valid from';
	export let validToLabel: string = 'Valid to';
	export let validFrom: string;
	export let validTo: string;
</script>

<div class="mb-6 grid gap-6 md:grid-cols-2">
	<div>
		<Label for="validfrom-input" class="mb-2 block">{validFromLabel}</Label>
		<Input id="validfrom-input" type="date" bind:value={validFrom} />
	</div>
	<div>
		<Label for="validto-input" class="mb-2 block">{validToLabel}</Label>
		<Input id="validto-input" type="date" bind:value={validTo} />
	</div>
</div>
