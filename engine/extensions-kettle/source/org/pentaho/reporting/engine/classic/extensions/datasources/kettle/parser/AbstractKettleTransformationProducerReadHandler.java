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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class AbstractKettleTransformationProducerReadHandler
    extends AbstractXmlReadHandler implements KettleTransformationProducerReadHandler
{
  private String name;
  private String stepName;
  private String username;
  private String password;
  private String repositoryName;
  private String[] definedArgumentNames;
  private ParameterMapping[] definedVariableNames;
  private ArrayList<ArgumentReadHandler> argumentHandlers;
  private ArrayList<VariableReadHandler> variablesHandlers;

  public AbstractKettleTransformationProducerReadHandler()
  {
    argumentHandlers = new ArrayList<ArgumentReadHandler>();
    variablesHandlers = new ArrayList<VariableReadHandler>();
  }

  public String getName()
  {
    return name;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    name = attrs.getValue(getUri(), "name");
    if (name == null)
    {
      throw new ParseException("Required attribute 'name' is not defined");
    }

    repositoryName = attrs.getValue(getUri(), "repository");
    if (repositoryName == null)
    {
      throw new ParseException("Required attribute 'repository' is not defined");
    }

    username = attrs.getValue(getUri(), "username");
    password = PasswordEncryptionService.getInstance().decrypt(getRootHandler(), attrs.getValue(getUri(), "password"));

    stepName = attrs.getValue(getUri(), "step");
    if (stepName == null)
    {
      throw new ParseException("Required attribute 'step' is not defined");
    }
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
  protected XmlReadHandler getHandlerForChild(final String uri, final String tagName, final Attributes atts) throws SAXException
  {
    if (getUri().equals(uri) == false)
    {
      return null;
    }

    if ("argument".equals(tagName))
    {
      final ArgumentReadHandler readHandler = new ArgumentReadHandler();
      argumentHandlers.add(readHandler);
      return readHandler;
    }

    if ("variable".equals(tagName))
    {
      final VariableReadHandler readHandler = new VariableReadHandler();
      variablesHandlers.add(readHandler);
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException
  {
    definedArgumentNames = new String[argumentHandlers.size()];
    for (int i = 0; i < definedArgumentNames.length; i++)
    {
      final ArgumentReadHandler o = (ArgumentReadHandler) argumentHandlers.get(i);
      definedArgumentNames[i] = o.getDataRowName();
    }

    definedVariableNames = new ParameterMapping[variablesHandlers.size()];
    for (int i = 0; i < definedVariableNames.length; i++)
    {
      final VariableReadHandler readHandler = (VariableReadHandler) variablesHandlers.get(i);
      definedVariableNames[i] = (ParameterMapping) readHandler.getObject();
    }
  }

  public String getStepName()
  {
    return stepName;
  }

  public String getUsername()
  {
    return username;
  }

  public String getPassword()
  {
    return password;
  }

  public String getRepositoryName()
  {
    return repositoryName;
  }

  public String[] getDefinedArgumentNames()
  {
    return definedArgumentNames;
  }

  public ParameterMapping[] getDefinedVariableNames()
  {
    return definedVariableNames;
  }

  public KettleTransformationProducer getTransformationProducer() throws SAXException
  {
    return getObject();
  }

  public abstract KettleTransformationProducer getObject() throws SAXException;
}
