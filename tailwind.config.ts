import type { Config } from 'tailwindcss'

export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: 'var(--color-primary)',
          container: 'var(--color-primary-container)',
          on: 'var(--color-on-primary)',
          'on-container': 'var(--color-on-primary-container)',
        },
        surface: {
          DEFAULT: 'var(--color-surface)',
          variant: 'var(--color-surface-variant)',
        },
        'on-surface': {
          DEFAULT: 'var(--color-on-surface)',
          variant: 'var(--color-on-surface-variant)',
        },
        card: {
          DEFAULT: 'var(--color-card)',
          hover: 'var(--color-card-hover)',
        },
        outline: {
          DEFAULT: 'var(--color-outline)',
          variant: 'var(--color-outline-variant)',
        },
        error: {
          DEFAULT: 'var(--color-error)',
          on: 'var(--color-on-error)',
        },
        success: 'var(--color-success)',
        warning: 'var(--color-warning)',
        navy: {
          50: '#E8F1FF',
          100: '#D1E4FF',
          200: '#9ECAFF',
          300: '#6BB1FF',
          400: '#3898FF',
          500: '#0061A4',
          600: '#004D83',
          700: '#003A63',
          800: '#002744',
          900: '#001D35',
          950: '#000D1A',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
      },
      borderRadius: {
        xl: '12px',
        '2xl': '16px',
        '3xl': '24px',
      },
      animation: {
        'fade-in-up': 'fadeInUp 0.6s ease forwards',
        'slide-in-right': 'slideInRight 0.5s ease forwards',
        'pulse-slow': 'pulse 3s ease-in-out infinite',
      },
    },
  },
  plugins: [],
} satisfies Config
