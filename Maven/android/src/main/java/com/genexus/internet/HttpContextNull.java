// $Log: HttpContextNull.java,v $
// Revision 1.14  2005/05/03 16:07:24  dmendez
// Propiedades Filename y Filetype en blobs.
//
// Revision 1.13  2005/02/18 19:33:51  iroqueta
// Implementacion de userId() para EJBs
//
// Revision 1.12  2005/02/17 13:36:22  iroqueta
// En el getRequest pregunto si es nulo para que no de nullPointer si se llama a un webwrapper en una aplicaion win.
//
// Revision 1.11  2004/11/26 18:43:32  iroqueta
// El arreglo anterior causaba que se necesitara el servlet.jar para correr aplicaciones win.... Se arreglo eso.
//
// Revision 1.10  2004/11/16 17:34:23  iroqueta
// Se hace que el httpservletRequest sea pasado al webwrapper (SAC 14709)
//
// Revision 1.9  2004/10/27 20:54:15  dmendez
// Soporte de upload control
//
// Revision 1.8  2004/09/02 21:12:52  iroqueta
// Se hace que el httprequest sea pasado al webwrapper (SAC 16702)
//
// Revision 1.7  2004/08/20 20:02:24  iroqueta
// Soporte para las funciones SetUserId y SetWrkSt
//
// Revision 1.6  2004/08/16 19:30:54  iroqueta
// Se volvieron para atras algunos casos donde no era necesario renombrara la entrada JAVA_USERID por LOGIN_AS_USERID
//
// Revision 1.5  2004/08/13 19:48:06  iroqueta
// Se cambio el nombre de la entrada JAVA_USERID por LOGIN_AS_USERID
//
// Revision 1.4  2004/08/12 14:39:27  dmendez
// Metodo deletePostValue
//
// Revision 1.3  2004/05/25 18:01:02  gusbro
// - Cambios en la getUserId cuando se tiene LDAP
//
// Revision 1.2  2002/08/02 20:11:08  gusbro
// - fix: Arreglo en la userid()
//
// Revision 1.1.1.1  2002/06/10 17:11:58  gusbro
// Entran los fuentes al CVS
//
// Revision 1.2  2002/06/10 17:11:57  aaguiar
// - Se definio un metodo getDefaultPath
//
// Revision 1.1.1.1  2002/04/23 20:55:14  gusbro
// GeneXus Java Olimar
//
package com.genexus.internet;

import java.net.URI;
import java.util.Hashtable;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import com.artech.base.services.AndroidContext;
import com.genexus.Application;
import com.genexus.ModelContext;
import com.genexus.db.DBConnectionManager;

public class HttpContextNull extends HttpContext
{
       private HttpRequest httprequest;


	public HttpContext copy()
	{
		HttpContextNull o = new HttpContextNull();
		copyCommon(o);

		return o;
	}

	public HttpContextNull()
	{
	  httprequest = null;
		//setWriter(new PrintWriter(new com.genexus.util.NullOutputStream()));
	}

	public String getResourceRelative( String path)
	{
		return "";
	}
	
	public String getResource( String path)
	{
		return path;
	}

	public String getContextPath()
	{
		return "";
	}

	public String getDefaultPath()
	{
		return "";
	}



	public String GetNextPar()
	{
		throw new InternalError();
	}

	public byte setHeader(String header, String value)
	{
		return 0;
	}

	public void setDateHeader(String header, int value)
	{
	}

	public void setRequestMethod(String method)
	{
	}
	public Hashtable getPostData()
	{
		throw new InternalError();
	}

	public String getRequestMethod()
	{
		return "";
	}

	public String getReferer()
	{
		return "";
	}

	public short setWrkSt(int handle, String wrkst)
	{
	  DBConnectionManager.getInstance().getUserInformation(handle).setProperty("WKST_NAME", wrkst.toUpperCase());
	  return 1;
	}

	public String getWorkstationId(int handle)
	{
	  String wrkstId = DBConnectionManager.getInstance().getUserInformation(handle).getProperty("WKST_NAME");

	  if	(wrkstId == null || wrkstId.equals(""))
	  {
		  return com.genexus.GXutil.wrkst();
	  }

	  return wrkstId;
	}

       public short setUserId(int handle, String user, String dataSource)
       {
	 DBConnectionManager.getInstance().getUserInformation(handle).setProperty("JAVA_USERID", user.toUpperCase());
	 return 1;
       }

