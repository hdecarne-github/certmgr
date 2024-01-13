import { writable } from "svelte/store";

const selectedEntry = writable('');

const dateTimeFormat = new Intl.DateTimeFormat((typeof navigator !== 'undefined' ? navigator.language : 'en'), { year: 'numeric', month: 'numeric', day: 'numeric' });

function dateToInput(date: Date): string {
    return date.toISOString().substring(0,10);
}

function inputToDate(input: string): Date {
    return new Date(input);    
}

const ui = {
    selectedEntry,
    dateTimeFormat,
    dateToInput,
    inputToDate,
}

export default ui;