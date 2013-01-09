import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClearProperties
{
  public static void main(String[] args) throws IOException
  {
    String path = "/Volumes/Datastorex/source/jfreereport/branch-3.8/engines/classic/core/source/org/pentaho/reporting/engine/classic/core/modules/gui/base/messages/messages_ja.properties";
        //"/Volumes/Datastorex/source/jfreereport/branch-3.8/engines/classic/core/source/org/pentaho/reporting/engine/classic/core/modules/misc/datafactory/messages_ja.properties";
//        "/Volumes/Datastorex/source/jfreereport/branch-3.8/engines/classic/core/source/org/pentaho/reporting/engine/classic/core/metadata/messages_ja.properties";
    final BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(path), "ISO-8859-1"));
    String s = fin.readLine();
    while (s != null)
    {
      if (s.indexOf(".ordinal") != -1)
      {
        s = fin.readLine();
        continue;
      }
      if (s.indexOf(".icon") != -1)
      {
        s = fin.readLine();
        continue;
      }


      System.out.println (s);
      s = fin.readLine();
    }
    fin.close();

  }
}
