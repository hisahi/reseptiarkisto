
package tikape.hisahi.reseptit; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnnosDao implements Dao<Annos, Integer> {

    private Database db;
    private RaakaAineDao rdao;
    
    public AnnosDao(Database db, RaakaAineDao rdao) {
        this.db = db;
        this.rdao = rdao;
    }
    
    @Override
    public Annos findOne(Integer key) throws SQLException {
        Annos a;
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Annos WHERE id = ?")) {
                stmt.setInt(1, key);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        a = new Annos(key, rs.getString("nimi"), rs.getString("ohje"));
                    } else {
                        return null;
                    }
                }
            }
        }
        this.addIngredientsToObject(a);
        return a;
    }

    @Override
    public List<Annos> findAll() throws SQLException {
        Annos a;
        ArrayList<Annos> ls = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Annos")) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        a = new Annos(rs.getInt("id"), rs.getString("nimi"), rs.getString("ohje"));
                        ls.add(a);
                    }
                }
            }
        }
        for (Annos ax: ls)
            this.addIngredientsToObject(ax);
        return ls;
    }

    @Override
    public Annos saveOrUpdate(Annos object) throws SQLException {
        if (this.findOne(object.getId()) != null) {
            return this.update(object);
        } else {
            return this.save(object);
        }
    }

    private Annos update(Annos object) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE Annos SET nimi = ?, ohje = ? WHERE id = ?")) {
                stmt.setString(1, object.getNimi());
                stmt.setString(2, object.getOhje());
                stmt.setInt(3, object.getId());
                stmt.executeUpdate();
            }
        }
        this.removeIngredients(object.getId());
        this.insertIngredients(object);
        return object;
    }

    private Annos save(Annos object) throws SQLException {
        int id = -1;
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO Annos (nimi, ohje) VALUES (?, ?)")) {
                stmt.setString(1, object.getNimi());
                stmt.setString(2, object.getOhje());
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            id = rs.getInt(1);
                            object.assignId(id);
                        }
                    }
                }
            }
        }
        if (object.getId() != Annos.AUTO_ID)
            this.insertIngredients(object);
        return object;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Annos WHERE id = ?")) {
                stmt.setInt(1, key);
                stmt.executeUpdate();
            }
        }
        this.removeIngredients(key);
    }

    private void addIngredientsToObject(Annos a) throws SQLException {
        int aid = a.getId();
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM AnnosRaakaAine WHERE annos = ? ORDER BY jarjestys ASC")) {
                stmt.setInt(1, aid);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        a.addRaakaAine(new AnnosRaakaAine(rs.getInt("id"), rdao.findOne(rs.getInt("raakaaine")), rs.getInt("lkm"), rs.getString("yksikko"), rs.getString("ohje")));
                    }
                }
            }
        }
    }

    private void insertIngredients(Annos object) throws SQLException {
        int j = 0;
        try (Connection conn = db.getConnection()) {
            for (AnnosRaakaAine ara: object.getRaakaAineet()) {
                try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO AnnosRaakaAine (annos, raakaaine, lkm, yksikko, jarjestys, ohje) VALUES (?, ?, ?, ?, ?, ?)")) {
                    stmt.setInt(1, object.getId());
                    stmt.setInt(2, ara.getRaakaAine().getId());
                    stmt.setInt(3, ara.getLkm());
                    stmt.setString(4, ara.getYksikko());
                    stmt.setInt(5, ++j);
                    stmt.setString(6, ara.getLisaohje());
                    stmt.executeUpdate();
                }
            }
        }
    }

    private void removeIngredients(Integer key) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM AnnosRaakaAine WHERE annos = ?")) {
                stmt.setInt(1, key);
                stmt.executeUpdate();
            }
        }
    }

}
