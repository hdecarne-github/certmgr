<script lang="ts">
	export let base: string;
	export let hidden: boolean;

	import {
		Drawer,
		CloseButton,
		Sidebar,
		SidebarWrapper,
		SidebarItem,
		SidebarGroup
	} from 'flowbite-svelte';
	import { sineIn } from 'svelte/easing';
	import {
		CirclePlusOutline,
		FileCirclePlusOutline,
		HomeOutline,
		InfoCircleOutline
	} from 'flowbite-svelte-icons';
	import { page } from '$app/stores';

	let transition = {
		x: -320,
		duration: 200,
		easing: sineIn
	};

	$: activeUrl = $page.url.pathname;
	$: baseUrl = new URL(base + '/', $page.url).pathname;
	$: newUrl = new URL(base + '/new/', $page.url).pathname;
	$: importUrl = new URL(base + '/import/', $page.url).pathname;
	$: aboutUrl = new URL(base + '/about/', $page.url).pathname;
</script>

<Drawer transitionType="fly" transitionParams={transition} bind:hidden id="sidebar2">
	<div class="flex items-center">
		<h5
			id="nav-drawer-label"
			class="text-base font-semibold uppercase text-gray-500 dark:text-gray-400"
		>
			Menu
		</h5>
		<CloseButton on:click={() => (hidden = true)} class="mb-4 dark:text-white" />
	</div>
	<Sidebar {activeUrl}>
		<SidebarWrapper>
			<SidebarGroup>
				<SidebarItem label="Certificates" href="{baseUrl}">
					<svelte:fragment slot="icon">
						<HomeOutline
							class="h-5 w-5 text-gray-500 transition duration-75 group-hover:text-gray-800 dark:text-gray-400 dark:group-hover:text-white"
						/>
					</svelte:fragment>
				</SidebarItem>
				<SidebarItem label="New certificate" href="{newUrl}">
					<svelte:fragment slot="icon">
						<CirclePlusOutline
							class="h-5 w-5 text-gray-500 transition duration-75 group-hover:text-gray-800 dark:text-gray-400 dark:group-hover:text-white"
						/>
					</svelte:fragment>
				</SidebarItem>
				<SidebarItem label="Import certificate(s)" href="{importUrl}">
					<svelte:fragment slot="icon">
						<FileCirclePlusOutline
							class="h-5 w-5 text-gray-500 transition duration-75 group-hover:text-gray-800 dark:text-gray-400 dark:group-hover:text-white"
						/>
					</svelte:fragment>
				</SidebarItem>
				<SidebarItem label="About" href="{aboutUrl}">
					<svelte:fragment slot="icon">
						<InfoCircleOutline
							class="h-5 w-5 text-gray-500 transition duration-75 group-hover:text-gray-800 dark:text-gray-400 dark:group-hover:text-white"
						/>
					</svelte:fragment>
				</SidebarItem>
			</SidebarGroup>
		</SidebarWrapper>
	</Sidebar>
</Drawer>
