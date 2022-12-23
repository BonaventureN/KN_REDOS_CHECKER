package dk.brics.automaton;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
/**
 * Class: EvilRegexDetection
 * Description: Cette classe implémente les tests des méthodes de détection des patterns evil dans les regex
 * @author Arnaud KENNE ET Bonaventure NDSIYO
 * Date: 2022-12-17
 * Version: 1.0
 */

public class Tests {

	public static void main(String[] args) {
		
	
	 
		System.out.println("==DETECTION DES VULNERABILITES DANS LES REGEX==");
		try {
					
				 //lire la regex
				Scanner scan =new Scanner(System.in);
				System.out.println("Entrer la regex à tester");
				String regex =scan.next();
					//vérifier la syntaxe de la regex
					
				Pattern pattern = Pattern.compile(regex);
					
				RegExp r = new RegExp(regex);

				System.out.println("Regex entrée: "+ regex);
				EvilRegexDetection ev =new EvilRegexDetection(regex);
				
				System.out.println("Regex normalisée: "+ev.getRegex());
				
				/*Dès qu'un test est à true le reste est corciuité et le message affiché
				 *au pire des cas les trois tests sont effectués 
				 * */
				if(ev.NQ_checker(r) || ev.QOA_checker(r) || ev.QOD_checker(r)) {
				
	            System.out.println("Regex vulnérable ");
				
						
				}else {
				 
		           System.out.println("Regex non vulnérable");
				}
				
	
				}catch(PatternSyntaxException ex){
					System.out.println("Erreur-->Regex non valide. Vérifier sa syntaxe.Regex non valide");
				}
	            catch(IllegalArgumentException e) {
					System.out.println("Erreur-->Vérifier la syntaxe de votre regex");
				}catch(Exception e) {
					System.out.println("Erreur-->Vérifier la syntaxe de votre regex");
				}
	}

}
