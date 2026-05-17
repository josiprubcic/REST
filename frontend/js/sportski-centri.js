(function () {
  const API_BASE_URL = 'http://localhost:8080/api';

  const state = {
    mjesta: [],
    centri: [],
    editingId: null,
    editingMjestoId: null,
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

    elements.mjestoForm = document.getElementById('mjestoForm');
    elements.mjestoFormTitle = document.getElementById('mjestoFormTitle');
    elements.resetMjestoFormButton = document.getElementById(
      'resetMjestoFormButton',
    );
    elements.nazivMjesta = document.getElementById('nazivMjesta');
    elements.postanskiBroj = document.getElementById('postanskiBroj');
    elements.saveMjestoButton = document.getElementById('saveMjestoButton');
    elements.mjestaTableBody = document.getElementById('mjestaTableBody');
  }

  function bindEvents() {
    elements.filterButton.addEventListener('click', loadCentri);
    elements.resetFilterButton.addEventListener('click', resetFilter);
    elements.centarForm.addEventListener('submit', saveCentar);
    elements.resetFormButton.addEventListener('click', resetForm);

    elements.mjestoForm.addEventListener('submit', saveMjesto);
    elements.resetMjestoFormButton.addEventListener('click', resetMjestoForm);
  }

  async function loadMjesta() {
    state.mjesta = await AppApi.request(`${API_BASE_URL}/mjesta`);

    AppApi.fillSelect(elements.mjestoFilter, state.mjesta, {
      placeholder: 'Sva mjesta',
      value: (m) => m.idMjesto,
      label: (m) => `${m.nazivMjesta} (${m.postanskiBroj})`,
    });

    AppApi.fillSelect(elements.idMjesto, state.mjesta, {
      placeholder: 'Odaberite mjesto...',
      value: (m) => m.idMjesto,
      label: (m) => `${m.nazivMjesta} (${m.postanskiBroj})`,
    });

    renderMjestaTable();
  }

  function renderMjestaTable() {
    elements.mjestaTableBody.innerHTML = '';

    state.mjesta.forEach((mjesto) => {
      const row = document.createElement('tr');

      AppApi.appendTextCell(row, mjesto.nazivMjesta);
      AppApi.appendTextCell(row, mjesto.postanskiBroj);

      const actionsCell = document.createElement('td');
      const actionWrap = document.createElement('div');
      actionWrap.className = 'row-actions';

      const editBtn = document.createElement('button');
      editBtn.type = 'button';
      editBtn.className = 'secondary-button';
      editBtn.style = 'min-height: 28px; padding: 2px 8px; font-size: 13px;';
      editBtn.textContent = 'Uredi';
      editBtn.addEventListener('click', () => fillMjestoForm(mjesto));

      const deleteBtn = document.createElement('button');
      deleteBtn.type = 'button';
      deleteBtn.className = 'danger-button';
      deleteBtn.style = 'min-height: 28px; padding: 2px 8px; font-size: 13px;';
      deleteBtn.textContent = 'Obriši';
      deleteBtn.addEventListener('click', () => deleteMjesto(mjesto));

      actionWrap.append(editBtn, deleteBtn);
      actionsCell.append(actionWrap);
      row.append(actionsCell);

      elements.mjestaTableBody.append(row);
    });
  }

  async function saveMjesto(event) {
    event.preventDefault();
    AppApi.showMessage(elements.pageMessage, '');

    const mjestoData = {
      nazivMjesta: elements.nazivMjesta.value.trim(),
      postanskiBroj: elements.postanskiBroj.value.trim(),
    };

    const isEdit = state.editingMjestoId !== null;
    const url = isEdit
      ? `${API_BASE_URL}/mjesta/${state.editingMjestoId}`
      : `${API_BASE_URL}/mjesta`;
    const method = isEdit ? 'PUT' : 'POST';

    try {
      await AppApi.request(url, {
        method,
        body: JSON.stringify(mjestoData),
      });

      AppApi.showMessage(
        elements.pageMessage,
        `Mjesto je uspješno ${isEdit ? 'ažurirano' : 'dodano'}.`,
        'success',
      );

      resetMjestoForm();
      await loadMjesta();
      await loadCentri();
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  async function deleteMjesto(mjesto) {
    if (
      !confirm(
        `Jeste li sigurni da želite obrisati mjesto "${mjesto.nazivMjesta}"?`,
      )
    ) {
      return;
    }

    try {
      await AppApi.request(`${API_BASE_URL}/mjesta/${mjesto.idMjesto}`, {
        method: 'DELETE',
      });

      AppApi.showMessage(
        elements.pageMessage,
        'Mjesto uspješno obrisano.',
        'success',
      );
      await loadMjesta();
      await loadCentri();
    } catch (error) {
      AppApi.showMessage(
        elements.pageMessage,
        'Nije moguće obrisati mjesto. Provjerite koristi li ga neki sportski centar.',
        'error',
      );
    }
  }

  function fillMjestoForm(mjesto) {
    state.editingMjestoId = mjesto.idMjesto;
    elements.mjestoFormTitle.textContent = 'Uredi mjesto';
    elements.saveMjestoButton.textContent = 'Spremi promjene';
    elements.nazivMjesta.value = mjesto.nazivMjesta || '';
    elements.postanskiBroj.value = mjesto.postanskiBroj || '';
    elements.nazivMjesta.focus();
  }

  function resetMjestoForm() {
    state.editingMjestoId = null;
    elements.mjestoForm.reset();
    elements.mjestoFormTitle.textContent = 'Novo mjesto';
    elements.saveMjestoButton.textContent = 'Spremi mjesto';
  }

  async function loadCentri() {
    const params = {
      search: elements.searchInput.value,
      idMjesto: elements.mjestoFilter.value,
    };
    const query = AppApi.buildQuery(params);

    state.centri = await AppApi.request(
      `${API_BASE_URL}/sportski-centri${query}`,
    );
    renderCentriTable();
  }

  function renderCentriTable() {
    elements.centriTableBody.innerHTML = '';
    elements.centriCount.textContent = String(state.centri.length);

    if (state.centri.length === 0) {
      const row = document.createElement('tr');
      const cell = document.createElement('td');
      cell.colSpan = 5;
      cell.className = 'empty-state';
      cell.textContent = 'Nema pronađenih sportskih centara.';
      row.append(cell);
      elements.centriTableBody.append(row);
      return;
    }

    state.centri.forEach((centar) => {
      const row = document.createElement('tr');

      const nameCell = document.createElement('td');
      const link = document.createElement('a');
      link.href = `sportski-centar-detalji.html?id=${centar.idCentar}`;
      link.textContent = centar.nazivCentra;
      nameCell.append(link);
      row.append(nameCell);

      AppApi.appendTextCell(row, centar.adresa);

      const mjestoObj = state.mjesta.find(
        (m) => m.idMjesto === centar.idMjesto,
      );
      const mjestoText = mjestoObj
        ? `${mjestoObj.nazivMjesta} (${mjestoObj.postanskiBroj})`
        : '-';
      AppApi.appendTextCell(row, mjestoText);

      AppApi.appendTextCell(row, AppApi.formatWorkingHours(centar));

      const actionsCell = document.createElement('td');
      const actionWrap = document.createElement('div');
      actionWrap.className = 'row-actions';

      const editButton = document.createElement('button');
      editButton.type = 'button';
      editButton.className = 'secondary-button';
      editButton.textContent = 'Uredi';
      editButton.addEventListener('click', () => fillForm(centar));

      actionWrap.append(editButton);
      actionsCell.append(actionWrap);
      row.append(actionsCell);

      elements.centriTableBody.append(row);
    });
  }

  async function saveCentar(event) {
    event.preventDefault();
    AppApi.showMessage(elements.pageMessage, '');

    const centarData = getFormData();
    const isEdit = state.editingId !== null;
    const url = isEdit
      ? `${API_BASE_URL}/sportski-centri/${state.editingId}`
      : `${API_BASE_URL}/sportski-centri`;
    const method = isEdit ? 'PUT' : 'POST';

    try {
      await AppApi.request(url, {
        method,
        body: JSON.stringify(centarData),
      });

      AppApi.showMessage(
        elements.pageMessage,
        `Centar je uspješno spremljen.`,
        'success',
      );
      resetForm();
      await loadCentri();
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  function resetFilter() {
    elements.searchInput.value = '';
    elements.mjestoFilter.value = '';
    loadCentri();
  }

  function getFormData() {
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
