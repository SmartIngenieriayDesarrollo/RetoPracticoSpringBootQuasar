import MainLayout from 'layouts/MainLayout.vue';
import BlankLayout from 'layouts/BlankLayout.vue';
import EmpresasPage from 'pages/EmpresasPage.vue';
import ProductosPage from 'pages/ProductosPage.vue';
import InventarioPage from 'pages/InventarioPage.vue';
import LoginPage from 'pages/LoginPage.vue';
import ErrorNotFound from 'pages/ErrorNotFound.vue';

const routes = [
  {
    path: '/login',
    component: BlankLayout,
    children: [
      { path: '', component: LoginPage, meta: { public: true } }
    ]
  },
  {
    path: '/',
    component: MainLayout,
    children: [

      { path: '', redirect: '/empresas' },
      { path: 'empresas', component: EmpresasPage },

      { path: 'productos', component: ProductosPage, meta: { requiresAuth: true, requiresAdmin: true } },
      { path: 'inventario', component: InventarioPage, meta: { requiresAuth: true, requiresAdmin: true } }
    ]
  },

  {
    path: '/:catchAll(.*)*',
    component: BlankLayout,
    children: [
      { path: '', component: ErrorNotFound }
    ]
  }
];

export default routes;
