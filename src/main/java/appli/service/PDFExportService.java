package appli.service;

import appli.model.Patient;
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

    private String orDash(String value) {
        return (value != null && !value.isBlank()) ? value : "-";
    }
}
