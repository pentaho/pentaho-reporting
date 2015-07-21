/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.webapp.servlet;

import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class JndiTestServlet extends HttpServlet {
  public JndiTestServlet() {
  }

  protected void doGet( final HttpServletRequest req,
                        final HttpServletResponse resp ) throws ServletException, IOException {
    final String jndi = req.getParameter( "ds" );
    if ( jndi == null || StringUtils.isEmpty( jndi ) ) {
      resp.setContentType( "text/plain" );
      resp.setStatus( HttpServletResponse.SC_BAD_REQUEST );
      final PrintWriter writer = resp.getWriter();
      writer.print( "Error: Parameter 'ds' is missing." );
      writer.flush();
      return;
    }

    final String user = req.getParameter( "user" );
    final String pass = req.getParameter( "password" );

    JndiConnectionProvider con = new JndiConnectionProvider( jndi, user, pass );
    try {
      final Connection connection = con.createConnection( null, null );
      connection.close();
      resp.setContentType( "text/plain" );
      resp.setStatus( HttpServletResponse.SC_OK );
      final PrintWriter writer = resp.getWriter();
      writer.print( "Success." );
      writer.flush();
    } catch ( SQLException e ) {
      resp.setContentType( "text/plain" );
      resp.setStatus( HttpServletResponse.SC_BAD_REQUEST );
      final PrintWriter writer = resp.getWriter();
      writer.print( "Error: Failed to query datasource: " + e.getMessage() );
      e.printStackTrace( writer );
      writer.flush();
    }
  }
}
