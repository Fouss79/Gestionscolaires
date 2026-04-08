package application;

public class Note {

    private long id;
    private String periode;
    private String classe;
    private String eleveNom;
    private String matiereNom;
    private double nClass;
    private double nExem;
    private double coeff;
    private double moyenne;
    private String mention;



    // 🔹 Constructeur complet
    public Note(long id, String periode, String classe, String eleveNom, String matiereNom,
                double nClass, double nExem, double coeff) {
        this.id = id;
        this.periode = periode;
        this.classe = classe;
        this.eleveNom = eleveNom;
        this.matiereNom = matiereNom;
        this.nClass = nClass;
        this.nExem = nExem;
        this.coeff = coeff;
        this.moyenne = (nClass + nExem*2) / 3;
        this.mention = calculerMention(this.moyenne);



    }

    // 🔹 Getters et Setters
    
    public Note() {
		// TODO Auto-generated constructor stub
    	 
	}
    private String calculerMention(double moyenne) {

        if (moyenne < 10) return "Insuffisant";
        else if (moyenne < 12) return "Passable";
        else if (moyenne < 14) return "Assez Bien";
        else if (moyenne < 16) return "Bien";
        else if (moyenne < 18) return "Très Bien";
        else return "Excellent";
    }


	public double getMoyenne() {
        return moyenne;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getMention() {
        return mention;
    }


    public String getPeriode() { return periode; }
    public void setPeriode(String periode) { this.periode = periode; }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public String getEleveNom() { return eleveNom; }
    public void setEleveNom(String eleveNom) { this.eleveNom = eleveNom; }

    public String getMatiereNom() { return matiereNom; }
    public void setMatiereNom(String matiereNom) { this.matiereNom = matiereNom; }

    public double getNClass() { return nClass; }
    public void setNClass(double nClass) { this.nClass = nClass; }

    public double getNExem() { return nExem; }
    public void setNExem(double nExem) { this.nExem = nExem; }

    public double getCoeff() { return coeff; }
    public void setCoeff(double coeff) { this.coeff = coeff; }

}
