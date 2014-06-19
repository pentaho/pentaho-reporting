package org.pentaho.openformula.ui.table;

import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class EditorMessages extends Messages
{
  private static EditorMessages utilMessages;

  public static Messages getInstance()
  {
    if (utilMessages == null)
    {
      utilMessages = new EditorMessages();
    }
    return utilMessages;
  }

  /**
   * Creates a new Messages-collection. The locale and baseName will be used to create the resource-bundle that backs up
   * this implementation.
   */
  private EditorMessages()
  {
    super(Locale.getDefault(), "org.pentaho.reporting.designer.core.util.messages",  // NON-NLS
        ObjectUtilities.getClassLoader(EditorMessages.class));
  }
}
