<script lang="ts">
	import NavDrawer from '$lib/components/navdrawer.svelte';
	import { Breadcrumb, BreadcrumbItem, Button, DarkMode } from 'flowbite-svelte';
	import { BarsOutline } from 'flowbite-svelte-icons';
	import { page } from '$app/stores';

	const base: string = "../..";

	let navHidden: boolean;

	let exportName: string;

	$: exportName = $page.params['name'];
</script>

<Breadcrumb aria-label="Export certificate" solid>
	<Button color="alternative" size="xs" on:click={() => (navHidden = false)}
		><BarsOutline size="xs" /></Button
	>
	<BreadcrumbItem href="{base}" home>
		<svelte:fragment slot="icon">
			<img src="{base}/images/certmgr.svg" class="me-3 h-6 sm:h-9" alt="CertMgr Logo" />
		</svelte:fragment>CertMgr</BreadcrumbItem
	>
	<BreadcrumbItem>Export certificate</BreadcrumbItem>
	<div class="absolute right-2">
		<DarkMode />
	</div>
</Breadcrumb>
<NavDrawer base="{base}" bind:hidden={navHidden} />
