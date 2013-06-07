package front;

import java.io.FileNotFoundException;
import java.io.IOException;

import main.GraphLoader;
import main.GraphSaver;
import main.HyperGraph;
import main.InvalidTimeException;
import main.algorithms.MinimumPathApproxAlgorithm;
import main.algorithms.MinimumPathExactAlgorithm;

/**
 * La clase GraphSolver compone el front end del proyecto. 
 *
 */
public class GraphSolver
{
	static final String exactArg = "exact";
	static final String approxArg = "approx";
	
	/**
	 * 
	 * Metodo <code>main</code> del proyecto.  Éste metodo recibe los argumentos especificados por el usuario,
	 * e invoca a la funcion correspondiente, luego de cargar el grafo.
	 * 
	 * @param args - Parametros enviados via linea de comandos.
	 */
	public static void main(String[] args)
	{
		if (args.length < 2 || args.length > 3)
		{
			printInstructions();
			return;
		}
		
		String graphName = args[0];
		HyperGraph graph = null;
		
		try
		{
			graph = GraphLoader.loadGraph(graphName);
			System.out.println("Archivo \"" + graphName + "\" cargado.");
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Archivo \"" + graphName + "\" no encontrado.");
			return;
		}
		catch (IOException e)
		{
			System.out.println(e.toString());
			return;
		}
		
		String solveMode = args[1];
		
		if (solveMode.equals(exactArg))
		{
			System.out.println("Calculando...");
			
			//Se llama al algoritmo exacto, y se imprime el peso del camino.
			try
			{
				double result = MinimumPathExactAlgorithm.execute(graph);
				System.out.println("El camino minimo pesa: " + result);
			}
			catch (OutOfMemoryError e)
			{
				System.out.println("Error: memoria insuficiente");
				return;
			}
		}
		else if (solveMode.equals(approxArg))
		{
			if (args.length != 3)
			{
				printInstructions();
				return;
			}
			
			try
			{
				int seconds = Integer.valueOf(args[2]);
				
				//Se llama al algoritmo aproximado, y se imprime el paso del camino creado.
				
				System.out.println("Calculando...");
				double result = MinimumPathApproxAlgorithm.execute(graph, seconds);
				System.out.println("El camino minimo es (aproximadamente): " + result);
				
			}
			catch (NumberFormatException e) 
			{
				System.out.println("Error: \"" + args[2] + "\" no es una cantidad de segundos valida.");
				return;
			}
			catch (InvalidTimeException e) 
			{
				System.out.println(e.getMessage());
				return;
			}
		}
		else
		{
			printInstructions();
			return;
		}
		
		try
		{
			GraphSaver.createGraphFiles(graph);
		}
		catch (IOException e)
		{
			System.out.println("Error al intentar crear los archivos.");
		}
	}
	
	private static void printInstructions()
	{
		System.out.println("Uso:");
		System.out.println("	java –jar tpe.jar [nombre de grafo] [modo] [tiempo]");
	}
}
