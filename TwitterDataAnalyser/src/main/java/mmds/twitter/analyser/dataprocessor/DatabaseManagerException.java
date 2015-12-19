package mmds.twitter.analyser.dataprocessor;

/**
 *
 * @author: ssnk
 */

public class DatabaseManagerException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public DatabaseManagerException() {
	}

	public DatabaseManagerException(Throwable cause) {
		super(cause);
	}

	public DatabaseManagerException(String message) {
		super(message);
	}

	public DatabaseManagerException(String message, Throwable cause) {
		super(message, cause);
	}
}
