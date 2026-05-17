(function () {
  const API_BASE_URL = 'http://localhost:8080/api';
  const state = {
    centarId: null,
    mjesta: [],
    sportovi: [],
    centar: null,
    tereni: [],
    editingTerenId: null,
  };

  const elements = {};

  document.addEventListener('DOMContentLoaded', init);

  async function init() {
    bindElements();
    bindEvents();

    const params = new URLSearchParams(window.location.search);
    state.centarId = params.get('id') || params.get('centarId');

    if (!state.centarId) {
      AppApi.showMessage(
        elements.pageMessage,
        'Nedostaje id sportskog centra u URL-u.',
        'error',
      );
      disableForms();
      return;
    }

    try {
      await loadLookups();
      await loadCentar();
      await loadTereni();
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  function bindElements() {
    elements.pageTitle = document.getElementById('pageTitle');
    elements.pageMessage = document.getElementById('pageMessage');
    elements.centarForm = document.getElementById('centarForm');
    elements.deleteCentarButton = document.getElementById('deleteCentarButton');
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
    elements.sportFilter = document.getElementById('sportFilter');
    elements.terenForm = document.getElementById('terenForm');
    elements.terenFormTitle = document.getElementById('terenFormTitle');
    elements.resetTerenFormButton = document.getElementById(
      'resetTerenFormButton',
    );
    elements.idTeren = document.getElementById('idTeren');
    elements.idSport = document.getElementById('idSport');
    elements.cijena = document.getElementById('cijena');
    elements.saveTerenButton = document.getElementById('saveTerenButton');
    elements.tereniTableBody = document.getElementById('tereniTableBody');
    elements.tereniCount = document.getElementById('tereniCount');
  }

  function bindEvents() {
    elements.centarForm.addEventListener('submit', saveCentar);
    elements.deleteCentarButton.addEventListener('click', deleteCentar);
    elements.sportFilter.addEventListener('change', loadTereni);
    elements.terenForm.addEventListener('submit', saveTeren);
    elements.resetTerenFormButton.addEventListener('click', resetTerenForm);
  }

  async function loadLookups() {
    const [mjesta, sportovi] = await Promise.all([
      AppApi.request(`${API_BASE_URL}/mjesta`),
      AppApi.request(`${API_BASE_URL}/sportovi`),
    ]);

    state.mjesta = mjesta;
    state.sportovi = sportovi;

    AppApi.fillSelect(elements.idMjesto, state.mjesta, {
      placeholder: 'Odaberi mjesto',
      value: (mjesto) => mjesto.idMjesto,
      label: (mjesto) => `${mjesto.nazivMjesta} (${mjesto.postanskiBroj})`,
    });
    AppApi.fillSelect(elements.idSport, state.sportovi, {
      placeholder: 'Odaberi sport',
      value: (sport) => sport.idSport,
      label: (sport) => sport.nazivSporta,
    });
    AppApi.fillSelect(elements.sportFilter, state.sportovi, {
      placeholder: 'Svi sportovi',
      value: (sport) => sport.idSport,
      label: (sport) => sport.nazivSporta,
    });
  }

  async function loadCentar() {
    state.centar = await AppApi.request(
      `${API_BASE_URL}/sportski-centri/${encodeURIComponent(state.centarId)}`,
    );
    fillCentarForm(state.centar);
  }

  async function loadTereni() {
    const query = AppApi.buildQuery({
      sportId: elements.sportFilter.value,
    });
    state.tereni = await AppApi.request(
      `${API_BASE_URL}/sportski-centri/${encodeURIComponent(state.centarId)}/tereni${query}`,
    );
    renderTereni();
    AppApi.showMessage(elements.pageMessage, '', null);
  }

  async function saveCentar(event) {
    event.preventDefault();

    try {
      const saved = await AppApi.request(
        `${API_BASE_URL}/sportski-centri/${encodeURIComponent(state.centarId)}`,
        {
          method: 'PUT',
          body: JSON.stringify(buildCentarPayload()),
        },
      );
      state.centar = saved;
      fillCentarForm(saved);
      AppApi.showMessage(
        elements.pageMessage,
        'Podaci sportskog centra su spremljeni.',
        'success',
      );
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  async function deleteCentar() {
    if (!window.confirm('Obrisati ovaj sportski centar?')) {
      return;
    }

    try {
      await AppApi.request(
        `${API_BASE_URL}/sportski-centri/${encodeURIComponent(state.centarId)}`,
        {
          method: 'DELETE',
        },
      );
      window.location.href = 'sportski-centri.html';
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  async function saveTeren(event) {
    event.preventDefault();

    const isEdit = Boolean(state.editingTerenId);
    const path = isEdit
      ? `${API_BASE_URL}/sportski-centri/${encodeURIComponent(state.centarId)}/tereni/${encodeURIComponent(state.editingTerenId)}`
      : `${API_BASE_URL}/sportski-centri/${encodeURIComponent(state.centarId)}/tereni`;
    const payload = isEdit
      ? buildTerenUpdatePayload()
      : buildTerenCreatePayload();

    try {
      await AppApi.request(path, {
        method: isEdit ? 'PUT' : 'POST',
        body: JSON.stringify(payload),
      });
      AppApi.showMessage(
        elements.pageMessage,
        'Teren je spremljen.',
        'success',
      );
      resetTerenForm();
      await loadTereni();
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  async function deleteTeren(teren) {
    if (!window.confirm(`Obrisati teren ${teren.idTeren}?`)) {
      return;
    }

    try {
      await AppApi.request(
        `${API_BASE_URL}/sportski-centri/${encodeURIComponent(state.centarId)}/tereni/${encodeURIComponent(teren.idTeren)}`,
        {
          method: 'DELETE',
        },
      );
      AppApi.showMessage(elements.pageMessage, 'Teren je obrisan.', 'success');
      resetTerenForm();
      await loadTereni();
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  function fillCentarForm(centar) {
    elements.pageTitle.textContent = centar.nazivCentra || 'Sportski centar';
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

  function buildTerenCreatePayload() {
    return {
      idTeren: elements.idTeren.value.trim(),
      idSport: Number(elements.idSport.value),
      cijena: Number(elements.cijena.value),
    };
  }

  function buildTerenUpdatePayload() {
    return {
      idSport: Number(elements.idSport.value),
      cijena: Number(elements.cijena.value),
    };
  }

  function renderTereni() {
    elements.tereniCount.textContent = String(state.tereni.length);
    elements.tereniTableBody.replaceChildren();

    if (state.tereni.length === 0) {
      const row = document.createElement('tr');
      const cell = document.createElement('td');
      cell.colSpan = 4;
      cell.className = 'empty-state';
      cell.textContent = 'Nema terena za odabrani filter.';
      row.append(cell);
      elements.tereniTableBody.append(row);
      return;
    }

    state.tereni.forEach((teren) => {
      const row = document.createElement('tr');
      AppApi.appendTextCell(row, teren.idTeren);
      AppApi.appendTextCell(row, teren.nazivSporta);
      AppApi.appendTextCell(row, AppApi.formatPrice(teren.cijena), 'price');

      const actions = document.createElement('td');
      const actionWrap = document.createElement('div');
      actionWrap.className = 'row-actions';

      const editButton = document.createElement('button');
      editButton.type = 'button';
      editButton.className = 'secondary-button';
      editButton.textContent = 'Uredi';
      editButton.addEventListener('click', () => fillTerenForm(teren));

      const deleteButton = document.createElement('button');
      deleteButton.type = 'button';
      deleteButton.className = 'danger-button';
      deleteButton.textContent = 'Obriši';
      deleteButton.addEventListener('click', () => deleteTeren(teren));

      actionWrap.append(editButton, deleteButton);
      actions.append(actionWrap);
      row.append(actions);
      elements.tereniTableBody.append(row);
    });
  }

  function fillTerenForm(teren) {
    state.editingTerenId = teren.idTeren;
    elements.terenFormTitle.textContent = 'Uredi teren';
    elements.saveTerenButton.textContent = 'Spremi promjene';
    elements.idTeren.value = teren.idTeren || '';
    elements.idTeren.disabled = true;
    elements.idSport.value = teren.idSport ? String(teren.idSport) : '';
    elements.cijena.value = teren.cijena == null ? '' : String(teren.cijena);
    elements.idSport.focus();
  }

  function resetTerenForm() {
    state.editingTerenId = null;
    elements.terenForm.reset();
    elements.idTeren.disabled = false;
    elements.terenFormTitle.textContent = 'Novi teren';
    elements.saveTerenButton.textContent = 'Spremi teren';
  }

  function disableForms() {
    [...document.querySelectorAll('input, select, button')].forEach(
      (element) => {
        element.disabled = true;
      },
    );
  }
})();
