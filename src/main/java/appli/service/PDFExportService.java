package appli.service;

import appli.model.*;
import appli.service.StatistiquesService.Indicateurs;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PDFExportService {

    private static final float MARGIN = 50;
    private static final float LINE_HEIGHT = 18;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void exportFichePatient(Patient patient, File file) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = page.getMediaBox().getHeight() - MARGIN;
                PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                // Titre
                cs.beginText();
                cs.setFont(bold, 16);
                cs.newLineAtOffset(MARGIN, y);
                cs.showText("Fiche Patient");
                cs.endText();
                y -= LINE_HEIGHT * 2;

                // Date export
                cs.beginText();
                cs.setFont(regular, 10);
                cs.newLineAtOffset(MARGIN, y);
                cs.showText("Date d'export : " + LocalDate.now().format(DATE_FMT));
                cs.endText();
                y -= LINE_HEIGHT * 2;

                // Informations patient
                y = drawSection(cs, bold, regular, y, "Informations personnelles");
                y = drawField(cs, regular, y, "Nom", patient.getNom());
                y = drawField(cs, regular, y, "Prenom", patient.getPrenom());
                y = drawField(cs, regular, y, "N° Securite Sociale", patient.getNumeroSecuriteSociale());
                y = drawField(cs, regular, y, "Date de naissance",
                        patient.getDateNaissance() != null ? patient.getDateNaissance().format(DATE_FMT) : "-");
                y = drawField(cs, regular, y, "Age", patient.getAge() + " ans");
                y = drawField(cs, regular, y, "Sexe",
                        patient.getSexe() != null ? patient.getSexe().getLibelle() : "-");
                y = drawField(cs, regular, y, "Groupe sanguin",
                        patient.getGroupeSanguin() != null ? patient.getGroupeSanguin().getLibelle() : "-");
                y -= LINE_HEIGHT;

                y = drawSection(cs, bold, regular, y, "Coordonnees");
                y = drawField(cs, regular, y, "Email", orDash(patient.getEmail()));
                y = drawField(cs, regular, y, "Telephone", orDash(patient.getTelephone()));
                y = drawField(cs, regular, y, "Adresse", orDash(patient.getAdresse()));
                y -= LINE_HEIGHT;

                y = drawSection(cs, bold, regular, y, "Contact d'urgence");
                y = drawField(cs, regular, y, "Nom", orDash(patient.getPersonneContactNom()));
                y = drawField(cs, regular, y, "Telephone", orDash(patient.getPersonneContactTelephone()));
                y = drawField(cs, regular, y, "Lien", orDash(patient.getPersonneContactLien()));
                y -= LINE_HEIGHT;

                y = drawSection(cs, bold, regular, y, "Informations medicales");
                y = drawField(cs, regular, y, "Medecin traitant", orDash(patient.getMedecinTraitant()));
                y = drawField(cs, regular, y, "Allergies", orDash(patient.getAllergiesConnues()));
                y = drawField(cs, regular, y, "Antecedents", orDash(patient.getAntecedentsMedicaux()));
                drawField(cs, regular, y, "Notes", orDash(patient.getNotesMedicales()));
            }

            doc.save(file);
        }
    }

    public void exportRapportStatistiques(
            Indicateurs indicateurs,
            Map<String, Integer> hospitalisations,
            Map<String, Integer> gravite,
            Map<String, Integer> produits,
            Map<String, Double> occupation,
            File file) throws IOException {

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                float y = page.getMediaBox().getHeight() - MARGIN;
                PDType1Font bold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                cs.beginText();
                cs.setFont(bold, 16);
                cs.newLineAtOffset(MARGIN, y);
                cs.showText("Rapport Statistiques");
                cs.endText();
                y -= LINE_HEIGHT * 2;

                cs.beginText();
                cs.setFont(regular, 10);
                cs.newLineAtOffset(MARGIN, y);
                cs.showText("Date d'export : " + LocalDate.now().format(DATE_FMT));
                cs.endText();
                y -= LINE_HEIGHT * 2;

                y = drawSection(cs, bold, regular, y, "Indicateurs cles");
                y = drawField(cs, regular, y, "Patients hospitalises", String.valueOf(indicateurs.nbPatientsHospitalises()));
                y = drawField(cs, regular, y, "Dossiers en attente", String.valueOf(indicateurs.nbDossiersEnAttente()));
                y = drawField(cs, regular, y, "Stocks critiques", String.valueOf(indicateurs.nbStocksCritiques()));
                y = drawField(cs, regular, y, "Taux d'occupation global", indicateurs.tauxOccupationGlobal() + "%");
                y -= LINE_HEIGHT;

                y = drawSection(cs, bold, regular, y, "Hospitalisations par semaine");
                for (Map.Entry<String, Integer> e : hospitalisations.entrySet()) {
                    y = drawField(cs, regular, y, e.getKey(), String.valueOf(e.getValue()));
                    if (y < MARGIN + LINE_HEIGHT) break;
                }
                y -= LINE_HEIGHT;

                if (y > MARGIN + LINE_HEIGHT * 4) {
                    y = drawSection(cs, bold, regular, y, "Repartition par gravite");
                    for (Map.Entry<String, Integer> e : gravite.entrySet()) {
                        y = drawField(cs, regular, y, e.getKey(), String.valueOf(e.getValue()));
                        if (y < MARGIN + LINE_HEIGHT) break;
                    }
                    y -= LINE_HEIGHT;
                }

                if (y > MARGIN + LINE_HEIGHT * 4) {
                    y = drawSection(cs, bold, regular, y, "Produits les plus demandes");
                    for (Map.Entry<String, Integer> e : produits.entrySet()) {
                        y = drawField(cs, regular, y, e.getKey(), String.valueOf(e.getValue()));
                        if (y < MARGIN + LINE_HEIGHT) break;
                    }
                    y -= LINE_HEIGHT;
                }

                if (y > MARGIN + LINE_HEIGHT * 4) {
                    y = drawSection(cs, bold, regular, y, "Taux d'occupation par type");
                    for (Map.Entry<String, Double> e : occupation.entrySet()) {
                        drawField(cs, regular, y, e.getKey(), e.getValue() + "%");
                        y -= LINE_HEIGHT;
                        if (y < MARGIN + LINE_HEIGHT) break;
                    }
                }
            }

            doc.save(file);
        }
    }

    private float drawSection(PDPageContentStream cs, PDType1Font bold, PDType1Font regular,
                               float y, String title) throws IOException {
        cs.beginText();
        cs.setFont(bold, 12);
        cs.newLineAtOffset(MARGIN, y);
        cs.showText(title);
        cs.endText();
        return y - LINE_HEIGHT;
    }

    private float drawField(PDPageContentStream cs, PDType1Font regular,
                             float y, String label, String value) throws IOException {
        cs.beginText();
        cs.setFont(regular, 10);
        cs.newLineAtOffset(MARGIN + 10, y);
        cs.showText(label + " : " + (value != null ? value : "-"));
        cs.endText();
        return y - LINE_HEIGHT;
    }

    // =========================================================================
    // Export dossier de prise en charge
    // =========================================================================

    public void exportDossierPriseEnCharge(
            DossierPriseEnCharge dossier,
            List<Ordonnance> ordonnances,
            Hospitalisation hospitalisation,
            File file) throws IOException {

        try (PDDocument doc = new PDDocument()) {
            PDType1Font bold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font regular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            // Gestion multi-pages via un tableau mutable
            PDPage firstPage = new PDPage(PDRectangle.A4);
            doc.addPage(firstPage);
            PDPageContentStream[] csBox = { new PDPageContentStream(doc, firstPage) };
            float[] yBox = { firstPage.getMediaBox().getHeight() - MARGIN };

            // ---- Titre ----
            writeText(csBox[0], bold, 16, MARGIN, yBox[0], "Dossier de Prise en Charge");
            yBox[0] -= LINE_HEIGHT * 2;
            writeText(csBox[0], regular, 10, MARGIN, yBox[0],
                    "Date d'export : " + LocalDate.now().format(DATE_FMT));
            yBox[0] -= LINE_HEIGHT * 2;

            // ---- Patient ----
            yBox[0] = newSection(doc, csBox, yBox, bold, regular, "Patient");
            Patient p = dossier.getPatient();
            if (p != null) {
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Nom", p.getNom());
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Prenom", p.getPrenom());
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "N° Securite Sociale", orDash(p.getNumeroSecuriteSociale()));
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Date de naissance",
                        p.getDateNaissance() != null ? p.getDateNaissance().format(DATE_FMT) : "-");
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Age", p.getAge() + " ans");
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Sexe",
                        p.getSexe() != null ? p.getSexe().getLibelle() : "-");
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Groupe sanguin",
                        p.getGroupeSanguin() != null ? p.getGroupeSanguin().getLibelle() : "-");
            } else {
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Patient ID", String.valueOf(dossier.getPatientId()));
            }
            yBox[0] -= LINE_HEIGHT;

            // ---- Dossier ----
            yBox[0] = newSection(doc, csBox, yBox, bold, regular, "Informations du dossier");
            yBox[0] = newField(doc, csBox, yBox, bold, regular, "N° Dossier", orDash(dossier.getNumeroDossier()));
            yBox[0] = newField(doc, csBox, yBox, bold, regular, "Date admission",
                    dossier.getDateAdmission() != null
                            ? dossier.getDateAdmission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-");
            yBox[0] = newField(doc, csBox, yBox, bold, regular, "Mode d'arrivee",
                    dossier.getModeArrivee() != null ? dossier.getModeArrivee().getLibelle() : "-");
            yBox[0] = newField(doc, csBox, yBox, bold, regular, "Niveau de gravite",
                    dossier.getNiveauGravite() != null
                            ? "N" + dossier.getNiveauGravite().getCode() + " - " + dossier.getNiveauGravite().getLibelle() : "-");
            yBox[0] = newField(doc, csBox, yBox, bold, regular, "Statut",
                    dossier.getStatut() != null ? dossier.getStatut().getLibelle() : "-");
            yBox[0] -= LINE_HEIGHT;

            // ---- Clinique ----
            yBox[0] = newSection(doc, csBox, yBox, bold, regular, "Donnees cliniques");
            yBox[0] = newField(doc, csBox, yBox, bold, regular, "Motif d'admission", orDash(dossier.getMotifAdmission()));
            yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Symptomes", orDash(dossier.getSymptomes()));
            yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Constantes vitales", orDash(dossier.getConstantesVitales()));
            yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Antecedents", orDash(dossier.getAntecedents()));
            yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Allergies", orDash(dossier.getAllergies()));
            yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Traitement en cours", orDash(dossier.getTraitementEnCours()));
            yBox[0] -= LINE_HEIGHT;

            // ---- Ordonnances ----
            yBox[0] = newSection(doc, csBox, yBox, bold, regular, "Ordonnances");
            if (ordonnances == null || ordonnances.isEmpty()) {
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "", "Aucune ordonnance.");
            } else {
                for (Ordonnance o : ordonnances) {
                    String header = o.getNumeroOrdonnance() + "  [" + o.getStatut().getLibelle() + "]";
                    if (o.getDatePrescription() != null) {
                        header += "  -  " + o.getDatePrescription().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    }
                    yBox[0] = newSubSection(doc, csBox, yBox, bold, regular, header);
                    if (o.getNotes() != null && !o.getNotes().isBlank()) {
                        yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Prescriptions", o.getNotes());
                    }
                    if (o.getDateDebut() != null) {
                        yBox[0] = newField(doc, csBox, yBox, bold, regular, "Du",
                                o.getDateDebut().format(DATE_FMT)
                                + (o.getDateFin() != null ? "  au  " + o.getDateFin().format(DATE_FMT) : ""));
                    }
                    // Lignes de l'ordonnance
                    if (o.getLignes() != null && !o.getLignes().isEmpty()) {
                        for (LigneOrdonnance l : o.getLignes()) {
                            String produitNom = l.getProduit() != null ? l.getProduit().getNom() : "Produit #" + l.getProduitId();
                            String ligne = produitNom + "  |  " + orDash(l.getPosologie())
                                    + "  |  Qte : " + l.getQuantite()
                                    + (l.getDureeJours() != null ? "  |  " + l.getDureeJours() + "j" : "")
                                    + "  [" + l.getVoieAdministration().getLibelle() + "]";
                            yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "  Medicament", ligne);
                        }
                    }
                    yBox[0] -= LINE_HEIGHT * 0.5f;
                }
            }
            yBox[0] -= LINE_HEIGHT;

            // ---- Hospitalisation ----
            yBox[0] = newSection(doc, csBox, yBox, bold, regular, "Hospitalisation");
            if (hospitalisation == null) {
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "", "Aucune hospitalisation associee.");
            } else {
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "N° Sejour", orDash(hospitalisation.getNumeroSejour()));
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Statut", hospitalisation.getStatut().getLibelle());
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Entree",
                        hospitalisation.getDateEntree() != null
                                ? hospitalisation.getDateEntree().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-");
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Sortie prevue",
                        hospitalisation.getDateSortiePrevue() != null
                                ? hospitalisation.getDateSortiePrevue().format(DATE_FMT) : "-");
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Sortie effective",
                        hospitalisation.getDateSortieEffective() != null
                                ? hospitalisation.getDateSortieEffective().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-");
                yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Motif", orDash(hospitalisation.getMotifHospitalisation()));
                yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Diagnostic entree", orDash(hospitalisation.getDiagnosticEntree()));
                yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Diagnostic sortie", orDash(hospitalisation.getDiagnosticSortie()));
                yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Observations", orDash(hospitalisation.getObservations()));
                yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Evolution", orDash(hospitalisation.getEvolution()));
                if (hospitalisation.getTypeSortie() != null) {
                    yBox[0] = newField(doc, csBox, yBox, bold, regular, "Type de sortie", hospitalisation.getTypeSortie().getLibelle());
                }
            }
            yBox[0] -= LINE_HEIGHT;

            // ---- Clôture ----
            yBox[0] = newSection(doc, csBox, yBox, bold, regular, "Cloture");
            if (dossier.getStatut() == DossierPriseEnCharge.Statut.TERMINE) {
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Date de cloture",
                        dossier.getDateCloture() != null
                                ? dossier.getDateCloture().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-");
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "Destination sortie",
                        dossier.getDestinationSortie() != null ? dossier.getDestinationSortie().getLibelle() : "-");
                yBox[0] = newWrappedField(doc, csBox, yBox, bold, regular, "Notes de cloture", orDash(dossier.getNotesCloture()));
            } else {
                yBox[0] = newField(doc, csBox, yBox, bold, regular, "", "Dossier non cloture.");
            }

            csBox[0].close();
            doc.save(file);
        }
    }

    // ---- Helpers multi-pages ----

    /** Ouvre une nouvelle page si l'espace restant est insuffisant. */
    private void checkPage(PDDocument doc, PDPageContentStream[] csBox, float[] yBox, int linesNeeded) throws IOException {
        if (yBox[0] < MARGIN + LINE_HEIGHT * linesNeeded) {
            csBox[0].close();
            PDPage newPage = new PDPage(PDRectangle.A4);
            doc.addPage(newPage);
            csBox[0] = new PDPageContentStream(doc, newPage);
            yBox[0] = newPage.getMediaBox().getHeight() - MARGIN;
        }
    }

    private float newSection(PDDocument doc, PDPageContentStream[] csBox, float[] yBox,
                              PDType1Font bold, PDType1Font regular, String title) throws IOException {
        checkPage(doc, csBox, yBox, 3);
        yBox[0] = drawSection(csBox[0], bold, regular, yBox[0], title);
        return yBox[0];
    }

    private float newSubSection(PDDocument doc, PDPageContentStream[] csBox, float[] yBox,
                                 PDType1Font bold, PDType1Font regular, String title) throws IOException {
        checkPage(doc, csBox, yBox, 2);
        csBox[0].beginText();
        csBox[0].setFont(bold, 10);
        csBox[0].newLineAtOffset(MARGIN + 10, yBox[0]);
        csBox[0].showText(sanitize(title));
        csBox[0].endText();
        yBox[0] -= LINE_HEIGHT;
        return yBox[0];
    }

    private float newField(PDDocument doc, PDPageContentStream[] csBox, float[] yBox,
                            PDType1Font bold, PDType1Font regular,
                            String label, String value) throws IOException {
        checkPage(doc, csBox, yBox, 1);
        yBox[0] = drawField(csBox[0], regular, yBox[0], label, value);
        return yBox[0];
    }

    /** Affiche un champ avec retour à la ligne automatique pour les textes longs. */
    private float newWrappedField(PDDocument doc, PDPageContentStream[] csBox, float[] yBox,
                                   PDType1Font bold, PDType1Font regular,
                                   String label, String value) throws IOException {
        if (value == null || value.equals("-")) {
            return newField(doc, csBox, yBox, bold, regular, label, "-");
        }
        float maxWidth = PDRectangle.A4.getWidth() - MARGIN * 2 - 160; // espace après le label
        List<String> lines = wrapText(value, regular, 10, maxWidth);

        checkPage(doc, csBox, yBox, 1);
        // Première ligne avec le label
        String prefix = label.isBlank() ? "    " : label + " : ";
        csBox[0].beginText();
        csBox[0].setFont(regular, 10);
        csBox[0].newLineAtOffset(MARGIN + 10, yBox[0]);
        csBox[0].showText(sanitize(prefix + (lines.isEmpty() ? "-" : lines.get(0))));
        csBox[0].endText();
        yBox[0] -= LINE_HEIGHT;

        // Lignes suivantes (indentées)
        for (int i = 1; i < lines.size(); i++) {
            checkPage(doc, csBox, yBox, 1);
            csBox[0].beginText();
            csBox[0].setFont(regular, 10);
            csBox[0].newLineAtOffset(MARGIN + 10 + 10, yBox[0]);
            csBox[0].showText(sanitize(lines.get(i)));
            csBox[0].endText();
            yBox[0] -= LINE_HEIGHT;
        }
        return yBox[0];
    }

    private void writeText(PDPageContentStream cs, PDType1Font font, float size,
                            float x, float y, String text) throws IOException {
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(sanitize(text));
        cs.endText();
    }

    /** Découpe le texte en lignes ne dépassant pas maxWidth (en points PDF). */
    private List<String> wrapText(String text, PDType1Font font, float fontSize, float maxWidth) throws IOException {
        List<String> result = new ArrayList<>();
        if (text == null || text.isBlank()) {
            result.add("");
            return result;
        }
        String[] words = text.replace("\r\n", " ").replace("\n", " ").split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            float width = font.getStringWidth(sanitize(candidate)) / 1000f * fontSize;
            if (width > maxWidth && !current.isEmpty()) {
                result.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }
        if (!current.isEmpty()) result.add(current.toString());
        return result;
    }

    /** Remplace les caractères non ASCII non supportés par les polices Standard14. */
    private String sanitize(String text) {
        if (text == null) return "";
        return text
                .replace("é", "e").replace("è", "e").replace("ê", "e").replace("ë", "e")
                .replace("à", "a").replace("â", "a").replace("ä", "a")
                .replace("î", "i").replace("ï", "i")
                .replace("ô", "o").replace("ö", "o")
                .replace("ù", "u").replace("û", "u").replace("ü", "u")
                .replace("ç", "c")
                .replace("É", "E").replace("È", "E").replace("Ê", "E")
                .replace("À", "A").replace("Â", "A")
                .replace("Î", "I").replace("Ô", "O").replace("Ù", "U")
                .replace("Ç", "C")
                .replaceAll("[^\\x20-\\x7E]", "?");
    }

    private String orDash(String value) {
        return (value != null && !value.isBlank()) ? value : "-";
    }
}
