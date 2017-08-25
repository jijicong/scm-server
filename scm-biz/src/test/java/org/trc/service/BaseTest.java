package org.trc.service;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.*;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by hzqph on 2017/6/20.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:config/resource-context.xml"})
@Rollback(value = true)
@Transactional(transactionManager = "transactionManager")
public abstract class BaseTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private DataSource dataSource;

    protected static IDatabaseConnection conn;

    private File tempFile;

    public static final String ROOT_URL = System.getProperty("user.dir") + "/src/test/resources/";

    @Before
    public void setup() throws Exception {
        System.out.println("this is @Before");
        //get DataBaseSourceConnection
        conn = new DatabaseConnection(DataSourceUtils.getConnection(dataSource));

        //config database as MySql
        DatabaseConfig dbConfig = conn.getConfig();
        dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,  new MySqlDataTypeFactory());

    }

    @After
    public void teardown() throws Exception {
        if (conn != null) {
            conn.close();
        }

    }

    /**
     * 准备数据:从xm中读取
     * @param fileName
     * @throws Exception
     */
    protected void prepareData(IDatabaseConnection conn, String fileName) throws Exception {
        //读取xml文件中的数据信息
        ReplacementDataSet createDataSet = createDataSet(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
        //INSERT TEST DATA
        DatabaseOperation.INSERT.execute(conn, createDataSet);
    }

    /**
     * 执行指定sql
     * @param sql
     * @throws Exception
     */
    protected void execSql(IDatabaseConnection conn,String sql) throws Exception {
        Connection con = conn.getConnection();
        Statement stmt = con.createStatement();
        try {
            stmt.execute(sql);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * This is used to assert the data from table and the expected data set. If all of the them has the same records, then the assert is true.
     * 对比结果
     * @param tableName
     * @param sql
     * @param expectedDataSet
     * @throws Exception
     */
    protected void assertDataSet(String tableName, String sql, IDataSet expectedDataSet, IDatabaseConnection conn) throws Exception {
        printDataAsXml(conn, tableName, sql);
        //建立连接查询实例
        QueryDataSet actDataSet = new QueryDataSet(conn);
        //查询实际结果
        actDataSet.addTable(tableName, sql);
        //获取查询结果数据
        ITable actData = actDataSet.getTable(tableName);
        //获取期望结果数据
        ITable expData= expectedDataSet.getTable(tableName);
        //排除那些你不想要比较的对象
        actData= DefaultColumnFilter.excludedColumnsTable(actData, new String[]{"id","create_operator","create_time","update_time"});
        expData = DefaultColumnFilter.excludedColumnsTable(expData, new String[]{"id","create_operator","create_time","update_time"});
        //比较查询结果集字段长度
        junit.framework.Assert.assertEquals(expData.getRowCount(), actData.getRowCount());
        DefaultColumnFilter.includedColumnsTable(actData, expData.getTableMetaData().getColumns());
        Assertion.assertEquals(expData, actData);
    }

    /**
     * This is used to assert the data from table and the expected data set. If all of the them has the same records, then the assert is true.
     * 对比结果
     * @param tableName
     * @param sql
     * @param expectedDataSet
     * @throws Exception
     */
    protected void assertDataSet(String tableName, String sql, IDataSet expectedDataSet, IDatabaseConnection conn,String[] str) throws Exception {
        printDataAsXml(conn, tableName, sql);
        //建立连接查询实例
        QueryDataSet actDataSet = new QueryDataSet(conn);
        //查询实际结果
        actDataSet.addTable(tableName, sql);
        //获取查询结果数据
        ITable actData = actDataSet.getTable(tableName);
        //获取期望结果数据
        ITable expData= expectedDataSet.getTable(tableName);
        //排除那些你不想要比较的对象
        actData= DefaultColumnFilter.excludedColumnsTable(actData, str);
        expData = DefaultColumnFilter.excludedColumnsTable(expData, str);
        //比较查询结果集字段长度
        junit.framework.Assert.assertEquals(expData.getRowCount(), actData.getRowCount());
        DefaultColumnFilter.includedColumnsTable(actData, expData.getTableMetaData().getColumns());
        Assertion.assertEquals(expData, actData);
    }


    /**
     * Export data for the table names by the given IDatabaseConnection into the resultFile.<br>
     * The export data will be DBUnit format.
     *
     * @param tableNameList
     * @param resultFile
     * @throws SQLException
     * @throws DatabaseUnitException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void exportData(List<String> tableNameList, String resultFile) throws SQLException, DatabaseUnitException,IOException {
        QueryDataSet dataSet = null;
        if (conn == null) {
            return;
        }
        if (tableNameList == null || tableNameList.size() == 0) {
            return;
        }
        try {
            dataSet = new QueryDataSet(conn);
            for (String tableName : tableNameList) {
                dataSet.addTable(tableName);
            }
        } finally {
            if (dataSet != null) {
                FlatXmlDataSet.write(dataSet, new FileOutputStream(resultFile));
            }
        }

    }
    /**
     * Create the data set by input stream which read from the dbunit xml data file.
     *
     * @param is
     * @return
     * @throws Exception
     */
    protected ReplacementDataSet createDataSet(InputStream is) throws Exception {
        return new ReplacementDataSet(new FlatXmlDataSetBuilder().build(is));
    }

    /**
     *
     * @Title: getXmlDataSet
     * @param name
     * @return
     * @throws DataSetException
     * @throws IOException
     */
    protected IDataSet getXmlDataSet(String name) throws DataSetException, IOException {
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        return builder.build(new FileInputStream(new File(ROOT_URL + name)));
    }

    /**
     * Get data by the SQL and table name, then convert the data in the ITable to List. And the print the data as xml data format.
     *
     * @param iconn
     * @param tableName
     * @param sql
     * @throws Exception
     */
    protected void printDataAsXml(IDatabaseConnection iconn, String tableName, String sql) throws Exception {
        List<Map<?, ?>> datas = getTableDataFromSql(iconn, tableName, sql);
        StringBuffer sb;
        for (Map<?, ?> data : datas) {
            sb = new StringBuffer();
            sb.append("<" + tableName.toUpperCase() + " ");
            for (Object o : data.keySet()) {
                sb.append(o + "=\"" + data.get(o) + "\" ");
            }
            sb.append("/>");
            System.out.println(sb.toString());
        }
    }

    /**
     * Get data by the SQL and table name, then convert the data in the ITable to List
     *
     * @param iconn
     * @param tableName
     * @param sql
     * @return
     * @throws Exception
     */
    protected List<Map<?, ?>> getTableDataFromSql(IDatabaseConnection iconn, String tableName, String sql) throws Exception {
        ITable table = iconn.createQueryTable(tableName, sql);
        return getDataFromTable(table);
    }

    /**
     * Convert the data in the ITable to List
     *
     * @param table
     * @return
     * @throws Exception
     */
    private List<Map<?, ?>> getDataFromTable(ITable table) throws Exception {
        List<Map<?, ?>> ret = new ArrayList<Map<?, ?>>();
        int count_table = table.getRowCount();
        if (count_table > 0) {
            Column[] columns = table.getTableMetaData().getColumns();
            for (int i = 0; i < count_table; i++) {
                Map<String, Object> map = new TreeMap<String, Object>();
                for (Column column : columns) {
                    map.put(column.getColumnName().toUpperCase(), table.getValue(i, column.getColumnName()));
                }
                ret.add(map);
            }
        }
        return ret;
    }

    /**
     * Get DB DataSet
     *
     * @Title: getDBDataSet
     * @return
     * @throws SQLException
     */
    protected IDataSet getDBDataSet() throws SQLException {
        return conn.createDataSet();
    }

    /**
     * Get Query DataSet
     *
     * @Title: getQueryDataSet
     * @return
     * @throws SQLException
     */
    protected QueryDataSet getQueryDataSet() throws SQLException {
        return new QueryDataSet(conn);
    }

    /**
     * Get Excel DataSet
     *
     * @Title: getXlsDataSet
     * @param name
     * @return
     * @throws SQLException
     * @throws DataSetException
     * @throws IOException
     */
    protected XlsDataSet getXlsDataSet(String name) throws SQLException, DataSetException,
            IOException {
        InputStream is = new FileInputStream(new File(ROOT_URL + name));

        return new XlsDataSet(is);
    }

    /**
     * backup the whole DB
     *
     * @Title: backupAll
     * @throws Exception
     */
    protected void backupAll() throws Exception {
        // create DataSet from database.
        IDataSet ds = conn.createDataSet();

        // create temp file
        tempFile = File.createTempFile("temp", "xml");

        // write the content of database to temp file
        FlatXmlDataSet.write(ds, new FileWriter(tempFile), "UTF-8");
    }

    /**
     * back specified DB table
     *
     * @Title: backupCustom
     * @param tableName
     * @throws Exception
     */
    protected void backupCustom(String... tableName) throws Exception {
        // back up specific files
        QueryDataSet qds = new QueryDataSet(conn);
        for (String str : tableName) {

            qds.addTable(str);
        }
        tempFile = File.createTempFile("temp", "xml");
        FlatXmlDataSet.write(qds, new FileWriter(tempFile), "UTF-8");

    }

    /**
     * rollback database
     *
     * @Title: rollback
     * @throws Exception
     */
    protected void rollback() throws Exception {

        // get the temp file
        FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
        builder.setColumnSensing(true);
        IDataSet ds =builder.build(new FileInputStream(tempFile));

        // recover database
        DatabaseOperation.CLEAN_INSERT.execute(conn, ds);
    }


    /**
     * Clear data of table
     *
     * @param tableName
     * @throws Exception
     */
    protected void clearTable(String tableName) throws Exception {
        DefaultDataSet dataset = new DefaultDataSet();
        dataset.addTable(new DefaultTable(tableName));
        DatabaseOperation.DELETE_ALL.execute(conn, dataset);
    }

    /**
     * verify Table is Empty
     *
     * @param tableName
     * @throws DataSetException
     * @throws SQLException
     */
    protected void verifyTableEmpty(String tableName) throws DataSetException, SQLException {
        Assert.assertEquals(0, conn.createDataSet().getTable(tableName).getRowCount());
    }

    /**
     * verify Table is not Empty
     *
     * @Title: verifyTableNotEmpty
     * @param tableName
     * @throws DataSetException
     * @throws SQLException
     */
    protected void verifyTableNotEmpty(String tableName) throws DataSetException, SQLException {
        Assert.assertNotEquals(0, conn.createDataSet().getTable(tableName).getRowCount());
    }

    /**
     *
     * @Title: createReplacementDataSet
     * @param dataSet
     * @return
     */
    protected ReplacementDataSet createReplacementDataSet(IDataSet dataSet) {
        ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet);

        // Configure the replacement dataset to replace '[NULL]' strings with null.
        replacementDataSet.addReplacementObject("[null]", null);

        return replacementDataSet;
    }

    /**
     * This is used to assert the data from table and the expected data set. If all of the them has the same records, then the assert is true.
     * 对比结果
     * @param tableName
     * @param sql
     * @param expectedDataSet
     * @throws Exception
     */
    protected void assertItable(String tableName, String sql, ITable expectedDataSet, IDatabaseConnection conn) throws Exception {
        printDataAsXml(conn, tableName, sql);
        //建立连接查询实例
        QueryDataSet actDataSet = new QueryDataSet(conn);
        //查询实际结果
        actDataSet.addTable(tableName, sql);
        //获取查询结果数据
        ITable actData = actDataSet.getTable(tableName);
        //获取期望结果数据
        ITable expData= expectedDataSet;
        //排除那些你不想要比较的对象
        actData= DefaultColumnFilter.excludedColumnsTable(actData, new String[]{"id","create_operator","create_time","update_time"});
        expData = DefaultColumnFilter.excludedColumnsTable(expData, new String[]{"id","create_operator","create_time","update_time"});
        //比较查询结果集字段长度
        junit.framework.Assert.assertEquals(expData.getRowCount(), actData.getRowCount());
        DefaultColumnFilter.includedColumnsTable(actData, expData.getTableMetaData().getColumns());
        Assertion.assertEquals(actData, expData);
    }

}
