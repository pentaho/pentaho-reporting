package org.pentaho.reporting.designer.core.editor.drilldown.parser;

import org.pentaho.reporting.designer.core.editor.drilldown.model.ParameterSelection;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 13.08.2010
 * Time: 17:34:04
 *
 * @author Thomas Morgner.
 */
public class ParameterValueReadHandler extends AbstractXmlReadHandler
{
  private ParameterSelection selection;
  private String parameterType;

  public ParameterValueReadHandler(final String parameterType)
  {
    this.parameterType = parameterType;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing(final Attributes attrs) throws SAXException
  {
    final String label = attrs.getValue(getUri(), "label"); // NON-NLS
    final String value = attrs.getValue(getUri(), "value"); // NON-NLS
    String type = attrs.getValue(getUri(), "type"); // NON-NLS
    if (StringUtils.isEmpty(type))
    {
      type = parameterType;
    }
    final boolean selected = "true".equals(attrs.getValue(getUri(), "selected")); // NON-NLS

    selection = new ParameterSelection(type, value, selected, label);
  }

  /**
   * Returns the object for this element or null, if this element does
   * not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException
  {
    return selection;
  }

  public ParameterSelection getSelection()
  {
    return selection;
  }
}