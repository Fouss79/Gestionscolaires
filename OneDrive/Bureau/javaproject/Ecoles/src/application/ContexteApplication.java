package application;

public class ContexteApplication {

    private static String anneeScolaire;
    private static int anneeId; // ← Ajout de l'ID
    private static Utilisateur utilisateurConnecte;
    private static AnneeScolaire anneeActive;

    public static AnneeScolaire getAnneeActive() {
        return anneeActive;
    }

    public static void setAnneeActive(AnneeScolaire annee) {
        anneeActive = annee;
    }

    public static Long getAnneeIdActive() {
        return anneeActive != null ? anneeActive.getId() : null;
    }

    // ===== Année scolaire =====
    public static String getAnneeScolaire() {
        return anneeScolaire;
    }

    public static void setAnneeScolaire(String annee) {
        anneeScolaire = annee;
    }

    // ===== Année scolaire ID =====
    public static int getAnneeId() {
        return anneeId;
    }

    public static void setAnneeId(int id) {
        anneeId = id;
    }

    // ===== Utilisateur connecté =====
    public static Utilisateur getUtilisateurConnecte() {
        return utilisateurConnecte;
    }

    public static void setUtilisateurConnecte(Utilisateur utilisateur) {
        utilisateurConnecte = utilisateur;
    }

    // ===== Reset session =====
    public static void reset() {
        anneeScolaire = null;
        anneeId = 0;
        utilisateurConnecte = null;
    }
}