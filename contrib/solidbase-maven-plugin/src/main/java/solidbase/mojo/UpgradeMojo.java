package solidbase.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import solidbase.Main;
import solidbase.config.ConfigListener;
import solidbase.config.Configuration;
import solidbase.core.FatalException;
import solidbase.core.Patcher;
import solidbase.core.ProgressListener;
import solidbase.core.SQLExecutionException;

import java.io.File;

/**
 * @author Ruud de Jong
 * @goal upgrade
 * @phase process-resources
 */
public class UpgradeMojo extends AbstractMojo {
	/**
	 * Database driver class.
	 * @parameter expression="${driver}
	 * @required
	 */
	private String driver;

	/**
	 * Database URL.
	 * @parameter expression="${url}
	 * @required
	 */
	private String url;

	/**
	 * Database user.
	 * @parameter expression="${user}
	 * @required
	 */
	private String user;

	/**
	 * Database password.
	 * @parameter expression="${password}
	 * @required
	 */
	private String password;

	/**
	 * File containing the upgrade.
	 * @parameter expression="${upgradefile}
	 * @required
	 */
	private String upgradefile;

	/**
	 * Target to upgrade the database to.
	 * @parameter expression="${target}
	 * @required
	 */
	private String target;

	/**
	 * The Maven Project Object
	 *
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;
	private boolean downgradeallowed;

	public void execute() throws MojoExecutionException, MojoFailureException {
		validate();

		Progress progress = new Progress( getLog() );

		Configuration configuration = new Configuration( progress );

		getLog().info("SolidBase v" + configuration.getVersion());
		getLog().info("(C) 2006-2009 René M. de Bloois");
		getLog().info("");

		try
		{
			Patcher.setCallBack( progress );

			Patcher.setDefaultConnection( new solidbase.core.Database( this.driver, this.url, this.user, this.password ) );

			Patcher.addConnection( new solidbase.config.Connection( "Some name", this.driver, this.url, this.user, this.password ) );

			progress.info( "Connecting to database..." );

			progress.info( Main.getCurrentVersion() );

			Patcher.openPatchFile( this.project.getBasedir(), this.upgradefile );
			try
			{
				if( this.target != null )
					Patcher.patch( this.target, this.downgradeallowed ); // TODO Print this target
				else
					throw new UnsupportedOperationException();
				progress.info( "" );
				progress.info( Main.getCurrentVersion() );
			}
			finally
			{
				Patcher.closePatchFile();
			}
		}
		catch( SQLExecutionException e )
		{
			throw new MojoExecutionException( e.getMessage() );
		}
		catch( FatalException e )
		{
			throw new MojoExecutionException( e.getMessage() );
		}
		finally
		{
			Patcher.end();
		}
	}

	private void validate() throws MojoExecutionException {
		if( this.driver == null )
			throw new MojoExecutionException( "The 'driver' attribute is mandatory." );
		if( this.url == null )
			throw new MojoExecutionException( "The 'url' attribute is mandatory." );
		if( this.user == null )
			throw new MojoExecutionException( "The 'user' attribute is mandatory." );
		if( this.password == null )
			throw new MojoExecutionException( "The 'password' attribute is mandatory." );
		if( this.upgradefile == null )
			throw new MojoExecutionException( "The 'upgradefile' attribute is mandatory." );
		if( this.target == null )
			throw new MojoExecutionException( "The 'target' attribute is mandatory." );
	}
}