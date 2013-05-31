package back;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Testing
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		HyperGraph g = GraphLoader.loadGraph("generated2.hg");
		
		AltGraph a = new AltGraph(g);
	
		a.exactAlgorithm();
		
		//GraphSaver.toDOT(g);
		
		//g.name = g.name + "Solved";
		//GraphSaver.toDOTwithPath(g);
	}
}
