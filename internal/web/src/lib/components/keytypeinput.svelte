<script lang="ts">
	import { Helper, Label, Select, type SelectOptionType } from 'flowbite-svelte';

	export let label: string = 'Key type';
	export let keyTypes: SelectOptionType<string>[];
	export let keyType: string;
	export const valid = {
		check(): boolean {
			if (keyType.trim().length == 0) {
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
		{label}
		<Select
			color={checkResult ? 'base' : 'red'}
			class="mt-2"
			items={keyTypes}
			bind:value={keyType}
			on:change={() => valid.check()}
		/>
		<Helper color="red">{checkMessage}</Helper>
	</Label>
</div>
