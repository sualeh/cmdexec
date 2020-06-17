package us.fatehi.cmdexec.server;


import static us.fatehi.cmdexec.client.Utility.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import us.fatehi.cmdexec.client.Command;
import us.fatehi.cmdexec.client.CommandResult;

public final class CommandExecutor
  implements Callable<List<CommandResult>>
{

  private final String clientHost;
  private final ExecutorService threadPool;
  private final List<Command> commands;

  public CommandExecutor(final String clientHost, final List<Command> commands)
  {
    this.clientHost = clientHost;
    if (commands == null)
    {
      throw new IllegalArgumentException("No commands specified");
    }
    this.commands = commands;
    threadPool = Executors.newFixedThreadPool(5);
  }

  public List<CommandResult> call()
    throws Exception
  {
    final List<CommandResult> commandResults = new ArrayList<CommandResult>(commands
      .size());
    for (final Command command: commands)
    {
      commandResults.add(execute(command));
    }

    threadPool.shutdown();

    return commandResults;
  }

  private CommandResult execute(final Command command)
  {

    class StreamReaderTask
      extends FutureTask<String>
    {

      StreamReaderTask(final InputStream in)
      {
        super(new Callable<String>()
        {

          public String call()
            throws Exception
          {
            final int capacity = 8192;
            final Reader reader = new BufferedReader(new InputStreamReader(in));
            final StringBuilder results = new StringBuilder(capacity);
            final char[] buffer = new char[capacity];
            int read;
            while ((read = reader.read(buffer, 0, buffer.length)) > 0)
            {
              results.append(buffer, 0, read);
            }
            reader.close();

            if (results.length() == 0)
            {
              return null;
            }
            else
            {
              return results.toString().replace("\r\r", "\r");
            }
          }
        });
      }

    }

    CommandResult commandResult;
    final ProcessBuilder processBuilder = new ProcessBuilder(command
      .getCommand());
    final Map<String, String> environment = command.getEnvironment();
    if ((environment != null) && !environment.isEmpty())
    {
      processBuilder.environment().putAll(environment);
    }
    final File directory = command.getDirectory();
    if ((directory != null) && directory.exists() && directory.isDirectory())
    {
      processBuilder.directory(directory);
    }

    final Date processStart = new Date();
    try
    {
      log(command.toString());

      final Process process = processBuilder.start();

      final FutureTask<String> inReaderTask = new StreamReaderTask(process
        .getInputStream());
      threadPool.execute(inReaderTask);
      final FutureTask<String> errReaderTask = new StreamReaderTask(process
        .getErrorStream());
      threadPool.execute(errReaderTask);

      final int exitValue = process.waitFor();

      final String processOutput = inReaderTask.get();
      final String processError = errReaderTask.get();

      commandResult = new CommandResult(clientHost,
                                        command,
                                        processStart,
                                        processOutput,
                                        processError,
                                        exitValue);
      log(commandResult.toString());
    }
    catch (final Throwable e)
    {
      commandResult = new CommandResult(clientHost, command, processStart, e);
      log("Error executing " + command, e);
    }

    return commandResult;
  }

}
