import { configure } from 'quasar/wrappers';

export default configure(function () {
  return {

    boot: ['pinia', 'axios'],

    css: ['app.scss'],

    extras: ['roboto-font', 'material-icons'],

    build: {
      target: {
        browser: ['es2022', 'edge88', 'firefox78', 'chrome87', 'safari13.1'],
        node: 'node20'
      },
      vueRouterMode: 'history',

      env: {
        API_URL: process.env.API_URL || 'http://localhost:8080'
      }
    },

    devServer: {
      open: false,
      port: 9000
    },

    framework: {

      plugins: ['Notify', 'Dialog', 'LoadingBar']
    }
  };
});
