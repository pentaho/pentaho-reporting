/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.parser;

import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.IPmdConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdConnectionProvider;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Michael D'Amour
 */
public class PmdConfigReadHandler extends AbstractXmlReadHandler implements IPmdConfigReadHandler {
  private String domain;
  private String xmiFile;

  public PmdConfigReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );

    xmiFile = attrs.getValue( getUri(), "xmi-file" );
    domain = attrs.getValue( getUri(), "domain" );
  }

  public String getDomain() {
    return domain;
  }

  public String getXmiFile() {
    return xmiFile;
  }


  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return null;
  }

  public IPmdConnectionProvider getConnectionProvider() {
    return new PmdConnectionProvider();
  }

}
