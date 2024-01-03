<script lang="ts">
	import { Label, Input, Helper } from 'flowbite-svelte';

	export let label: string = 'Domain(s)';
	export let domains: string;
	export const valid = {
		check(): boolean {
			if (domains.trim().length == 0) {
				checkResult = false;
				checkMessage = 'Cannot be empty';
				return false;
			}
			// This pattern is not exhaustive, but sufficient for a 1st check (full check is done by ACME provider)
			const pattern = /^(((([^\s.,])+\.)+([^\s.,]+))\s*,\s*)*(([^\s.,])+\.)+([^\s.,]+)$/i;
			if (!pattern.test(domains.trim())) {
				checkResult = false;
				checkMessage = 'Enter a comma separated list for domains';
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
	<Label for="domainsinput" color={checkResult ? 'gray' : 'red'} class="mb-2 block">{label}</Label>
	<Input
		id="domainsinput"
		color={checkResult ? 'base' : 'red'}
		placeholder="Enter domain names"
		bind:value={domains}
		on:input={() => valid.check()}
	/>
	<Helper color="red">{checkMessage}</Helper>
</div>
