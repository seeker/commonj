package io;


public class SchemaUpdateException extends Exception {
	private static final long serialVersionUID = 1L;

	public SchemaUpdateException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SchemaUpdateException(String reason, Throwable cause) {
		super(reason, cause);
	}

	public SchemaUpdateException(String reason) {
		super(reason);
	}

	public SchemaUpdateException(Throwable cause) {
		super(cause);
	}
}
