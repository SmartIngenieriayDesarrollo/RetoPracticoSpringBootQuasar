import { route } from 'quasar/wrappers';
import { createRouter, createMemoryHistory, createWebHistory, createWebHashHistory } from 'vue-router';
import { useAuthStore } from 'stores/auth';
import routes from './routes';

export default route(function () {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : (process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory);

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,
    history: createHistory(process.env.VUE_ROUTER_BASE)
  });

  Router.beforeEach((to) => {
    const auth = useAuthStore();

    // guard de roles el backend igual valida con jwt
    if (to.meta.requiresAdmin && !auth.isAdmin) {
      return { path: auth.isAuthenticated ? '/empresas' : '/login' };
    }
    if (to.meta.requiresAuth && !auth.isAuthenticated) {
      return { path: '/login' };
    }
    return true;
  });

  return Router;
});
