<template>
  <q-page class="flex flex-center">
    <q-card style="width: 360px; max-width: 90vw">
      <q-card-section>
        <div class="text-h6">Iniciar sesion</div>
        <div class="text-caption text-grey">Reto Spring Boot + Quasar</div>
      </q-card-section>

      <q-form @submit.prevent="ingresar">
        <q-card-section class="q-gutter-md">
          <q-input
            v-model="correo"
            type="email"
            label="Correo"
            :rules="[(v) => !!v || 'Requerido']"
            autofocus
          />
          <q-input
            v-model="password"
            :type="verPass ? 'text' : 'password'"
            label="Contrasena"
            :rules="[(v) => !!v || 'Requerido']"
          >
            <template #append>
              <q-icon
                :name="verPass ? 'visibility' : 'visibility_off'"
                class="cursor-pointer"
                @click="verPass = !verPass"
              />
            </template>
          </q-input>
        </q-card-section>

        <q-card-actions vertical class="q-pa-md">
          <q-btn type="submit" color="primary" label="Entrar" :loading="cargando" />
        </q-card-actions>
      </q-form>

      <q-card-section class="text-caption text-grey">
        Demo admin: admin@reto.com / Admin123*<br />
        Demo externo: externo@reto.com / Externo123*
      </q-card-section>
    </q-card>
  </q-page>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from 'stores/auth';
import { useRouter } from 'vue-router';
import { useQuasar } from 'quasar';

const correo = ref('');
const password = ref('');
const verPass = ref(false);
const cargando = ref(false);

const auth = useAuthStore();
const router = useRouter();
const $q = useQuasar();

async function ingresar() {
  cargando.value = true;
  try {
    await auth.login(correo.value, password.value);
    $q.notify({ type: 'positive', message: 'Bienvenido' });
    router.push(auth.isAdmin ? '/inventario' : '/empresas');
  } catch (e) {

    void e;
  } finally {
    cargando.value = false;
  }
}
</script>
