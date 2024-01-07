package Voyageur_De_Commerce;

import Algo_Genetiques.Individu;
import java.util.*;

public class Individu_VDC implements Individu {

    private int[] parcours;
    private double[] m_coord_x;
    private double[] m_coord_y;

    //Constructeur
    public Individu_VDC(double[] coord_x, double[] coord_y) {
        parcours = new int[coord_x.length];
        for (int i = 0; i < parcours.length; i++) {
            parcours[i] = i;
        }
        shuffleArray(parcours);
        m_coord_x = coord_x;
        m_coord_y = coord_y;
    }

    // Constructeur de copie
    public Individu_VDC(Individu_VDC autre) {
        // Copier le tableau de parcours
        this.parcours = Arrays.copyOf(autre.parcours, autre.parcours.length);

        // Copier les tableaux de coordonnées x et y
        this.m_coord_x = Arrays.copyOf(autre.m_coord_x, autre.m_coord_x.length);
        this.m_coord_y = Arrays.copyOf(autre.m_coord_y, autre.m_coord_y.length);
    }

    /* Classes de l'interface Individu
     */
    @Override
    public double adaptation() {
        // Calculate and return the total distance or fitness of the individual
        // You need to implement the calculation based on the order of cities in 'parcours'
        double totalDistance = 0;
        for (int i = 0; i < parcours.length - 1; i++) {
            int city1 = parcours[i];
            int city2 = parcours[i + 1];
            double distance = calculateDistance(city1, city2);
            totalDistance += distance;
        }
        // Add distance from the last city back to the starting city
        totalDistance += calculateDistance(parcours[parcours.length - 1], parcours[0]);

        return 1 / totalDistance; // Assuming it's a minimization problem, adjust as needed
    }

    @Override
// VOYAGEUR DE COMMERCE PARTIE CROISEMENT
    public Individu[] croisement(Individu conjoint) {
        Individu_VDC conjoint_vdc = (Individu_VDC) conjoint;
        Individu_VDC[] enfants = new Individu_VDC[2];
        enfants[0] = new Individu_VDC(this);
        enfants[1] = new Individu_VDC(conjoint_vdc);
        boolean[] b1 = new boolean[parcours.length];
        boolean[] b2 = new boolean[parcours.length];
        for (int i = 0; i < parcours.length; i++) {
            b1[i] = false;
            b2[i] = false;
        }
        Random r = new Random();
        int ind = r.nextInt(parcours.length);
        // on regarde les villes qu'on rencontre dans la premiere partie
        for (int i = 0; i < ind; i++) {
            b1[this.parcours[i]] = true;
            b2[conjoint_vdc.parcours[i]] = true;
        }
        int ind1 = ind;
        int ind2 = ind;
        for (int i = ind; i < parcours.length; i++) {
            //si la ville n'a pas été visitée dans la premiere partie, on prend
            if (!b1[conjoint_vdc.parcours[i]]) {
                b1[conjoint_vdc.parcours[i]] = true;
                enfants[0].parcours[ind1] = conjoint_vdc.parcours[i];
                ind1++;
            }
            if (!b2[this.parcours[i]]) {
                b2[this.parcours[i]] = true;
                enfants[1].parcours[ind2] = this.parcours[i];
                ind2++;
            }
        }
        // on complète avec les villes non rencontrées
        for (int i = 0; i < parcours.length; i++) {
            if (!b1[i]) {
                enfants[0].parcours[ind1] = i;
                ind1++;
            }
            if (!b2[i]) {
                enfants[1].parcours[ind2] = i;
                ind2++;
            }
        }
        if (Math.random() < 0.8) {
            enfants[0].optim_2opt();
            enfants[1].optim_2opt();
        }
        enfants[0].calcule_adaptation();
        enfants[1].calcule_adaptation();
        return enfants;
    }

    @Override
    public void mutation(double prob) {
        // Implement mutation based on the given probability
        // For example, swap two random cities in the 'parcours' array with a certain probability
        if (Math.random() < prob) {
            Random rand = new Random();
            int index1 = rand.nextInt(parcours.length);
            int index2 = rand.nextInt(parcours.length);
            swapCities(index1, index2);
        }
    }

    private void swapCities(int index1, int index2) {
        int temp = parcours[index1];
        parcours[index1] = parcours[index2];
        parcours[index2] = temp;
    }

    /* Accesseurs (pour Display_VDC)
     */
    public int[] get_parcours() {
        return parcours;
    }

    public double[] get_coord_x() {
        return m_coord_x;
    }

    public double[] get_coord_y() {
        return m_coord_y;
    }

    private double calculateDistance(int city1, int city2) {
        // Implement the calculation of distance between two cities
        // You can use Euclidean distance or any other suitable metric
        double dx = m_coord_x[city1] - m_coord_x[city2];
        double dy = m_coord_y[city1] - m_coord_y[city2];
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static void shuffleArray(int[] array) {
        // Helper method to shuffle the array (Fisher-Yates shuffle)
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }


    private void reverseSegment(int i, int j) {
        while (i < j) {
            int temp = parcours[i];
            parcours[i] = parcours[j];
            parcours[j] = temp;
            i++;
            j--;
        }
    }

    // FONCTION SUPPLEMENTAIRE POUR CROISEMENT
    public void calcule_adaptation() {
        double adapt = 0;
        for (int i = 0; i < parcours.length; i++) {
            int ville_actu = parcours[i];
            int ville_prec = parcours[(i + 1) % parcours.length];
            adapt += calculateDistance(ville_actu, ville_prec);
        }
        if (adapt != 0) {
            // Use floating-point division
            adapt = 1.0 / adapt;
        } else {
            adapt = 0;
        }
    }

    public void optim_2opt() {
        for (int i = 0; i < parcours.length; i++) {
            for (int j = i + 1; j < parcours.length; j++) {
                if (gain(i, j) < 0) {
                    for (int k = 0; k < (j - i + 1) / 2; k++) {
                        int tmp = parcours[i + k];
                        parcours[i + k] = parcours[j - k];
                        parcours[j - k] = tmp;

                    }
                }
            }
        }
    }

    private double gain(int i, int j) {
        // Calculate the gain of reversing the segment from city i to j in the parcours
        int a = parcours[i];
        int b = parcours[(i + 1) % parcours.length];
        int c = parcours[j];
        int d = parcours[(j + 1) % parcours.length];

        double originalDistance = calculateDistance(a, b) + calculateDistance(c, d);
        double reversedDistance = calculateDistance(a, c) + calculateDistance(b, d);

        return reversedDistance - originalDistance;
    }
}
