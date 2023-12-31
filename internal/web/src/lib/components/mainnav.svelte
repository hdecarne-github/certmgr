<script lang="ts">
	export let base: string;

	import { Navbar, NavBrand, NavHamburger, NavUl, NavLi, DarkMode } from 'flowbite-svelte';
	import { page } from '$app/stores';

	$: activeUrl = $page.url.pathname;
	$: rootUrl = new URL(base + '/', $page.url).pathname;
	$: aboutUrl = new URL(base + '/about/', $page.url).pathname;
</script>

<Navbar>
	<NavBrand href="{base}/">
		<img src="{base}/images/certmgr.svg" class="me-3 h-6 sm:h-9" alt="CertMgr Logo" />
		<span class="self-center whitespace-nowrap text-xl font-semibold dark:text-white">CertMgr</span>
	</NavBrand>
	<NavHamburger />
	<NavUl {activeUrl}>
		<NavLi href={rootUrl}>Certificates</NavLi>
		<NavLi href={aboutUrl}>About</NavLi>
	</NavUl>
	<DarkMode />
</Navbar>
