const API_BASE_URL = 'http://localhost:8080/api';
const TRENUTNI_KORISNIK_ID = 1;

document.addEventListener('DOMContentLoaded', inicijalizirajAplikaciju);
document
  .getElementById('traziBtn')
  .addEventListener('click', dohvatiSlobodneTermine);
document.getElementById('datumInput').min = new Date()
  .toISOString()
  .split('T')[0];

function prikaziPoruku(tekst, tip = '') {
  const msgElement = document.getElementById('pageMessage');
  if (!tekst) {
    msgElement.classList.add('skriveno');
    msgElement.className = 'message';
    return;
  }
  msgElement.innerText = tekst;
  msgElement.className = 'message';
  if (tip === 'success') msgElement.classList.add('success');
  if (tip === 'error') msgElement.classList.add('error');
  msgElement.classList.remove('skriveno');
}

async function inicijalizirajAplikaciju() {
  const centarSelect = document.getElementById('centarSelect');

  try {
    const response = await fetch(`${API_BASE_URL}/sportski-centri`);
    if (!response.ok)
      throw new Error('Neuspješno dohvaćanje sportskih centara.');

    const centri = await response.json();
    centarSelect.innerHTML = '<option value="">Odaberi centar</option>';

    centri.forEach((centar) => {
      const option = document.createElement('option');
      option.value = centar.idCentar;
      option.innerText = `${centar.nazivCentra} (${centar.adresa}, ${centar.nazivMjesta})`;
      centarSelect.appendChild(option);
    });
  } catch (error) {
    centarSelect.innerHTML =
      '<option value="">Greška pri učitavanju centara</option>';
    console.error('Greška:', error);
  }

  dohvatiKorisnikoveRezervacije();
}

async function dohvatiSlobodneTermine() {
  const idCentar = document.getElementById('centarSelect').value;
  const datum = document.getElementById('datumInput').value;
  const kontejner = document.getElementById('tereni-kontejner');
  const glavniBrojac = document.getElementById('terminiCount');

  if (!idCentar || !datum) {
    alert('Molimo odaberite i sportski centar i datum!');
    return;
  }

  prikaziPoruku('Učitavam slobodne termine...', '');
  kontejner.innerHTML = '';
  glavniBrojac.innerText = '0';

  try {
    const url = `${API_BASE_URL}/rezervacije/available?idCentar=${idCentar}&datum=${datum}`;
    const response = await fetch(url);
    if (!response.ok) throw new Error('Greška prilikom dohvaćanja termina.');

    const slobodniTermini = await response.json();
    prikaziPoruku('');

    glavniBrojac.innerText = slobodniTermini.length;

    if (slobodniTermini.length === 0) {
      kontejner.innerHTML =
        '<div class="empty-state">Nema slobodnih termina za odabrani dan.</div>';
      return;
    }

    const grupiraniTereni = {};
    slobodniTermini.forEach((termin) => {
      if (!grupiraniTereni[termin.idTeren]) {
        grupiraniTereni[termin.idTeren] = {
          naziv: termin.nazivTerena,
          id: termin.idTeren,
          slotovi: [],
        };
      }
      grupiraniTereni[termin.idTeren].slotovi.push(termin);
    });

    for (const terenId in grupiraniTereni) {
      const teren = grupiraniTereni[terenId];

      const subsection = document.createElement('div');
      subsection.className = 'subsection';
      subsection.style.marginBottom = '16px';

      subsection.innerHTML = `
        <div class="section-title-row compact">
          <h3>${teren.idTeren}</h3>
        </div>
      `;

      const grid = document.createElement('div');
      grid.className = 'termini-grid';

      teren.slotovi.forEach((slot) => {
        const satPrikaz = new Date(slot.vrijemePocetka).toLocaleTimeString(
          'hr-HR',
          { hour: '2-digit', minute: '2-digit' },
        );

        const btn = document.createElement('button');
        btn.className = 'secondary-button termin-button';
        btn.type = 'button';
        btn.innerText = satPrikaz;
        btn.onclick = () => kreirajRezervaciju(slot, idCentar);
        grid.appendChild(btn);
      });

      subsection.appendChild(grid);
      kontejner.appendChild(subsection);
    }
  } catch (error) {
    prikaziPoruku('Došlo je do pogreške: ' + error.message, 'error');
  }
}

