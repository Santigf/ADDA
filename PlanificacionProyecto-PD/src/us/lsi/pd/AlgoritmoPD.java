package us.lsi.pd;



import java.util.*;
import java.util.stream.Collectors;

import us.lsi.algoritmos.AbstractAlgoritmo;
import us.lsi.common.Lists2;
import us.lsi.pd.ProblemaPD.Tipo;

import com.google.common.collect.*;

/**
 * <p> Algoritmo que implementa la t�cnica de Programaci�n Din�mica con sus variantes. 
 * Un problema que se quiera resolver con esta t�cnica debe implementar el interface ProblemaPD &lt; S,A &gt; y asumimos que 
 * queremos minimizar la propiedad getObjetivo() </p>
 * 
 * <p>La documentaci�n puede encontarse en el: <a href="../../../document/Tema14.pdf" target="_blank">Tema14</a></p>
 * 
 * 
 * @author Miguel Toro
 *
 * @param <S> El tipo de la soluci�n
 * @param <A> El tipo de la alternativa
 * @param <T> El tipo de la solucion parcial
 */
public class AlgoritmoPD<S,A,T> extends AbstractAlgoritmo {
       
	/**
	 * Si se quiere aplicar la t�cnica aleatoria para escoger una de las alternativas
	 */
	public static boolean isRandomize = false;
	/**
	 * Tama�o umbral a partir del cual se escoge aleatoriamente una de las alternativas
	 */
	public static Integer sizeRef = 10;
	
	public Map<ProblemaPD<S,A ,T>,Sp<A, T>> solucionesParciales;
	private ProblemaPD<S,A ,T> problema;    
    private Iterable<ProblemaPD<S,A ,T>> problemas;
    private static Double mejorValor = Double.MAX_VALUE;
    private static Tipo tipo;

    
    private boolean isMin(){
    	return tipo.equals(Tipo.Min);
    }
    
    private boolean isMax(){
    	return tipo.equals(Tipo.Max);
    }
    
    /**
	 * @return El mejor valor de la propiedade objetivo del problema inicial encontrado por el algoritmo hasta este momento
	 */
	public static Double getMejorValor() {
		return mejorValor;
	}
    
	public AlgoritmoPD(Iterable<ProblemaPD<S,A ,T>> ps){		
	    problema = Iterables.get(ps, 0);
	    problemas = ps;
	    tipo = problema.getTipo();
	    mejorValor = isMin()? Double.MAX_VALUE: Double.MIN_VALUE;  
	}
	
	public void ejecuta() {
		
		do {
			solucionesParciales = Maps.newHashMap();
			for (ProblemaPD<S, A ,T> p : problemas) {					
					pD(p);				
			}		
		} while (isRandomize && solucionesParciales.get(problema)==null);			
	}
	
	private Iterable<A> randomize(ProblemaPD<S,A ,T> p, Iterable<A> alternativas){
		Iterable<A> alt;
		if(isRandomize && p.size()>sizeRef){
			List<A> ls = Lists.newArrayList(alternativas);			
			alt = Lists2.randomUnitary(ls);
		}else{
			alt = alternativas;
		}
		return alt;
	}
	
	private void actualizaMejorValor(ProblemaPD<S,A ,T> p){
		Double objetivo = p.getObjetivo();			
		if(isMin() && objetivo < mejorValor || isMax() && objetivo > mejorValor) {
			mejorValor = objetivo;
		}
	}
	
	private Sp<A,T> pD(ProblemaPD<S,A ,T> p){
		Sp<A,T> e = null;	
		if (solucionesParciales.containsKey(p)){
			e = solucionesParciales.get(p);
		} else if( p.esCasoBase()) {
			e = p.getSolucionCasoBase();	        
			solucionesParciales.put(p, e); 					
		} else {
			List<Sp<A,T>> solucionesDeAlternativas = Lists.newArrayList(); 
			for(A a: randomize(p,p.getAlternativas())){
				if(isMin() && p.getObjetivoEstimado(a) >= mejorValor) continue;
				if(isMax() && p.getObjetivoEstimado(a) <= mejorValor) continue;
				int numeroDeSubProblemas = p.getNumeroSubProblemas(a);
				List<Sp<A ,T>> solucionesDeSubProblemas = Lists.newArrayList();  
				boolean haySolucion = true;
				for(int i = 0; i < numeroDeSubProblemas; i++){
					ProblemaPD<S,A, T> pr = p.getSubProblema(a,i); 				
					Sp<A, T> sp = pD(pr);
					if(sp==null) { haySolucion=false; break;}					
					solucionesDeSubProblemas.add(sp);   	    		
				}
				Sp<A, T> sa = haySolucion?p.combinaSolucionesParciales(a, solucionesDeSubProblemas): Sp.create(a,null,null);
				solucionesDeAlternativas.add(sa);
			}
			solucionesDeAlternativas = solucionesDeAlternativas.stream().filter(x -> x.propiedad != null).collect(Collectors.toList());
			if (!solucionesDeAlternativas.isEmpty()) {
				e = p.seleccionaAlternativa(solucionesDeAlternativas);
			}
			if(e!=null) {
				e.solucionesDeAlternativas = solucionesDeAlternativas;
			}
			solucionesParciales.put(p, e); 			
		}
		actualizaMejorValor(p);	
		return e;
	}
	
