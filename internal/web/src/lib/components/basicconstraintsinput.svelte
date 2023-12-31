<script lang="ts">
	import { Checkbox, Label, NumberInput, Select, type SelectOptionType } from 'flowbite-svelte';
	import type { BasicConstraints } from '$lib/certs';

	export let extension: BasicConstraints;

	let pathLenConstraintOptions: SelectOptionType<number>[] = [
		{ name: '<none>', value: -1 },
		{ name: '0', value: 0 },
		{ name: '1', value: 1 },
		{ name: '2', value: 2 },
		{ name: '3', value: 3 },
		{ name: '4', value: 4 },
		{ name: '5', value: 5 },
		{ name: '6', value: 6 },
		{ name: '7', value: 7 },
		{ name: '8', value: 8 },
		{ name: '9', value: 9 }
	];
</script>

<div class="mb-6">
	<Checkbox bind:checked={extension.ca}>CA</Checkbox>
</div>
<div class="mb-6">
	<Label>
		Path lenght constraint
		<Select
			class="mt-2"
			items={pathLenConstraintOptions}
			bind:value={extension.pathLenConstraint}
		/>
	</Label>
</div>
