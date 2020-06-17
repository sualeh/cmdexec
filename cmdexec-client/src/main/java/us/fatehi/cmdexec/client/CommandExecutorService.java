package us.fatehi.cmdexec.client;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.util.List;

public interface CommandExecutorService
  extends Remote
{

  List<CommandResult> executeCommands(List<Command> commands)
    throws RemoteException, ServerNotActiveException;

  void shutdown()
    throws RemoteException;

  long test(long testCode)
    throws RemoteException;

}
