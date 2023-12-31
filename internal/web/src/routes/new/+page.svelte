<script lang="ts">
	import { onMount } from 'svelte';
	import {
		Label,
		Input,
		Select,
		Button,
		type SelectOptionType,
		Accordion,
		AccordionItem,
		Toggle
	} from 'flowbite-svelte';
	import { goto } from '$app/navigation';
	import api, { GenerateLocal } from '$lib/api';
	import certs, { BasicConstraints, ExtKeyUsage, KeyUsage, KeyUsageFlag } from '$lib/certs';
	import ui from '$lib/ui';
	import SelfNav from '$lib/components/selfnav.svelte';
	import DnInput from '$lib/components/dninput.svelte';
	import KeyTypeInput from '$lib/components/keytypeinput.svelte';
	import IssuerInput from '$lib/components/issuerinput.svelte';
	import ValidityInput from '$lib/components/validityinput.svelte';
	import KeyUsageInput from '$lib/components/keyusageinput.svelte';
	import ExtKeyUsageInput from '$lib/components/extkeyusageinput.svelte';
	import BasicConstraintInput from '$lib/components/basicconstraintsinput.svelte';

	let selectedName: string = '';
	let cas: SelectOptionType<string>[] = [];
	let selectedCA: string = '';

	let selectedLocalDn: string = '';
	let localKeyTypes: SelectOptionType<string>[] = certs.defaultKeyTypes.map((keyType) => {
		return { name: keyType[0], value: keyType[1] };
	});
	let selectedLocalKeyType: string = '';
	let selectedLocalIssuer: string = '';
	let selectedLocalValidFrom: string = '';
	let selectedLocalValidTo: string = '';
	let localKeyUsageEnabled: boolean = false;
	let localKeyUsage: KeyUsage = new KeyUsage();
	let localExtKeyUsageEnabled: boolean = false;
	let localExtKeyUsage: ExtKeyUsage = new ExtKeyUsage();
	let localBasicConstraintEnabled: boolean = false;
	let localBasicConstraints: BasicConstraints = new BasicConstraints();

	let selectedRemoteDn: string = '';
	let remoteKeyTypes: SelectOptionType<string>[] = localKeyTypes;
	let selectedRemoteKeyType: string = '';

	let acmeKeyTypes: SelectOptionType<string>[] = certs.acmeKeyTypes.map((keyType) => {
		return { name: keyType[0], value: keyType[1] };
	});
	let selectedACMEKeyType: string = '';

	function onGenerate() {
		if (certs.isLocalCA(selectedCA)) {
			onGenerateLocal();
		} else if (certs.isRemoteCA(selectedCA)) {
		} else if (certs.isACMECA(selectedCA)) {
		}
	}

	function onGenerateLocal() {
		let generate = new GenerateLocal();
		generate.name = selectedName;
		generate.ca = selectedCA;
		generate.dn = selectedLocalDn;
		generate.keyType = selectedLocalKeyType;
		generate.issuer = selectedLocalIssuer;
		generate.validFrom = ui.inputToDate(selectedLocalValidFrom);
		generate.validTo = ui.inputToDate(selectedLocalValidTo);
		if (localKeyUsageEnabled) {
			generate.keyUsage = localKeyUsage.toSpec();
		}
		if (localExtKeyUsageEnabled) {
			generate.extKeyUsage = localExtKeyUsage.toSpec();
		}
		if (localBasicConstraintEnabled) {
			generate.basicConstraints = localBasicConstraints.toSpec();
		}
		api.generateLocal.put('..', generate).then((response) => {
			console.log(response);
		});
	}

	function onCancel() {
		goto('..');
	}

	onMount(() => {
		api.cas.get('..').then((response) => {
			cas = response.cas.map((ca) => {
				return { name: ca.name, value: ca.name };
			});
		});
		let defaultValidFrom = new Date();
		let defaultValidTo = new Date();
		defaultValidTo.setMonth(defaultValidTo.getMonth() + 6);
		selectedLocalValidFrom = ui.dateToInput(defaultValidFrom);
		selectedLocalValidTo = ui.dateToInput(defaultValidTo);
	});
</script>

<SelfNav base=".." title="New Certificate" />
<div class="p-8">
	<div class="mb-6">
		<Label for="name-input" class="mb-2 block">Name</Label>
		<Input id="name-input" placeholder="Enter entry name" bind:value={selectedName} />
	</div>
	<div class="mb-6">
		<Label>
			Select a Certificate Authority
			<Select class="mt-2" items={cas} bind:value={selectedCA} />
		</Label>
	</div>
	{#if certs.isLocalCA(selectedCA)}
		<DnInput label="Certificate DN" bind:dn={selectedLocalDn} />
		<KeyTypeInput keyTypes={localKeyTypes} bind:keyType={selectedLocalKeyType} />
		<IssuerInput
			selfsigned={true}
			keyUsage={KeyUsageFlag.KeyCertSign}
			bind:issuer={selectedLocalIssuer}
		/>
		<ValidityInput bind:validFrom={selectedLocalValidFrom} bind:validTo={selectedLocalValidTo} />
		<div class="mb-6">
			<Accordion flush multiple>
				<AccordionItem>
					<span slot="header"
						><Toggle size="small" bind:checked={localKeyUsageEnabled}>Key usage extension</Toggle
						></span
					>
					<KeyUsageInput bind:extension={localKeyUsage} />
				</AccordionItem>
				<AccordionItem>
					<span slot="header"
						><Toggle size="small" bind:checked={localExtKeyUsageEnabled}
							>Extended key usage extension</Toggle
						></span
					>
					<ExtKeyUsageInput bind:extension={localExtKeyUsage} />
				</AccordionItem>
				<AccordionItem>
					<span slot="header"
						><Toggle size="small" bind:checked={localBasicConstraintEnabled}
							>Basic constraint extension</Toggle
						></span
					>
					<BasicConstraintInput bind:extension={localBasicConstraints} />
				</AccordionItem>
			</Accordion>
		</div>
	{/if}
	{#if certs.isRemoteCA(selectedCA)}
		<DnInput label="Certificate Request DN" dn={selectedRemoteDn} />
		<KeyTypeInput keyTypes={remoteKeyTypes} bind:keyType={selectedRemoteKeyType} />
	{/if}
	{#if certs.isACMECA(selectedCA)}
		<div class="mb-6">
			<Label for="acmedomains-input" class="mb-2 block">Domain(s)</Label>
			<Input id="acmedomains-input" placeholder="Enter certificate Domain(s)" />
		</div>
		<KeyTypeInput keyTypes={acmeKeyTypes} bind:keyType={selectedACMEKeyType} />
	{/if}
	<div class="mb-6">
		<Button type="submit" on:click={onGenerate}>Generate</Button>
		<Button color="alternative" on:click={onCancel}>Cancel</Button>
	</div>
</div>
