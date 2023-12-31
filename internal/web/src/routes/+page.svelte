<script lang="ts">
	import { onMount } from 'svelte';
	import {
		Sidebar,
		SidebarGroup,
		SidebarItem,
		SidebarWrapper,
		Button,
		Search,
		Toggle,
		Table,
		TableHead,
		TableHeadCell,
		TableBodyRow,
		TableBodyCell,
		Badge
	} from 'flowbite-svelte';
	import { CirclePlusOutline, FileCirclePlusOutline } from 'flowbite-svelte-icons';
	import MainNav from '$lib/components/mainnav.svelte';
	import api, { Entries, EntriesFilter } from '$lib/api';

	let entries: Entries = new Entries();

	onMount(() => {
		let entriesRange = new EntriesFilter();
		entriesRange.start = 0;
		entriesRange.limit = 100;
		api.entries.get('.', entriesRange).then((response) => {
			entries = response;
		});
	});
</script>

<MainNav base="." />
<div class="flex">
	<Sidebar>
		<SidebarWrapper>
			<SidebarGroup>
				<SidebarItem label="New certificate" href="./new">
					<svelte:fragment slot="icon">
						<CirclePlusOutline
							class="h-5 w-5 text-gray-500 transition duration-75 group-hover:text-gray-800 dark:text-gray-400 dark:group-hover:text-white"
						/>
					</svelte:fragment>
				</SidebarItem>
				<SidebarItem label="Import certificate(s)" href="./import">
					<svelte:fragment slot="icon">
						<FileCirclePlusOutline
							class="h-5 w-5 text-gray-500 transition duration-75 group-hover:text-gray-800 dark:text-gray-400 dark:group-hover:text-white"
						/>
					</svelte:fragment>
				</SidebarItem>
			</SidebarGroup>
			<SidebarGroup border>
				<Search>
					<Button>Search</Button>
				</Search>
			</SidebarGroup>
			<SidebarGroup border>
				<Toggle size="small" checked={true}>Active only</Toggle>
				<Toggle size="small" checked={true}>CA only</Toggle>
			</SidebarGroup>
		</SidebarWrapper>
	</Sidebar>
	<div class="flex-1">
		<Table>
			<TableHead>
				<TableHeadCell>Name</TableHeadCell>
				<TableHeadCell>Type</TableHeadCell>
				<TableHeadCell>Key</TableHeadCell>
				<TableHeadCell>DN</TableHeadCell>
				<TableHeadCell>Serial</TableHeadCell>
				<TableHeadCell>Valid from</TableHeadCell>
				<TableHeadCell>Valid to</TableHeadCell>
			</TableHead>
			{#each entries.entries as entry}
				<TableBodyRow>
					<TableBodyCell>{entry.name}</TableBodyCell>
					<TableBodyCell>
						{#if entry.key}
							<Badge color="green">Key</Badge>
						{/if}
						{#if entry.crt}
							<Badge color="dark">CRT</Badge>
						{/if}
						{#if entry.csr}
							<Badge color="indigo">CSR</Badge>
						{/if}
						{#if entry.crl}
							<Badge color="yellow">CRL</Badge>
						{/if}
						{#if entry.ca}
							<Badge>CA</Badge>
						{/if}
					</TableBodyCell>
					<TableBodyCell>{entry.keyType}</TableBodyCell>
					<TableBodyCell>{entry.dn}</TableBodyCell>
					<TableBodyCell>{entry.serial}</TableBodyCell>
					<TableBodyCell>
						{#if entry.crt}
							{entry.validFrom}
						{/if}
					</TableBodyCell>
					<TableBodyCell>
						{#if entry.crt}
							{entry.validTo}
						{/if}
					</TableBodyCell>
				</TableBodyRow>
			{/each}
		</Table>
	</div>
</div>
