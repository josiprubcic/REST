(function () {
  const API_BASE_URL = 'http://localhost:8080/api';

  const state = {
    sportovi: [],
    editingSportId: null,
  };

  const elements = {};

  document.addEventListener('DOMContentLoaded', init);

  async function init() {
    bindElements();
    bindEvents();

    try {
      await loadSportovi();
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  function bindElements() {
    elements.pageMessage = document.getElementById('pageMessage');
    elements.sportForm = document.getElementById('sportForm');
    elements.sportFormTitle = document.getElementById('sportFormTitle');
    elements.resetSportFormButton = document.getElementById(
      'resetSportFormButton',
    );
    elements.nazivSporta = document.getElementById('nazivSporta');
    elements.saveSportButton = document.getElementById('saveSportButton');
    elements.sportoviTableBody = document.getElementById('sportoviTableBody');
    elements.sportoviCount = document.getElementById('sportoviCount');
  }

  function bindEvents() {
    elements.sportForm.addEventListener('submit', saveSport);
    elements.resetSportFormButton.addEventListener('click', resetSportForm);
  }

  async function loadSportovi() {
    state.sportovi = await AppApi.request(`${API_BASE_URL}/sportovi`);
    renderSportoviTable();
  }

  function renderSportoviTable() {
    elements.sportoviTableBody.replaceChildren();
    elements.sportoviCount.textContent = String(state.sportovi.length);

    if (state.sportovi.length === 0) {
      const row = document.createElement('tr');
      const cell = document.createElement('td');
      cell.colSpan = 2;
      cell.className = 'empty-state';
      cell.textContent = 'Nema unesenih sportova.';
      row.append(cell);
      elements.sportoviTableBody.append(row);
      return;
    }

    state.sportovi.forEach((sport) => {
      const row = document.createElement('tr');

      AppApi.appendTextCell(row, sport.nazivSporta);

      const actionsCell = document.createElement('td');
      const actionWrap = document.createElement('div');
      actionWrap.className = 'row-actions';

      const editButton = document.createElement('button');
      editButton.type = 'button';
      editButton.className = 'secondary-button';
      editButton.style = 'min-height: 28px; padding: 2px 8px; font-size: 13px;';
      editButton.textContent = 'Uredi';
      editButton.addEventListener('click', () => fillSportForm(sport));

      const deleteButton = document.createElement('button');
      deleteButton.type = 'button';
      deleteButton.className = 'danger-button';
      deleteButton.style =
        'min-height: 28px; padding: 2px 8px; font-size: 13px;';
      deleteButton.textContent = 'Obriši';
      deleteButton.addEventListener('click', () => deleteSport(sport));

      actionWrap.append(editButton, deleteButton);
      actionsCell.append(actionWrap);
      row.append(actionsCell);

      elements.sportoviTableBody.append(row);
    });
  }

  async function saveSport(event) {
    event.preventDefault();
    AppApi.showMessage(elements.pageMessage, '');

    const isEdit = state.editingSportId !== null;
    const url = isEdit
      ? `${API_BASE_URL}/sportovi/${state.editingSportId}`
      : `${API_BASE_URL}/sportovi`;
    const method = isEdit ? 'PUT' : 'POST';

    try {
      await AppApi.request(url, {
        method,
        body: JSON.stringify(buildSportPayload()),
      });

      AppApi.showMessage(
        elements.pageMessage,
        `Sport je uspješno ${isEdit ? 'ažuriran' : 'dodan'}.`,
        'success',
      );

      resetSportForm();
      await loadSportovi();
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  async function deleteSport(sport) {
    if (
      !window.confirm(
        `Jeste li sigurni da želite obrisati sport "${sport.nazivSporta}"?`,
      )
    ) {
      return;
    }

    try {
      await AppApi.request(`${API_BASE_URL}/sportovi/${sport.idSport}`, {
        method: 'DELETE',
      });

      AppApi.showMessage(
        elements.pageMessage,
        'Sport je uspješno obrisan.',
        'success',
      );
      resetSportForm();
      await loadSportovi();
    } catch (error) {
      AppApi.showMessage(
        elements.pageMessage,
        'Nije moguće obrisati sport. Provjerite koristi li ga neki teren.',
        'error',
      );
    }
  }

  function fillSportForm(sport) {
    state.editingSportId = sport.idSport;
    elements.sportFormTitle.textContent = 'Uredi sport';
    elements.saveSportButton.textContent = 'Spremi promjene';
    elements.nazivSporta.value = sport.nazivSporta || '';
    elements.nazivSporta.focus();
  }

  function resetSportForm() {
    state.editingSportId = null;
    elements.sportForm.reset();
    elements.sportFormTitle.textContent = 'Novi sport';
    elements.saveSportButton.textContent = 'Spremi sport';
  }

  function buildSportPayload() {
    return {
      nazivSporta: elements.nazivSporta.value.trim(),
    };
  }
})();
