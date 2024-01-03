<script lang="ts">
	import { Label, Input, Helper } from 'flowbite-svelte';

	export let validFromLabel: string = 'Valid from';
	export let validToLabel: string = 'Valid to';
	export let validFrom: string;
	export let validTo: string;
	export const valid = {
		check(): boolean {
			let validFromValue: number = Date.parse(validFrom);
			if (isNaN(validFromValue)) {
				checkValidFromResult = false;
				checkValidFromMessage = 'Enter valid date';
			} else {
				checkValidFromResult = true;
				checkValidFromMessage = '';
			}
			let validToValue: number = Date.parse(validTo);
			if (isNaN(validToValue)) {
				checkValidToResult = false;
				checkValidToMessage = 'Enter valid date';
			} else {
				checkValidToResult = true;
				checkValidToMessage = '';
			}
			if (!checkValidFromResult || !checkValidToResult) {
				return false;
			}
			if (validFromValue >= validToValue) {
				checkValidFromResult = false;
				checkValidFromMessage = 'Must be before ' + validToLabel;
				checkValidToResult = false;
				checkValidToMessage = 'Must be after ' + validFromLabel;
				return false;
			}
			checkValidFromResult = true;
			checkValidFromMessage = '';
			checkValidToResult = true;
			checkValidToMessage = '';
			return true;
		}
	};
	let checkValidFromResult: boolean = true;
	let checkValidFromMessage: string = '';
	let checkValidToResult: boolean = true;
	let checkValidToMessage: string = '';
</script>

<div class="mb-6 grid gap-6 md:grid-cols-2">
	<div>
		<Label for="validfrominput" color={checkValidFromResult ? 'gray' : 'red'} class="mb-2 block"
			>{validFromLabel}</Label
		>
		<Input
			id="validfrominput"
			color={checkValidFromResult ? 'base' : 'red'}
			type="date"
			bind:value={validFrom}
			on:input={() => valid.check()}
		/>
		<Helper color="red">{checkValidFromMessage}</Helper>
	</div>
	<div>
		<Label for="validtoinput" color={checkValidToResult ? 'gray' : 'red'} class="mb-2 block"
			>{validToLabel}</Label
		>
		<Input
			id="validtoinput"
			color={checkValidToResult ? 'base' : 'red'}
			type="date"
			bind:value={validTo}
			on:input={() => valid.check()}
		/>
		<Helper color="red">{checkValidToMessage}</Helper>
	</div>
</div>
