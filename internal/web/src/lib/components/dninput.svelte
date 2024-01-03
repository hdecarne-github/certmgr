<script lang="ts">
	import { Label, Input, Helper } from 'flowbite-svelte';

	export let label: string = 'Distinguished Name (DN)';
	export let dn: string;
	export const valid = {
		check(): boolean {
			if (dn.trim().length == 0) {
				checkResult = false;
				checkMessage = 'Cannot be empty';
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
	<Label for="dninput" color={checkResult ? 'gray' : 'red'} class="mb-2 block">{label}</Label>
	<Input
		id="dninput"
		color={checkResult ? 'base' : 'red'}
		placeholder="Enter DN"
		bind:value={dn}
		on:input={() => valid.check()}
	/>
	<Helper color="red">{checkMessage}</Helper>
</div>
