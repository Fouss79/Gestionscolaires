package application;

public class Frais {
	private String motif;
	private Double montant;
	private String anneeScolaire;
	private String  niveau;
	
	public Frais() {}
	public Frais(String motif,Double montant,String anneeScolaire,String niveau) {
		this.motif=motif;
		this.montant=montant;
		this.anneeScolaire=anneeScolaire;
		this.niveau = niveau;
	}	
		public String getMotif(){
			return motif;}
		public void setMotif(String motif) {
			this.motif=motif;
		}
		public Double getMontant() { return montant;}
		public void setMontant(Double montant) {this.montant=montant;}
		
		public String getAnneeScolaire() {return anneeScolaire;}
		public void setAnneeScolaire(String anneeScolaire) {this.anneeScolaire=anneeScolaire;}
		
		public String getNiveau() {return niveau;}
		public void setNiveau(String niveau) {this.niveau=niveau;}
		

}
