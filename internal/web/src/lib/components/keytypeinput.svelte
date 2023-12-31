<script lang="ts">
	import { Label, Select, type SelectOptionType } from 'flowbite-svelte';

	export let label: string = 'Key type';
	export let keyTypes: SelectOptionType<string>[];
	export let keyType: string;
</script>

<div class="mb-6">
	<Label>
		{label}
		<Select class="mt-2" items={keyTypes} bind:value={keyType} />
	</Label>
</div>
