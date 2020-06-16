package sf.cmdexec.client;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Command
  implements Serializable
{

  private static final long serialVersionUID = -4541891469136348326L;

  private static List<String> parseCommand(final String commandWithArgs)
  {
    final List<String> commandList = new ArrayList<String>();
    if (commandWithArgs == null)
    {
      return commandList;
    }
    final Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
    final Matcher regexMatcher = regex.matcher(commandWithArgs);
    while (regexMatcher.find())
    {
      if (regexMatcher.group(1) != null)
      {
        // Add double-quoted string without the quotes
        commandList.add(regexMatcher.group(1));
      }
      else if (regexMatcher.group(2) != null)
      {
        // Add single-quoted string without the quotes
        commandList.add(regexMatcher.group(2));
      }
      else
      {
        // Add unquoted word
        commandList.add(regexMatcher.group());
      }
    }
    return commandList;
  }

  private final UUID uuid;
  private final String description;
  private final List<String> command;
  private final Map<String, String> environment;
  private final File directory;

  public Command(final String description, final String commandWithArgs)
  {
    if ((description == null) || (description.trim().length() == 0))
    {
      this.description = null;
    }
    else
    {
      this.description = description;
    }

    command = parseCommand(commandWithArgs);

    environment = null;
    directory = null;
    uuid = UUID.randomUUID();
  }

  public Command(final String description,
                 final String command,
                 final List<String> args,
                 final Map<String, String> environment,
                 final File directory)
  {
    if ((description == null) || (description.trim().length() == 0))
    {
      this.description = null;
    }
    else
    {
      this.description = description;
    }

    if ((command == null) || (command.trim().length() == 0))
    {
      throw new IllegalArgumentException("No command provided");
    }
    final List<String> commandList = new ArrayList<String>();
    commandList.add(command);
    if (args != null)
    {
      commandList.addAll(args);
    }
    this.command = commandList;

    this.environment = environment;

    this.directory = directory;

    uuid = UUID.randomUUID();
  }

  public Command(final String description,
                 final String command,
                 final String... args)
  {
    this(description, command, Arrays.asList(args), null, null);
  }

  public List<String> getCommand()
  {
    return command;
  }

  public String getDescription()
  {
    return description;
  }

  public File getDirectory()
  {
    return directory;
  }

  public Map<String, String> getEnvironment()
  {
    return environment;
  }

  public UUID getUuid()
  {
    return uuid;
  }

  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("Command@");
    builder.append(uuid);
    builder.append(" [");
    if (description != null)
    {
      builder.append("\"").append(description).append("\"");
    }
    if (command != null)
    {
      builder.append(command);
    }
    if (directory != null)
    {
      builder.append("\n\tdirectory=");
      builder.append(directory);
    }
    if (environment != null)
    {
      builder.append("\n\tenvironment=");
      builder.append(environment);
      builder.append("\n");
    }
    builder.append("]");
    return builder.toString();
  }

}
