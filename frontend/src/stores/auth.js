import { defineStore } from 'pinia';
import { api } from 'src/boot/axios';

export const useAuthStore = defineStore('auth', {

  state: () => ({
    token: localStorage.getItem('token') || null,
    correo: localStorage.getItem('correo') || null,
    roles: JSON.parse(localStorage.getItem('roles') || '[]')
  }),

  getters: {

    isAuthenticated: (state) => !!state.token,

    isAdmin: (state) => state.roles.includes('ADMIN')
  },

  actions: {

    async login(correo, password) {
      const { data } = await api.post('/api/auth/login', { correo, password });
      this.token = data.token;
      this.correo = data.correo;
      this.roles = data.roles;
      localStorage.setItem('token', data.token);
      localStorage.setItem('correo', data.correo);
      localStorage.setItem('roles', JSON.stringify(data.roles));
    },

    logout() {
      this.token = null;
      this.correo = null;
      this.roles = [];
      localStorage.removeItem('token');
      localStorage.removeItem('correo');
      localStorage.removeItem('roles');
    }
  }
});
