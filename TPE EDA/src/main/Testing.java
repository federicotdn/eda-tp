package main;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Testing
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		//Metodo main de paquete Main - keep it clean
		
		HyperGraph g = GraphLoader.loadGraph("A.hg");
		
		g.minimumPathExact();
		
//		HyperGraph A = GraphLoader.loadGraph("A.hg");
//		HyperGraph B = GraphLoader.loadGraph("B.hg");
//		HyperGraph en = GraphLoader.loadGraph("enunciado.hg");
	}
}
