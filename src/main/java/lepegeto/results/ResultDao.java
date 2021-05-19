package lepegeto.results;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.ArrayList;

@RegisterBeanMapper(lepegeto.results.GameResult.class)
public interface ResultDao {
    @SqlUpdate("""
            CREATE TABLE IF NOT EXISTS lepegeto (
            winner VARCHAR2,
            player1 VARCHAR2,
            player2 VARCHAR2,
            steps INTEGER,
            created TIMESTAMP
            )
            """)
    void createTable();

    @SqlUpdate("""
            INSERT INTO lepegeto values(:winner,:player1,:player2,:steps,:created)
            """) void insertIntoTable(@BindBean GameResult gameResult);

    @SqlQuery("""
            SELECT * FROM lepegeto ORDER BY steps ASC
            """)
    ArrayList<GameResult> fetch();
}
