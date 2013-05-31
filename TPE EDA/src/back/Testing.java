package back;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Testing
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		HyperGraph g = GraphLoader.loadGraph("A.hg");
		
		GraphSaver.toDOT(g);
	}
}
