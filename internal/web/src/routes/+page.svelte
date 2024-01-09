<script lang="ts">
	import { onMount } from 'svelte';
	import {
		Button,
		Table,
		TableHead,
		TableHeadCell,
		TableBodyRow,
		TableBodyCell,
		Badge,
		TableBody,
		Modal,
		Hr,
		Breadcrumb,
		BreadcrumbItem,
		DarkMode
	} from 'flowbite-svelte';
	import {
	BarsOutline,
		DotsHorizontalOutline,
		InfoCircleOutline
	} from 'flowbite-svelte-icons';
	import api, { Entries, EntriesFilter, EntryDetails } from '$lib/api';
	import ui from '$lib/ui';
	import NavDrawer from '$lib/components/navdrawer.svelte';

	let navHidden = true;

	let entries: Entries = new Entries();
	let entryDetails: EntryDetails = new EntryDetails();
	let displayDetails: boolean = false;

	onMount(() => {
		let entriesRange = new EntriesFilter();
		entriesRange.start = 0;
		entriesRange.limit = 100;
		api.entries.get('.', entriesRange).then((response) => {
			entries = response;
		});
	});

	function onDetails(name: string) {
		api.details.get('.', name).then((response) => {
			entryDetails = response;
			displayDetails = true;
		});
	}

	function onActions(name: string) {}
</script>

<Breadcrumb aria-label="Certificates" solid>
	<Button color="alternative" size="xs" on:click={() => (navHidden = false)}
		><BarsOutline size="xs" /></Button
	>
	<BreadcrumbItem href="/" home>
		<svelte:fragment slot="icon">
			<img src="./images/certmgr.svg" class="me-3 h-6 sm:h-9" alt="CertMgr Logo" />
		</svelte:fragment>Certificates</BreadcrumbItem
	>
	<div class="absolute right-2">
		<DarkMode />
	</div>
</Breadcrumb>
<NavDrawer base="." bind:hidden={navHidden} />
<div class="flex-auto overflow-scroll">
	<Table>
		<TableHead>
			<TableHeadCell colspan="2"></TableHeadCell
			>
			<TableHeadCell>Name</TableHeadCell>
			<TableHeadCell>Type</TableHeadCell>
			<TableHeadCell>Key</TableHeadCell>
			<TableHeadCell>DN</TableHeadCell>
			<TableHeadCell>Serial</TableHeadCell>
			<TableHeadCell>Valid from</TableHeadCell>
			<TableHeadCell>Valid to</TableHeadCell>
		</TableHead>
		<TableBody>
			{#each entries.entries as entry}
				<TableBodyRow>
					<TableBodyCell>
						<InfoCircleOutline size="sm" on:click={() => onDetails(entry.name)} />
					</TableBodyCell>
					<TableBodyCell>
						<DotsHorizontalOutline on:click={() => onActions(entry.name)} />
					</TableBodyCell>
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
							{ui.dateToInput(new Date(entry.validFrom))}
						{/if}
					</TableBodyCell>
					<TableBodyCell>
						{#if entry.crt}
							{ui.dateToInput(new Date(entry.validFrom))}
						{/if}
					</TableBodyCell>
				</TableBodyRow>
			{/each}
		</TableBody>
	</Table>
</div>
<Modal title={entryDetails.name} bind:open={displayDetails} autoclose outsideclose>
	<Table noborder={true}>
		<TableBody>
			{#each entryDetails.groups as group}
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
