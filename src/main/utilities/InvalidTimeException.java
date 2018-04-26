package main.utilities;

/**
 * La clase InvaidTimeException es una clase de utilidad para ser usada en el algoritmo aproximado.
 * Es arrojada cuando el algoritmo se queda sin tiempo para generar respuestas mejores.
 *
 */
public class InvalidTimeException extends Exception
{
	public InvalidTimeException()
	{
		super();
	}
	
	public InvalidTimeException(String s)
	{
		super(s);
	}
	
}
