package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class Inser {

    public static void inserer() {
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {

            // 🔴 Activer les clés étrangères
            stmt.execute("PRAGMA foreign_keys = ON");

            // =========================
            // CREATION TABLES
            // =========================
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS niveau (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS serie (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS groupe (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS tarif (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    salaire_horaire REAL NOT NULL,
                    taux_surveillance REAL NOT NULL
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS type (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS motif (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE,
                    type_id INTEGER NOT NULL,
                    statut_id INTEGER NOT NULL,
                    FOREIGN KEY (type_id) REFERENCES type(id) ON DELETE CASCADE
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS anneescolaire (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    libelle TEXT NOT NULL UNIQUE,
                    date_debut TEXT,
                    date_fin TEXT,
                    active INTEGER DEFAULT 0
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS classe (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    niveau_id INTEGER NOT NULL,
                    serie_id INTEGER,
                    groupe_id INTEGER NOT NULL,
                    FOREIGN KEY(niveau_id) REFERENCES niveau(id),
                    FOREIGN KEY(serie_id) REFERENCES serie(id),
                    FOREIGN KEY(groupe_id) REFERENCES groupe(id)
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS matiere (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS enseignant (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    prenom TEXT,
                    date_naissance TEXT,
                    adresse TEXT,
                    telephone TEXT,
                    specialite TEXT
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS habilitation (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    enseignant_id INTEGER NOT NULL,
                    matiere_id INTEGER NOT NULL,
                    annee_id INTEGER NOT NULL,
                    FOREIGN KEY(enseignant_id) REFERENCES enseignant(id),
                    FOREIGN KEY(matiere_id) REFERENCES matiere(id),
                    FOREIGN KEY(annee_id) REFERENCES anneescolaire(id)
                );
            """);

            System.out.println("Tables créées ✅");

            // =========================
            // INSERT DONNÉES TEST
            // =========================

            // 🔹 Niveau
            PreparedStatement psNiveau = conn.prepareStatement("INSERT OR IGNORE INTO niveau(nom) VALUES (?)");
            String[] niveaux = {"12ème", "11ème", "10ème"};
            for (String n : niveaux) {
                psNiveau.setString(1, n);
                psNiveau.executeUpdate();
            }

            // 🔹 Série
            PreparedStatement psSerie = conn.prepareStatement("INSERT OR IGNORE INTO serie(nom) VALUES (?)");
            String[] series = {"SCIENCES", "LETTRE", "TSECO","TSE","TSS","TSEXP","C"};
            for (String s : series) {
                psSerie.setString(1, s);
                psSerie.executeUpdate();
            }

            // 🔹 Groupe
            PreparedStatement psGroupe = conn.prepareStatement("INSERT OR IGNORE INTO groupe(nom) VALUES (?)");
            String[] groupes = {"G1", "G2", "G3"};
            for (String g : groupes) {
                psGroupe.setString(1, g);
                psGroupe.executeUpdate();
            }

           

            // 🔹 Type
            stmt.execute("""
                INSERT OR IGNORE INTO type(id, nom) VALUES
                (1, 'RECETTE'),
                (2, 'DEPENSE');
            """);

            // 🔹 Motif
            stmt.execute("""
                INSERT OR IGNORE INTO motif(nom, type_id) VALUES
                ('Salaire', 2),
                ('Reparation', 2),
                ('Prime', 2),
                ('Avance', 2);
            """);

           
            // 🔹 Matière
            PreparedStatement psMatiere = conn.prepareStatement(
                    "INSERT OR IGNORE INTO matiere(nom) VALUES (?)"
            );
            psMatiere.setString(1, "Mathématiques");
            psMatiere.executeUpdate();

            // 🔹 Enseignant
            PreparedStatement psEnseignant = conn.prepareStatement(
                    "INSERT OR IGNORE INTO enseignant(nom, prenom, date_naissance, adresse, telephone, specialite) VALUES (?, ?, ?, ?, ?, ?)"
            );
            psEnseignant.setString(1, "Traoré");
            psEnseignant.setString(2, "Moussa");
            psEnseignant.setString(3, "1985-05-10");
            psEnseignant.setString(4, "Bamako");
            psEnseignant.setString(5, "70000000");
            psEnseignant.setString(6, "Mathématiques");
            psEnseignant.executeUpdate();

            // 🔹 Habilitation
            PreparedStatement psHabilitation = conn.prepareStatement(
                    "INSERT OR IGNORE INTO habilitation(enseignant_id, matiere_id, annee_id) VALUES (?, ?, ?)"
            );
            psHabilitation.setInt(1, 1);
            psHabilitation.setInt(2, 1);
            psHabilitation.setInt(3, 1);
            psHabilitation.executeUpdate();

            System.out.println("Insertion de données test réussie ✅");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}