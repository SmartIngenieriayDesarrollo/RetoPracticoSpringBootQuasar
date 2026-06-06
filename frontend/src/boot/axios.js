import { boot } from 'quasar/wrappers';
import axios from 'axios';
import { Notify } from 'quasar';

const api = axios.create({
  baseURL: process.env.API_URL || 'http://localhost:8080'
});

export default boot(({ app, router, store }) => {

  api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  api.interceptors.response.use(
    (response) => response,
    (error) => {
      const status = error.response?.status;
      const mensaje = error.response?.data?.mensaje || error.message;

      if (status === 401) {

        localStorage.removeItem('token');
        localStorage.removeItem('roles');
        localStorage.removeItem('correo');
        Notify.create({ type: 'negative', message: 'Sesion expirada o credenciales invalidas' });
        router.push('/login');
      } else if (status === 403) {
        Notify.create({ type: 'negative', message: 'No tienes permisos para esta accion' });
      } else if (status) {
        Notify.create({ type: 'negative', message: mensaje });
      } else {
        Notify.create({ type: 'negative', message: 'No se pudo conectar con el servidor' });
      }
      return Promise.reject(error);
    }
  );

  app.config.globalProperties.$api = api;

  void store;
});

export { api };