	public String getUserId(String key, ModelContext context, int handle, String dataSource)
	{

		if	(key.toLowerCase().equals("server") &&  !Application.getUserIdServerAsUserId(handle))
		{
				return com.genexus.GXutil.userId(key, context, handle, dataSource);
		}

		String user = DBConnectionManager.getInstance().getUserInformation(handle).getProperty("JAVA_USERID");

		if	(user == null || user.length() == 0)
			return com.genexus.GXutil.userId("", context, handle, dataSource);

		return user;
	}

	public String getRemoteAddr()
	{
		return "";
	}

        public boolean isSmartDevice()
        {
            return false;
        }

	public int getBrowserType()
	{
		return 0;
	}

	public boolean isIE55()
	{
		return false;
	}

	public String getBrowserVersion()
	{
		return "";
	}

	public Object getSessionValue(String name)
	{
		return "";
	}

	// ---- Set values
	public void webPutSessionValue(String name, Object value)
	{
	}

	public void webPutSessionValue(String name, long value)
	{
	}

	public void webPutSessionValue(String name, double value)
	{
	}

	public void webSessionId(String[] id)
	{
	}

	public String webSessionId()
	{
		return "0";
	}

	public String getCookie(String name)
	{
		return "";
	}

	public byte setCookieRaw(String name, String value, String path, java.util.Date expiry, String domain, double secure)
	{
		return 0;
	}

	public byte setCookie(String name, String value, String path, java.util.Date expiry, String domain, double secure)
	{
		return 0;
	}

	public String getServerName()
	{
		URI uriServer = getAndroidServerRootUri();
  	  	return uriServer.getHost();
	}

	public int getServerPort()
	{
		URI uriServer = getAndroidServerRootUri();
  	  	int port = uriServer.getPort(); 
  	  	if (port == -1 )
  	  		port = 80;
  	  	return port; 
	}

	public String getScriptPath()
	{
		URI uriServer = getAndroidServerRootUri();
		return uriServer.getPath();
	}

	public int getHttpSecure()
	{
		URI uriServer = getAndroidServerRootUri();
		if (uriServer.getScheme().equalsIgnoreCase("https"))
			return 1;

		return 0;
	}

	private URI getAndroidServerRootUri() 
	{
		String uri = AndroidContext.ApplicationContext.getRootUri();
		if (!uri.endsWith("/"))
			uri = uri + "/";
		URI uriServer = URI.create( uri);
		return uriServer;
	}
	
	public byte setContentType(String type)
	{
		return 1;
	}

	public byte responseContentType (String type)
	{
		return 0;
	}


	public String getHeader(String header)
	{
		return "";
	}


	public void sendError(int error)
	{
	}

	public void setQueryString(String qs)
	{
	}

	public String getQueryString()
	{
		return "";
	}

	public String getPackage()
	{
		return "";
	}

	public String cgiGet(String parm)
	{
		return "";
	}

    public void changePostValue(String ctrl, String value)
    {
    }
    public void deletePostValue(String ctrl)
    {
    }

    /*
	public HttpServletRequest getRequest()
	{
			return null;
	}

	public HttpServletResponse getResponse()
	{
		throw new InternalError();
	}
	*/
    
	public HttpResponse getHttpResponse()
	{
		return null;
		//throw new InternalError();
	}

	public HttpRequest getHttpRequest()
	{
		//in android return default httprequest if null
		if (httprequest==null)
			return new HttpRequest(this); 
	    return httprequest;
	      //throw new InternalError();
	}

	public void setHttpRequest(HttpRequest httprequest)
	{
	      this.httprequest = httprequest;
	}

	public void redirect(String url) {}
	public void popup(String url) {}
	public void popup(String url, Object[] returnParms) {}
	public void ajax_rsp_command_close(){};
        public void dispatchAjaxCommands() {};
        public void closeHtmlHeader() {};
        public boolean getHtmlHeaderClosed() { return false; }

	public void setStream(){}
	public void flushStream(){}
	public String cgiGetFileName(String parm) {return "";}
	public String cgiGetFileType(String parm) {return "";}
	public void getMultimediaValue(String internalName, String[] blobVar, String[] uriVar) { blobVar[0] = ""; uriVar[0] = ""; }
	public void cleanup() {}
	public boolean isMultipartContent() { return false; }
	
	public java.util.Date webcli2server(java.util.Date dt)
	{
		return dt;
	}

    public java.util.Date server2webcli(java.util.Date dt)
	{
		return dt;
	}
}
