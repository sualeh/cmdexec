package us.fatehi.cmdexec.client;

import static us.fatehi.cmdexec.client.Utility.log;

import java.io.File;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public final class CommandlineCommandExecutorClient extends CommandExecutorClient {

  public static void main(final String[] args) throws Exception {
    final String host;
    final int port;
    if (args.length == 0) {
      host = null; // localhost
      port = Registry.REGISTRY_PORT;
    } else if (args.length == 1) {
      final String[] rmiserver = args[0].split(":");
      if (rmiserver.length == 2) {
        host = rmiserver[0];
        port = Integer.parseInt(rmiserver[1]);
      } else {
        throw new IllegalArgumentException("host:port");
      }
    } else {
      throw new IllegalArgumentException("host:port");
    }

    final CommandlineCommandExecutorClient client = new CommandlineCommandExecutorClient(host, port);
    List<CommandResult> commandResults;
    final Scanner in = new Scanner(System.in);
    String command = null;

    while (!"exit".equals(command)) {
      System.out.print("Command: ");
      command = in.nextLine();
      try {
        commandResults = client.executeCommands(new Command("Command", command));
        for (final CommandResult commandResult : commandResults) {
          if (commandResult.isErroredOut()) {
            log(commandResult.toString());
          } else {
            System.out.println(commandResult.getProcessOutput());
          }
        }
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
    in.close();
  }

  public CommandlineCommandExecutorClient(final String host, final int port) throws Exception {
    super(host, port);
  }

  protected List<CommandResult> dirTest() throws Exception {
    return executeCommands(new Command("Directory listing for non-existing directory", "cmd", "/c", "dir", "qw"),
        new Command("Current working directory", "cmd", "/c", "cd"), new Command("Directory listing for root directory",
            "cmd", Arrays.asList(new String[] { "/c", "dir", "/b" }), null, new File("\\")));
  }

}
