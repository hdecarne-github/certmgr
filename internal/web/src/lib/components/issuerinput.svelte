<script lang="ts">
	import { onMount } from 'svelte';
	import { Helper, Label, Select, type SelectOptionType } from 'flowbite-svelte';
	import api from '$lib/api';
	import ui from '$lib/ui';

	export let label: string = 'Issuer';
	export let issuer: string;
	export let keyUsage: number;
	export const valid = {
		check(): boolean {
			if (issuer.trim().length == 0) {
				checkResult = false;
				checkMessage = 'Select an entry';
				return false;
			}
			checkResult = true;
			checkMessage = '';
			return true;
		}
	};
	let checkResult: boolean = true;
	let checkMessage: string = '';

	let issuers: SelectOptionType<string>[] = [];

	onMount(() => {
		api.issuers.get('..', { keyUsage: keyUsage }).then((response) => {
			issuers = [{ name: '<self signed>', value: '*' }];
			issuers = issuers.concat(
				response.entries.map((entry) => {
					return { name: ui.entryString(entry), value: entry.name };
				})
			);
		});
	});
</script>

<div class="mb-6">
	<Label color={checkResult ? 'gray' : 'red'}>
		{label}
		<Select
			color={checkResult ? 'base' : 'red'}
			class="mt-2"
			items={issuers}
			bind:value={issuer}
			on:change={() => valid.check()}
		/>
	</Label>
	<Helper color="red">{checkMessage}</Helper>
</div>
