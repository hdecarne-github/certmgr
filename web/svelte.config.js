import adapter from '@sveltejs/adapter-static';
import { vitePreprocess } from '@sveltejs/kit/vite';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	preprocess: vitePreprocess(),

	kit: {
        adapter: adapter({
            pages: '../internal/server/htdocs',
            assets: '../internal/server/htdocs',
            fallback: undefined,
            precompress: false,
            strict: true
        }),
		paths: {
			relative: true
		}
	}
};

export default config;
