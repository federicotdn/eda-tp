package back;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import back.HyperGraph.HyperEdge;
import back.HyperGraph.Node;


public class GraphSaver {

    public static void toDOT(HyperGraph hGraph) throws IOException{
	
	FileWriter fileOutput = new FileWriter("grafo.dot");
	BufferedWriter writer = new BufferedWriter(fileOutput);
	
	writer.write("digraph prueba {");
	
	for(Node node: hGraph.nodes){
	    writer.write(node.name + " [label=" + node.name + "]");
	    writer.newLine();
	    
	}
	
	for(HyperEdge edge: hGraph.hEdges){
	    writer.write(edge.name +  " [shape=box, height=0.18, fontsize=12, label=" + edge.name + "]");
	    writer.newLine();
	    
	    for(Node node: edge.heads){
		writer.write(node.name +  "->" + edge.name);
		writer.newLine();
	    }
	    
	    for(Node node: edge.tails){
		writer.write(edge.name +  "->" + node.name);
		writer.newLine();
	    }
	    

	}
	writer.write("}");
	writer.close();
    }
}
