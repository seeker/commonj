package io;

import java.sql.SQLException;

public class SchemaUpdateException extends SQLException {
	private static final long serialVersionUID = 1L;
	String message;
	
	public SchemaUpdateException(String message){
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
