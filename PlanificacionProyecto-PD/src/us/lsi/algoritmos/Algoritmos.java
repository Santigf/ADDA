package us.lsi.algoritmos;

import java.util.List;

import us.lsi.pd.AlgoritmoPD;
import us.lsi.pd.ProblemaPD;

import com.google.common.collect.Lists;

public class Algoritmos {
	
	/**
	 * @param <S> El tipo de la soluci�n
	 * @param <A> El tipo de la alternativa
	 * @param <T> El tipo de las soluciones intermedias
	 * @param p - Problema a resolver 
	 * @return Algoritmo de Programaci�n Din�mica para resolver le problema
	 */
	public static <S, A, T> AlgoritmoPD<S,A, T> createPD(ProblemaPD<S, A, T> p) {
		List<ProblemaPD<S, A, T>> lis = Lists.newArrayList();
		lis.add(p);
		return new AlgoritmoPD<S, A, T>(lis);
	}

	/**
	 * @param <S> El tipo de la soluci�n
	 * @param <A> El tipo de la alternativa
	 * @param <T> El tipo de las soluciones intermedias
	 * @param p - Conjunto de problemas a resolver 
	 * @return Algoritmo de Programaci�n Din�mica para resolver los problemas
	 */
	public static <S, A, T> AlgoritmoPD<S,A,T> createPD(Iterable<ProblemaPD<S, A, T>> p) {
		return new AlgoritmoPD<S, A, T>(p);
	}

}
