package programs.publicmodule.core.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import programs.publicmodule.core.db.tables.TableTask;

/**
 * Created by caijiang.chen on 2017/10/13.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private final static String PublicDbName = "ProgramsDb.data";//数据库名
    private final static int PublicDbVersion = 1;//数据库版本

    private static DatabaseHelper databaseHelper = null;
    private Map<String, Dao> daos = new HashMap<String, Dao>();

    public DatabaseHelper(Context context) {
        super(context, PublicDbName, null, PublicDbVersion);
    }

    public static synchronized DatabaseHelper getDatabaseHelper(Context context) {
        context = context.getApplicationContext();
        if (null == databaseHelper) {
            synchronized (DatabaseHelper.class) {
                databaseHelper = new DatabaseHelper(context);
            }
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        // 创建表
        try {
            TableUtils.createTable(connectionSource, TableTask.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
//        try
//        {
//            getDao(TableTask.class).executeRaw("ALTER TABLE 'TASK' ADD COLUMN BUSI_ID VARCHAR");
//            TableUtils.dropTable(connectionSource, TableTask.class, true);
//            onCreate(database, connectionSource);
//        } catch (SQLException e)
//        {
//            e.printStackTrace();
//        }
    }

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();
        if (daos.containsKey(className)) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }
}
