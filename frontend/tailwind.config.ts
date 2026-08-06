import type { Config } from 'tailwindcss'

const config: Config = {
  content: [
    './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
    './src/components/**/*.{js,ts,jsx,tsx,mdx}',
    './src/app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        slate: {
          950: '#030712',
        },
        cyan: {
          900: '#164e63',
        },
        blue: {
          900: '#1e3a8a',
        },
      },
    },
  },
  plugins: [],
}
export default config
