package me.niko.kingdom.data.sort;

import java.util.Comparator;

import me.niko.kingdom.data.KingdomConstructor;

public class KingdomPointsSort implements Comparator<KingdomConstructor> { 

    public int compare(KingdomConstructor a, KingdomConstructor b) { 
        return b.getPoints() - a.getPoints();
    } 

}