async function kreirajRezervaciju(slot, idCentar) {
  const satPrikaz = new Date(slot.vrijemePocetka).toLocaleTimeString('hr-HR', {
    hour: '2-digit',
    minute: '2-digit',
  });
  if (
    !confirm(
      `Želiš li sigurno rezervirati ${slot.nazivTerena} u ${satPrikaz}h?`,
    )
  )
    return;

  const rezervacijaData = {
    idKorisnik: TRENUTNI_KORISNIK_ID,
    idCentar: parseInt(idCentar),
    idTeren: slot.idTeren,
    vrijemePocetka: slot.vrijemePocetka,
    vrijemeZavrsetka: slot.vrijemeZavrsetka,
  };

  try {
    const response = await fetch(`${API_BASE_URL}/rezervacije`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(rezervacijaData),
    });

    if (response.ok) {
      prikaziPoruku('Uspješno ste rezervirali termin!', 'success');
      dohvatiSlobodneTermine();
      dohvatiKorisnikoveRezervacije();
    } else {
      alert('Greška pri rezervaciji: ' + (await response.text()));
    }
  } catch (error) {
    alert('Server nije dostupan: ' + error.message);
  }
}

async function dohvatiKorisnikoveRezervacije() {
  const tBody = document.getElementById('rezervacije-body');
  const loading = document.getElementById('loading-rezervacije');
  const brojac = document.getElementById('rezervacije-brojac');

  tBody.innerHTML = '';
  loading.classList.remove('skriveno');

  try {
    const url = `${API_BASE_URL}/rezervacije?idKorisnik=${TRENUTNI_KORISNIK_ID}`;
    const response = await fetch(url);

    if (!response.ok)
      throw new Error('Neuspješno dohvaćanje tvojih rezervacija.');

    const rezervacije = await response.json();
    loading.classList.add('skriveno');

    brojac.innerText = rezervacije.length;

    if (rezervacije.length === 0) {
      tBody.innerHTML = `
        <tr>
          <td colspan="5">
            <div class="empty-state">Trenutno nemate aktivnih rezervacija.</div>
          </td>
        </tr>`;
      return;
    }

    rezervacije.forEach((rez) => {
      const tr = document.createElement('tr');

      const datum = new Date(rez.vrijemePocetka).toLocaleDateString('hr-HR');
      const pocetak = new Date(rez.vrijemePocetka).toLocaleTimeString('hr-HR', {
        hour: '2-digit',
        minute: '2-digit',
      });
      const kraj = new Date(rez.vrijemeZavrsetka).toLocaleTimeString('hr-HR', {
        hour: '2-digit',
        minute: '2-digit',
      });

      tr.innerHTML = `
        <td><strong>${rez.nazivCentra || 'Sportski Centar'}</strong></td>
        <td>${rez.nazivTerena || 'Teren'}</td>
        <td>${datum}</td>
        <td class="price">${pocetak} - ${kraj}h</td>
        <td>
          <div class="row-actions">
            <button class="danger-button" style="min-height: 32px; padding: 4px 10px; font-size: 14px;" type="button" onclick="otkaziRezervaciju(${rez.idRezervacija})">Otkaži</button>
          </div>
        </td>
      `;
      tBody.appendChild(tr);
    });
  } catch (error) {
    loading.classList.add('skriveno');
    brojac.innerText = '!';
    console.error(error);
  }
}

async function otkaziRezervaciju(idRezervacija) {
  if (!confirm('Jeste li sigurni da želite otkazati ovu rezervaciju?')) return;

  try {
    const response = await fetch(
      `${API_BASE_URL}/rezervacije/${idRezervacija}`,
      {
        method: 'DELETE',
      },
    );

    if (response.ok) {
      prikaziPoruku('Rezervacija je uspješno otkazana.', 'success');
      dohvatiKorisnikoveRezervacije();
      const idCentar = document.getElementById('centarSelect').value;
      const datum = document.getElementById('datumInput').value;
      if (idCentar && datum) {
        dohvatiSlobodneTermine();
      }
    } else {
      alert('Greška pri otkazivanju: ' + (await response.text()));
    }
  } catch (error) {
    alert('Server nije dostupan: ' + error.message);
  }
}
