package io;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SchemaUpdaterTest {
	MySQL sql;
	Properties local;

	@Before
	public void setUp() throws Exception {
		sql = mock(MySQL.class);
		local = new Properties();
		local.put(DBsettings.SchemaVersion.toString(), "3");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test(expected=SchemaUpdateException.class)
	public void testInvalidLocal() throws SchemaUpdateException {
		local = new Properties();
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("1");
		SchemaUpdater.update(sql, local);
	}

	@Test(expected=SchemaUpdateException.class)
	public void testInvalidVersion1() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("-1");
		SchemaUpdater.update(sql, local);
	}
	
	@Test(expected=SchemaUpdateException.class)
	public void testInvalidVersion2() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("0");
		SchemaUpdater.update(sql, local);
	}
	
	@Test(expected=SchemaUpdateException.class)
	public void testInvalidVersion3() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("a");
		SchemaUpdater.update(sql, local);
	}
	
	@Test
	public void testUpdate() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("1");
		SchemaUpdater.update(sql, local);
		verify(sql,times(1)).sendStatement(anyString());
	}
	
	@Test
	public void testNoUpdate() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("3");
		SchemaUpdater.update(sql, local);
		verify(sql,times(0)).sendStatement(anyString());
	}
	
	@Test(expected=SchemaUpdateException.class)
	public void testRemoteIsNewer() throws SchemaUpdateException {
		when(sql.getSetting(DBsettings.SchemaVersion)).thenReturn("4");
		SchemaUpdater.update(sql, local);
	}

}
