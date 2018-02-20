
package tikape.hisahi.reseptit; 

public class AnnosRaakaAine {
    public static final int AUTO_ID = -1;
    private int id;
    private RaakaAine raakaAine;
    private int lkm;
    private String yksikko;
    private String lisaohje;
    public AnnosRaakaAine(int id, RaakaAine raakaAine, int lkm, String yksikko, String lisaohje) {
        this.raakaAine = raakaAine;
        this.lkm = lkm;
        this.yksikko = yksikko;
        this.lisaohje = lisaohje;
    }
    public int getId() {
        return this.id;
    }
    public RaakaAine getRaakaAine() {
        return this.raakaAine;
    }
    public int getLkm() {
        return this.lkm;
    }
    public String getYksikko() {
        return this.yksikko;
    }
    public String getLisaohje() {
        return this.lisaohje;
    }
    public void assignId(int id) {
        if (this.id == AUTO_ID)
            this.id = id;
    }
}
