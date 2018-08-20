package server.core.command;

/**
 * All CommandsRequest implements this Interface. CommandRequest are sendet form
 * the View to request something out of the System.
 */
public interface RequestCommand {

	/**
	 * This is the Execution form the requested Command
	 */
	public void execute();

	/**
	 * This Method Return the Requested Object
	 */
	public void getObject();

}