(function () {
    const state = {
        centri: [],
        termini: []
    };

    const elements = {};

    document.addEventListener("DOMContentLoaded", init);

    async function init() {
        bindElements();
        bindEvents();
        setDefaultDate();

        try {
            await loadCentri();
        } catch (error) {
            AppApi.showMessage(elements.pageMessage, error.message, "error");
        }
    }

    function bindElements() {
        elements.centarSelect = document.getElementById("centarSelect");
        elements.datumInput = document.getElementById("datumInput");
        elements.korisnikInput = document.getElementById("korisnikInput");
        elements.traziButton = document.getElementById("traziButton");
        elements.pageMessage = document.getElementById("pageMessage");
        elements.terminiContainer = document.getElementById("terminiContainer");
        elements.terminiCount = document.getElementById("terminiCount");
    }

    function bindEvents() {
        elements.traziButton.addEventListener("click", loadSlobodniTermini);
    }

    function setDefaultDate() {
        const today = new Date().toISOString().slice(0, 10);
        elements.datumInput.min = today;
        elements.datumInput.value = today;
    }

    async function loadCentri() {
        state.centri = await AppApi.request("/api/sportski-centri");
        AppApi.fillSelect(elements.centarSelect, state.centri, {
            placeholder: "Odaberi centar",
            value: (centar) => centar.idCentar,
            label: (centar) => `${centar.nazivCentra} (${centar.adresa}, ${centar.nazivMjesta})`
        });
    }

    async function loadSlobodniTermini() {
        const idCentar = elements.centarSelect.value;
        const datum = elements.datumInput.value;

        if (!idCentar || !datum) {
            AppApi.showMessage(elements.pageMessage, "Odaberi sportski centar i datum.", "error");
            return;
        }

        try {
            const query = AppApi.buildQuery({
                idCentar,
                datum
            });
            state.termini = await AppApi.request(`/api/rezervacije/available${query}`);
            renderTermini();
            AppApi.showMessage(elements.pageMessage, "", null);
        } catch (error) {
            AppApi.showMessage(elements.pageMessage, error.message, "error");
        }
    }

    function renderTermini() {
        elements.terminiContainer.replaceChildren();
        elements.terminiCount.textContent = String(state.termini.length);

        if (state.termini.length === 0) {
            const empty = document.createElement("p");
            empty.className = "empty-state";
            empty.textContent = "Nema slobodnih termina za odabrani dan.";
            elements.terminiContainer.append(empty);
            return;
        }

        const grouped = groupByTeren(state.termini);
        Object.entries(grouped).forEach(([terenId, termini]) => {
            const section = document.createElement("section");
            section.className = "subsection reservation-group";

            const titleRow = document.createElement("div");
            titleRow.className = "section-title-row compact";

            const title = document.createElement("h3");
            title.textContent = `Teren ${terenId}`;

            const count = document.createElement("span");
            count.className = "counter";
            count.textContent = String(termini.length);

            titleRow.append(title, count);

            const grid = document.createElement("div");
            grid.className = "termini-grid";

            termini.forEach((termin) => {
                const button = document.createElement("button");
                button.type = "button";
                button.className = "secondary-button termin-button";
                button.textContent = formatSlotLabel(termin);
                button.addEventListener("click", () => createReservation(termin));
                grid.append(button);
            });

            section.append(titleRow, grid);
            elements.terminiContainer.append(section);
        });
    }

    function groupByTeren(termini) {
        return termini.reduce((result, termin) => {
            if (!result[termin.idTeren]) {
                result[termin.idTeren] = [];
            }
            result[termin.idTeren].push(termin);
            return result;
        }, {});
    }

    async function createReservation(termin) {
        const idKorisnik = Number(elements.korisnikInput.value);
        const idCentar = Number(elements.centarSelect.value);

        if (!idKorisnik) {
            AppApi.showMessage(elements.pageMessage, "Unesi korisnik ID.", "error");
            return;
        }

        if (!window.confirm(`Rezervirati teren ${termin.idTeren} u terminu ${formatSlotLabel(termin)}?`)) {
            return;
        }

        try {
            await AppApi.request("/api/rezervacije", {
                method: "POST",
                body: JSON.stringify({
                    idKorisnik,
                    idCentar,
                    idTeren: termin.idTeren,
                    vrijemePocetka: termin.vrijemePocetka,
                    vrijemeZavrsetka: termin.vrijemeZavrsetka
                })
            });
            AppApi.showMessage(elements.pageMessage, "Rezervacija je uspješno kreirana.", "success");
            await loadSlobodniTermini();
        } catch (error) {
            AppApi.showMessage(elements.pageMessage, error.message, "error");
        }
    }

    function formatSlotLabel(termin) {
        return `${formatDateTime(termin.vrijemePocetka)} - ${formatDateTime(termin.vrijemeZavrsetka)}`;
    }

    function formatDateTime(value) {
        const date = new Date(value);
        return date.toLocaleTimeString("hr-HR", {
            hour: "2-digit",
            minute: "2-digit"
        });
    }
})();
