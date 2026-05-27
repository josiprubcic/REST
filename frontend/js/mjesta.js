(function () {
  const API_BASE_URL = 'http://localhost:8080/api';

  const state = {
    mjesta: [],
    editingMjestoId: null,
  };

  const elements = {};

  document.addEventListener('DOMContentLoaded', init);

  async function init() {
    bindElements();
    bindEvents();

    try {
      await loadMjesta();
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  function bindElements() {
    elements.pageMessage = document.getElementById('pageMessage');
    elements.mjestoForm = document.getElementById('mjestoForm');
    elements.mjestoFormTitle = document.getElementById('mjestoFormTitle');
    elements.resetMjestoFormButton = document.getElementById(
      'resetMjestoFormButton',
    );
    elements.nazivMjesta = document.getElementById('nazivMjesta');
    elements.postanskiBroj = document.getElementById('postanskiBroj');
    elements.saveMjestoButton = document.getElementById('saveMjestoButton');
    elements.mjestaTableBody = document.getElementById('mjestaTableBody');
    elements.mjestaCount = document.getElementById('mjestaCount');
  }

  function bindEvents() {
    elements.mjestoForm.addEventListener('submit', saveMjesto);
    elements.resetMjestoFormButton.addEventListener('click', resetMjestoForm);
  }

  async function loadMjesta() {
    state.mjesta = await AppApi.request(`${API_BASE_URL}/mjesta`);
    renderMjestaTable();
  }

  function renderMjestaTable() {
    elements.mjestaTableBody.replaceChildren();
    elements.mjestaCount.textContent = String(state.mjesta.length);

    if (state.mjesta.length === 0) {
      const row = document.createElement('tr');
      const cell = document.createElement('td');
      cell.colSpan = 3;
      cell.className = 'empty-state';
      cell.textContent = 'Nema unesenih mjesta.';
      row.append(cell);
      elements.mjestaTableBody.append(row);
      return;
    }

    state.mjesta.forEach((mjesto) => {
      const row = document.createElement('tr');

      AppApi.appendTextCell(row, mjesto.nazivMjesta);
      AppApi.appendTextCell(row, mjesto.postanskiBroj);

      const actionsCell = document.createElement('td');
      const actionWrap = document.createElement('div');
      actionWrap.className = 'row-actions';

      const editButton = document.createElement('button');
      editButton.type = 'button';
      editButton.className = 'secondary-button';
      editButton.style = 'min-height: 28px; padding: 2px 8px; font-size: 13px;';
      editButton.textContent = 'Uredi';
      editButton.addEventListener('click', () => fillMjestoForm(mjesto));

      const deleteButton = document.createElement('button');
      deleteButton.type = 'button';
      deleteButton.className = 'danger-button';
      deleteButton.style =
        'min-height: 28px; padding: 2px 8px; font-size: 13px;';
      deleteButton.textContent = 'Obriši';
      deleteButton.addEventListener('click', () => deleteMjesto(mjesto));

      actionWrap.append(editButton, deleteButton);
      actionsCell.append(actionWrap);
      row.append(actionsCell);

      elements.mjestaTableBody.append(row);
    });
  }

  async function saveMjesto(event) {
    event.preventDefault();
    AppApi.showMessage(elements.pageMessage, '');

    const isEdit = state.editingMjestoId !== null;
    const url = isEdit
      ? `${API_BASE_URL}/mjesta/${state.editingMjestoId}`
      : `${API_BASE_URL}/mjesta`;
    const method = isEdit ? 'PUT' : 'POST';

    try {
      await AppApi.request(url, {
        method,
        body: JSON.stringify(buildMjestoPayload()),
      });

      AppApi.showMessage(
        elements.pageMessage,
        `Mjesto je uspješno ${isEdit ? 'ažurirano' : 'dodano'}.`,
        'success',
      );

      resetMjestoForm();
      await loadMjesta();
    } catch (error) {
      AppApi.showMessage(elements.pageMessage, error.message, 'error');
    }
  }

  async function deleteMjesto(mjesto) {
    if (
      !window.confirm(
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
        'Mjesto je uspješno obrisano.',
        'success',
      );
      resetMjestoForm();
      await loadMjesta();
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

  function buildMjestoPayload() {
    return {
      nazivMjesta: elements.nazivMjesta.value.trim(),
      postanskiBroj: elements.postanskiBroj.value.trim(),
    };
  }
})();
