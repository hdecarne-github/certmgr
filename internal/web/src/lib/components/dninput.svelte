<script lang="ts">
	import { Label, Input } from 'flowbite-svelte';

	export let label: string = 'Distinguished Name (DN)';
	export let dn: string;
</script>

<div class="mb-6">
	<Label for="localdn-input" class="mb-2 block">{label}</Label>
	<Input id="localdn-input" placeholder="Enter DN" bind:value={dn} />
</div>
