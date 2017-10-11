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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The reportgenerator initializes the parser and provides an interface the the default parser.
 * <p/>
 * To create a report from an URL, use <code> ReportGenerator.getInstance().parseReport (URL myURl, URL contentBase);
 * </code>
 *
 * @author Thomas Morgner
 * @deprecated Use LibLoader directly.
 */
public class ReportGenerator {
  /**
   * Enable DTD validation of the parsed XML.
   */
  public static final String PARSER_VALIDATE_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.parser.base.Validate";

  /**
   * disable DTD validation by default.
   */
  public static final boolean PARSER_VALIDATE_DEFAULT = true;

  /**
   * The report generator.
   */
  private static ReportGenerator generator;

  private HashMap helperObjects;
  private boolean validateDTD;

  /**
   * Creates a new report generator. The generator uses the singleton pattern by default, so use generator.getInstance()
   * to get the generator.
   */
  protected ReportGenerator() {
    helperObjects = new HashMap();
  }

  /**
   * Set to false, to globaly disable the xml-validation.
   *
   * @param validate
   *          true, if the parser should validate the xml files.
   */
  public void setValidateDTD( final boolean validate ) {
    this.validateDTD = validate;
  }

  /**
   * returns true, if the parser should validate the xml files against the DTD supplied with JFreeReport.
   *
   * @return true, if the parser should validate, false otherwise.
   */
  public boolean isValidateDTD() {
    return validateDTD;
  }

  /**
   * Parses a report using the given parameter as filename and the directory containing the file as content base.
   *
   * @param file
   *          the file name.
   * @return the report.
   * @throws java.io.IOException
   *           if an I/O error occurs.
   */
  public MasterReport parseReport( final String file ) throws IOException, ResourceException {
    if ( file == null ) {
      throw new NullPointerException( "File may not be null" );
    }

    return parseReport( new File( file ) );
  }

  /**
   * Parses an XML file which is loaded using the given URL. All needed relative file- and resourcespecification are
   * loaded using the URL <code>file</code> as base.
   *
   * @param file
   *          the URL for the report template file.
   * @return the report.
   * @throws java.io.IOException
   *           if an I/O error occurs.
   */
  public MasterReport parseReport( final URL file ) throws IOException, ResourceException {
    return parseReport( file, file );
  }

  /**
   * Parses an XML file which is loaded using the given URL. All needed relative file- and resourcespecification are
   * loaded using the URL <code>contentBase</code> as base.
   * <p/>
   * After the report is generated, the ReportDefinition-source and the contentbase are stored as string in the
   * reportproperties.
   *
   * @param file
   *          the URL for the report template file.
   * @param contentBase
   *          the URL for the report template content base.
   * @return the parsed report.
   */
  public MasterReport parseReport( final URL file, final URL contentBase ) throws ResourceException {
    return parse( file, contentBase );
  }

  /**
   * Parses the report from a given URL.
   *
   * @param file
   *          the report definition location.
   * @param contentBase
   *          the report's context (used to load content that has been referenced with relative URLs).
   * @return the parsed report.
   * @throws ResourceException
   *           if parsing or loading failed for some reason.
   */
  private MasterReport parse( final URL file, final URL contentBase ) throws ResourceException {
    final ResourceManager resourceManager = new ResourceManager();
    final ResourceKey contextKey = resourceManager.createKey( contentBase );

    // Build the main key. That key also contains all context/parse-time
    // parameters as they will influence the resulting report. It is not
    // wise to keep caching independent from that.
    final HashMap map = new HashMap();
    final Iterator it = this.helperObjects.keySet().iterator();
    while ( it.hasNext() ) {
      final String name = (String) it.next();
      map.put( new FactoryParameterKey( name ), helperObjects.get( name ) );
    }

    final ResourceKey key = resourceManager.createKey( file, map );
    final Resource resource = resourceManager.create( key, contextKey, MasterReport.class );
    return (MasterReport) resource.getResource();
  }

  /**
   * @noinspection IOResourceOpenedButNotSafelyClosed
   */
  private byte[] extractData( final InputSource input ) throws IOException {
    final InputStream byteStream = input.getByteStream();
    if ( byteStream != null ) {
      try {
        final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
        IOUtils.getInstance().copyStreams( byteStream, bout );
        return bout.toByteArray();
      } finally {
        byteStream.close();
      }
    }

    final Reader characterStream = input.getCharacterStream();
    if ( characterStream == null ) {
      throw new IOException( "InputSource has neither an Byte nor a CharacterStream" );
    }

    try {
      final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
      final OutputStreamWriter owriter = new OutputStreamWriter( bout );
      IOUtils.getInstance().copyWriter( characterStream, owriter );
      owriter.close();
      return bout.toByteArray();
    } finally {
      characterStream.close();
    }
  }

