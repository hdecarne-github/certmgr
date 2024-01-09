<script lang="ts">
	import NavDrawer from '$lib/components/navdrawer.svelte';
	import { Breadcrumb, BreadcrumbItem, Button, DarkMode } from 'flowbite-svelte';
	import { BarsOutline } from 'flowbite-svelte-icons';

	let navHidden: boolean;
</script>

<Breadcrumb aria-label="Import certificate(s)" solid>
	<Button color="alternative" size="xs" on:click={() => (navHidden = false)}
		><BarsOutline size="xs" /></Button
	>
	<BreadcrumbItem href=".." home>
		<svelte:fragment slot="icon">
			<img src="../images/certmgr.svg" class="me-3 h-6 sm:h-9" alt="CertMgr Logo" />
		</svelte:fragment>Certificates</BreadcrumbItem
	>
	<BreadcrumbItem>New</BreadcrumbItem>
	<div class="absolute right-2">
		<DarkMode />
	</div>
</Breadcrumb>
<NavDrawer base=".." bind:hidden={navHidden} />
