package interfaces;

import java.sql.SQLException;

/**
 * @author CatDevil
 *
 */

public interface DataWork 
{
	public void loadData();
	public void addData() throws SQLException;
	public void changeData() throws SQLException;
	public void deleteData() throws SQLException;
	public void saveData() throws SQLException;
}
