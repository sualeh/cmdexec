package us.fatehi.cmdexec.client;

import static us.fatehi.cmdexec.client.Utility.*;
import static us.fatehi.cmdexec.client.Utility.testImplementation;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class CommandExecutorClient {

  private final String host;
  private final int port;

  public CommandExecutorClient(final String host, final int port) throws Exception {
    this.host = host;
    this.port = port;
    test();
  }

  @Override
  public String toString() {
    return "CommandExecutorClient [host=" + host + ", port=" + port + "]";
  }

  public List<CommandResult> executeCommands(final Command... commands) throws Exception {
    return executeCommands(Arrays.asList(commands));
  }

  protected List<CommandResult> executeCommands(final List<Command> commands) throws Exception {
    final CommandExecutorService cmdExecSvc = locateService();
    log(String.format("Executing commands: %s", commands));
    final List<CommandResult> commandResults = cmdExecSvc.executeCommands(commands);
    return commandResults;
  }

  protected boolean test() throws Exception {
    final CommandExecutorService cmdExecSvc = locateService();
    final long testCode = new Date().getTime();
    final boolean test = testImplementation(testCode) == cmdExecSvc.test(testCode);
    log(String.format("Connection test is %ssuccessful", (test ? "" : "NOT ")));
    return test;
  }

  private CommandExecutorService locateService() throws RemoteException, NotBoundException {
    try {
      final InetAddress server = InetAddress.getByName(host);
      final InetAddress address = InetAddress.getLocalHost();
      log(String.format("Connecting to RMI server %s:%d from %s", server, port, address));
    } catch (final UnknownHostException e) {
      log("Could not get host name", e);
    }
    final Registry registry = LocateRegistry.getRegistry(host, port);
    final CommandExecutorService cmdExecSvc = (CommandExecutorService) registry
        .lookup(CommandExecutorService.class.getSimpleName());
    return cmdExecSvc;
  }

}
