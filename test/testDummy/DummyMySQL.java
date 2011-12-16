package testDummy;

import filter.FilterItem;
import io.MySQL;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Properties;

public class DummySql extends MySQL{
	boolean blacklisted, filter;

	public DummySql(boolean blacklisted,boolean filter) {
		super(new Properties());
		this.blacklisted = blacklisted;
		this.filter = filter;
	}

	@Override
	public void addHash(String hash, String path, long size) throws SQLException {
		// do nothing
	}
	
	@Override
	public boolean isBlacklisted(String hash) {
		return blacklisted;
	}
	
	@Override
	public boolean isDnw(String hash) {
		return filter;
	}
	
	@Override
	public boolean isArchived(String hash) {
		return filter;
	}
	
	@Override
	public boolean isHashed(String hash) {
		return filter;
	}
	
	@Override
	public void delete(String table, String id) {
		// do nothing
	}
	
	@Override
	public LinkedList<FilterItem> getPendingFilters() {
		return new LinkedList<FilterItem>();
	}
	
}