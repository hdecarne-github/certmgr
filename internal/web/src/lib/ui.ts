import { writable } from "svelte/store";
import type { Entry } from "./api";

const selectedEntry = writable('');

const dateTimeFormat = new Intl.DateTimeFormat((typeof navigator !== 'undefined' ? navigator.language : 'en'), { year: 'numeric', month: 'numeric', day: 'numeric' });

function dateToInput(date: Date): string {
    return date.toISOString().substring(0,10);
}

function inputToDate(input: string): Date {
    return new Date(input);    
}

function entryString(entry: Entry): string {
    return `${entry.name} (DN: ${entry.dn}, Serial: ${entry.serial}, Key: ${entry.keyType})`;
}

const ui = {
    selectedEntry,
    dateTimeFormat,
    dateToInput,
    inputToDate,
    entryString,
}

export default ui;