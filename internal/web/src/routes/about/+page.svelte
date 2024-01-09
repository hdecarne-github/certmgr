<script lang="ts">
	import { onMount } from 'svelte';
	import {
		Img,
		Heading,
		P,
		A,
		Breadcrumb,
		DarkMode,
		BreadcrumbItem,
		Button
	} from 'flowbite-svelte';
	import api, { type AboutInfo } from '$lib/api';
	import NavDrawer from '$lib/components/navdrawer.svelte';
	import { BarsOutline } from 'flowbite-svelte-icons';

	let navHidden: boolean;

	let aboutInfo: AboutInfo = {
		version: 'n/a',
		timestamp: 'n/a'
	};

	onMount(() => {
		api.about.get('..').then((response) => {
			aboutInfo = response;
		});
	});
</script>

<Breadcrumb aria-label="About" solid>
	<Button color="alternative" size="xs" on:click={() => (navHidden = false)}
		><BarsOutline size="xs" /></Button
	>
	<BreadcrumbItem href="/" home>
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
<Img src="../images/certmgr.svg" alt="CertMgr logo" size="max-w-lg" alignment="mx-auto" />
<Heading class="p-8" tag="h1" customSize="text-3xl">About CertMgr</Heading>
<P class="px-8 py-4">
	Version {aboutInfo.version} ({aboutInfo.timestamp}) - Copyright (C) 2015-2024 Holger de Carne and
	contributors
</P>
<P class="px-8 py-4">
	Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
	associated documentation files (the "Software"), to deal in the Software without restriction,
	including without limitation the rights to use, copy, modify, merge, publish, distribute,
	sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
</P>
<P class="px-8 py-4">
	The above copyright notice and this permission notice shall be included in all copies or
	substantial portions of the Software.
</P>
<P class="px-8 py-4">
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
	NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
	NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
	DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
	OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
</P>
<P class="px-8 py-4">
	You should have received a copy of the license along with this program. If not, see online <A
		href="https://raw.githubusercontent.com/hdecarne-github/certmgr/next/LICENSE">LICENSE</A
	> file.
</P>
