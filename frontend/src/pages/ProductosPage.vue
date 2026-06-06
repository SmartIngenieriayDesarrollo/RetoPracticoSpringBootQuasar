<template>
  <q-page class="page-padding">
    <div class="row items-center q-mb-md">
      <div class="text-h5">Productos</div>
      <q-space />
      <q-btn color="primary" icon="add" label="Registrar producto" :disable="!empresaSel" @click="abrirCrear" />
    </div>

    <q-select
      v-model="empresaSel"
      :options="opcionesEmpresa"
      label="Empresa"
      emit-value
      map-options
      outlined
      class="q-mb-md"
      style="max-width: 420px"
      @update:model-value="cargarProductos"
    />

    <q-table
      :rows="productos"
      :columns="columnas"
      row-key="id"
      :loading="cargando"
      flat
      bordered
    >

      <template #body-cell-precios="props">
        <q-td :props="props">
          <q-chip v-for="p in props.row.precios" :key="p.moneda" dense>
            {{ p.moneda }} {{ p.valor }}
          </q-chip>
        </q-td>
      </template>
      <template #body-cell-categorias="props">
        <q-td :props="props">
          <q-chip v-for="c in props.row.categorias" :key="c.id" dense color="secondary" text-color="white">
            {{ c.nombre }}
          </q-chip>
        </q-td>
      </template>
      <template #body-cell-acciones="props">
        <q-td :props="props">
          <q-btn dense flat icon="delete" color="negative" @click="eliminar(props.row)" />
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="dialogo">
      <q-card style="width: 520px; max-width: 95vw">
        <q-card-section class="text-h6">Nuevo producto</q-card-section>
        <q-form @submit.prevent="guardar">
          <q-card-section class="q-gutter-sm">
            <q-input v-model="form.codigo" label="Codigo" :rules="[req]" />
            <q-input v-model="form.nombre" label="Nombre" :rules="[req]" />
            <q-input v-model="form.caracteristicas" label="Caracteristicas" type="textarea" autogrow />

            <q-select
              v-model="form.categoriaIds"
              :options="opcionesCategoria"
              label="Categorias"
              multiple
              emit-value
              map-options
              use-chips
            />

            <div class="text-subtitle2 q-mt-sm">Precios</div>
            <div v-for="(p, i) in form.precios" :key="i" class="row q-gutter-sm items-center">
              <q-select
                v-model="p.moneda"
                :options="['COP', 'USD', 'EUR']"
                label="Moneda"
                style="width: 110px"
                :rules="[req]"
              />
              <q-input
                v-model.number="p.valor"
                type="number"
                step="0.01"
                label="Valor"
                style="width: 160px"
                :rules="[req]"
              />
              <q-btn dense flat icon="remove" color="negative" @click="form.precios.splice(i, 1)" />
            </div>
            <q-btn flat dense icon="add" label="Agregar precio" @click="form.precios.push({ moneda: 'COP', valor: null })" />
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
import { useQuasar } from 'quasar';

const $q = useQuasar();

const empresas = ref([]);
const empresaSel = ref(null);
const categorias = ref([]);
const productos = ref([]);
const cargando = ref(false);
const dialogo = ref(false);
const guardando = ref(false);
const form = ref(nuevoForm());

const req = (v) => (v !== null && v !== '' && v !== undefined) || 'Requerido';

const columnas = [
  { name: 'codigo', label: 'Codigo', field: 'codigo', align: 'left', sortable: true },
  { name: 'nombre', label: 'Nombre', field: 'nombre', align: 'left', sortable: true },
  { name: 'precios', label: 'Precios', field: 'precios', align: 'left' },
  { name: 'categorias', label: 'Categorias', field: 'categorias', align: 'left' },
  { name: 'acciones', label: 'Acciones', field: 'acciones', align: 'center' }
];

const opcionesEmpresa = ref([]);
const opcionesCategoria = ref([]);

function nuevoForm() {
  return { codigo: '', nombre: '', caracteristicas: '', categoriaIds: [], precios: [{ moneda: 'COP', valor: null }] };
}

async function cargarCatalogos() {
  const [emp, cat] = await Promise.all([
    api.get('/api/empresas'),
    api.get('/api/categorias')
  ]);
  empresas.value = emp.data;
  categorias.value = cat.data;
  opcionesEmpresa.value = emp.data.map((e) => ({ label: `${e.nombre} (${e.nit})`, value: e.nit }));
  opcionesCategoria.value = cat.data.map((c) => ({ label: c.nombre, value: c.id }));
}

async function cargarProductos() {
  if (!empresaSel.value) return;
  cargando.value = true;
  try {
    const { data } = await api.get('/api/productos', { params: { empresaNit: empresaSel.value } });
    productos.value = data;
  } finally {
    cargando.value = false;
  }
}

function abrirCrear() {
  form.value = nuevoForm();
  dialogo.value = true;
}

async function guardar() {
  guardando.value = true;
  try {
    await api.post('/api/productos', { ...form.value, empresaNit: empresaSel.value });
    $q.notify({ type: 'positive', message: 'Producto registrado' });
    dialogo.value = false;
    await cargarProductos();
  } catch (e) {
    void e;
  } finally {
    guardando.value = false;
  }
}

function eliminar(row) {
  $q.dialog({ title: 'Confirmar', message: `Eliminar ${row.nombre}?`, cancel: true }).onOk(async () => {
    await api.delete(`/api/productos/${row.id}`);
    $q.notify({ type: 'positive', message: 'Eliminado' });
    await cargarProductos();
  });
}

onMounted(cargarCatalogos);
</script>
