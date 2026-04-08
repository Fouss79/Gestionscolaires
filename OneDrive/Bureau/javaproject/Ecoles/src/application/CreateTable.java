package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class CreateTable {

    public static void createTables() {
        // SQL des tables
        String sqlAnnee = """
            CREATE TABLE IF NOT EXISTS anneescolaire (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                libelle TEXT NOT NULL,
                date_debut TEXT,
                date_fin TEXT,
                active INTEGER DEFAULT 0
            );
        """;

        String sqlNiveau = """
            CREATE TABLE IF NOT EXISTS niveau (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL UNIQUE
            );
        """;
        
        String sqlSerie = """
        	    CREATE TABLE IF NOT EXISTS serie (
        	        id INTEGER PRIMARY KEY AUTOINCREMENT,
        	        nom TEXT NOT NULL UNIQUE
        	    );
        	""";
        String sqlGroupe = """
        	    CREATE TABLE IF NOT EXISTS groupe (
        	        id INTEGER PRIMARY KEY AUTOINCREMENT,
        	        nom TEXT NOT NULL UNIQUE
        	    );
        	""";
        
        String sqlMatiere = """
                CREATE TABLE IF NOT EXISTS Matiere (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE
                );
            """;

        String sqlClasse = """
    
CREATE TABLE IF NOT EXISTS classe (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    niveau_id INTEGER NOT NULL,
    serie_id INTEGER,
    groupe_id INTEGER NOT NULL
   
)
        """;

        String sqlEleve = """
            CREATE TABLE IF NOT EXISTS eleve (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                prenom TEXT NOT NULL,
                date_naissance TEXT,
                adresse TEXT,
                telephone TEXT,
                classe_id INTEGER,
                numero_matricule TEXT UNIQUE,
                sexe TEXT,
                annee_id INTEGER,
                FOREIGN KEY (classe_id) REFERENCES classe(id),
                FOREIGN KEY (annee_id) REFERENCES anneescolaire(id)
            );
        """;
        String sqlType = """
                CREATE TABLE IF NOT EXISTS type (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE
                );
                		""";
       
        String sqlMotif ="""
        		
        		    CREATE TABLE IF NOT EXISTS motif (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL UNIQUE,
                    type_id INTEGER NOT NULL,
                    FOREIGN KEY (type_id) REFERENCES type(id) ON DELETE CASCADE
                );
        		
            """;
        
        String sqlTarif = """
        	    CREATE TABLE IF NOT EXISTS tarif (
        	        id INTEGER PRIMARY KEY AUTOINCREMENT,
        	        motif_id INTEGER,
        	        annee_id INTEGER,
        	        montant REAL,
        	        FOREIGN KEY (motif_id) REFERENCES motif(id),
        	        FOREIGN KEY (annee_id) REFERENCES anneescolaire(id)
        	    );
        	""";
        
        String sqlFrais = """
        	    CREATE TABLE IF NOT EXISTS frais (
        	        id INTEGER PRIMARY KEY AUTOINCREMENT,
        	        motif_id INTEGER,
        	        annee_id INTEGER,
        	        montant REAL,
        	        niveau_id INTEGER,
        	        FOREIGN KEY (motif_id) REFERENCES motif(id),
        	        FOREIGN KEY (annee_id) REFERENCES anneescolaire(id),
        	        FOREIGN KEY (niveau_id) REFERENCES niveau(id)
        	    );
        	""";

        
     
        
        String sqlEmploiDuTemps = """
        		CREATE TABLE IF NOT EXISTS emploi_du_temps (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    jour TEXT NOT NULL,
    heure_debut TEXT NOT NULL,
    heure_fin TEXT NOT NULL,
    matiere TEXT NOT NULL,
    professeur TEXT NOT NULL,
    classe TEXT NOT NULL,
    annee_id INTEGER NOT NULL,
    FOREIGN KEY (annee_id) REFERENCES anneescolaire(id) ON DELETE CASCADE
);

        		""";
        String sqlRole = """
        CREATE TABLE IF NOT EXISTS role (
        	    id INTEGER PRIMARY KEY AUTOINCREMENT,
        	    nom TEXT NOT NULL UNIQUE
        	);
""";
        String sqlUtilisateur = """  
        CREATE TABLE IF NOT EXISTS utilisateur (
        	    id INTEGER PRIMARY KEY AUTOINCREMENT,
        	    username TEXT NOT NULL UNIQUE,
        	    password TEXT NOT NULL,
        	    role_id INTEGER NOT NULL,
        	    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE RESTRICT ON UPDATE CASCADE
        	); """;

        

        String sqlInscription = """
            CREATE TABLE IF NOT EXISTS inscription (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                eleve_id INTEGER NOT NULL,
                classe_id INTEGER NOT NULL,
                annee_id INTEGER NOT NULL,
                date_inscription TEXT NOT NULL,
                statut TEXT DEFAULT 'Actif',
                FOREIGN KEY (eleve_id) REFERENCES eleve(id),
                FOREIGN KEY (classe_id) REFERENCES classe(id),
                FOREIGN KEY (annee_id) REFERENCES anneescolaire(id)
            );
        """;
            

        
        String sqlEnseignant = """ 
        		
        		CREATE TABLE IF NOT EXISTS enseignant (
        	    id INTEGER PRIMARY KEY AUTOINCREMENT,
        	    nom TEXT NOT NULL,
        	    prenom TEXT NOT NULL,
        	    date_naissance TEXT NOT NULL,
        	    adresse TEXT NOT NULL,
        	    telephone TEXT NOT NULL,
        	    specialite TEXT NOT NULL
        	      );
            """;
        
        String sqlNote = """ 
        	   CREATE TABLE IF NOT EXISTS note (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    eleve_id INTEGER,
    matiere_id INTEGER,
    classe_id INTEGER,
    annee_id INTEGER,
    periode TEXT,
    n_class REAL,
    n_exem REAL,
    coeff REAL
);

        	""";


          
String sqlHabilitation = """ 
		CREATE TABLE IF NOT EXISTS habilitation (
        	    id INTEGER PRIMARY KEY AUTOINCREMENT,
        	    enseignant_id INTEGER NOT NULL,
        	    matiere_id INTEGER NOT NULL,
        	    annee_id INTEGER NOT NULL,

        	    FOREIGN KEY (enseignant_id) REFERENCES enseignant(id) ON DELETE CASCADE,
        	    FOREIGN KEY (matiere_id) REFERENCES matiere(id) ON DELETE CASCADE,
        	    FOREIGN KEY (annee_id) REFERENCES anneescolaire(id) ON DELETE CASCADE
        	);

        		
        	""";
        
                
        
        
        String sqlEnseignement = """ 
        		CREATE TABLE IF NOT EXISTS enseignement (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    classe_id INTEGER NOT NULL,
    matiere_id INTEGER NOT NULL,
    coeff INTEGER NOT NULL CHECK(coeff > 0),
    annee_id INTEGER NOT NULL,

    -- 🔹 Clés étrangères
    FOREIGN KEY (classe_id) REFERENCES classe(id) ON DELETE CASCADE,
    FOREIGN KEY (matiere_id) REFERENCES matiere(id) ON DELETE CASCADE,
    FOREIGN KEY (annee_id) REFERENCES anneescolaire(id) ON DELETE CASCADE,

    -- 🔹 Empêcher les doublons
    UNIQUE (classe_id, matiere_id, annee_id)
);

            """;
               
 String sqlEmarge = """ 
        		CREATE TABLE IF NOT EXISTS emargement (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    jour TEXT NOT NULL,
    enseignant_id INTEGER NOT NULL,
    enseignant TEXT NOT NULL,
    classe TEXT NOT NULL,
    matiere TEXT NOT NULL,
    date_heure TEXT NOT NULL,
    duree INTEGER NOT NULL,
    present INTEGER NOT NULL DEFAULT 1,
    annee_id INTEGER NOT NULL,

    FOREIGN KEY (enseignant_id) REFERENCES enseignant(id) ON DELETE CASCADE,
    FOREIGN KEY (annee_id) REFERENCES anneescolaire(id) ON DELETE CASCADE
);""";
            
 String sqlPaie = """
		 CREATE TABLE IF NOT EXISTS paiement (
		     id INTEGER PRIMARY KEY AUTOINCREMENT,

		     nom TEXT NOT NULL,
		     prenom TEXT NOT NULL,

		     motif TEXT NOT NULL,
		     type TEXT,

		     enseignant_id INTEGER NOT NULL,

		     date_debut TEXT,
		     date_fin TEXT,

		     montant_total REAL NOT NULL DEFAULT 0 CHECK(montant_total >= 0),
		     montant_paye REAL NOT NULL DEFAULT 0 CHECK(montant_paye >= 0),
		     reste REAL NOT NULL DEFAULT 0 CHECK(reste >= 0),

		     total_minutes REAL DEFAULT 0 CHECK(total_minutes >= 0),

		     date_creation TEXT DEFAULT CURRENT_DATE,
		     mois TEXT,
             annee_id INTEGER NOT NULL,
FOREIGN KEY (annee_id) REFERENCES anneescolaire(id) ON DELETE CASCADE
		     FOREIGN KEY (enseignant_id) 
		         REFERENCES enseignant(id) 
		         ON DELETE CASCADE
		 );
		 """;
 String sqlPaiement = """
 CREATE TABLE IF NOT EXISTS paiements (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    eleve_id INTEGER,
    frais_id INTEGER,
    montant_paye REAL,
    mode_paiement TEXT,
    date_paiement DATETIME DEFAULT CURRENT_TIMESTAMP,
    annee_id INTEGER,

    FOREIGN KEY (eleve_id) REFERENCES eleve(id),
    FOREIGN KEY (frais_id) REFERENCES frais(id),
    FOREIGN KEY (annee_id) REFERENCES anneescolaire(id)
);""";
 
               
 String sqlTrans = """
		 CREATE TABLE IF NOT EXISTS transaction_enseignant (
		     id INTEGER PRIMARY KEY AUTOINCREMENT,

		     paiement_id INTEGER NOT NULL,
		     enseignant_id INTEGER NOT NULL,

		     montant REAL NOT NULL CHECK(montant > 0),
		     date_transaction TEXT NOT NULL,

		     mode_paiement TEXT,
		     commentaire TEXT,

		     FOREIGN KEY (paiement_id) 
		         REFERENCES paiement(id) 
		         ON DELETE CASCADE,

		     FOREIGN KEY (enseignant_id) 
		         REFERENCES enseignant(id) 
		         ON DELETE CASCADE
		 );
		 """;
 String sqlTransaction = """
 		
 		CREATE TABLE IF NOT EXISTS transaction_eleve (
		    id INTEGER PRIMARY KEY AUTOINCREMENT,

		    paiement_id INTEGER NOT NULL,
		    eleve_id INTEGER NOT NULL,

		    montant REAL NOT NULL CHECK(montant > 0),
		    date_transaction TEXT NOT NULL,

		    mode_paiement TEXT,
		    commentaire TEXT,

		    FOREIGN KEY (paiement_id) 
		        REFERENCES paiements(id) 
		        ON DELETE CASCADE,

		    FOREIGN KEY (eleve_id) 
		        REFERENCES eleve(id) 
		        ON DELETE CASCADE
		); """;

 
 

        
        

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {

            // ✅ Activer les FK
            stmt.execute("PRAGMA foreign_keys = ON");

            // Création des tables dans l'ordre correct
            stmt.execute(sqlAnnee);
            stmt.execute(sqlNiveau);
            stmt.execute(sqlSerie);
            stmt.execute(sqlGroupe);
            stmt.execute(sqlRole);          // 🔥 d'abord role
            stmt.execute(sqlUtilisateur);   // ensuite utilisateur
            stmt.execute(sqlMatiere);
            stmt.execute(sqlClasse);
            stmt.execute(sqlEleve);
            stmt.execute(sqlType);
            stmt.execute(sqlMotif);
            stmt.execute(sqlTarif);
            stmt.execute(sqlFrais);
            stmt.execute(sqlEnseignant);
            stmt.execute(sqlNote);
            stmt.execute(sqlInscription);
            stmt.execute(sqlEmploiDuTemps);
            stmt.execute(sqlEmarge);
            stmt.execute(sqlHabilitation);
            stmt.execute(sqlEnseignement);
            stmt.execute(sqlPaiement);
            stmt.execute(sqlPaie);
            stmt.execute(sqlTrans);
            stmt.execute(sqlTransaction);
            
            

            System.out.println("Toutes les tables sont créées avec succès ✅");
          
          
           
      
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    








