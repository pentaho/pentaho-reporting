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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.content;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data.DataDefinition;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.settings.BundleSettings;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.resourceloader.FactoryParameterKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.Enumeration;
import java.util.Map;

/**
 * The content root handler is the first handler that is parsed when dealing with bundle-reports. This file contains all
 * the forwards to the various report-files.
 * <p/>
 * A bundle always contains a master-report. It is not possible to parse a full bundle into a subreport. However, it is
 * possible to parse the subreport xml files contained in a bundle into subreports, if needed.
 *
 * @author Thomas Morgner
 */
public class ContentRootElementHandler extends AbstractXmlReadHandler {
  private static final Log logger = LogFactory.getLog( ContentRootElementHandler.class );
  public static final String PRPT_SPEC_VERSION = "prpt-spec-version";
  private MasterReport report;

  public ContentRootElementHandler() {
  }

  /**
   * Initialises the handler.
   *
   * @param rootHandler
   *          the root handler.
   * @param tagName
   *          the tag name.
   */
  public void init( final RootXmlReadHandler rootHandler, final String uri, final String tagName ) throws SAXException {
    super.init( rootHandler, uri, tagName );
    rootHandler.setHelperObject( "property-expansion", Boolean.FALSE );
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final Object maybeReport = getRootHandler().getHelperObject( ReportParserUtil.HELPER_OBJ_REPORT_NAME );
    if ( maybeReport instanceof MasterReport == false ) {
      // replace it ..
      report = new MasterReport();
      report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.FILEFORMAT, "unified-fileformat" );

      final ResourceKey key = getRootHandler().getSource();
      if ( key.getParent() != null ) {
        final ResourceManager resourceManager = getRootHandler().getResourceManager();
        try {
          final Resource bundleData = resourceManager.create( key.getParent(), null, DocumentBundle.class );
          final DocumentBundle documentBundle = (DocumentBundle) bundleData.getResource();
          report.setBundle( documentBundle );

          final DocumentMetaData metaData = documentBundle.getMetaData();
          final int versionMajorRaw = getBundleAttribute( metaData, "prpt-spec.version.major" );
          final int versionMinorRaw = getBundleAttribute( metaData, "prpt-spec.version.minor" );
          final int versionPatchRaw = getBundleAttribute( metaData, "prpt-spec.version.patch" );

          if ( versionMajorRaw == -1 || versionMinorRaw == -1 || versionPatchRaw == -1
              || ( versionMajorRaw == 0 && versionMinorRaw == 0 && versionPatchRaw == 0 ) ) {
            // any of the version attributes is missing. Assume we are running with a legacy report.
            getRootHandler().setHelperObject( PRPT_SPEC_VERSION, ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );
          } else {
            validateVersionNumbers( versionMajorRaw, versionMinorRaw, versionPatchRaw );

            // file has been created with at least 3.9.0 or a development version. Therefore the
            // version number is set to either the number or zero for dev versions.
            getRootHandler().setHelperObject( PRPT_SPEC_VERSION,
                ClassicEngineBoot.computeVersionId( versionMajorRaw, versionMinorRaw, versionPatchRaw ) );
          }
        } catch ( ResourceException e ) {
          getRootHandler().warning(
              new SAXParseException( "Unable to load the bundle. Bundle data may be unavailable.", getLocator() ) );
        }
      }
    } else {
      report = (MasterReport) maybeReport;
    }
  }

  private void validateVersionNumbers( final int prptVersionMajorRaw, final int prptVersionMinorRaw,
      final int prptVersionPatchRaw ) throws ParseException {
    final ClassicEngineBoot.VersionValidity v =
        ClassicEngineBoot.isValidVersion( prptVersionMajorRaw, prptVersionMinorRaw, prptVersionPatchRaw );
    if ( v == ClassicEngineBoot.VersionValidity.INVALID_RELEASE ) {
      throw new ParseException( String.format( "The report file you are trying to load was created with "
          + "Pentaho Reporting %d.%d but you are trying to run it with Pentaho Reporting %s. "
          + "Please update your reporting installation to match the report designer that was used "
          + "to create this file.", prptVersionMajorRaw, prptVersionMinorRaw, ClassicEngineInfo.getInstance()
          .getVersion() ) );
    } else if ( v == ClassicEngineBoot.VersionValidity.INVALID_PATCH ) {
      logger.warn( String.format( "The report file you are trying to load was created with Pentaho "
          + "Reporting %d.%d.%d but you are trying to run it with Pentaho Reporting %s. "
          + "Your reporting engine version may not have all features or bug-fixes required to display "
          + "this report properly.", prptVersionMajorRaw, prptVersionMinorRaw, prptVersionPatchRaw, ClassicEngineInfo
          .getInstance().getVersion() ) );
    }
  }

  private int getBundleAttribute( final DocumentMetaData metaData, final String name ) {
    final Object raw = metaData.getBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, name );
    if ( raw == null ) {
      return -1;
    }
    try {
      return Integer.parseInt( String.valueOf( raw ) );
    } catch ( NumberFormatException nfe ) {
      return -1;
    }
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( BundleNamespaces.CONTENT.equals( uri ) == false ) {
      return null;
    }

    if ( "settings".equals( tagName ) ) {
      final String primaryFile = atts.getValue( getUri(), "ref" );
      if ( primaryFile == null ) {
        throw new ParseException( "Required attribute 'ref' is not specified", getLocator() );
      }

      if ( parseSettings( primaryFile ) == false ) {
        final String fallbackFile = atts.getValue( getUri(), "local-copy" );
        if ( fallbackFile != null ) {
          if ( parseSettings( fallbackFile ) == false ) {
            throw new ParseException( "Parsing the specified local-copy failed", getLocator() );
          }
        }
      }

      return new IgnoreAnyChildReadHandler();
    }
    if ( "data-definition".equals( tagName ) ) {
      final String primaryFile = atts.getValue( getUri(), "ref" );
      if ( primaryFile == null ) {
        throw new ParseException( "Required attribute 'ref' is not specified", getLocator() );
      }

      if ( parseDataDefinition( primaryFile ) == false ) {
        final String fallbackFile = atts.getValue( getUri(), "local-copy" );
        if ( fallbackFile != null ) {
          if ( parseDataDefinition( fallbackFile ) == false ) {
            throw new ParseException( "Parsing the specified local-copy failed", getLocator() );
          }
        }
      }
      return new IgnoreAnyChildReadHandler();
    }
    if ( "styles".equals( tagName ) ) {
      final String primaryFile = atts.getValue( getUri(), "ref" );
      if ( primaryFile == null ) {
        throw new ParseException( "Required attribute 'ref' is not specified", getLocator() );
      }

      if ( parseStyles( primaryFile ) == false ) {
        final String fallbackFile = atts.getValue( getUri(), "local-copy" );
        if ( fallbackFile != null ) {
          if ( parseStyles( fallbackFile ) == false ) {
            throw new ParseException( "Parsing the specified local-copy failed", getLocator() );
          }
        }
      }
      return new IgnoreAnyChildReadHandler();
    }
    if ( "layout".equals( tagName ) ) {
      final String primaryFile = atts.getValue( getUri(), "ref" );
      if ( primaryFile == null ) {
        throw new ParseException( "Required attribute 'ref' is not specified", getLocator() );
      }

      if ( parseLayout( primaryFile ) == false ) {
        final String fallbackFile = atts.getValue( getUri(), "local-copy" );
        if ( fallbackFile != null ) {
          if ( parseLayout( fallbackFile ) == false ) {
            throw new ParseException( "Parsing the specified local-copy failed", getLocator() );
          }
        }
      }
      return new IgnoreAnyChildReadHandler();
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    // Now, after all the user-defined and global files have been parsed, finally override whatever had been
    // defined in these files with the contents from the bundle. This will merge all the settings from the bundle
    // with the global definitions but grants the local settings higer preference
    parseLocalFiles();

    final Object definedCompatLevel = report.getCompatibilityLevel();
    if ( definedCompatLevel instanceof Integer == false ) {
      final Object specRaw = getRootHandler().getHelperObject( PRPT_SPEC_VERSION );
      final Integer x = ClassicEngineBoot.computeVersionId( 999, 999, 999 );
      if ( specRaw instanceof Integer && x.equals( specRaw ) == false ) {
        report.setCompatibilityLevel( (Integer) specRaw );
      } else {
        report.setCompatibilityLevel( x );
      }
    }
  }

  private void parseLocalFiles() throws ParseException {
    parseSettings( "settings.xml" );
    parseDataDefinition( "datadefinition.xml" );
    parseDataSchema();
    parseStyles( "styles.xml" );
    parseLayout( "layout.xml" );
  }

  private boolean parseLayout( final String layout ) throws ParseException {
    try {
      final Map parameters = deriveParseParameters();
      parameters.put( new FactoryParameterKey( ReportParserUtil.HELPER_OBJ_REPORT_NAME ), report );
      parameters.put( new FactoryParameterKey( ReportParserUtil.INCLUDE_PARSING_KEY ),
          ReportParserUtil.INCLUDE_PARSING_VALUE );
      final MasterReport report = (MasterReport) performExternalParsing( layout, MasterReport.class, parameters );
      return report == this.report;
    } catch ( ResourceLoadingException e ) {
      ContentRootElementHandler.logger.warn( "Unable to parse the parameter for this bundle from file: " + layout );
      return false;
    }
  }

  private boolean parseStyles( final String stylefile ) throws ParseException {
    try {
      final Map parameters = deriveParseParameters();
      parameters.put( new FactoryParameterKey( ReportParserUtil.HELPER_OBJ_REPORT_NAME ), report );
      parameters.put( new FactoryParameterKey( ReportParserUtil.INCLUDE_PARSING_KEY ),
          ReportParserUtil.INCLUDE_PARSING_VALUE );
      final MasterReport report = (MasterReport) performExternalParsing( stylefile, MasterReport.class, parameters );
      return report == this.report;
    } catch ( ResourceLoadingException e ) {
      ContentRootElementHandler.logger.warn( "Unable to parse the parameter for this bundle from file: " + stylefile );
      return false;
    }
  }

  private boolean parseDataDefinition( final String parameterFile ) throws ParseException {
    try {
      final Map parameters = deriveParseParameters();
      parameters.put( new FactoryParameterKey( ReportParserUtil.HELPER_OBJ_REPORT_NAME ), null );
      final DataDefinition dataDefinition =
          (DataDefinition) performExternalParsing( parameterFile, DataDefinition.class, parameters );

      report.setQuery( dataDefinition.getQuery() );
      report.setQueryLimit( dataDefinition.getQueryLimit() );
      report.setQueryTimeout( dataDefinition.getQueryTimeout() );

      final DataFactory dataFactory = dataDefinition.getDataFactory();
      if ( dataFactory != null ) {
        report.setDataFactory( dataFactory );
      }
      final ReportParameterDefinition definition = dataDefinition.getParameterDefinition();
      if ( definition != null ) {
        report.setParameterDefinition( definition );
      }
      final Expression[] expressions = dataDefinition.getExpressions();
      if ( expressions != null ) {
        for ( int i = 0; i < expressions.length; i++ ) {
          final Expression expression = expressions[i];
          report.addExpression( expression );
        }
      }
      return true;
    } catch ( ResourceLoadingException e ) {
      ContentRootElementHandler.logger.warn( "Unable to parse the parameter for this bundle from file: "
          + parameterFile );
      return false;
    } catch ( ReportDataFactoryException e ) {
      ContentRootElementHandler.logger.warn( "Unable to parse the parameter for this bundle from file: "
          + parameterFile );
      return false;
    }
  }

  private boolean parseSettings( final String settingsFile ) throws ParseException {
    try {
      final Map parameters = deriveParseParameters();
      parameters.put( new FactoryParameterKey( ReportParserUtil.HELPER_OBJ_REPORT_NAME ), null );
      final BundleSettings settings =
          (BundleSettings) performExternalParsing( settingsFile, BundleSettings.class, parameters );
      // todo: Apply settings
      final Configuration configuration = settings.getConfiguration();
      final Enumeration configProperties = configuration.getConfigProperties();
      while ( configProperties.hasMoreElements() ) {
        final String key = (String) configProperties.nextElement();
        final String value = configuration.getConfigProperty( key );
        if ( value != null ) {
          report.getReportConfiguration().setConfigProperty( key, value );
        }
      }
      return true;
    } catch ( ResourceLoadingException e ) {
      ContentRootElementHandler.logger.warn( "Unable to parse the settingsFile for this bundle from file: "
          + settingsFile );
      return false;
    }
  }

  /**
   * Parsing the meta-data is error-resistant. If parsing the meta-data fails for a "file-not-found-error", the parse
   * continues. Meta-data parsing is not mergeable - there is only one source for the meta-data of a report-bundle.
   *
   * @return true, if the meta-data bundle has been updated, false otherwise.
   * @throws ParseException
   *           if the parsing fails.
   */
  private boolean parseDataSchema() throws ParseException {
    try {
      final Map parameters = deriveParseParameters();
      parameters.put( new FactoryParameterKey( ReportParserUtil.HELPER_OBJ_REPORT_NAME ), null );
      final DataSchemaDefinition metaData =
          (DataSchemaDefinition) performExternalParsing( "dataschema.xml", DataSchemaDefinition.class, parameters );
      report.setDataSchemaDefinition( metaData );
      return true;
    } catch ( ResourceLoadingException e ) {
      ContentRootElementHandler.logger.warn( "Unable to parse the dataschema for this bundle." );
      return false;
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return report;
  }
}
