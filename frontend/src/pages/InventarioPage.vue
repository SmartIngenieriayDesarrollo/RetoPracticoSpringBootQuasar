<template>
  <q-page class="page-padding">
    <div class="row items-center q-mb-md">
      <div class="text-h5">Inventario</div>
      <q-space />

      <q-btn color="primary" icon="picture_as_pdf" label="Descargar PDF" class="q-mr-sm" :loading="descargando" @click="descargarPdf" />
      <q-btn color="secondary" icon="email" label="Enviar por correo" @click="dialogoCorreo = true" />
    </div>

    <q-table
      :rows="items"
      :columns="columnas"
      row-key="productoId"
      :loading="cargando"
      flat
      bordered
    >

      <template #body-cell-cantidad="props">
        <q-td :props="props">
          <q-input
            v-model.number="props.row.cantidad"
            type="number"
            dense
            style="width: 110px"
            @blur="actualizarCantidad(props.row)"
          />
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="dialogoCorreo">
      <q-card style="width: 420px; max-width: 90vw">
        <q-card-section class="text-h6">Enviar inventario por correo</q-card-section>
        <q-form @submit.prevent="enviarCorreo">
          <q-card-section>
            <q-input
              v-model="destinatario"
              type="email"
              label="Correo destinatario"
              :rules="[(v) => !!v || 'Requerido']"
            />
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat label="Cancelar" v-close-popup />
            <q-btn type="submit" color="primary" label="Enviar" :loading="enviando" />
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

const items = ref([]);
const cargando = ref(false);
const descargando = ref(false);
const enviando = ref(false);
const dialogoCorreo = ref(false);
const destinatario = ref('');

const columnas = [
  { name: 'codigo', label: 'Codigo', field: 'codigoProducto', align: 'left', sortable: true },
  { name: 'producto', label: 'Producto', field: 'nombreProducto', align: 'left', sortable: true },
  { name: 'empresa', label: 'Empresa', field: 'empresaNombre', align: 'left' },
  { name: 'cantidad', label: 'Cantidad', field: 'cantidad', align: 'left' }
];

async function cargar() {
  cargando.value = true;
  try {
    const { data } = await api.get('/api/inventario');
    items.value = data;
  } finally {
    cargando.value = false;
  }
}

async function descargarPdf() {
  descargando.value = true;
  try {
    // responseType blob para recibir el binario del pdf
    const { data } = await api.get('/api/inventario/pdf', { responseType: 'blob' });
    const url = window.URL.createObjectURL(new Blob([data], { type: 'application/pdf' }));
    const a = document.createElement('a');
    a.href = url;
    a.download = 'inventario.pdf';
    a.click();
    window.URL.revokeObjectURL(url);
  } finally {
    descargando.value = false;
  }
}

async function enviarCorreo() {
  enviando.value = true;
  try {
    await api.post('/api/inventario/enviar', { destinatario: destinatario.value });
    $q.notify({ type: 'positive', message: 'Inventario enviado' });
    dialogoCorreo.value = false;
  } catch (e) {
    void e;
  } finally {
    enviando.value = false;
  }
}

async function actualizarCantidad(row) {
  try {
    await api.post('/api/inventario/cantidad', null, {
      params: { productoId: row.productoId, cantidad: row.cantidad }
    });
    $q.notify({ type: 'positive', message: 'Cantidad actualizada', timeout: 800 });
  } catch (e) {
    void e;
    await cargar();
  }
}

onMounted(cargar);
</script>
