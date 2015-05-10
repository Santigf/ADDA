package us.lsi.pd.planificacionProyecto;

import us.lsi.algoritmos.Algoritmos;
import us.lsi.pd.AlgoritmoPD;

public class TestPlanificacionProyecto {
	
	public static String ruta = ".\\ficheros\\";
	// Ruta en MacOS
//	public static String ruta = "./ficheros/";

	public static void main(String[] args) {
		
		ProblemaPlanificacionProyectoPD  p = ProblemaPlanificacionProyectoPD.create(AlgoritmoPD.getRaiz() + "componentes1.txt", 8);		
		AlgoritmoPD.isRandomize = false;
		System.out.println(p.componentes);
		System.out.println("Problema Inicial = " + p);
		AlgoritmoPD<SolucionPlanificacionProyecto, Integer,Object> a = Algoritmos.createPD(p);
		a.ejecuta();		
		a.showAllGraph(ruta + "pruebaPlanificacionProyecto.gv", "PlanificacionProyecto", p);
		System.out.println("Solucion = " + a.getSolucion(p));

	}

}
