const API_BASE_URL = 'http://localhost:8080/api';
const TRENUTNI_KORISNIK_ID = 1; // Simulacija ulogiranog sportaša

// --- NOVO: Pokretanje aplikacije i inicijalni fetch centara ---
document.addEventListener('DOMContentLoaded', inicijalizirajAplikaciju);
document
  .getElementById('traziBtn')
  .addEventListener('click', dohvatiSlobodneTermine);
document.getElementById('datumInput').min = new Date()
  .toISOString()
  .split('T')[0];

// 1. FETCH: Dohvaćanje svih sportskih centara za select izbornik
async function inicijalizirajAplikaciju() {
  const centarSelect = document.getElementById('centarSelect');

  try {
    const response = await fetch(`${API_BASE_URL}/sportski/centri`);
    if (!response.ok)
      throw new Error('Neuspješno dohvaćanje sportskih centara.');

    // Ovo je lista SportskiCentarDTO-ova koju tvoj kontroler vraća!
    const centri = await response.json();

    centarSelect.innerHTML = '<option value="">-- Odaberi centar --</option>';

    centri.forEach((centar) => {
      const option = document.createElement('option');
      option.value = centar.idCentar; // ID centra koji šaljemo u idući fetch
      option.innerText = `${centar.nazivCentra} (${centar.adresa}, ${centar.nazivMjesta})`;
      centarSelect.appendChild(option);
    });
  } catch (error) {
    centarSelect.innerHTML =
      '<option value="">Greška pri učitavanju centara</option>';
    console.error('Greška:', error);
  }
}

// 2. FETCH: Dohvaćanje slobodnih termina (Ostaje isto, samo koristi dinamički ID)
async function dohvatiSlobodneTermine() {
  const idCentar = document.getElementById('centarSelect').value;
  const datum = document.getElementById('datumInput').value;

  if (!idCentar || !datum) {
    alert('Molimo odaberite i sportski centar i datum!');
    return;
  }

  const kontejner = document.getElementById('tereni-kontejner');
  const loading = document.getElementById('loading');

  kontejner.innerHTML = '';
  loading.classList.remove('skriveno');

  try {
    const url = `${API_BASE_URL}/rezervacije/available?idCentar=${idCentar}&datum=${datum}`;
    const response = await fetch(url);
    if (!response.ok) throw new Error('Greška prilikom dohvaćanja termina.');

    const slobodniTermini = await response.json();
    loading.classList.add('skriveno');

    if (slobodniTermini.length === 0) {
      kontejner.innerHTML =
        '<p style="text-align:center;">Nema slobodnih termina za odabrani dan.</p>';
      return;
    }

    // Grupiranje i iscrtavanje...
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
      const terenKartica = document.createElement('div');
      terenKartica.className = 'teren-kartica';
      terenKartica.innerHTML = `<h2>${teren.naziv}</h2>`;

      const grid = document.createElement('div');
      grid.className = 'termini-grid';

      teren.slotovi.forEach((slot) => {
        const satPrikaz = new Date(slot.vrijemePocetka).toLocaleTimeString(
          'hr-HR',
          { hour: '2-digit', minute: '2-digit' },
        );
        const btn = document.createElement('button');
        btn.className = 'termin-btn';
        btn.innerText = satPrikaz;
        btn.onclick = () => kreirajRezervaciju(slot, idCentar);
        grid.appendChild(btn);
      });

      terenKartica.appendChild(grid);
      kontejner.appendChild(terenKartica);
    }
  } catch (error) {
    loading.classList.add('skriveno');
    alert('Došlo je do pogreške: ' + error.message);
  }
}

// 3. FETCH: Slanje POST zahtjeva za kreiranje rezervacije (Ostaje isto)
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
      alert('🎉 Uspješno ste rezervirali termin!');
      dohvatiSlobodneTermine(); // Osvježi
    } else {
      alert('Greška pri rezervaciji: ' + (await response.text()));
    }
  } catch (error) {
    alert('Server nije dostupan: ' + error.message);
  }
}
