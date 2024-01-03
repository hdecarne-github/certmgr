import { writable } from "svelte/store";

const selectedEntry = writable('');

function dateToInput(date: Date): string {
    return date.toISOString().substring(0,10);
}

function inputToDate(input: string): Date {
    return new Date(input);    
}

const ui = {
    selectedEntry,
    dateToInput,
    inputToDate,
}

export default ui;