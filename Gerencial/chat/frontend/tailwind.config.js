/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        whatsapp: {
          green: '#25d366',
          dark: '#075e54',
          light: '#128c7e',
          teal: '#00a884',
        },
        chat: {
          bg: '#0b141a',
          panel: '#0b141a',
          header: '#202c33',
          input: '#2a3942',
          'bubble-out': '#005c4b',
          'bubble-in': '#202c33',
        },
      },
    },
  },
  plugins: [],
}
