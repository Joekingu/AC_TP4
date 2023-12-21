package Algo_Genetiques;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Population<Indiv extends Individu> {
	
	// Liste contenant les différents individus d'une génération
	private List<Indiv> population;

	/**
	 * construit une population à partir d'un tableau d'individu
	 */
	public  Population(Indiv[] popu){
		population = new ArrayList<>();
		population.addAll(Arrays.asList(popu));
	}
	
	/**
	 * sélectionne un individu (sélection par roulette par exemple, cf TD)
	 * @param adapt_totale somme des adaptations de tous les individus (pour ne pas avoir à la recalculer)
	 * @return indice de l'individu sélectionné
	 */
	public int selection(double adapt_totale){
		//on utilise un générateur de nombres aléatoires pour choisir un nombre entre 0 et adapt_totale
		Random rand = new Random();
		double randValue = rand.nextDouble() * adapt_totale;

		//on parcourt tous les individus et on calcule leur adaptation relative
		//c'est-à-dire leur adaptation divisée par la somme des adaptations de tous les individus
		double adapt_relative = 0;
		for (int i = 0; i < population.size(); i++) {
			adapt_relative += population.get(i).adaptation() / adapt_totale;

			//si le nombre aléatoire est inférieur à l'adaptation relative de l'individu,
			//c'est que l'individu a été sélectionné
			if (randValue <= adapt_relative) {
				return i;
			}
		}

		//si aucun individu n'a été sélectionné, on retourne l'indice de l'individu ayant la plus grande adaptation
		return population.size() - 1;
	}
	
	/**
	 * remplace la génération par la suivante
	 * (croisement + mutation)
	 * @param prob_mut probabilité de mutation
	 */
	@SuppressWarnings("unchecked")
	public void reproduction(double prob_mut) {
		
		/***** on construit la nouvelle génération ****/
		List<Indiv> new_generation = new ArrayList<Indiv>();
		double adapt_total = adaptation_totale();

		// tant qu'on n'a pas le bon nombre 
		while (new_generation.size()<population.size()-1){
			// on sélectionne les parents
			Indiv parent1 = population.get(selection(adapt_total));
			Indiv parent2 = population.get(selection(adapt_total));
			
			// ils se reproduisent
			Individu[] enfants = parent1.croisement(parent2);

			// on les ajoute à la nouvelle génération
			new_generation.add((Indiv) enfants[0]);
			new_generation.add((Indiv) enfants[1]);
		}
		
		// on applique une éventuelle mutation à toute la nouvelle génération
		new_generation.forEach(indiv -> indiv.mutation(prob_mut));

		/* élitisme */
		new_generation.add(individu_maximal());

		//on remplace l'ancienne par la nouvelle
		population = new_generation;
	}
	
	/**
	 * renvoie l'individu de la population ayant l'adaptation maximale
	 */	
	public Indiv individu_maximal(){
		Indiv max = population.getFirst();
		double max_adapt = max.adaptation();
		for(Indiv people : population ){
			double current_adapt = people.adaptation();
			if(current_adapt>max_adapt){
				max = people;
				max_adapt = current_adapt;
			}
		}
		return max;
	}

	/**
	 * renvoie l'adaptation moyenne de la population
	 */
	public double adaptation_moyenne(){
		double moy_adapt = 0;
		for(Indiv people : population ){
			moy_adapt+=people.adaptation();
		}
		return (moy_adapt/population.size());
	}
	
	/**
	 * renvoie l'adaptation maximale de la population
	 */	
	public double adaptation_maximale(){
		double max_adapt = population.getFirst().adaptation();
		for(Indiv people : population ){
			double current_adapt = people.adaptation();
			if(current_adapt>max_adapt){
				max_adapt = current_adapt;
			}
		}
		return max_adapt;
	}

	public double adaptation_totale(){
		double adapt_total = 0;
		for(Indiv people : population ){
				adapt_total += people.adaptation() ;
		}
		return adapt_total;
	}
}
