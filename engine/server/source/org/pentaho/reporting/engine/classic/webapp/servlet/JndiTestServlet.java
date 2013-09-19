package org.pentaho.reporting.engine.classic.webapp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class JndiTestServlet extends HttpServlet
{
  public JndiTestServlet()
  {
  }

  protected void doGet(final HttpServletRequest req,
                       final HttpServletResponse resp) throws ServletException, IOException
  {
    final String jndi = req.getParameter("ds");
    if (jndi == null || StringUtils.isEmpty(jndi))
    {
      resp.setContentType("text/plain");
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      final PrintWriter writer = resp.getWriter();
      writer.print("Error: Parameter 'ds' is missing.");
      writer.flush();
      return;
    }

    final String user = req.getParameter("user");
    final String pass = req.getParameter("password");

    JndiConnectionProvider con = new JndiConnectionProvider(jndi, user, pass);
    try
    {
      final Connection connection = con.createConnection(null, null);
      connection.close();
      resp.setContentType("text/plain");
      resp.setStatus(HttpServletResponse.SC_OK);
      final PrintWriter writer = resp.getWriter();
      writer.print("Success.");
      writer.flush();
    }
    catch (SQLException e)
    {
      resp.setContentType("text/plain");
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      final PrintWriter writer = resp.getWriter();
      writer.print("Error: Failed to query datasource: " + e.getMessage());
      e.printStackTrace(writer);
      writer.flush();
    }
  }
}
