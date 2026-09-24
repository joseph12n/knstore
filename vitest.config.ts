import { fileURLToPath } from 'node:url';

import { defineConfig, mergeConfig } from 'vitest/config';

import viteConfig from './vite.config.ts';

export default mergeConfig(
  viteConfig,
  defineConfig({
    test: {
      globals: true,
      environment: 'happy-dom',
      setupFiles: [fileURLToPath(new URL('./src/main/webapp/app/setup-tests.ts', import.meta.url))],
      reporters: ['default', 'vitest-sonar-reporter'],
      outputFile: {
        'vitest-sonar-reporter': fileURLToPath(new URL('./target/test-results/TESTS-results-vitest.xml', import.meta.url)),
      },
      coverage: {
        provider: 'v8',
        include: ['src/main/webapp/app/**/*.{ts,tsx}'],
        exclude: [
          'src/main/webapp/app/**/*.spec.{ts,tsx}',
          'src/main/webapp/app/setup-tests.ts',
          'src/main/webapp/app/**/*.d.ts',
          'src/main/webapp/app/entities/**',
          'src/main/webapp/app/modules/**',
          'src/main/webapp/app/shared/**',
        ],
        reportsDirectory: fileURLToPath(new URL('./target/test-results/lcov-report', import.meta.url)),
        thresholds: {
          statements: 45,
          branches: 35,
          functions: 40,
          lines: 45,
        },
      },
    },
  }),
);
