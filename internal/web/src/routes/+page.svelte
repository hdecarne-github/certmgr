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
		Badge,
		TableBody,
		Modal,
		Hr,
		Drawer,
		CloseButton
	} from 'flowbite-svelte';
	import {
		ChevronDoubleRightOutline,
		CirclePlusOutline,
		DotsHorizontalOutline,
		FileCirclePlusOutline,
		InfoCircleOutline
	} from 'flowbite-svelte-icons';
	import MainNav from '$lib/components/mainnav.svelte';
	import api, { Entries, EntriesFilter, EntryDetails } from '$lib/api';
	import ui from '$lib/ui';
	import { sineIn } from 'svelte/easing';

	let drawerHidden = true;
	let drawerTransition = {
		x: -320,
		duration: 200,
		easing: sineIn
	};

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

<MainNav base="." />
<Drawer transitionType="fly" transitionParams={drawerTransition} bind:hidden={drawerHidden} id="sidebar2">
	<div class="flex items-center">
		<h5
			id="drawer-navigation-label-3"
			class="text-base font-semibold uppercase text-gray-500 dark:text-gray-400"
		>
			Menu
		</h5>
		<CloseButton on:click={() => (drawerHidden = true)} class="mb-4 dark:text-white" />
	</div>
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
				<Toggle size="small" checked={false}>CA only</Toggle>
			</SidebarGroup>
		</SidebarWrapper>
	</Sidebar>
</Drawer>
<div class="flex-auto overflow-scroll">
	<Table>
		<TableHead>
			<TableHeadCell colspan="2"
				><Button size="xs" on:click={() => (drawerHidden = false)}><ChevronDoubleRightOutline size="xs"/></Button></TableHeadCell
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
						<InfoCircleOutline on:click={() => onDetails(entry.name)} />
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
