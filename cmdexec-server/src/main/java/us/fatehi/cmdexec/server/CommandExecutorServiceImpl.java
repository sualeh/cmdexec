package us.fatehi.cmdexec.server;


import static us.fatehi.cmdexec.client.Utility.testImplementation;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import us.fatehi.cmdexec.client.Command;
import us.fatehi.cmdexec.client.CommandExecutorService;
import us.fatehi.cmdexec.client.CommandResult;

public final class CommandExecutorServiceImpl
  extends RemoteServer
  implements CommandExecutorService
{

  private static final long serialVersionUID = -4200390651052800245L;

  private final ExecutorService threadPool;
  private final int timeout;

  public CommandExecutorServiceImpl(final int threads, final int timeout)
  {
    threadPool = Executors.newFixedThreadPool(threads);
    this.timeout = timeout;
  }

  @Override
  public List<CommandResult> executeCommands(final List<Command> commands)
    throws RemoteException, ServerNotActiveException
  {
    try
    {
      final CommandExecutor commandExecutor = new CommandExecutor(getClientHost(),
                                                                  commands);
      final FutureTask<List<CommandResult>> futureTask = new FutureTask<List<CommandResult>>(commandExecutor);
      threadPool.execute(futureTask);

      return futureTask.get(timeout, TimeUnit.SECONDS);
    }
    catch (final InterruptedException e)
    {
      throw new RemoteException("Could not execute commands", e);
    }
    catch (final ExecutionException e)
    {
      throw new RemoteException("Could not execute commands", e);
    }
    catch (final TimeoutException e)
    {
      throw new RemoteException("Could not execute commands", e);
    }
  }

  public void shutdown()
    throws RemoteException
  {
    try
    {
      threadPool.shutdown();
    }
    catch (final Exception e)
    {
      throw new RemoteException("Could not shut down executor service", e);
    }
  }

  public long test(final long testCode)
    throws RemoteException
  {
    return testImplementation(testCode);
  }

}
