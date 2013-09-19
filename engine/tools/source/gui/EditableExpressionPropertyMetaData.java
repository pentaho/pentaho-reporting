package gui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.metadata.AbstractMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyMetaData;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

public class EditableExpressionPropertyMetaData extends AbstractEditableMetaData
{

  public EditableExpressionPropertyMetaData(final DefaultExpressionPropertyMetaData expressionPropertyMetaData)
  {
    super(expressionPropertyMetaData);
  }

  public boolean isValid(final Locale locale, final boolean deepCheck)
  {

    final String[] textProperties =
        {"display-name", "grouping"};
    for (int i = 0; i < textProperties.length; i++)
    {
      final String property = textProperties[i];
      if (StringUtils.isEmpty(getMetaAttribute(property, locale)))
      {
        return false;
      }
    }

    final String[] optionalTextProperties = {"description", "deprecated"};
    for (int i = 0; i < optionalTextProperties.length; i++)
    {
      final String property = optionalTextProperties[i];
      if (getMetaAttribute(property, locale) == null)
      {
        return false;
      }
    }


    final String[] integerProperties = {"grouping.ordinal", "ordinal"};
    for (int i = 0; i < integerProperties.length; i++)
    {
      final String property = integerProperties[i];
      if (ParserUtil.parseInt(getMetaAttribute(property, locale), Integer.MAX_VALUE) == Integer.MAX_VALUE)
      {
        return false;
      }
    }
    return true;
  }

  public String printBundleText(final Locale locale)
  {
    final AbstractMetaData backend = getBackend();
    final String[] properties =
        {"display-name", "grouping", "grouping.ordinal",
            "ordinal", "description", "deprecated"};

    final StringWriter writer = new StringWriter();
    PrintWriter p = new PrintWriter(writer);
    for (int i = 0; i < properties.length; i++)
    {
      final String property = properties[i];
      PropertyHelper.saveConvert(backend.getKeyPrefix(), PropertyHelper.ESCAPE_KEY, p);
      PropertyHelper.saveConvert(backend.getName(), PropertyHelper.ESCAPE_KEY, p);
      p.print('.');
      PropertyHelper.saveConvert(property, PropertyHelper.ESCAPE_KEY, p);
      p.print('=');
      PropertyHelper.saveConvert(getMetaAttribute(property, locale), PropertyHelper.ESCAPE_VALUE, p);
      p.println();
    }
    p.close();
    return writer.toString();
  }
}
