package sf.cmdexec.server;

import static sf.cmdexec.client.Utility.log;

import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import sf.cmdexec.client.CommandExecutorService;

public final class ServiceMain
{

  private static final String commandExecutorServiceKey = CommandExecutorService.class.getSimpleName();

  public static void main(final String[] args) throws Exception
  {
    final ServiceMain serviceMain = new ServiceMain(args);
    serviceMain.start();

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

      public void run()
      {
        try {
          serviceMain.stop();
        }
        catch (final Exception e) {
          log("Could not stop service", e);
        }
      }
    }));

    log("Service started");
  }

  private final int port;
  private final int timeout;
  private Registry registry;
  // IMPORTANT: Maintain a strong class-member reference to the CommandExecutorService
  // http://stackoverflow.com/questions/645208/java-rmi-nosuchobjectexception-no-such-object-in-table
  private CommandExecutorService cmdExecSvc;

  public ServiceMain(final String[] args)
  {
    port = (args.length < 1) ? Registry.REGISTRY_PORT : Integer.parseInt(args[0]);
    timeout = (args.length < 2) ? 60 : Integer.parseInt(args[1]);
  }

  public void start() throws Exception
  {
    // Start RMI server
    final InetAddress address = InetAddress.getLocalHost();
    registry = LocateRegistry.createRegistry(port);
    log(String.format("Started RMI registry on %s:%d", address, port));
    log(String.format("Command time-out is set to %d seconds", timeout));

    // Create and bind the command executor
    // IMPORTANT: Maintain a strong class-member reference to the CommandExecutorService
    // http://stackoverflow.com/questions/645208/java-rmi-nosuchobjectexception-no-such-object-in-table
    cmdExecSvc = new CommandExecutorServiceImpl(/*threads*/5, timeout);
    final CommandExecutorService cmdExecStub = (CommandExecutorService) UnicastRemoteObject.exportObject(cmdExecSvc, 0);

    registry.bind(commandExecutorServiceKey, cmdExecStub);
    log(String.format("Bound service with name \"%s\"", commandExecutorServiceKey));

    log("Running...");
  }

  public void stop() throws Exception
  {
    if (registry != null) {
      if (cmdExecSvc != null) {
        cmdExecSvc.shutdown();
      }
      registry.unbind(commandExecutorServiceKey);
      log(String.format("Shut-down and unbound service with name \"%s\"", commandExecutorServiceKey));
      UnicastRemoteObject.unexportObject(registry, true);
      log(String.format("Service stopped"));
    }
    else {
      throw new RuntimeException("Could not stop service, since the registry was not found");
    }
  }

}
