/*--
 * Copyright 2010 Ren� M. de Bloois
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

import java.sql.SQLException;

import org.testng.Assert;
import org.testng.annotations.Test;

public class DeprecatedVersion
{
	static private final String db = "jdbc:hsqldb:mem:testDeprecated";

	@Test
	public void testDeprecated1() throws SQLException
	{
		TestUtil.dropHSQLDBSchema( db, "sa", null );
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch1.sql", db );

		patcher.upgrade( "1.0.2" );
		TestUtil.verifyVersion( patcher, "1.0.2", null, 2, null );

		patcher.end();
	}

	@Test(dependsOnMethods="testDeprecated1")
	public void testDeprecated2()
	{
		UpgradeProcessor patcher = Setup.setupUpgradeProcessor( "testpatch-deprecated-version-1.sql", db );

		try
		{
			patcher.upgrade( "1.0.1" );
			Assert.fail( "Expected a FatalException" );
		}
		catch( FatalException e )
		{
			Assert.assertTrue( e.getMessage().contains( "The current database version 1.0.2 is not available in the upgrade file." ) );
		}

		patcher.end();
	}
}
