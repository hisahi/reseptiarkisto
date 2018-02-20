
package tikape.hisahi.reseptit; 

public class RaakaAineTilasto {
    private String nimi;
    private int annokset;
    public RaakaAineTilasto(String nimi, int annokset) {
        this.nimi = nimi;
        this.annokset = annokset;
    }
    public String getAine() {
        return this.nimi;
    }
    public int getReseptiLkm() {
        return this.annokset;
    }
}
