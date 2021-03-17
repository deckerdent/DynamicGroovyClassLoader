import java.io.File
import groovy.lang.GroovyClassLoader
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.codehaus.groovy.control.CompilerConfiguration;
import groovy.lang.Binding
import java.io.FileNotFoundException

class DynamicGroovyClassLoader extends GroovyClassLoader {

	private GroovyShell gs
	private Binding bdg
	/*
	 * @constructor
	 * @args: none
	 *
	 * @description:	the default constructor is hidden from the user as we hardly need a parent and a binding
	 *
	*/
	private DynamicClassLoader(){

	}
	/*
	 * @constructor
	 * @args: 			Classloader parent, Binding binding
	 *
	 * @description: 	builds a new DynamicClassloader with a standard IX CompilerConfig
	*/
	public DynamicClassLoader( ClassLoader parent, Binding binding ){
		super( parent, DynamicClassLoader.createDefaultCompilerConfiguration() )
		this.bdg = binding
		//this.addDefaultClassPath()
	}

	/*
	 * @constructor
	 * @args: 			Binding binding
	 *
	 * @description: 	builds a new DynamicClassloader with a standard IX CompilerConfig
	*/
	public DynamicClassLoader( Binding binding ){
		super( Thread.currentThread().getContextClassLoader(), DynamicClassLoader.createDefaultIXCompilerConfiguration() )
		this.bdg = binding
		this.addDefaultClassPath()
	}

	/*
	 * @args: 			String name, Object[] constructorParams
	 * @return: 		Object
	 * @description: 	returns an instance of a class. name must be the full qualifing name of the class including package declaration.
	 * 					Works only for simple classes. But not for e.g. Singleton Patterns or other static content. Constructor must be public.
	*/
	public Object newObject( String name, Object... constructorParams ){
		if( constructorParams == null || constructorParams.length == 0 ) return newObjectWithEmptyConstructor( name )
		Class[] constructorClasses = new Class[ constructorParams.length ]

		for( int i = 0; I < constructorParams.length; i++){
			constructorClasses[i] = o.getClass()
		}

		Class<?> clazz = super.loadClass( name )
		Object instanceOfClazz = clazz.getDeclaredConstructor( constructorClasses ).newInstance( constructorParams )

		return instanceOfClazz

	}

	/*
	 * @args: 			String pathToScript
	 * @return: 		Object
	 * @throws:			FileNotFoundException
	 * @description: 	runs a script from the current folder
	*/
	public Object runScript( String pathToScript ) throws FileNotFoundException{
		if( this.gs == null ) instanciateGroovyShell()
		File ixScriptFile = new File( pathToScript )
		this.gs.getClassLoader().clearCache()
		Script script = ( IxGroovyScript ) this.gs.parse( ixScriptFile )
		IxGroovyScript ixScript = ( IxGroovyScript ) script
		Object result = ixScript.run()

		return result
	}

	/*
	 * @args: 			none
	 * @description: 	Getter for GroovyShell property
	*/
	public GroovyShell getGroovyShell(){
		if( this.gs == null ) instanciateGroovyShell()
		return this.gs
	}

	/*
	 * @args: 			  none
	 * @description: 	Getter for Binding
	*/
	public Binding getBinding(){
		return this.bdg != null ? this.bdg : null
	}

	/*
	 * @args: 			none
	 * @description: 	get the classpath as String[]
	*/
	public String[] getClassPath(){
		return super.getClassPath()
	}
	/*
	 * @args: 			  none
	 * @description: 	get a Class. Name must be full qualifing binary name.
	*/
	public Class findClass( String name ){
		return super.findClass( name )
	}

	/*
	 * @args: 			String name
	 * @return: 		Object
	 * @description: 	returns an instance of a class. name must be the full qualifing name of the class including package declaration.
	 * 					Works only for simple classes. But not for e.g. Singleton Patterns. Constructor must be public.
	*/
	private Object newObjectWithEmptyConstructor( String name ){
		Class<?> clazz = super.loadClass( name )
		Object instanceOfClazz = clazz.newInstance()

		return instanceOfClazz
	}

	/* REMOVE COMMENTS TO USE
	 * @args: none
	 * @return: none
	 * @description: adds the default paths to the classpath
	
	private void addDefaultClassPath(){
		String parentDirectory = "lib/custom/classes/"

    try{
			URL path = new File( parentDirectory ).toURI().toURL()
			super.addURL( path )
		}catch( Exception e){
			//do something
		}
	}
  */
	/*
	 * @args: 			none
	 * @return: 		none
	 * @description: 	instaciates the objects groovyshell with this object as parent classloader and the binding property as binding
	*/
	private void instanciateGroovyShell(){
		super.clearCache()
		this.gs = new GroovyShell( super, this.bdg, DynamicClassLoader.createDefaultIXCompilerConfiguration() )
	}

	/*
	 * @args: none
	 * @return: CompilerConfiguration
	 * @see(link)
	 *
	 * description: returns a custom compiler configuration which forces the encoding to be UTF-8 and the ScriptBaseClass to be of type "IxGroovyScript"
	*/
	private static CompilerConfiguration createDefaultIXCompilerConfiguration(){
		CompilerConfiguration ccfg = new CompilerConfiguration()
		ccfg.setSourceEncoding("UTF-8");
    //TODO 
    //change base script or add other compiler configurations, depending on your needs
		ccfg.setScriptBaseClass(Script.class.getName());

		return ccfg
	}

}
