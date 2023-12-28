package Voyageur_De_Commerce;

import java.io.*;

import Algo_Genetiques.Individu_SAD;
import Util.Lecture;
import Algo_Genetiques.Population;

public class Client_Voyageur_De_Commerce {


	/**
	 * lit une liste de poids dans un fichier
	 * @param nomFichier  nom du fichier texte contenant les coordonnées des villes
	 * @param nbr_villes nombre de villes
	 * @param coord_x et coord_y : les 2 tableaux que la fonction remplit et qui vont contenir les coordonnées des villes
	 */
	public static void charge_coords(String nomFichier, int nbr_villes, double[] coord_x, double[] coord_y){
		assert(coord_x.length==coord_y.length) : "charge_coords : coord_x et coord_y n'ont pas la même taille ?";
		InputStream IS = Lecture.ouvrir(nomFichier);
		if (IS==null){
			System.err.println("pb d'ouverture du fichier "+nomFichier);
		}
		int i=0;
		while(!Lecture.finFichier(IS) && i<coord_x.length){
			coord_x[i] = Lecture.lireDouble(IS);
			coord_y[i] = Lecture.lireDouble(IS);
			i++;
		}
		Lecture.fermer(IS);
	}

	public static void main(String[] args) throws InterruptedException{

		/* on initialise les coordonnées des villes en les lisant ds un fichier 
		 */
		int nbr_indiv = 100;
		double prob_mut=0.1;

		int nbr_villes = 250;
		double[] coord_x = new double[nbr_villes];
		double[] coord_y = new double[nbr_villes];
		charge_coords("data_vdc/"+nbr_villes+"coords.txt",nbr_villes, coord_x, coord_y);
		//charge_coords("data_vdc/quadraturecercle_200.txt",nbr_villes, coord_x, coord_y);

		/* Exemple d'utilisation de Display_VDCC (il faut d'abord faire le constructeur pour ce test fonctionne, ainsi que compléter les accesseurs)
		 */
		Individu_VDC[] pop_sad = new Individu_VDC[nbr_indiv];
		for(int i =0; i<nbr_indiv;i++){
			pop_sad[i] = new Individu_VDC(coord_x, coord_y);
		}
		Population<Individu_VDC> population = new Population<>(pop_sad);

		int generation = 1;
		int nbr_iteration = 2000	;
		while(generation <= nbr_iteration) {
			population.reproduction(prob_mut);
			System.out.println("Generation " + generation + " : Adaptation moyenne - " + population.adaptation_moyenne()+" // Adaptation maximale - " + population.adaptation_maximale());
			generation++;
		}

		Individu_VDC max = new Individu_VDC(population.individu_maximal());
		Display_VDC disp = new Display_VDC(max); //on l'affiche
	}
}