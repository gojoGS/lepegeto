package lepegeto.results;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class ResultManager {
    static Jdbi jdbi;

    public ResultManager() {
        jdbi = Jdbi.create("jdbc:h2:file:"+ System.getProperty("user.home") + File.separator + "lepegeto");
        jdbi.installPlugin(new SqlObjectPlugin());
    }

    public static Jdbi getJdbi() {
        return jdbi;
    }

    public void insert(GameResult gameResult) {
        try (Handle handle = jdbi.open()) {
            ResultDao resultDao = handle.attach(ResultDao.class);
            resultDao.createTable();
            resultDao.insertIntoTable(gameResult);
        }
    }

    public ArrayList<GameResult> fetch() {
        try (Handle handle = jdbi.open()) {
            ResultDao resultDao = handle.attach(ResultDao.class);
            resultDao.createTable();
            return resultDao.fetch();
        }
    }

    public static void main(String[] args) {
        GameResult gameResult = new GameResult("a", "b", "c", 23, ZonedDateTime.now());
        var asd = new ResultManager();

        asd.insert(gameResult);
        asd.fetch();
    }

}
