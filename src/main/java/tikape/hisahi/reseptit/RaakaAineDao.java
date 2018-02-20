
package tikape.hisahi.reseptit; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RaakaAineDao implements Dao<RaakaAine, Integer> {

    private Database db;
    
    public RaakaAineDao(Database db) {
        this.db = db;
    }
    
    @Override
    public RaakaAine findOne(Integer key) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM RaakaAine WHERE id = ?")) {
                stmt.setInt(1, key);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new RaakaAine(key, rs.getString("nimi"));
                    } else {
                        return null;
                    }
                }
            }
        }
    }

    @Override
    public List<RaakaAine> findAll() throws SQLException {
        ArrayList<RaakaAine> ls = new ArrayList<>();
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM RaakaAine")) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ls.add(new RaakaAine(rs.getInt("id"), rs.getString("nimi")));
                    }
                    return ls;
                }
            }
        }
    }

    @Override
    public RaakaAine saveOrUpdate(RaakaAine object) throws SQLException {
        if (this.findOne(object.getId()) != null) {
            return this.update(object);
        } else {
            return this.save(object);
        }
    }

    private RaakaAine update(RaakaAine object) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("UPDATE RaakaAine SET nimi = ? WHERE id = ?")) {
                stmt.setString(1, object.getNimi());
                stmt.setInt(2, object.getId());
                stmt.executeUpdate();
                return object;
            }
        }
    }

    private RaakaAine save(RaakaAine object) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO RaakaAine (nimi) VALUES (?)")) {
                stmt.setString(1, object.getNimi());
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int id = rs.getInt(1);
                            object.assignId(id);
                        }
                    }
                }
                return object;
            }
        }
    }

    @Override
    public void delete(Integer key) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM RaakaAine WHERE id = ?")) {
                stmt.setInt(1, key);
                stmt.executeUpdate();
            }
        }
    }
    
    public int getNumberOfDishesIn(Integer raakaAineId) throws SQLException {
        try (Connection conn = db.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT Annos.id FROM Annos JOIN AnnosRaakaAine ON Annos.id = AnnosRaakaAine.annos JOIN RaakaAine ON AnnosRaakaAine.raakaaine = RaakaAine.id WHERE RaakaAine.id = ?")) {
                stmt.setInt(1, raakaAineId);
                try (ResultSet rs = stmt.executeQuery()) {
                    int count = 0;
                    while (rs.next())
                        ++count;
                    return count;
                }
            }
        }
    }

}