	/**
	 * @param pd - 
	 * @return Si pd es un subproblema encontrado al resolver el problema inicial
	 */
	public boolean isSubproblema(ProblemaPD<S,A, T> pd){
		return this.solucionesParciales.containsKey(pd);
	}
	
	/**
	 * @return N�mero de subproblemas encontrado al resolver el problema inicial
	 */
	public int getNumeroDeSubproblemas(){
		return this.solucionesParciales.keySet().size();
	}
	
	/**
	 * @param pd - Problema del que se quiere obtener la soluci�n parcial
	 * @return Soluci�n parcial del problema o null si no tiene soluci�n o no ha ha sido encontrado por el algoritmo
	 */
	public Sp<A, T> getSolucionParcial(ProblemaPD<S,A ,T> pd) {
		Sp<A, T> e = null;		
		if(solucionesParciales.containsKey(pd)){
			e = solucionesParciales.get(pd);
		}
		return  e;
	}
	
	/**
	 * @param pd - Problema del que se quiere obtener la soluci�n
	 * @return Soluci�n del problema o null si no tiene soluci�n o no ha ha sido encontrado por el algoritmo
	 */
	public S getSolucion(ProblemaPD<S, A, T> pd) {	
		S s = null;
		if (solucionesParciales.containsKey(pd)) {
			Sp<A, T> e = solucionesParciales.get(pd);
			if (e != null) {
				if (pd.esCasoBase()) {					
					s = pd.getSolucionReconstruida(e);
				} else if (e.alternativa != null) {
					List<S> soluciones = Lists.<S> newArrayList();
					for (int i = 0; i < pd.getNumeroSubProblemas(e.alternativa); i++) {
						soluciones.add(getSolucion(pd.getSubProblema(e.alternativa, i)));
					}
					s = pd.getSolucionReconstruida(e, soluciones);
				} else if (e.alternativa == null) {
					List<S> solucionesAlternativas = Lists.<S> newArrayList();
					for (Sp<A, T> e1 : e.solucionesDeAlternativas) {
						List<S> soluciones = Lists.<S> newArrayList();
						if(e1.propiedad == null) continue;
						for (int i = 0; i < pd.getNumeroSubProblemas(e1.alternativa); i++) {
							soluciones.add(getSolucion(pd.getSubProblema(e1.alternativa, i)));
						}
						s = pd.getSolucionReconstruida(e1, soluciones);
						solucionesAlternativas.add(s);
					}
					s = pd.getSolucionReconstruida(e,solucionesAlternativas);
				}
			}
		}
		return s;
	}

	/**
	 * @param nombre - Fichero d�nde se almacenar� el grafo para ser representado
	 * @param titulo - T�tulo del gr�fico
	 * @param pd - Problema y sus subproblemas que forman el grafo
	 */
	public void showAllGraph(String nombre,String titulo,ProblemaPD<S,A, T> pd){
		super.setFile(nombre);
		super.getFile().println("digraph "+titulo+" {  \n size=\"100,100\"; ");		
		showAll(pd);
		super.getFile().println("}");
	}
	
	private void marcarEnSolucion(ProblemaPD<S,A, T> pd){
		if(solucionesParciales.containsKey(pd)){
			Sp<A, T> e = solucionesParciales.get(pd);		
			if(e!=null){
				e.estaEnLaSolucion =true;
				A alternativa = e.alternativa;			
				if (!pd.esCasoBase()) {
					for (int i = 0; i < pd.getNumeroSubProblemas(alternativa); i++) {
						ProblemaPD<S, A, T> pds = pd.getSubProblema(
								alternativa, i);
						marcarEnSolucion(pds);
					}
				}	
			}
		}
	}

