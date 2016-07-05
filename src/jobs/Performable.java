package jobs;

import db.DatabaseException;

public interface Performable {
	
	public void perform() throws DatabaseException;

}
