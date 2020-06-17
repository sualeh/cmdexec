package us.fatehi.cmdexec.client;


import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Date;

public final class CommandResult
  implements Serializable
{

  private static final long serialVersionUID = 1939722027934576356L;

  private final String clientHost;
  private final Command command;
  private final String processOutput;
  private final String processError;
  private final int exitValue;
  private final Date started;
  private final Date completed;
  private final boolean encounteredException;

  public CommandResult(final String clientHost,
                       final Command command,
                       final Date started,
                       final String processOutput,
                       final String processError,
                       final int exitValue)
  {
    this.clientHost = clientHost;
    this.command = command;
    this.processOutput = processOutput;
    this.processError = processError;
    this.exitValue = exitValue;
    this.started = started;
    completed = new Date();
    encounteredException = false;
  }

  public CommandResult(final String clientHost,
                       final Command command,
                       final Date started,
                       final Throwable e)
  {
    this.clientHost = clientHost;
    if (e == null)
    {
      encounteredException = true;
      processError = null;
    }
    else
    {
      encounteredException = true;
      final StringWriter writer = new StringWriter();
      e.printStackTrace(new PrintWriter(writer));
      processError = writer.toString();
    }

    this.command = command;
    processOutput = null;
    exitValue = 0;
    this.started = started;
    completed = new Date();
  }

  public String getClientHost()
  {
    return clientHost;
  }

  public Command getCommand()
  {
    return command;
  }

  public Date getCompleted()
  {
    return completed;
  }

  public int getExitValue()
  {
    return exitValue;
  }

  public String getProcessError()
  {
    return processError;
  }

  public String getProcessOutput()
  {
    return processOutput;
  }

  public Date getStarted()
  {
    return started;
  }

  public boolean isEncounteredException()
  {
    return encounteredException;
  }

  public boolean isErroredOut()
  {
    return encounteredException || (exitValue != 0);
  }

  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("CommandResult [");
    if (command != null)
    {
      builder.append("command=");
      builder.append(command);
      builder.append(", ");
    }
    builder.append("encounteredException=");
    builder.append(encounteredException);
    builder.append(", ");
    if (clientHost != null)
    {
      builder.append("clientHost=");
      builder.append(clientHost);
      builder.append(", ");
    }
    if (started != null)
    {
      builder.append("started=");
      builder.append(started);
      builder.append(", ");
    }
    if (completed != null)
    {
      builder.append("completed=");
      builder.append(completed);
      builder.append(", ");
    }
    builder.append("exitValue=");
    builder.append(exitValue);
    builder.append(", ");
    if (processError != null)
    {
      builder.append("processError=");
      builder.append(processError);
      builder.append(", ");
    }
    if (processOutput != null)
    {
      builder.append("processOutput=");
      builder.append(processOutput);
    }
    builder.append("]");
    return builder.toString();
  }

}
