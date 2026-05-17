(function () {
  const API_BASE_URL = 'http://localhost:8080/api';
  const state = {
    mjesta: [],
    centri: [],
    editingId: null,
  };

  const elements = {};

  document.addEventListener('DOMContentLoaded', init);

  async function init() {
    bindElements();
    bindEvents();

    try {
      await loadMjesta();
      await loadCentri();
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  function bindElements() {
    elements.searchInput = document.getElementById('searchInput');
    elements.mjestoFilter = document.getElementById('mjestoFilter');
    elements.filterButton = document.getElementById('filterButton');
    elements.resetFilterButton = document.getElementById('resetFilterButton');
    elements.pageMessage = document.getElementById('pageMessage');
    elements.centriTableBody = document.getElementById('centriTableBody');
    elements.centriCount = document.getElementById('centriCount');
    elements.centarForm = document.getElementById('centarForm');
    elements.formTitle = document.getElementById('centarFormTitle');
    elements.resetFormButton = document.getElementById('resetFormButton');
    elements.nazivCentra = document.getElementById('nazivCentra');
    elements.adresa = document.getElementById('adresa');
    elements.idMjesto = document.getElementById('idMjesto');
    elements.radnoVrijemeTjedanOd = document.getElementById(
      'radnoVrijemeTjedanOd',
    );
    elements.radnoVrijemeTjedanDo = document.getElementById(
      'radnoVrijemeTjedanDo',
    );
    elements.radnoVrijemeVikendOd = document.getElementById(
      'radnoVrijemeVikendOd',
    );
    elements.radnoVrijemeVikendDo = document.getElementById(
      'radnoVrijemeVikendDo',
    );
    elements.saveCentarButton = document.getElementById('saveCentarButton');
  }

  function bindEvents() {
    elements.filterButton.addEventListener('click', loadCentri);
    elements.resetFilterButton.addEventListener('click', () => {
      elements.searchInput.value = '';
      elements.mjestoFilter.value = '';
      loadCentri();
    });
    elements.centarForm.addEventListener('submit', saveCentar);
    elements.resetFormButton.addEventListener('click', resetForm);
  }

  async function loadMjesta() {
    state.mjesta = await AppApi.request(`${API_BASE_URL}/mjesta`);
    AppApi.fillSelect(elements.mjestoFilter, state.mjesta, {
      placeholder: 'Sva mjesta',
      value: (mjesto) => mjesto.idMjesto,
      label: (mjesto) => `${mjesto.nazivMjesta} (${mjesto.postanskiBroj})`,
    });
    AppApi.fillSelect(elements.idMjesto, state.mjesta, {
      placeholder: 'Odaberi mjesto',
      value: (mjesto) => mjesto.idMjesto,
      label: (mjesto) => `${mjesto.nazivMjesta} (${mjesto.postanskiBroj})`,
    });
  }

  async function loadCentri() {
    const query = AppApi.buildQuery({
      search: elements.searchInput.value,
      mjestoId: elements.mjestoFilter.value,
    });

    state.centri = await AppApi.request(
      `${API_BASE_URL}/sportski-centri${query}`,
    );
    renderCentri();
    AppApi.showMessage(elements.pageMessage, '', null);
  }

  function renderCentri() {
    elements.centriCount.textContent = String(state.centri.length);
    elements.centriTableBody.replaceChildren();

    if (state.centri.length === 0) {
      renderEmptyRow('Nema sportskih centara za zadane kriterije.');
      return;
    }

    state.centri.forEach((centar) => {
      const row = document.createElement('tr');
      AppApi.appendTextCell(row, centar.nazivCentra);
      AppApi.appendTextCell(row, centar.adresa);
      AppApi.appendTextCell(row, centar.nazivMjesta);
      AppApi.appendTextCell(row, AppApi.formatWorkingHours(centar));

      const actions = document.createElement('td');
      const actionWrap = document.createElement('div');
      actionWrap.className = 'row-actions';

      const detailsButton = document.createElement('button');
      detailsButton.type = 'button';
      detailsButton.className = 'primary-button';
      detailsButton.textContent = 'Detalji';
      detailsButton.addEventListener('click', () => {
        window.location.href = `sportski-centar-detalji.html?id=${encodeURIComponent(centar.idCentar)}`;
      });

      const editButton = document.createElement('button');
      editButton.type = 'button';
      editButton.className = 'secondary-button';
      editButton.textContent = 'Uredi';
      editButton.addEventListener('click', () => fillForm(centar));

      actionWrap.append(detailsButton, editButton);
      actions.append(actionWrap);
      row.append(actions);
      elements.centriTableBody.append(row);
    });
  }

  function renderEmptyRow(message) {
    const row = document.createElement('tr');
    const cell = document.createElement('td');
    cell.colSpan = 5;
    cell.className = 'empty-state';
    cell.textContent = message;
    row.append(cell);
    elements.centriTableBody.append(row);
  }

  async function saveCentar(event) {
    event.preventDefault();
    const payload = buildCentarPayload();
    const path = state.editingId
      ? `${API_BASE_URL}/sportski-centri/${encodeURIComponent(state.editingId)}`
      : `${API_BASE_URL}/sportski-centri`;
    const method = state.editingId ? 'PUT' : 'POST';
    const wasEditing = Boolean(state.editingId);

    try {
      const saved = await AppApi.request(path, {
        method,
        body: JSON.stringify(payload),
      });
      AppApi.showMessage(
        elements.pageMessage,
        'Sportski centar je spremljen.',
        'success',
      );
      resetForm();
      await loadCentri();
      if (!wasEditing && saved && saved.idCentar) {
        window.location.href = `sportski-centar-detalji.html?id=${encodeURIComponent(saved.idCentar)}`;
      }
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  function buildCentarPayload() {
    return {
      nazivCentra: elements.nazivCentra.value.trim(),
      adresa: elements.adresa.value.trim(),
      idMjesto: Number(elements.idMjesto.value),
      radnoVrijemeTjedanOd: elements.radnoVrijemeTjedanOd.value || null,
      radnoVrijemeTjedanDo: elements.radnoVrijemeTjedanDo.value || null,
      radnoVrijemeVikendOd: elements.radnoVrijemeVikendOd.value || null,
      radnoVrijemeVikendDo: elements.radnoVrijemeVikendDo.value || null,
    };
  }

  function fillForm(centar) {
    state.editingId = centar.idCentar;
    elements.formTitle.textContent = 'Uredi centar';
    elements.saveCentarButton.textContent = 'Spremi promjene';
    elements.nazivCentra.value = centar.nazivCentra || '';
    elements.adresa.value = centar.adresa || '';
    elements.idMjesto.value = centar.idMjesto ? String(centar.idMjesto) : '';
    elements.radnoVrijemeTjedanOd.value = AppApi.formatTime(
      centar.radnoVrijemeTjedanOd,
    );
    elements.radnoVrijemeTjedanDo.value = AppApi.formatTime(
      centar.radnoVrijemeTjedanDo,
    );
    elements.radnoVrijemeVikendOd.value = AppApi.formatTime(
      centar.radnoVrijemeVikendOd,
    );
    elements.radnoVrijemeVikendDo.value = AppApi.formatTime(
      centar.radnoVrijemeVikendDo,
    );
    elements.nazivCentra.focus();
  }

  function resetForm() {
    state.editingId = null;
    elements.centarForm.reset();
    elements.formTitle.textContent = 'Novi centar';
    elements.saveCentarButton.textContent = 'Spremi centar';
  }
})();
