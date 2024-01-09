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
		InfoCircleOutline
	} from 'flowbite-svelte-icons';

	let transition = {
		x: -320,
		duration: 200,
		easing: sineIn
	};
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
	<Sidebar>
		<SidebarWrapper>
			<SidebarGroup>
				<SidebarItem label="New certificate" href="{base}/new">
					<svelte:fragment slot="icon">
						<CirclePlusOutline
							class="h-5 w-5 text-gray-500 transition duration-75 group-hover:text-gray-800 dark:text-gray-400 dark:group-hover:text-white"
						/>
					</svelte:fragment>
				</SidebarItem>
				<SidebarItem label="Import certificate(s)" href="{base}/import">
					<svelte:fragment slot="icon">
						<FileCirclePlusOutline
							class="h-5 w-5 text-gray-500 transition duration-75 group-hover:text-gray-800 dark:text-gray-400 dark:group-hover:text-white"
						/>
					</svelte:fragment>
				</SidebarItem>
				<SidebarItem label="About" href="{base}/about">
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
