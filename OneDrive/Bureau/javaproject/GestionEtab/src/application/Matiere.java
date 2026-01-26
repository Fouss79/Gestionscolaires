package application;

public class Matiere {
    private String id;
    private String nom;
    private String classe;
    private int coeff;

    // 🔹 Constructeur vide (obligatoire pour JavaFX)
    public Matiere() {}

    // 🔹 Constructeur avec paramètres
    public Matiere(String id, String nom) {
        this.id = id;
        this.nom = nom;
        this.classe= classe;
        this.coeff= coeff;
    }

    // 🔹 Getters & Setters
    public String getId() { 
        return id; 
    }
    public void setId(String id) { 
        this.id = id; 
    }

    public String getNom() { 
        return nom; 
    }
    public void setNom(String nom) { 
        this.nom = nom; 
    }
    
    public String getClasse() { 
        return classe; 
    }
    public void setClasse(String classe) { 
        this.classe = classe; 
    }
    public int getCoeff() { 
        return coeff; 
    }
    public void setCoeff(int  coeff) { 
        this.coeff = coeff; 
    }


    // 🔹 Méthode utilitaire
    @Override
    public String toString() {
        return "Matiere{" +
                "id=" + id +
                ", nom='" + nom + 
                '\'' +
                
                '}';
    }
}
