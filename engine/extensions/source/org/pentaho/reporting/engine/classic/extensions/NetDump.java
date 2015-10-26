/*
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
 * Copyright (c) 2000 - 2009 Pentaho Corporation, Simba Management Limited and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;

/**
 * The NetDump utility can be used to trace simple HTTP-Calls.
 *
 * @author Thomas Morgner
 * @noinspection UseOfSystemOutOrSystemErr
 */
public final class NetDump {
  /**
   * Default Constructor.
   */
  private NetDump() {
  }

  /**
   * THe connection info is used to extract all necessary information from the given URL.
   */
  private static class ConnectionInfo {
    /**
     * the name of the host, to which to connect.
     */
    private String host;
    /**
     * the port on the target host.
     */
    private int port;
    /**
     * the URI which should be queried.
     */
    private String uri;

    /**
     * Creates a new ConnectionInfo object for the given URL.
     *
     * @param url
     *          the URL to which to connect to.
     */
    public ConnectionInfo( final URL url ) {
      host = url.getHost();
      port = url.getPort();
      if ( port == -1 ) {
        port = 80;
      }

      final String file = url.getFile();
      // String query = url.getQuery();
      final String ref = url.getRef();

      uri = file;
      /*
       * if (query != null) { uri += "?"; uri += query; }
       */
      if ( ref != null ) {
        uri += "#";
        uri += ref;
      }
    }

    /**
     * Gets the host to which to connect.
     *
     * @return the target host.
     */
    public String getHost() {
      return host;
    }

    /**
     * Gets the server port on the host, to which to connect.
     *
     * @return the port on the server.
     */
    public int getPort() {
      return port;
    }

    /**
     * Gets the URI, that should be queried.
     *
     * @return the target URI.
     */
    public String getUri() {
      return uri;
    }
  }

  /**
   * Connects to the given URL using the specified HTTP method, something like GET or POST.
   *
   * @param args
   *          the connection arguments, the method followed by an url.
   */
  public static void main( final String[] args ) {
    if ( args.length != 2 ) {
      System.err.println( "Need an Method + URL as parameter" );
      System.exit( 1 );
    }

    try {
      final String method = args[0];
      final URL url = new URL( args[1] );
      if ( "http".equals( url.getProtocol() ) == false ) {
        System.err.println( "The given url must be a HTTP url" );
        System.exit( 1 );
      }

      final ConnectionInfo ci = new ConnectionInfo( url );
      System.out.println( "Connecting to: " + ci.getHost() + ":" + ci.getPort() );
      final Socket socket = new Socket( ci.getHost(), ci.getPort() );
      final OutputStream out = socket.getOutputStream();
      final StringBuffer b = new StringBuffer();
      b.append( method.toUpperCase() );
      b.append( " " );
      b.append( ci.getUri() );
      b.append( " HTTP/1.0\n" );
      b.append( "\n" );
      System.out.println( b.toString() );
      out.write( b.toString().getBytes() );

      final InputStream in = socket.getInputStream();
      final BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
      String line = reader.readLine();
      while ( line != null ) {
        System.out.println( line );
        line = reader.readLine();
      }
      in.close();
    } catch ( Exception e ) {
      System.err.println( "Failed to perform request: " );
      e.printStackTrace();
      System.exit( 1 );
    }
    System.exit( 0 );
  }
}
