package us.fatehi.cmdexec.client;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility
{

  private static final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

  public static void log(final String message)
  {
    System.out.println(String.format("%s %s", df.format(new Date()), message));
  }

  public static void log(final String message, final Throwable e)
  {
    final StringWriter writer = new StringWriter();
    e.printStackTrace(new PrintWriter(writer));
    System.err.println(String.format("%s %s", df.format(new Date()), String
      .format("%s\n%s", message, writer)));
  }

  public static long testImplementation(final long testCode)
  {
    return testCode + 1;
  }

  private Utility()
  {
  }

}
