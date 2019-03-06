// $Log: GXDBMSaccess.java,v $
// Revision 1.5  2006/12/18 16:34:17  alevin
// - (CMuriando) Implementacion de las propiedades "Lock time-out" y "Lock retry count".
//
// Revision 1.4  2004/09/17 21:45:34  dmendez
// Messagelist como estructura
// Soporte de updates optimizados (APC)
//
// Revision 1.3  2004/05/27 20:26:52  gusbro
// - Agrego getId que devuelve un entero identificando el DBMS
//
// Revision 1.2  2002/12/06 19:13:32  aaguiar
// - Se agrego el isAlive. Por ahora hace el getservertime()
//
// Revision 1.1.1.1  2002/05/15 21:14:40  gusbro
// Entran los fuentes al CVS
//
// Revision 1.1.1.1  2002/05/15 21:14:40  gusbro
// GeneXus Java Olimar
//
//
//   Rev 1.6   23 Sep 1998 19:48:14   AAGUIAR
//
//   Rev 1.5   May 28 1998 10:00:08   DMENDEZ
//Sincro28May1998

package com.genexus.db.driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.genexus.CommonUtil;

public class GXDBMSaccess implements GXDBMS
{	
	public ResultSet executeQuery(PreparedStatement stmt, boolean hold) throws SQLException
	{
		return stmt.executeQuery();
	}

	public void setDatabaseName(String dbName)
	{
	}
	public String getDatabaseName()
	{
		return "";
	}
	public void setInReorg()
	{
	}

	public boolean isAlive(GXConnection con)
	{
		return true;
	}

	public boolean DataTruncation(SQLException e)
	{
		return false;
	}

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}

	public boolean useReadOnlyConnections()
	{
		return true;
	}

	public boolean ReferentialIntegrity(SQLException e)
	{
		return false;
	}

	public boolean EndOfFile(SQLException e)
	{
		return	(	e.getErrorCode() == 100
				);
	}

	public boolean DuplicateKeyValue(SQLException e)
	{
		return	(	e.getErrorCode() == 2627 ||		/*	Violation of %s constraint '%.*s': Attempt to insert duplicate key in object '%.*s'.*/
 					e.getErrorCode() == 2601			/* Attempt to insert duplicate key row in object '%.*s' with unique index '%.*s'*/
				);
	}

	public boolean ObjectLocked(SQLException e)
	{
		return	(	e.getErrorCode() == -903
				);
	}

	public boolean ObjectNotFound(SQLException e)
	{
	
		return	(	e.getErrorCode() == -1305 ||	/* Table not found */
					e.getErrorCode() == -1404 ||	/* Index not found */
					e.getErrorCode() == 3701 ||	/* Index not found */
					e.getErrorCode() == 4902 ||	/* Table not found in Alter statement */
					e.getErrorCode() == 3728 ||	/*  .. is not a constraint.*/
					e.getErrorCode() == 3727 		/* Constraint not found */

				);
	}

	public java.util.Date nullDate()
	{
		return CommonUtil.ymdhmsToT_noYL(1753, 1, 1, 0, 0, 0);
	}

	public boolean useDateTimeInDate()
	{
		return true;
	}

	public boolean useCharInDate()
	{
		return false;
	}

	public void setConnectionProperties(java.util.Properties props)
	{
	}

	public void onConnection(GXConnection con) throws SQLException
	{
	}
	
	public java.util.Date serverDateTime(GXConnection con) throws SQLException
	{
		ResultSet rslt = con.getStatement("_ServerDT_", "SELECT GETDATE()", false).executeQuery();
		
		rslt.next();
		Date value = rslt.getTimestamp(1);
		rslt.close();

		return value;
	}
	
	public String serverVersion(GXConnection con) throws SQLException
	{
		return "";		
	}	
	
	public String connectionPhysicalId(GXConnection con)
	{
		return "";
	}	

	public boolean getSupportsAutocommit()
	{
		return true;
	}

	public void commit(Connection con) throws SQLException
	{
		con.commit();
	}

	public void rollback(Connection con) throws SQLException
	{
		con.rollback();
	}
	public boolean ignoreConnectionError(SQLException e)
	{
		return false;
	}
        
        public boolean rePrepareStatement(SQLException e)
        {
		return false;
	}

	public boolean getSupportsQueryTimeout()
	{
		return false;
	}

	public boolean useStreamsInNullLongVarchar()
	{
		return false;
	}

	public boolean useStreamsInLongVarchar()
	{
		return false;
	}

	public int getId()
	{
		return DBMS_ACCESS;
	}
        public int getLockRetryCount(int lockRetryCount, int waitRecord){
          return lockRetryCount;
        }
		
	public boolean connectionClosed(SQLException e)
	{
		return false;
	}		
}
