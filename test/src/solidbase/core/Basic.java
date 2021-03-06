/*--
 * Copyright 2006 Ren� M. de Bloois
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package solidbase.core;

import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

public class Basic
{
	static private final String db = "jdbc:hsqldb:mem:testBasic";

	@Test
	public void testBasic() throws SQLException
	{
		TestUtil.dropHSQLDBSchema( db, "sa", null );
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch1.sql", db );

		Set< String > targets = patcher.getTargets( false, null, false );
		assert targets.size() > 0;
		patcher.upgrade( "1.0.2" );
		TestUtil.verifyVersion( patcher, "1.0.2", null, 2, null );

		patcher.end();
	}

	@Test(dependsOnMethods="testBasic")
	public void testRepeat() throws SQLException
	{
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch1.sql", db );

		TestUtil.verifyVersion( patcher, "1.0.2", null, 2, null );
		patcher.upgrade( "1.0.2" );
		TestUtil.verifyVersion( patcher, "1.0.2", null, 2, null );

		patcher.end();
	}

	@Test(dependsOnMethods="testRepeat")
	public void testMissingGo() throws SQLException
	{
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch2.sql", db );

		try
		{
			patcher.upgrade( "1.0.3" );
			Assert.fail();
		}
		catch( SQLExecutionException e )
		{
//			System.out.println( e.getMessage() );
			Assert.assertTrue( e.getMessage().contains( "unexpected token: /" ) );
		}
		TestUtil.verifyVersion( patcher, "1.0.2", null, 2, null );

		patcher.end();
	}

	@Test(dependsOnMethods="testMissingGo")
	public void testDumpXML()
	{
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch1.sql", db );

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		patcher.logToXML( out );
//		String xml = out.toString( "UTF-8" );
//		System.out.println( xml );

		patcher.end();
	}

	@Test(dependsOnMethods="testMissingGo")
	public void testOverrideControlTables()
	{
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch-overridecontroltables.sql" );

		assert patcher.getCurrentVersion() == null;
		patcher.upgrade( "1.0.1" );
		assert patcher.getCurrentVersion().equals( "1.0.1" );

		patcher.end();
	}

	@Test
	public void testOpen() throws SQLException
	{
		TestUtil.dropHSQLDBSchema( "jdbc:hsqldb:mem:testdb", "sa", null );
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch-open.sql" );

		patcher.upgrade( "1.0.*" );
		TestUtil.verifyVersion( patcher, "1.0.2", "1.0.3", 1, null );

		patcher.end();
	}

	@Test(expectedExceptions=NonDelimitedStatementException.class)
	public void testUnterminatedCommand1() throws SQLException
	{
		TestUtil.dropHSQLDBSchema( "jdbc:hsqldb:mem:testdb", "sa", null );
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch-unterminated1.sql" );

		try
		{
			patcher.upgrade( "1.0.1" );
		}
		finally
		{
			TestUtil.verifyVersion( patcher, null, "1.0.1", 1, null );
			patcher.end();
		}
	}

	@Test(expectedExceptions=NonDelimitedStatementException.class)
	public void testUnterminatedCommand2() throws SQLException
	{
		TestUtil.dropHSQLDBSchema( "jdbc:hsqldb:mem:testdb", "sa", null );
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch-unterminated2.sql" );

		try
		{
			patcher.upgrade( "1.0.1" );
		}
		finally
		{
			TestUtil.verifyVersion( patcher, null, "1.0.1", 1, null );
			patcher.end();
		}
	}

	// TODO Create test that failes immediately and check that the target is still null

	@Test
	public void testSharedPatchBlock() throws SQLException
	{
		TestUtil.dropHSQLDBSchema( "jdbc:hsqldb:mem:testdb", "sa", null );
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch-sharedpatch1.sql" );

		patcher.upgrade( "1.0.2" );
		TestUtil.verifyVersion( patcher, "1.0.2", null, 3, null );

		patcher.end();
	}

	@Test
	public void testMultipleTargets() throws SQLException
	{
		TestUtil.dropHSQLDBSchema( "jdbc:hsqldb:mem:testdb", "sa", null );
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch-multipletargets.sql" );

		try
		{
			patcher.upgrade( "1.0.*" );
			assert false;
		}
		catch( FatalException e )
		{
			assert e.getMessage().startsWith( "More than one possible target found for" );
		}

		patcher.end();
	}

	//@Test TODO This is a test for INIT CONNECTION
	public void testConnectionSetup() throws SQLException
	{
		TestUtil.dropHSQLDBSchema( "jdbc:hsqldb:mem:testdb", "sa", null );
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch-connectionsetup1.sql" );
		patcher.databases.addDatabase( new Database( "queues", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:testdb", "sa", null, patcher.getProgressListener() ) );

		patcher.upgrade( "1.0.1" );
		TestUtil.verifyVersion( patcher, "1.0.1", null, 1, "1.1" );

		patcher.end();
	}

	//@Test TODO This is a test for INITIALIZATION
	public void testInitialization() throws SQLException
	{
		TestUtil.dropHSQLDBSchema( "jdbc:hsqldb:mem:testdb", "sa", null );
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch-initialization.sql" );
		patcher.databases.addDatabase( new Database( "queues", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:testdb", "sa", null, patcher.getProgressListener() ) );

		patcher.upgrade( "1.0.1" );
		TestUtil.verifyVersion( patcher, "1.0.1", null, 1, "1.1" );

		patcher.end();
	}

	// @Test TODO
	public void testSetUser() throws SQLException
	{
		TestUtil.dropHSQLDBSchema( "jdbc:hsqldb:mem:testdb", "sa", null );
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch-setuser.sql" );

		patcher.upgrade( null );
		patcher.end();
	}

	@Test
	public void testJdbcEscaping() throws SQLException
	{
		TestUtil.dropHSQLDBSchema( "jdbc:hsqldb:mem:testdb", "sa", null );
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch-jdbc-escaping.sql" );

		try
		{
			patcher.upgrade( "2" );
			assert false;
		}
		catch( SQLExecutionException e )
		{
			assert e.getMessage().contains( "42582" );
			assert e.getMessage().contains( "unknown token" );
		}

		patcher.upgrade( "3" );

		patcher.end();
	}
}
