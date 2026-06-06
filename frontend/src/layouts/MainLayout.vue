<template>
  <q-layout view="hHh lpR fFf">

    <q-header elevated>
      <q-toolbar>
        <q-toolbar-title>Reto Inventario</q-toolbar-title>

        <q-btn flat label="Empresas" to="/empresas" />
        <q-btn v-if="auth.isAdmin" flat label="Productos" to="/productos" />
        <q-btn v-if="auth.isAdmin" flat label="Inventario" to="/inventario" />

        <q-space />

        <template v-if="auth.isAuthenticated">
          <div class="q-mr-sm">{{ auth.correo }}</div>
          <q-btn flat icon="logout" label="Salir" @click="salir" />
        </template>
        <q-btn v-else flat icon="login" label="Ingresar" to="/login" />
      </q-toolbar>
    </q-header>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { useAuthStore } from 'stores/auth';
import { useRouter } from 'vue-router';

const auth = useAuthStore();
const router = useRouter();

function salir() {
  auth.logout();
  router.push('/login');
}
</script>
