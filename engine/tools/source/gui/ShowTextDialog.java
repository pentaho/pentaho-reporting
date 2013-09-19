package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;

public class ShowTextDialog extends CommonDialog
{
  private JTextArea textArea;

  public ShowTextDialog()
  {
    init();
  }

  public ShowTextDialog(final Frame owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  public ShowTextDialog(final Dialog owner)
      throws HeadlessException
  {
    super(owner);
    init();
  }

  protected void init()
  {
    super.init();
    pack();
    setSize(800, 600);
  }

  protected String getDialogId()
  {
    return getClass().getName();
  }

  protected Component createContentPane()
  {
    textArea = new JTextArea();
    textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    textArea.setLineWrap(false);
    textArea.setEditable(true);

    final JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
    return panel;
  }

  protected boolean hasCancelButton()
  {
    return false;
  }

  public void showText(String text)
  {
    textArea.setText(text);
    setModal(false);
    setVisible(true);
  }
}
