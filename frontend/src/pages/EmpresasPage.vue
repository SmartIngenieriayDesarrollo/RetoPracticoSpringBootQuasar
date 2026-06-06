<template>
  <q-page class="page-padding">
    <div class="row items-center q-mb-md">
      <div class="text-h5">Empresas</div>
      <q-space />

      <q-btn v-if="auth.isAdmin" color="primary" icon="add" label="Nueva empresa" @click="abrirCrear" />
    </div>

    <q-table
      :rows="empresas"
      :columns="columnas"
      row-key="nit"
      :loading="cargando"
      flat
      bordered
    >

      <template #body-cell-acciones="props">
        <q-td :props="props">
          <template v-if="auth.isAdmin">
            <q-btn dense flat icon="edit" @click="abrirEditar(props.row)" />
            <q-btn dense flat icon="delete" color="negative" @click="eliminar(props.row)" />
          </template>
          <span v-else class="text-grey">-</span>
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="dialogo">
      <q-card style="width: 420px; max-width: 90vw">
        <q-card-section class="text-h6">{{ editando ? 'Editar' : 'Nueva' }} empresa</q-card-section>
        <q-form @submit.prevent="guardar">
          <q-card-section class="q-gutter-sm">

            <q-input v-model="form.nit" label="NIT" :disable="editando" :rules="[req]" />
            <q-input v-model="form.nombre" label="Nombre" :rules="[req]" />
            <q-input v-model="form.direccion" label="Direccion" />
            <q-input v-model="form.telefono" label="Telefono" />
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat label="Cancelar" v-close-popup />
            <q-btn type="submit" color="primary" label="Guardar" :loading="guardando" />
          </q-card-actions>
        </q-form>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { api } from 'src/boot/axios';
import { useAuthStore } from 'stores/auth';
import { useQuasar } from 'quasar';

const auth = useAuthStore();
const $q = useQuasar();

const empresas = ref([]);
const cargando = ref(false);
const dialogo = ref(false);
const editando = ref(false);
const guardando = ref(false);
const form = ref({ nit: '', nombre: '', direccion: '', telefono: '' });

const req = (v) => !!v || 'Requerido';

const columnas = [
  { name: 'nit', label: 'NIT', field: 'nit', align: 'left', sortable: true },
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left', sortable: true },
  { name: 'direccion', label: 'Direccion', field: 'direccion', align: 'left' },
  { name: 'telefono', label: 'Telefono', field: 'telefono', align: 'left' },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' }
];

async function cargar() {
  cargando.value = true;
  try {
    const { data } = await api.get('/api/empresas');
    empresas.value = data;
  } finally {
    cargando.value = false;
  }
}

function abrirCrear() {
  editando.value = false;
  form.value = { nit: '', nombre: '', direccion: '', telefono: '' };
  dialogo.value = true;
}

function abrirEditar(row) {
  editando.value = true;
  form.value = { ...row };
  dialogo.value = true;
}

async function guardar() {
  guardando.value = true;
  try {
    if (editando.value) {
      await api.put(`/api/empresas/${form.value.nit}`, form.value);
    } else {
      await api.post('/api/empresas', form.value);
    }
    $q.notify({ type: 'positive', message: 'Guardado' });
    dialogo.value = false;
    await cargar();
  } catch (e) {
    void e;
  } finally {
    guardando.value = false;
  }
}

function eliminar(row) {
  $q.dialog({
    title: 'Confirmar',
    message: `Eliminar la empresa ${row.nombre}?`,
    cancel: true
  }).onOk(async () => {
    await api.delete(`/api/empresas/${row.nit}`);
    $q.notify({ type: 'positive', message: 'Eliminada' });
    await cargar();
  });
}

onMounted(cargar);
</script>
