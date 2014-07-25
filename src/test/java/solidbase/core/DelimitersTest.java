package solidbase.core;

import org.testng.Assert;
import org.testng.annotations.Test;

import solidbase.core.Delimiter.Type;

public class DelimitersTest
{
	@Test
	public void testDelimiterRegexpCharacter()
	{
		String contents = "COMMAND\n^\n";
		SQLSource source = new SQLSource( contents );
		source.setDelimiters( new Delimiter[] { new Delimiter( "^", Type.ISOLATED ) } );
		Command command = source.readCommand();
		assert command != null;
		Assert.assertEquals( command.getCommand(), "COMMAND\n" );
	}
}