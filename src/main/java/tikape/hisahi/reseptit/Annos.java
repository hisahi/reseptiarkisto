
package tikape.hisahi.reseptit; 

import java.util.ArrayList;
import java.util.List;

public class Annos {
    public static final int AUTO_ID = -1;
    private int id;
    private String nimi;
    private String ohje;
    private List<AnnosRaakaAine> raakaAineet;
    public Annos(int id, String nimi, String ohje) {
        this.id = id;
        this.nimi = nimi;
        this.ohje = ohje;
        this.raakaAineet = new ArrayList<>();
    }
    public int getId() {
        return this.id;
    }
    public String getNimi() {
        return this.nimi;
    }
    public String getOhje() {
        return this.ohje;
    }
    public void addRaakaAine(AnnosRaakaAine ara) {
        this.raakaAineet.add(ara);
    }
    public List<AnnosRaakaAine> getRaakaAineet() {
        return this.raakaAineet;
    }
    public void assignId(int id) {
        if (this.id == AUTO_ID)
            this.id = id;
    }
    
}