  /**
   * Parses an XML file which is loaded using the given file. All needed relative file- and resourcespecification are
   * loaded using the parent directory of the file <code>file</code> as base.
   *
   * @param file
   *          the report template file.
   * @return the parsed report.
   * @throws java.io.IOException
   *           if an I/O error occurs.
   */
  public MasterReport parseReport( final File file ) throws IOException, ResourceException {
    if ( file == null ) {
      throw new NullPointerException();
    }
    if ( file.isDirectory() ) {
      throw new IOException( "File is not a directory." );
    }
    final File contentBase = file.getCanonicalFile().getParentFile();
    final ResourceManager resourceManager = new ResourceManager();
    final ResourceKey contextKey = resourceManager.createKey( contentBase );

    // Build the main key. That key also contains all context/parse-time
    // parameters as they will influence the resulting report. It is not
    // wise to keep caching independent from that.
    final HashMap map = new HashMap();
    final Iterator it = this.helperObjects.keySet().iterator();
    while ( it.hasNext() ) {
      final String name = (String) it.next();
      map.put( new FactoryParameterKey( name ), helperObjects.get( name ) );
    }

    final ResourceKey key = resourceManager.createKey( file, map );
    final Resource resource = resourceManager.create( key, contextKey, MasterReport.class );
    return (MasterReport) resource.getResource();
  }

  /**
   * Parses the report from a given SAX-InputSource.
   *
   * @param input
   *          the report definition location.
   * @param contentBase
   *          the report's context (used to load content that has been referenced with relative URLs).
   * @return the parsed report.
   * @throws ResourceException
   *           if parsing or loading failed for some reason.
   * @throws IOException
   *           if an IO-related error occurs.
   */
  public MasterReport parseReport( final InputSource input, final URL contentBase ) throws IOException,
    ResourceException {
    if ( input.getCharacterStream() != null ) {
      // Sourceforge Bug #1712734. We cannot safely route the character-stream through libloader.
      // Therefore we skip libloader and parse the report directly. This is for backward compatibility,
      // all other xml-based objects will still rely on LibLoader.

      return parseReportDirectly( input, contentBase );
    }

    final byte[] bytes = extractData( input );
    final ResourceManager resourceManager = new ResourceManager();
    final ResourceKey contextKey;
    if ( contentBase != null ) {
      contextKey = resourceManager.createKey( contentBase );
    } else {
      contextKey = null;
    }
    final HashMap map = new HashMap();

    final Iterator it = this.helperObjects.keySet().iterator();
    while ( it.hasNext() ) {
      final String name = (String) it.next();
      map.put( new FactoryParameterKey( name ), helperObjects.get( name ) );
    }

    final ResourceKey key = resourceManager.createKey( bytes, map );
    final Resource resource = resourceManager.create( key, contextKey, MasterReport.class );
    return (MasterReport) resource.getResource();
  }

  private MasterReport parseReportDirectly( final InputSource input, final URL contentBase )
    throws ResourceKeyCreationException, ResourceCreationException, ResourceLoadingException {
    final ResourceManager manager = new ResourceManager();
    final HashMap map = new HashMap();

    final Iterator it = this.helperObjects.keySet().iterator();
    while ( it.hasNext() ) {
      final String name = (String) it.next();
      map.put( new FactoryParameterKey( name ), helperObjects.get( name ) );
    }

    final MasterReportXmlResourceFactory resourceFactory = new MasterReportXmlResourceFactory();
    resourceFactory.initializeDefaults();
    if ( contentBase != null ) {
      return (MasterReport) resourceFactory.parseDirectly( manager, input, manager.createKey( contentBase ), map );
    } else {
      return (MasterReport) resourceFactory.parseDirectly( manager, input, null, map );
    }
  }

  /**
   * Parses the report using the provided resource manager.
   *
   * @param manager
   *          the resource manager (can be null).
   * @param input
   *          the resource key pointing to the report definition.
   * @param contextKey
   *          the report's context (used to load content that has been referenced with relative URLs).
   * @return the parsed report.
   * @throws ResourceException
   *           if parsing or loading failed for some reason.
   */
  public MasterReport parseReport( ResourceManager manager, final ResourceKey input, final ResourceKey contextKey )
    throws ResourceException {
    if ( manager == null ) {
      manager = new ResourceManager();
    }

    final HashMap map = new HashMap( input.getFactoryParameters() );
    final Iterator it = this.helperObjects.keySet().iterator();
    while ( it.hasNext() ) {
      final String name = (String) it.next();
      map.put( new FactoryParameterKey( name ), helperObjects.get( name ) );
    }

    final ResourceKey key =
        new ResourceKey( input.getParent(), input.getSchema(), input.getIdentifier(), input.getFactoryParameters() );
    final Resource resource = manager.create( key, contextKey, MasterReport.class );
    return (MasterReport) resource.getResource();
  }

  /**
   * Returns a single shared instance of the <code>ReportGenerator</code>. This instance cannot add helper objects to
   * configure the report parser.
   *
   * @return The shared report generator.
   */
  public static synchronized ReportGenerator getInstance() {
    if ( generator == null ) {
      generator = new ReportGenerator();
    }
    return generator;
  }

  /**
   * Returns a private (non-shared) instance of the <code>ReportGenerator</code>. Use this instance when defining helper
   * objects.
   *
   * @return The shared report generator.
   */
  public static ReportGenerator createInstance() {
    return new ReportGenerator();
  }

  /**
   * Assigns a parse-context object.
   *
   * @param key
   *          the parse-context key used to lookup the object later.
   * @param value
   *          the value.
   */
  public void setObject( final String key, final Object value ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( value == null ) {
      helperObjects.remove( key );
    } else {
      helperObjects.put( key, value );
    }
  }

  /**
   * Returns the parse context object for the given key.
   *
   * @param key
   *          the key.
   * @return the value or null if there is no such value.
   */
  public Object getObject( final String key ) {
    return helperObjects.get( key );
  }
}
