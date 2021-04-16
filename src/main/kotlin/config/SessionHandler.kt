package config

import kotliquery.HikariCP
import org.eclipse.jetty.nosql.mongodb.MongoSessionDataStoreFactory
import org.eclipse.jetty.server.session.*

object SessionHandler {
    fun sqlSessionHandler() = SessionHandler().apply {
        sessionCache = DefaultSessionCache(this).apply {
            sessionDataStore = JDBCSessionDataStoreFactory().apply {
                setDatabaseAdaptor(DatabaseAdaptor().apply {
                    //setDriverInfo("org.firebirdsql.jdbc.FBDriver","jdbc:firebirdsql://localhost:3050/JavalinVue?encoding=UTF8&user=sysdba&password=masterkey")
                    datasource = HikariCP.dataSource()
                })

            }.getSessionDataStore(sessionHandler)
        }
        httpOnly = true
    }

    fun mongoSessionHandler() = SessionHandler().apply {
        sessionCache = DefaultSessionCache(this).apply {
            sessionDataStore = MongoSessionDataStoreFactory().apply {
                connectionString = "mongodb://admin:admin@localhost:27017/"
                dbName = "JavalinSessionDb"
                collectionName = "Session"
            }.getSessionDataStore(sessionHandler)
        }
        httpOnly = true
    }
}