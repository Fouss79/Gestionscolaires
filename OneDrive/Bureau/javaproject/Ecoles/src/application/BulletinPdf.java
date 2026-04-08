package application;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class BulletinPdf {

    public static void genererBulletin(long eleveId, String periode, String classeNom, String cheminFichier) {
        try (Connection conn = Database.connect()) {

            // 1️⃣ Récupérer infos élève
            PreparedStatement psEleve = conn.prepareStatement(
                    "SELECT nom, prenom, numero_matricule FROM eleve WHERE id = ?"
            );
            psEleve.setLong(1, eleveId);
            ResultSet rsEleve = psEleve.executeQuery();

            String nom = "", prenom = "", matricule = "";
            if (rsEleve.next()) {
                nom = rsEleve.getString("nom");
                prenom = rsEleve.getString("prenom");
                matricule = rsEleve.getString("numero_matricule");
            }

            // 2️⃣ Récupérer toutes les notes
            PreparedStatement psNotes = conn.prepareStatement("""
                SELECT m.nom AS matiere, n.n_class, n.n_exem, n.coeff
                FROM note n
                JOIN matiere m ON n.matiere_id = m.id
                JOIN classe c ON n.classe_id = c.id
                WHERE n.eleve_id = ? AND n.periode = ? AND c.nom = ?
            """);
            psNotes.setLong(1, eleveId);
            psNotes.setString(2, periode);
            psNotes.setString(3, classeNom);

            ResultSet rsNotes = psNotes.executeQuery();

            // 3️⃣ Création du PDF
            PdfWriter writer = new PdfWriter(new File(cheminFichier));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Titre
            Paragraph titre = new Paragraph("Bulletin Scolaire")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titre);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Nom : " + nom));
            document.add(new Paragraph("Prénom : " + prenom));
            document.add(new Paragraph("Matricule : " + matricule));
            document.add(new Paragraph("Classe : " + classeNom));
            document.add(new Paragraph("Période : " + periode));
            document.add(new Paragraph("\n"));

            // 4️⃣ Table des notes
            float[] columnWidths = {200, 60, 60, 60, 80};
            Table table = new Table(columnWidths);

            table.addHeaderCell("Matière");
            table.addHeaderCell("N.Class");
            table.addHeaderCell("N.Exam");
            table.addHeaderCell("Moyenne");
            table.addHeaderCell("Mention");

            double totalMoyenne = 0;
            double totalCoeff = 0;

            while (rsNotes.next()) {
                String matiere = rsNotes.getString("matiere");
                double nClass = rsNotes.getDouble("n_class");
                double nExem = rsNotes.getDouble("n_exem");
                double coeff = rsNotes.getDouble("coeff");

                double moyenne = (nClass + nExem*2)/3;
                totalMoyenne += moyenne * coeff;
                totalCoeff += coeff;

                String mention;
                if (moyenne < 10) mention = "Insuffisant";
                else if (moyenne < 12) mention = "Passable";
                else if (moyenne < 14) mention = "Assez Bien";
                else if (moyenne < 16) mention = "Bien";
                else if (moyenne < 18) mention = "Très Bien";
                else mention = "Excellent";

                table.addCell(matiere);
                table.addCell(String.format("%.2f", nClass));
                table.addCell(String.format("%.2f", nExem));
                table.addCell(String.format("%.2f", moyenne));
                table.addCell(mention);
            }

            document.add(table);

            // 5️⃣ Moyenne générale
            double moyenneGenerale = totalCoeff > 0 ? totalMoyenne / totalCoeff : 0;
            String mentionGen;
            if (moyenneGenerale < 10) mentionGen = "Insuffisant";
            else if (moyenneGenerale < 12) mentionGen = "Passable";
            else if (moyenneGenerale < 14) mentionGen = "Assez Bien";
            else if (moyenneGenerale < 16) mentionGen = "Bien";
            else if (moyenneGenerale < 18) mentionGen = "Très Bien";
            else mentionGen = "Excellent";

            document.add(new Paragraph("\nMoyenne Générale : " + String.format("%.2f", moyenneGenerale))
                    .setBold());
            document.add(new Paragraph("Mention : " + mentionGen)
                    .setBold()
                    .setFontColor(ColorConstants.BLUE));

            document.close();

            System.out.println("Bulletin PDF généré : " + cheminFichier);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
