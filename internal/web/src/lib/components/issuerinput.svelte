<script lang="ts">
	import { onMount } from 'svelte';
	import { Label, Select, type SelectOptionType } from 'flowbite-svelte';
	import api from '$lib/api';

	export let label: string = 'Issuer';
	export let selfsigned: boolean;
	export let issuer: string;
	export let keyUsage: number;

	let issuers: SelectOptionType<string>[] = selfsigned
		? [{ name: '<self signed>', value: '*' }]
		: [];

	onMount(() => {
		api.issuers.get('..', { keyUsage: keyUsage }).then((response) => {
			issuers = issuers.concat(
				response.entries.map((entry) => {
					return { name: entry.name, value: entry.name };
				})
			);
		});
	});
</script>

<div class="mb-6">
	<Label>
		{label}
		<Select class="mt-2" bind:items={issuers} bind:value={issuer} />
	</Label>
</div>
