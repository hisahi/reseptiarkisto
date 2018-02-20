package tikape.hisahi.reseptit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Main {
    
    static Database db;
    static RaakaAineDao rdao;
    static AnnosDao adao;

    public static void main(String[] args) throws Exception {
        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }
        
        db = new Database(System.getenv("JDBC_DATABASE_URL") != null 
                                ? System.getenv("JDBC_DATABASE_URL") 
                                : "jdbc:sqlite:db/reseptit.db");
        rdao = new RaakaAineDao(db);
        adao = new AnnosDao(db, rdao);
        
        Spark.get("/", (req, res) -> {
            List<Annos> reseptit = new ArrayList<>();
            reseptit.addAll(adao.findAll());
            List<RaakaAine> aineet = new ArrayList<>();
            aineet.addAll(rdao.findAll());
            HashMap map = new HashMap<>();
            map.put("annoslista", reseptit);
            map.put("ainelista", aineet);
            map.put("raakaainecount", aineet.size());
            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("uusiaine", (req, res) -> {
            return new ModelAndView(new HashMap<>(), "uusiaine");
        }, new ThymeleafTemplateEngine());
        
        Spark.post("uusiaine", (req, res) -> {
            String nimi = req.queryParams("nimi");
            rdao.saveOrUpdate(new RaakaAine(RaakaAine.AUTO_ID, nimi));
            HashMap map = new HashMap<>();
            map.put("text", "Raaka-aine '" + nimi + "' on lisätty onnistuneesti.");
            return new ModelAndView(map, "ok");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("uusiannos", (req, res) -> {
            List<RaakaAine> aineet = new ArrayList<>();
            aineet.addAll(rdao.findAll());
            HashMap map = new HashMap<>();
            map.put("ainelista", aineet);
            return new ModelAndView(map, "uusiannos");
        }, new ThymeleafTemplateEngine());
        
        Spark.post("uusiannos", (req, res) -> {
            HashMap map = new HashMap<>();
            String nimi = req.queryParams("nimi");
            if (nimi.length() < 1) throw new IllegalArgumentException();
            String ohje = req.queryParams("ohje");
            int j = 1;
            while (req.queryParamOrDefault("aine" + j + "osa", null) != null
                    && req.queryParamOrDefault("aine" + j + "yksikko", null) != null
                    && req.queryParamOrDefault("aine" + j + "lkm", null) != null
                    && req.queryParamOrDefault("aine" + j + "ohje", null) != null)
                ++j;
            Annos a = new Annos(Annos.AUTO_ID, nimi, ohje);
            for (int i = 1; i < j; ++i) {
                String yksikko = req.queryParams("aine" + i + "yksikko");
                if (yksikko.isEmpty())
                    yksikko = "kpl";
                AnnosRaakaAine ara = new AnnosRaakaAine(
                        AnnosRaakaAine.AUTO_ID, 
                        rdao.findOne(Integer.parseInt(req.queryParams("aine" + i + "osa"))), 
                        Integer.parseInt(req.queryParams("aine" + i + "lkm")), 
                        yksikko, 
                        req.queryParams("aine" + i + "ohje"));
                a.addRaakaAine(ara);
            }
            adao.saveOrUpdate(a);
            map.put("text", "Resepti '" + nimi + "' on lisätty onnistuneesti.");
            return new ModelAndView(map, "ok");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("annos/:id", (req, res) -> {
            Annos a = adao.findOne(Integer.parseInt(req.params(":id")));
            HashMap map = new HashMap<>();
            map.put("nimi", a.getNimi());
            map.put("ainesosat", a.getRaakaAineet());
            map.put("ohje", a.getOhje());
            return new ModelAndView(map, "resepti");
        }, new ThymeleafTemplateEngine());
        
        Spark.get("ainetilastot", (req, res) -> {
            List<RaakaAineTilasto> lista = new ArrayList<>();
            for (RaakaAine ra: rdao.findAll()) {
                lista.add(new RaakaAineTilasto(ra.getNimi(), rdao.getNumberOfDishesIn(ra.getId())));
            }
            Collections.sort(lista, new Comparator<RaakaAineTilasto>(){
                @Override
                public int compare(RaakaAineTilasto o1, RaakaAineTilasto o2) {
                    return o2.getReseptiLkm() - o1.getReseptiLkm();
                }
            });
            HashMap map = new HashMap<>();
            map.put("aineet", lista);
            return new ModelAndView(map, "ainetilastot");
        }, new ThymeleafTemplateEngine());
        
    }

}
