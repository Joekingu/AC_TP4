package Algo_Genetiques;

import java.util.Arrays;
import java.util.Random;
public class Individu_SAD implements Individu {
    //attributs
    private boolean[] m_inBag;
    private double[] m_poids;
    private double m_capacite;

    //Constructeur
    public Individu_SAD(double[] poids, double capacite) {
        Random random = new Random();
        m_inBag = new boolean[poids.length];
        m_poids = new double[poids.length];
        for (int i = 0; i < poids.length; i++) {
            m_poids[i] = poids[i];
            m_inBag[i] = random.nextBoolean();
        }
        m_capacite = capacite;
    }

    public Individu_SAD(Individu_SAD autre) {
        m_inBag = Arrays.copyOf(autre.m_inBag, autre.m_inBag.length);
        m_poids = Arrays.copyOf(autre.m_poids, autre.m_poids.length);
        m_capacite = autre.m_capacite;
    }

    @Override
    public double adaptation() {
        double adapt =0;
        for(int i =0; i<m_poids.length;i++){
            if(m_inBag[i]){
                adapt+= m_poids[i];
            }
        }
        adapt = (adapt>m_capacite)? 0:adapt;
        return adapt;
    }

    @Override
    public Individu[] croisement(Individu conjoint) {
        int indice = (int)(Math.random()*m_inBag.length); //indice de aléatoire dans le sac du this
        Individu_SAD[] people = new Individu_SAD[2];
        people[0] = new Individu_SAD(this);
        people[1] = new Individu_SAD((Individu_SAD)conjoint);
        for(int i =0; i<indice;i++){
            people[0].m_inBag[i] = ((Individu_SAD) conjoint).m_inBag[i];
            people[1].m_inBag[i] = (this).m_inBag[i];
        }
        return people;
    }

    @Override
    public void mutation(double prob) {
        for(int i =0; i<m_inBag.length;i++){
            if(Math.random()<prob){
                m_inBag[i] = !m_inBag[i];
            }
        }
    }
}
