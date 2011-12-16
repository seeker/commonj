package testDummy;

import io.ConnectionPool;
import io.MySQL;
import io.ResourceCreationException;

import java.util.Properties;


public class DummyConnectionPool extends ConnectionPool{
	boolean block = false, filter = false;
	
	public DummyConnectionPool() {
		super(new Properties(), 10); // default values
	}
	
	
	
	public DummyConnectionPool(boolean block, boolean filter) {
		super(new Properties(), 10); // default values
		this.block = block;
		this.filter = filter;
	}



	@Override
	public MySQL getResource() throws InterruptedException,ResourceCreationException {
		return new DummySql(block,filter);
	}
	
	@Override
	public MySQL getResource(long maxWaitMillis) throws InterruptedException,	ResourceCreationException {
		return new DummySql(block,filter);
	}

	@Override
	public void returnResource(MySQL res) {

	}
}