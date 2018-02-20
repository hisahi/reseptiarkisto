
package tikape.hisahi.reseptit; 

public class RaakaAine {
    public static final int AUTO_ID = -1;
    private int id;
    private String nimi;
    public RaakaAine(int id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }
    public int getId() {
        return this.id;
    }
    public String getNimi() {
        return this.nimi;
    }
    public void assignId(int id) {
        if (this.id == AUTO_ID)
            this.id = id;
    }
}
