// $Log: GXDBMSpostgresql.java,v $
// Revision 1.10  2007/05/24 14:21:04  alevin
// - (CMurialdo) Mas de la implementaci�n de indices unique.
//
// Revision 1.8  2006/12/18 16:34:17  alevin
// - (CMuriando) Implementacion de las propiedades "Lock time-out" y "Lock retry count".
//
// Revision 1.7  2004/09/17 21:45:34  dmendez
// Messagelist como estructura
// Soporte de updates optimizados (APC)
//
// Revision 1.6  2004/07/12 20:14:51  gusbro
// - Cambio la serverDateTime porque no estaba devolviendo correctamente la hora del server
//
// Revision 1.5  2004/05/27 20:26:52  gusbro
// - Agrego getId que devuelve un entero identificando el DBMS
//
// Revision 1.4  2004/03/29 17:08:02  gusbro
// - Faltaba ignorar el error cuando se intenta eliminar una sequence que no existe
//
// Revision 1.3  2004/03/23 20:51:34  gusbro
// - En la version 7.4 del driver cuando no puede lockear una tabla porque no existe esta
//   poniendo 'relation "xxx" does not exist' y antes ponia 'Relation "xxx" does not exist'
//
// Revision 1.2  2002/12/06 19:13:32  aaguiar
// - Se agrego el isAlive. Por ahora hace el getservertime()
//
// Revision 1.1.1.1  2002/05/15 21:15:32  gusbro
// Entran los fuentes al CVS
//
// Revision 1.1.1.1  2002/05/15 21:15:32  gusbro
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

public class GXDBMSpostgresql implements GXDBMS
{
	public void setDatabaseName(String dbName)
	{
	}
	public String getDatabaseName()
	{
		return "";
	}	
	public ResultSet executeQuery(PreparedStatement stmt, boolean hold) throws SQLException
	{
		return stmt.executeQuery();
	}

	public void setInReorg()
	{
	}
	public boolean isAlive(GXConnection con)
	{
		try
		{
			serverDateTime(con);
		}
		catch (SQLException e)
		{
			return false;
		}

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

	public boolean EndOfFile(SQLException e)
	{
		return	(	e.getErrorCode() == 100
				);
	}

	public boolean ReferentialIntegrity(SQLException e)
	{
		return false;
	}

	public boolean DuplicateKeyValue(SQLException e)
	{
	    String sqlstate = e.getSQLState().toLowerCase();
        //23505 duplicate key violates unique constraint xxx
        if (sqlstate.indexOf("23505")>=0){
            return true;
        }else
	    {
          String msg = e.getMessage().toLowerCase();
          return (msg.indexOf("duplicate key") >= 0 && msg.indexOf("unique") >= 0);
	    }
	}

	public boolean ObjectLocked(SQLException e)
	{
		String sqlstate = e.getSQLState().toLowerCase();
		if (sqlstate.indexOf("55p03")>=0)
			return true;
		return false;
	}

	public boolean ObjectNotFound(SQLException e)
	{
		String sqlstate = e.getSQLState().toLowerCase();
        String msg = e.getMessage().toLowerCase();
        //42704 index xxx does not exist
        //42P01  relation tableName does not exist
        //42p06 schema already exists

		if (sqlstate.indexOf("42p01")>=0){
			//Los codigos -dbmsErrorCode=5632, emsg 'error: 42p01: no existe la tabla xxx 
			// y dbmsErrorCode=5632, emsg 'error: 42p01: no existe la vista xxx
			//No se capturan, la reorg captura con catch.
			if (msg.indexOf(" vista ") >= 0 || msg.indexOf(" view ") >= 0 || msg.indexOf(" tabla ") >= 0 || msg.indexOf(" table ") >= 0){
				return false;
			}
		}
		if (sqlstate.indexOf("42704")>=0 || sqlstate.indexOf("42p01")>=0 || sqlstate.indexOf("42p06")>=0){
            return true;
        }else{
		    return (
				(msg.indexOf("table") >= 0 || msg.indexOf("relation") >= 0 || msg.indexOf("sequence") >= 0 ) && msg.indexOf("does not exist") >= 0) ||
			 	(msg.indexOf("index") >= 0 && (msg.indexOf("nonexistent") >= 0 || msg.indexOf("does not exist") >= 0))  ;
		}
	}

	public java.util.Date nullDate()
	{
		return CommonUtil.ymdhmsToT_noYL(1, 1, 1, 0, 0, 0);
	}

	public boolean useDateTimeInDate()
	{
		return false;
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
			try
			{
				ResultSet rslt = con.getStatement("_ServerDT_", "SELECT clock_timestamp()", false).executeQuery();
				rslt.next();
				// This line is to check if the DBMS support this sentence in the way we are expecting
				rslt.getTimestamp(1);
				rslt.close();				
			}
			catch(SQLException e)
			{
				con.setDontUseNewDateTimeFunction();
				con.rollback();
				con.dropAllCursors();
			}	
	}

	public java.util.Date serverDateTime(GXConnection con) throws SQLException
	{
		// Antes haciamos SELECT now(), pero esto retorna la hora del server pero en el TimeZone del cliente
		// con lo cual si eran distintos timezones quedaba mal la hora
		ResultSet rslt = null;
		
		if (con.getUseOldDateTimeFuntion())
		{
			rslt = con.getStatement("_ServerDT_", "SELECT clock_timestamp()", false).executeQuery();				
		}
		else
		{
			rslt = con.getStatement("_ServerDT_", "SELECT LOCALTIMESTAMP", false).executeQuery();			
		}

		rslt.next();
		Date value = rslt.getTimestamp(1);
		rslt.close();		
		
		return value;
	}
	
	public String serverVersion(GXConnection con) throws SQLException
	{
		ResultSet rslt = con.getStatement("_ServerVERSION_", "SHOW server_version", false).executeQuery();
		
		rslt.next();
		String value = rslt.getString(1);
		rslt.close();

		return value;		
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
		//Las reorgs corren en modo autocommit con lo cual no se debe hacer commit ni rollback implicitos.
		if	(!com.genexus.ApplicationContext.getInstance().getReorganization())
		{
			con.commit();
		}
	}

	public void rollback(Connection con) throws SQLException
	{
		//Las reorgs corren en modo autocommit con lo cual no se debe hacer commit ni rollback implicitos.
		if	(!com.genexus.ApplicationContext.getInstance().getReorganization())
		{		
			con.rollback();
		}
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
		return DBMS_POSTGRESQL;
	}
        public int getLockRetryCount(int lockRetryCount, int waitRecord){
          return lockRetryCount;
        }
		
	public boolean connectionClosed(SQLException e)
	{
		return (e.getSQLState() == "08006");
	}		
}
