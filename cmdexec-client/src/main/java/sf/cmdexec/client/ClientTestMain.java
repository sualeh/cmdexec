package sf.cmdexec.cmdexec.client;

import static sf.cmdexec.client.Utility.log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import sf.cmdexec.client.Command;
import sf.cmdexec.client.CommandExecutorClient;
import sf.cmdexec.client.CommandResult;

public class ClientTestMain
{

  public static void main(final String[] args) throws Exception
  {
    final String host = args[0];
    final int port = Integer.parseInt(args[1]);
    try {
      final InetAddress server = InetAddress.getByName(host);
      log(String.format("Connecting to RMI server %s:%d", server, port));

      final InetAddress address = InetAddress.getLocalHost();
      log("Connecting to RMI server from " + address);
    }
    catch (final UnknownHostException e) {
      log("Could not get host name", e);
    }

    final CommandExecutorClient
      cmdExecClient = new CommandExecutorClient(host, port) {};
    final List<CommandResult> commandResults = cmdExecClient.executeCommands(
        new Command("cmd", "/c", "dir", "qw"),
        new Command("cmd", "/c", "dir", "/b", "\\"));
    log(commandResults.toString());
  }

}
