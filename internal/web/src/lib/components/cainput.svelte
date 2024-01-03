<script lang="ts">
	import { Helper, Label, Select, type SelectOptionType } from 'flowbite-svelte';

	export let cas: SelectOptionType<string>[];
	export let ca: string;
	export const valid = {
		check(): boolean {
			if (ca.trim().length == 0) {
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
</script>

<div class="mb-6">
	<Label color={checkResult ? 'gray' : 'red'}>
		Select a Certificate Authority
		<Select
			color={checkResult ? 'base' : 'red'}
			class="mt-2"
			items={cas}
			bind:value={ca}
			on:change={() => valid.check()}
		/>
		<Helper color="red">{checkMessage}</Helper>
	</Label>
</div>
