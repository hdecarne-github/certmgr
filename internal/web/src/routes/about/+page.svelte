<script lang="ts">
	import { onMount } from 'svelte';
	import { Img, Heading, P, A } from 'flowbite-svelte';
	import MainNav from '$lib/components/mainnav.svelte';
	import api, { type AboutInfo } from '$lib/api';

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

<MainNav base=".." />
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