	private String problema(ProblemaPD<S,A, T> p, Sp<A, T> e){
		String s= "    "+"\""+p+"\"";
		if(e!=null){
			s = s+" [shape=box]";
		} else{
			s = s+" [shape=diamond]";
		}
		return s+";";
	}
	
	private String alternativa(ProblemaPD<S,A, T> p, A alternativa){
		String s = "    "+"\""+p+","+alternativa+"\""+" [label="+alternativa+"]";
		return s+";";
	}
	
	private String aristaProblemaToAlternativa(ProblemaPD<S,A, T> p, A alternativa, Sp<A, T> e){
		String s = "    "+"\""+p+"\""+" -> "+"\""+p+","+alternativa+"\"";
		if(e.estaEnLaSolucion && e.alternativa.equals(alternativa)){
			s = s+ "[style=bold,arrowhead=dot]";
		}
		return s+";";
	}
	
	private String aristaAlternativaToProblema(ProblemaPD<S,A, T> p, A alternativa, ProblemaPD<S,A, T> ps, Sp<A, T> e){
		String s = "    "+"\""+p+","+alternativa+"\""+" -> "+"\""+ps+"\"";
		if(e.estaEnLaSolucion && e.alternativa.equals(alternativa)){
			s = s+ "[style=bold,arrowhead=dot]";
		}
		return s+";";
	}


	private void showAll(ProblemaPD<S,A, T> p){		
		if (solucionesParciales.get(p).alternativa!=null) {
			marcarEnSolucion(p);
		}
		for(ProblemaPD<S,A, T> pd:solucionesParciales.keySet()){			
			Sp<A, T> e = solucionesParciales.get(pd);
			if(e!=null)super.getFile().println(problema(pd,e));
			if(e!=null && e.solucionesDeAlternativas!=null){			
				for(Sp<A, T> solParAlt:e.solucionesDeAlternativas){			
					super.getFile().println(alternativa(pd,solParAlt.alternativa));
					super.getFile().println(aristaProblemaToAlternativa(pd,solParAlt.alternativa,e));
					for(int i = 0; i < pd.getNumeroSubProblemas(solParAlt.alternativa); i++){	
						ProblemaPD<S,A, T> pds= pd.getSubProblema(solParAlt.alternativa,i);
						if(solucionesParciales.get(pds)==null)super.getFile().println(problema(pds,null));						
						super.getFile().println(aristaAlternativaToProblema(pd,solParAlt.alternativa,pds,e));
					}
				}
			}
		}
	}
	
	/**
	 * Un tipo dise�ado para representar soluciones parciales a partir de las cuales 
	 * se puede reconstruir la soluci�n del problema. 
	 * Esta formado por un par: una alternativa y el valor de una propiedad. La soluci�n del problema 
	 * es la que se obtendr�a tomando la alternativa y el valor estar�a en la propiedad
	 * 
	 * El valor null para este tipo representar� la no existencia de soluci�n
	 * 
	 * @param <A1> Tipo de la alternativa
	 */
	public static class Sp<A1,T1> implements Comparable<Sp<A1,T1>> {
		

		public A1 alternativa;
		public Double propiedad;
		public T1 solucionParcial;
		private List<Sp<A1,T1>> solucionesDeAlternativas = null; 
		private boolean estaEnLaSolucion = false;
					
		public static <A2> Sp<A2,Object> create(A2 alternativa,Double propiedad){
			return new Sp<A2,Object>(alternativa, propiedad, null);
		}
		
		public static <A2,T2> Sp<A2,T2> create(A2 alternativa,Double propiedad, T2 solucionParcial){
			return new Sp<A2,T2>(alternativa, propiedad,solucionParcial);
		}
		
		protected Sp(A1 alternativa, Double propiedad, T1 solucionParcial) {
			super();
			this.alternativa = alternativa;
			this.propiedad = propiedad;	
			this.solucionParcial = solucionParcial;
		}		

		@Override
		public String toString(){
			String r = this.solucionParcial==null? ")" : ","+this.solucionParcial+")";
			return "("+alternativa+","+propiedad+ r;
		}

		@Override
		public int compareTo(Sp<A1,T1> ob) {
			return this.propiedad.compareTo(ob.propiedad);
		}
		
	}	
	
}
