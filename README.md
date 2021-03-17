# DynamicGroovyClassLoader
A basic dynamic class loader that helps loading groovy scripts and classes as well as java classes and jars at runtime. It'll be extended for your specific use case. In my case for example I added some static Methods for getting default paths to where my scripts or classes are. 
It's prepared to take a custom compiler configuration and always having a parent class loader to ensure that all standard libraries you have on your classpath are available as well. 
