package application;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.util.List;

public class PDFGenerator {

    /**
     * Génère un bulletin PDF pour un élève.
     *
     * @param eleveNom Nom complet de l'élève
     * @param classeNom Classe
     * @param periode Période (ex: Trimestre 1)
     * @param moyenne Moyenne générale
     * @param rang Rang
     * @param notes Liste de notes
     * @param filePath Chemin de sortie du PDF
     */
    public static void genererBulletin(String eleveNom, String classeNom, String periode,
                                       double moyenne, String rang, List<Note> notes, String filePath) {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // --- Police ---
            Font titreFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font sousTitreFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
            Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font tableCellFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

            // --- Titre ---
            Paragraph titre = new Paragraph("BULLETIN SCOLAIRE", titreFont);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(20);
            document.add(titre);

            // --- Informations élève ---
            Paragraph infos = new Paragraph(
                    "Élève : " + eleveNom + "\n" +
                    "Classe : " + classeNom + "\n" +
                    "Période : " + periode,
                    sousTitreFont
            );
            infos.setSpacingAfter(20);
            document.add(infos);

            // --- Tableau des notes ---
            PdfPTable table = new PdfPTable(5); // 5 colonnes : Matière, NClasse, NExem, Coeff, Moyenne
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Largeur des colonnes
            float[] columnWidths = {3f, 1.5f, 1.5f, 1f, 1.5f};
            table.setWidths(columnWidths);

            // --- Entête ---
            addTableHeader(table, "Matière", tableHeaderFont);
            addTableHeader(table, "Note Classe", tableHeaderFont);
            addTableHeader(table, "Note Examen", tableHeaderFont);
            addTableHeader(table, "Coeff", tableHeaderFont);
            addTableHeader(table, "Moyenne", tableHeaderFont);

            // --- Lignes des notes ---
            for (Note note : notes) {
                addTableCell(table, note.getMatiereNom(), tableCellFont);
                addTableCell(table, String.format("%.2f", note.getNClass()), tableCellFont);
                addTableCell(table, String.format("%.2f", note.getNExem()), tableCellFont);
                addTableCell(table, String.format("%.2f", note.getCoeff()), tableCellFont);
                addTableCell(table, String.format("%.2f", note.getMoyenne()), tableCellFont);
            }

            document.add(table);

            // --- Moyenne et Rang ---
            Paragraph summary = new Paragraph(
                    "Moyenne Générale : " + String.format("%.2f", moyenne) + "\n" +
                    "Rang : " + rang,
                    sousTitreFont
            );
            summary.setSpacingBefore(20);
            document.add(summary);

            document.close();
            System.out.println("PDF généré avec succès : " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthodes auxiliaires pour le tableau
    private static void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }
}
