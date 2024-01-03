<script lang="ts">
	import { Label, Input, Helper } from 'flowbite-svelte';

	export let label: string = 'Entry name';
	export let name: string;
	export const valid = {
		check(): boolean {
			if (name.trim().length == 0) {
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
	<Label for="nameinput" color={checkResult ? 'gray' : 'red'} class="mb-2 block">{label}</Label>
	<Input
		id="nameinput"
		color={checkResult ? 'base' : 'red'}
		placeholder="Enter entry name"
		bind:value={name}
		on:input={() => valid.check() }
	/>
	<Helper color="red">{checkMessage}</Helper>
</div>
