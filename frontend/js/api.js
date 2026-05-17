(function () {
  async function request(path, options) {
    const config = options || {};
    const headers = new Headers(config.headers || {});

    if (config.body && !headers.has('Content-Type')) {
      headers.set('Content-Type', 'application/json');
    }
    if (!headers.has('Accept')) {
      headers.set('Accept', 'application/json');
    }

    const response = await fetch(path, {
      ...config,
      headers,
    });

    if (response.status === 204) {
      return null;
    }

    let body = null;
    const contentType = response.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
      body = await response.json();
    } else {
      body = await response.text();
    }

    if (!response.ok) {
      const message =
        body && body.message ? body.message : `HTTP ${response.status}`;
      const error = new Error(message);
      error.status = response.status;
      error.body = body;
      throw error;
    }

    return body;
  }

  function buildQuery(params) {
    const query = new URLSearchParams();
    Object.entries(params).forEach(([key, value]) => {
      if (
        value !== null &&
        value !== undefined &&
        String(value).trim() !== ''
      ) {
        query.set(key, value);
      }
    });
    const serialized = query.toString();
    return serialized ? `?${serialized}` : '';
  }

  function fillSelect(select, items, config) {
    const currentValue = select.value;
    const placeholder = config.placeholder || null;
    select.replaceChildren();

    if (placeholder) {
      const option = document.createElement('option');
      option.value = '';
      option.textContent = placeholder;
      select.append(option);
    }

    items.forEach((item) => {
      const option = document.createElement('option');
      option.value = String(config.value(item));
      option.textContent = config.label(item);
      select.append(option);
    });

    if ([...select.options].some((option) => option.value === currentValue)) {
      select.value = currentValue;
    }
  }

  function showMessage(element, message, type) {
    element.textContent = message || '';
    element.className = 'message';
    if (type) {
      element.classList.add(type);
    }
  }

  function formatTime(value) {
    if (!value) {
      return '';
    }
    return String(value).slice(0, 5);
  }

  function formatWorkingHours(centar) {
    const tjedan =
      centar.radnoVrijemeTjedanOd && centar.radnoVrijemeTjedanDo
        ? `${formatTime(centar.radnoVrijemeTjedanOd)}-${formatTime(centar.radnoVrijemeTjedanDo)}`
        : 'Tjedan nije unesen';
    const vikend =
      centar.radnoVrijemeVikendOd && centar.radnoVrijemeVikendDo
        ? `${formatTime(centar.radnoVrijemeVikendOd)}-${formatTime(centar.radnoVrijemeVikendDo)}`
        : 'Vikend nije unesen';
    return `${tjedan}, ${vikend}`;
  }

  function formatPrice(value) {
    const number = Number(value);
    if (Number.isNaN(number)) {
      return '';
    }
    return number.toFixed(2);
  }

  function appendTextCell(row, value, className) {
    const cell = document.createElement('td');
    cell.textContent = value == null || value === '' ? '-' : String(value);
    if (className) {
      cell.className = className;
    }
    row.append(cell);
    return cell;
  }

  window.AppApi = {
    request,
    buildQuery,
    fillSelect,
    showMessage,
    formatTime,
    formatWorkingHours,
    formatPrice,
    appendTextCell,
  };
})();
