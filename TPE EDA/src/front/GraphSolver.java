package front;

import java.io.FileNotFoundException;
import java.io.IOException;

import main.GraphLoader;
import main.HyperGraph;

public class GraphSolver
{
	static final String exactArg = "exact";
	static final String approxArg = "approx";
	
	public static void main(String[] args)
	{
		if (args.length < 2 || args.length > 3)
			printInstructions();
		
		String graphName = args[0];
		HyperGraph graph = null;
		
		try
		{
			graph = GraphLoader.loadGraph(graphName);
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
		
		if (solveMode == exactArg)
		{
			//LLamar al algoritmo exacto
			double result = graph.minimumPathExact();
			System.out.println("El camino minimo pesa: " + result);
		}
		else if (solveMode == approxArg)
		{
			if (args.length != 3)
				printInstructions();
			
			try
			{
				int seconds = Integer.valueOf(args[2]);
				//LLamar al algoritmo aproximado
				
				double result = graph.minimumPathApprox(seconds);
				System.out.println("El camino minimo es (aproximadamente): " + result);
				
			}
			catch(NumberFormatException e) 
			{
				System.out.println("Error: \"" + args[2] + "\" no es una cantidad de segundos valida.");
				return;
			}
		}
		else
			printInstructions();
		
	}
	
	private static void printInstructions()
	{
		System.out.println("Uso:");
		System.out.println("	java –jar tpe.jar [nombre de grafo] [modo] [tiempo]");
	}
}
