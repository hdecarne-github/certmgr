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
		Breadcrumb,
		BreadcrumbItem,
		DarkMode,
		Dropdown,
		DropdownItem,
		A
	} from 'flowbite-svelte';
	import { BarsOutline, DotsVerticalOutline } from 'flowbite-svelte-icons';
	import { goto } from '$app/navigation';
	import NavDrawer from '$lib/components/navdrawer.svelte';
	import EntryDetailsModal from '$lib/components/entrydetailsmodal.svelte';
	import api, { Entries, EntriesFilter, EntryDetails } from '$lib/api';
	import certs from '$lib/certs';
	import ui from '$lib/ui';
	import { applyAction } from '$app/forms';

	const base: string = '.';

	let navHidden = true;

	let entries: Entries = new Entries();

	let entryDetails: EntryDetails = new EntryDetails();
	let openDetails: boolean = false;

	onMount(() => {
		reload();
	});

	function reload() {
		let entriesRange = new EntriesFilter();
		entriesRange.start = 0;
		entriesRange.limit = 100;
		api.entries.get(base, entriesRange).then((response) => {
			entries = response;
		});
	}

	function onDetails(name: string) {
		api.details.get(base, name).then((response) => {
			entryDetails = response;
			openDetails = true;
		});
	}
	
	function onExport(name: string) {
		goto(base + '/export/' + name);
	}


	function onDelete(name: string) {
		api.del.delete(base, name).then((response) => {
			reload();
			goto(base);
		});
	}

	function expiryColor(date: Date): 'red' | 'yellow' | 'none' {
		switch (certs.checkValidTo(date)) {
			case 'expired':
				return 'red';
			case 'expiring':
				return 'yellow';
			default:
				return 'none';
		}
	}
</script>

<Breadcrumb aria-label="Certificates" solid>
	<Button color="alternative" size="xs" on:click={() => (navHidden = false)}
		><BarsOutline size="xs" /></Button
	>
	<BreadcrumbItem href="{base}" home>
		<svelte:fragment slot="icon">
			<img src="./images/certmgr.svg" class="me-3 h-6 sm:h-9" alt="CertMgr Logo" />
		</svelte:fragment>CertMgr</BreadcrumbItem
	>
	<div class="absolute right-2">
		<DarkMode />
	</div>
</Breadcrumb>
<NavDrawer base="{base}" bind:hidden={navHidden} />
<div class="flex-auto overflow-scroll">
	<Table>
		<TableHead>
			<TableHeadCell>&nbsp;</TableHeadCell>
			<TableHeadCell>Name</TableHeadCell>
			<TableHeadCell>Type</TableHeadCell>
			<TableHeadCell>DN</TableHeadCell>
			<TableHeadCell>Serial</TableHeadCell>
			<TableHeadCell>Expires</TableHeadCell>
		</TableHead>
		<TableBody>
			{#each entries.entries as entry, entryIndex}
				<TableBodyRow>
					<TableBodyCell>
						<DotsVerticalOutline class="dots-menu{entryIndex} dark:text-white" />
						<Dropdown placement="right" triggeredBy=".dots-menu{entryIndex}">
							<DropdownItem on:click={() => onDetails(entry.name)}>Details</DropdownItem>
							<DropdownItem on:click={() => onExport(entry.name)}>Export</DropdownItem>
							<DropdownItem on:click={() => onDelete(entry.name)}>Delete</DropdownItem>
						</Dropdown>
					</TableBodyCell>
					<TableBodyCell on:click={() => onDetails(entry.name)}>{entry.name}</TableBodyCell>
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
					<TableBodyCell>{entry.dn}</TableBodyCell>
					<TableBodyCell>{entry.serial}</TableBodyCell>
					<TableBodyCell>
						{#if entry.crt}
							<Badge color={expiryColor(new Date(entry.validTo))}
								>{ui.dateTimeFormat.format(new Date(entry.validTo))}</Badge
							>
						{/if}
					</TableBodyCell>
				</TableBodyRow>
			{/each}
		</TableBody>
	</Table>
</div>
<EntryDetailsModal bind:details={entryDetails} bind:open={openDetails} />