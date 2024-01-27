<script lang="ts">
	import { Table, TableBodyRow, TableBodyCell, TableBody, Modal, Hr } from 'flowbite-svelte';
	import { EntryDetails } from '$lib/api';

	export let details: EntryDetails;
	export let open: boolean;
</script>

<Modal title={details.name} bind:open={open} autoclose outsideclose>
	<Table noborder={true}>
		<TableBody>
			{#each details.groups as group}
				<TableBodyRow>
					<TableBodyCell class="py-2 text-right">{group.title}</TableBodyCell>
					<TableBodyCell class="py-2"><Hr hrClass="my-2" /></TableBodyCell>
				</TableBodyRow>
				{#each group.attributes as attribute}
					<TableBodyRow>
						<TableBodyCell class="py-1 text-right">{attribute.key}</TableBodyCell>
						<TableBodyCell class="py-1">{attribute.value}</TableBodyCell>
					</TableBodyRow>
				{/each}
			{/each}
		</TableBody>
	</Table>
</Modal>
