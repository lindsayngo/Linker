import java.util.ArrayList;
import java.util.Scanner;

public class Linkers_Final{
	
	public static void main(String[] args){

		Scanner input = new Scanner(System.in);
		
		
		System.out.println("Enter input: ");
		/* Determine base address for each mod, abs address for each sym,
		 		   Add symbols to symTable, add symbol abs addresses to symTableAddresses
		 		   Add each moduleSize to moduleSizes*/
		
		ArrayList<String> symTable = new ArrayList<String>();
		ArrayList<String> symTableAddresses = new ArrayList<String>();
		ArrayList<String> AllUsedSymbols = new ArrayList<String> ();
		ArrayList<Integer> moduleSizes = new ArrayList<Integer>();
		ArrayList<Integer> numberofusespermod = new ArrayList <Integer>();
		ArrayList<ArrayList> usesList = new ArrayList <ArrayList> ();
		ArrayList<Integer> ProgramTexts = new ArrayList<Integer>();
		
		int definitions = 0;
		int uses = 0;
		int moduleSize = 0;
		String sym;
		
		int baseAddress = 0;
		int symRelAdd = 0;
		int symAbsAdd = 0;
		 
		boolean UsedButNotDefined = false;
		boolean ExceedSize = false;
		
		int number_of_modules = input.nextInt();
		
		for(int i = 0; i < number_of_modules; i ++){ //go through each module's definitions, uses, and program text
			
			definitions = input.nextInt();
			ArrayList <String> defRelAddresses = new ArrayList <String>();

			for(int j = 0; j < definitions; j++){
				
				sym = input.next();
				
				if(symTable.indexOf(sym) != -1){ //If symbol has already been defined, we want to use the most recently defined value
					System.out.println("Error: Symbol " + sym + " multiply defined. Last value is used");
					symRelAdd = input.nextInt();//number after symbol is its new relative address
					int newsymAbsAdd = baseAddress + symRelAdd;//The new absolute address for symbol S defined in module M = the base address of M + relative address of S within M
					symTableAddresses.set(symTableAddresses.indexOf(Integer.toString(symAbsAdd)),Integer.toString(newsymAbsAdd));//replace the old absolute address of the symbol with the new absolute address
					
					defRelAddresses.add(sym);
					defRelAddresses.add(Integer.toString(symRelAdd));

				}
				
				else{
					symRelAdd = input.nextInt();//number after symbol is its relative address
					symAbsAdd = baseAddress + symRelAdd;//The absolute address for symbol S defined in module M = the base address of M + relative adress of S within M
					
					symTable.add(sym);
					symTableAddresses.add(Integer.toString(symAbsAdd));
					
					defRelAddresses.add(sym);
					defRelAddresses.add(Integer.toString(symRelAdd));

				}
			}
			
			uses = input.nextInt();

			numberofusespermod.add(uses);
		
			ArrayList <String> smallList = new ArrayList <String> ();
			for(int j = 0; j < uses; j ++){
				String usedsymbol = input.next();
				AllUsedSymbols.add(usedsymbol);
				smallList.add(usedsymbol);
				int usedAdd = input.nextInt();
					while( usedAdd != -1){
						smallList.add(Integer.toString(usedAdd));
						usedAdd = input.nextInt();
						
					}
					usesList.add(smallList);
					smallList = new ArrayList <String> ();
			}


			 int changedAddress = 0;

			 moduleSize = input.nextInt();
			 moduleSizes.add(moduleSize);
			 
			 boolean isInteger = true;
			 int index = 0;
			 
			 
			//Test if definition relative address is bigger than modulesize	 
			 for(int g = 0; g < defRelAddresses.size(); g++){
				 
				 String element = defRelAddresses.get(g);
				 
				 for(int h = 0; h < element.length(); h++){
					 if(!Character.isDigit(element.charAt(h))){
						 isInteger = false;
						 break;
					 }
					 else{
						 isInteger = true;
					 }
					
				 }
				 
				 if(isInteger == true){
					 if(Integer.parseInt(element) >= moduleSize){ //if the definition exceeds module size

						 if( (g-1) != -1){
							 
								 String symboll = defRelAddresses.get(g-1);
								 for(int s = 0; s < symTable.size(); s ++){
									 if(symboll.equals(symTable.get(s))){
										 index = s;
									 }
								 }
								 
								int changetome = Integer.parseInt(symTableAddresses.get(index)) - (Integer.parseInt(element) - (moduleSize-1));
								 symTableAddresses.set(index, Integer.toString(changetome));
								 System.out.println("Error: Definition " + defRelAddresses.get(g-1) + " with relative address " + defRelAddresses.get(g) + " exceeds module size " + moduleSize);
						
						 }//end of if
						 
					 }
					 
				 }
				 
			 }
			 //End of test
			 
			
				for(int j = 0; j < moduleSize; j++){ //run through each module's program text
					
					int progtext = input.nextInt();
					int originalAddress = (int) (progtext/10); //four left digits
					int specific_addresstype = (int) (progtext % 10); //right most digit
					
					if(specific_addresstype == 1){ //immediate, unchanged
						ProgramTexts.add(originalAddress);
					}
					
					else if(specific_addresstype == 2){ //absolute, unchanged
						String last_3_digits = Integer.toString(originalAddress);
						int last3digits = Integer.parseInt(last_3_digits.substring(1));
							if(last3digits > 300){
								String OG = last_3_digits.substring(0,1) + "299";
								originalAddress = Integer.parseInt(OG);
								System.out.println("Error: " + OG);
								ExceedSize = true;
							}
							
							else{
								ExceedSize = false;
							}
						
						ProgramTexts.add(originalAddress);
					}
					
					else if(specific_addresstype == 3){ //relative, relocated
						changedAddress = originalAddress + baseAddress;
						ProgramTexts.add(changedAddress);
					}
					
					else if(specific_addresstype == 4){ //external, resolved
						ProgramTexts.add(progtext);
					}
					
				}//finished running through module's program texts
			
				baseAddress = baseAddress + moduleSize; //change the baseaddress after this module has been run through
	
			}//end of for loop
			 
		input.close();


		//Print Symbol Table below
			int count = 0; 
			System.out.println("Symbol Table");
			
			for(int w = 0; w < symTable.size(); w++){
				System.out.println(symTable.get(w) + " = " + symTableAddresses.get(count));
				count += 1;
			}
			
			System.out.println();
	
			int completedtext = 0;
			int index = 0;
			int previous = 0;
			int modnumber = 0; //keeps track of what number module we are on
			int numberoftimesinstructionused = 0; //used for multiple symbols listed are used in the same instruction error
			String symbolToUse = null;

		//Go through each program text
		for(int p = 0; p < ProgramTexts.size(); p++){
			
			numberoftimesinstructionused = 0; //For error:
											  //resets for each program text because for each program text, we want to see if more than one symbol is being used
											  //if more than one symbol is being used at the address, we want to use the most recently given symbol
			String progtextstr = Integer.toString(ProgramTexts.get(p)); //read each program text as a string at first
			if(progtextstr.length() == 4){//If its length is 4, the specific address was either 1, 2, or 3, and was fixed and then added to ArrayList ProgramTexts
				System.out.println(p + ": " + ProgramTexts.get(p));//So, we can just print it out
				
				if(ExceedSize == true){//Check error
					System.out.println("^Error: Absolute address exceeds size of machine. Largest legal value is used.");
					ExceedSize = false;
				}
				
			}
			
			else{//We know that the program text needs to be resolved, since it is 5 numbers long and the last number we know is a 4
				int OGaddress = ProgramTexts.get(p) / 10; //We cut off the last number and only deal with the external address
				
				String symbolAbsoluteAddress = null;
	
				for(int l = previous; l < previous + numberofusespermod.get(index); l++){ //checks the UsesList however many uses there are per the current mod
				
					for(int q = 0; q < usesList.get(l).size(); q++){ //checks each string in the smallList in the bigger UsesList
						
						if(Integer.toString(completedtext).equals( usesList.get(l).get(q) ) ){//sees if the current index of the program text matches the address that the symbol is supposed to be used at

							symbolToUse = (String) usesList.get(l).get(0);
							numberoftimesinstructionused += 1;
							
							if(symTable.indexOf(symbolToUse) == -1){ //If this symbol is not in the defined symbols, it is an error
								UsedButNotDefined = true;
								symbolAbsoluteAddress = "111";
							}
							else{
								symbolAbsoluteAddress = symTableAddresses.get(symTable.indexOf(symbolToUse));
							}
		
							
						}
					
					} 
				}
				
				int x = OGaddress - (int) (OGaddress/1000) * 1000;
				int changedadd = OGaddress + Integer.parseInt(symbolAbsoluteAddress) - x;
				System.out.println(p + ": " + changedadd);
				
				if(numberoftimesinstructionused > 1){
					System.out.println("^Error: multiple variables listed in instruction. Last usage given is used.");
				}
				if(UsedButNotDefined == true){
					System.out.println("^Error: " + symbolToUse + " is not defined; value 111 used.");
				}

				
			}//end of else statement, not for loop
			
			completedtext += 1;
	
			if(completedtext == moduleSizes.get(modnumber)){ //If the current mod has been completed
		
				completedtext = 0; //reset completed program text count
				index += 1; //move on to the next modsize 
				previous = previous + numberofusespermod.get(modnumber); //move on to the next mod uses in the UsesList
				modnumber += 1; 

			}

		}//end of for loop
		
		for (int i = 0; i < symTable.size(); i++){
				if (AllUsedSymbols.indexOf(symTable.get(i)) == -1){
				System.out.println("Warning: " + symTable.get(i) + " was defined but never used.");
			}
		}
		
	
	}//end of print static void main function
	
}