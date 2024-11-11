/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.wizard.parser;

import org.pentaho.reporting.engine.classic.wizard.model.DefaultWizardSpecification;
import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.WizardSpecification;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class WizardSpecificationReadHandler extends AbstractXmlReadHandler {
  private WizardSpecification wizardSpecification;
  private GroupDefinitionsReadHandler groupDefinitionsReadHandler;
  private DetailsFieldDefinitionsReadHandler detailsFieldDefinitionsReadHandler;

  public WizardSpecificationReadHandler() {
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    wizardSpecification = new DefaultWizardSpecification();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "column-footer".equals( tagName ) ) {
      return new RootBandDefinitionReadHandler( wizardSpecification.getColumnFooter() );
    }
    if ( "column-header".equals( tagName ) ) {
      return new RootBandDefinitionReadHandler( wizardSpecification.getColumnHeader() );
    }
    if ( "watermark-specification".equals( tagName ) ) {
      return new WatermarkDefinitionReadHandler( wizardSpecification.getWatermarkDefinition() );
    }
    if ( "group-definitions".equals( tagName ) ) {
      groupDefinitionsReadHandler = new GroupDefinitionsReadHandler();
      return groupDefinitionsReadHandler;
    }
    if ( "detail-fields".equals( tagName ) ) {
      detailsFieldDefinitionsReadHandler = new DetailsFieldDefinitionsReadHandler();
      return detailsFieldDefinitionsReadHandler;
    }

    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    if ( groupDefinitionsReadHandler != null ) {
      final GroupDefinition[] groupDefinitions = groupDefinitionsReadHandler.getGroupDefinitions();
      wizardSpecification.setGroupDefinitions( groupDefinitions );
    }

    if ( detailsFieldDefinitionsReadHandler != null ) {
      wizardSpecification.setDetailFieldDefinitions( detailsFieldDefinitionsReadHandler.getDetailFieldDefinitions() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return wizardSpecification;
  }
}
